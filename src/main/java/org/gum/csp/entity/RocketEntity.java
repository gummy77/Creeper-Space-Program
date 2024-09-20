package org.gum.csp.entity;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.gum.csp.datastructs.RocketSettings;
import org.gum.csp.registries.ItemRegistry;
import org.gum.csp.registries.NetworkingConstants;
import org.gum.csp.registries.ParticleRegistry;
import org.gum.csp.registries.SoundRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RocketEntity extends Entity {

    public static final double GRAVITY = 0.02;

    @Nullable
    private Entity linkedEntity;
    private int linkedEntityId;
    @Nullable
    private NbtCompound fuseNbt;

    //ROCKET SETTINGS
    private RocketSettings rocketSettings;

    public Vec3d rocketRotation = new Vec3d(0, 1f, 0);

    private boolean isLaunching = false;
    private float launchTime;
    private double launchDirection;

    public static final EntitySettings settings = new EntitySettings(
            "rocket_entity",
            SpawnGroup.MISC,
            0.6f, 2f,
            true
    );

    public RocketEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);

    }

    public void Launch(double launchDirection){
        if(!isLaunching) {

            this.isLaunching = true;
            this.launchTime = 0;
            this.launchDirection = launchDirection;

            this.launchParticles();
        }
    }

    public boolean hasLaunched(){
        return this.isLaunching;
    }

    public void networkLaunch() {
        double launchDirection = Random.create().nextFloat() * Math.PI * 4;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIntList(IntList.of(
                this.getId()
        ));
        buf.writeDouble(launchDirection);

        Launch(launchDirection);
        playSound(SoundRegistry.WOODEN_ROCKET_LAUNCH, 3f, 1f);

        getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1, Explosion.DestructionType.BREAK);

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.getBlockPos())) {
            ServerPlayNetworking.send(player, NetworkingConstants.LAUNCH_ROCKET_PACKET_ID, buf);
        }
    }

    private void launchParticles() {
        float smokeForce = 0.2f; //rocketSettings.Power / 10;

        //TODO foreach engine present
        for (int i = 0; i < 360; i += 20) {
            float randomForce = Random.create().nextFloat();
            world.addParticle(ParticleRegistry.EXHAUST, this.getX(), this.getY(), this.getZ(), Math.sin(i) * smokeForce * randomForce, 0, Math.cos(i) * smokeForce * randomForce);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isClient) {
            this.updateFuse();
            this.networkUpdateSettings();
        }

        if (this.isLaunching) {
            launchTime += 1;

            if(launchTime < 150) {
                enginesActive();
                if(launchTime % 10 == 0) {
                    playSound(SoundRegistry.WOODEN_ROCKET_LAUNCH, 3f, 1f);
                }
            } else {
                if(Math.abs(getVelocity().y) < 1) {
                    //System.out.println("Peaked!");
                    //kill();
                }
            }

            if(verticalCollision) {
                if(this.getVelocity().length() >= 1) {
                    getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2, Explosion.DestructionType.BREAK);
                    kill();
                }else{
                    this.setVelocity(0,0,0);
                }
            }

            this.move(MovementType.SELF, getVelocity());
            addVelocity(0, -GRAVITY, 0);
        }
    }

    private void enginesActive(){
        float force = this.getRocketSettings().Acceleration * 0.2f;
        this.addVelocity(rocketRotation.x * force, rocketRotation.y * force, rocketRotation.z * force);
        rocketRotation = rocketRotation.rotateX((float) Math.sin(this.launchDirection) * 0.004f);
        rocketRotation = rocketRotation.rotateZ((float) Math.cos(this.launchDirection) * 0.004f);

        //TODO foreach engine present
        Vec3d particlePosition = getPos();
        world.addParticle(ParticleTypes.FLAME, true, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
        for (int i = 0; i < 2; i++) {
            world.addImportantParticle(ParticleRegistry.EXHAUST, true, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
        }
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if(attacker instanceof PlayerEntity) {
            if(((PlayerEntity) attacker).getStackInHand(((PlayerEntity) attacker).getActiveHand()).isOf(ItemRegistry.DEV_WAND)){
                kill();
            }
        }
        return false;//super.handleAttack(attacker);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getLinkedEntity() == player) {
            this.detachFuse(true, !player.getAbilities().creativeMode);
            return ActionResult.success(this.world.isClient);
        } else {
            ActionResult actionResult = this.interactWithItem(player, hand);
            if (actionResult.isAccepted()) {
                return actionResult;
            } else {
                return super.interact(player, hand);
            }
        }
    }

    private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(ItemRegistry.LAUNCH_KIT)) {
            if(player.getInventory().contains(ItemRegistry.FUSE.getDefaultStack()) || player.isCreative()){
                if(!player.isCreative())
                    player.getInventory().removeStack(player.getInventory().getSlotWithStack(ItemRegistry.FUSE.getDefaultStack()), 1);

                this.attachFuse(player, true);

                return ActionResult.success(this.world.isClient);
            }
        } else if (itemStack.isOf(ItemRegistry.DEV_WAND)) {

            String out;

            if(this.world.isClient) {
                out = "client: ";
            } else{
                out = "server: ";
            }
            System.out.println(out + "wand: " + this.getRocketSettings().blocks.length);
        }
        return ActionResult.PASS;
    }

    public RocketSettings getRocketSettings() {
        if(rocketSettings == null)
            return RocketSettings.SIMPLE_ROCKET;
        return rocketSettings;
    }
    public void setRocketSettings(RocketSettings rocketSettings) {
        this.rocketSettings = rocketSettings;
    }

    protected void updateFuse() {
        if (this.fuseNbt != null) {
            this.readFuseNbt();
        }

        if (this.linkedEntity != null) {
            if (!this.isAlive() || !this.linkedEntity.isAlive()) {
                this.detachFuse(true, true);
            }
        }
    }

    public void detachFuse(boolean sendPacket, boolean dropItem) {
        if (this.linkedEntity != null) {
            this.linkedEntity = null;
            this.fuseNbt = null;
            if (!this.world.isClient && dropItem) {
                this.dropItem(ItemRegistry.FUSE);
            }

            if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
                networkAttachFuse(null);
            }
        }
    }

    @Nullable
    public Entity getLinkedEntity() {
        if (this.linkedEntity == null && this.linkedEntityId != 0 && this.world.isClient) {
            this.linkedEntity = this.world.getEntityById(this.linkedEntityId);
        }

        return this.linkedEntity;
    }

    public void networkUpdateSettings() {
        if(rocketSettings != null) {
            PacketByteBuf buf = PacketByteBufs.create(); //TODO move to its own function and update to work with saving
            buf.writeInt(this.getId());
            buf.writeNbt(rocketSettings.toNbt());
            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.getBlockPos())) {
                ServerPlayNetworking.send(player, NetworkingConstants.ASSEMBLE_ROCKET_PACKET_ID, buf);
            }
        }
    }

    public void attachFuse(Entity entity, boolean sendPacket) {
        this.linkedEntity = entity;
        this.fuseNbt = null;
        if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
            networkAttachFuse(this.linkedEntity);
        }
    }

    public void setLinkedEntityId(int id) {
        this.linkedEntityId = id;
        this.detachFuse(false, false);
    }

    public void networkAttachFuse(@Nullable Entity linkedEntity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIntList(IntList.of(
                this.getId(),
                (linkedEntity != null ? linkedEntity.getId() : 0)
        ));

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.getBlockPos())) {
            ServerPlayNetworking.send(player, NetworkingConstants.ATTACH_FUSE_PACKET_ID, buf);
        }
    }

    private void readFuseNbt() {
        if (this.fuseNbt != null && this.world instanceof ServerWorld) {
            if (this.fuseNbt.containsUuid("UUID")) {
                UUID uUID = this.fuseNbt.getUuid("UUID");
                Entity entity = ((ServerWorld)this.world).getEntity(uUID);
                if (entity != null) {
                    this.attachFuse(entity, true);
                }
            }
        }
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected void initDataTracker() {
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Fuse", 10)) {
            this.fuseNbt = nbt.getCompound("Fuse");
        }
        if(nbt.contains("RocketSettings")) {
            this.rocketSettings = RocketSettings.fromNbt(nbt.getCompound("RocketSettings"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        NbtCompound nbtCompound;

        if(rocketSettings != null) {
            nbt.put("RocketSettings", this.rocketSettings.toNbt());
        }

        if (this.linkedEntity != null) {
            nbtCompound = new NbtCompound();
            if (this.linkedEntity instanceof LivingEntity) {
                UUID uUID = this.linkedEntity.getUuid();
                nbtCompound.putUuid("UUID", uUID);
            }

            nbt.put("Fuse", nbtCompound);
        } else if (this.fuseNbt != null) {
            nbt.put("Fuse", this.fuseNbt.copy());
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}