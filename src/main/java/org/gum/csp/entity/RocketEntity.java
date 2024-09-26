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
import org.gum.csp.datastructs.Payload;
import org.gum.csp.datastructs.RocketSettings;
import org.gum.csp.item.PayloadItem;
import org.gum.csp.registries.*;
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

    private BlockPos launchposition;

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

            this.launchposition = getBlockPos();
            System.out.println("Launch Position: " + getPos());
            System.out.println("Calculated Height: " + calculateMaxHeight());
        }
    }

    private float calculateMaxHeight(){
        float P = this.getRocketSettings().Power;
        float M = this.getRocketSettings().Mass;
        float T = this.getRocketSettings().burnTime;
        System.out.println("P: "+P + " M: " + M + " T: " + T);

        float log = (float) (  1f / (Math.pow(P*T, 3f) * 20f)  );
        float H = (float) -(  Math.log10(log) * ((300f*P*T)/M)  );

        return H;
    }

    public boolean hasLaunched(){
        return this.isLaunching;
    }

    public void networkLaunch() {
        if(Random.create().nextFloat() * 100f < getRocketSettings().Volatility) { //TODO network a failure event
            //FAILURE ON LAUNCH
            //getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1, true, Explosion.DestructionType.BREAK);
            //kill();
            return;
        }

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

        //TODO foreach engine present -> if we get to boosters
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

            if(launchTime < this.getRocketSettings().burnTime * 20) {
                enginesActive();
                if(launchTime % 10 == 0) {
                    playSound(SoundRegistry.WOODEN_ROCKET_LAUNCH, 3f, 1f);
                }
            } else {
                if(Math.abs(getVelocity().y) < 0.1f) {
                    if(this.getRocketSettings().payload != null){
                        Payload payload = PayloadRegistry.getPayload(this.getRocketSettings().payload);
                        if(payload.onDeploy(this , this.getBlockPos())){
                            payload.onLand(this, new BlockPos(this.getPos().multiply(5f/3f)));
                        };
                    } else {
                        kill();
                    }
                    return;
                }
            }

//            if(this.getPos().y > 750) {
//                System.out.println("MAX HEIGHT REACHED");
//                kill();
//            }

            world.addImportantParticle(ParticleRegistry.EXHAUST, true, getPos().x, getPos().y, getPos().z, 0, 0, 0);

            if(verticalCollision) {
                if(this.getVelocity().length() >= 0.25f) {
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
        float force = 0.15f;
        this.addVelocity(rocketRotation.x * force, rocketRotation.y * force, rocketRotation.z * force);
        rocketRotation = rocketRotation.rotateX((float) Math.sin(this.launchDirection) * 0.01f);
        rocketRotation = rocketRotation.rotateZ((float) Math.cos(this.launchDirection) * 0.01f);

        //TODO foreach engine present
        Vec3d particlePosition = getPos();
        world.addParticle(ParticleTypes.FLAME, true, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
        for (int i = 0; i < 3; i++) {
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
                out = "\nClient: ";
            } else {
                out = "\nServer: ";
            }

            System.out.println(out + "\n" +
                    "Mass: " + this.getRocketSettings().Mass + "\n" +
                    "Volatility: " + this.getRocketSettings().Volatility + "\n" +
                    "Power: " + this.getRocketSettings().Power + "\n" +
                    "Acceleration: " + getRocketSettings().Acceleration + "\n" +
                    "Burn Time: " + getRocketSettings().burnTime * 20 + " ticks (" + getRocketSettings().burnTime + " seconds)"
            );
        } else if(itemStack.getItem().getClass() == PayloadItem.class) {
            return addPayload(itemStack);
        }
        return ActionResult.PASS;
    }

    public ActionResult addPayload(ItemStack payloadItem) {
        this.getRocketSettings().payload = PayloadRegistry.PAYLOADS.DEFAULT;
        return ActionResult.SUCCESS;
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