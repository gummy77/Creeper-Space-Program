package org.gum.csp.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.gum.csp.datastructs.RocketSettings;
import org.gum.csp.packet.EntityLinkS2CPacket;
import org.gum.csp.registries.ItemRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RocketEntity extends Entity {

    @Nullable
    private Entity linkedEntity;
    private int linkedEntityId;
    @Nullable
    private NbtCompound fuseNbt;

    //ROCKET SETTINGS
    public RocketSettings rocketSettings = RocketSettings.SIMPLE_ROCKET;
    @Nullable
    private NbtCompound rocketState;


    private boolean isLaunching = false;

    public Vec3d arcRotation = new Vec3d(0, 0.1f, 0);
    public Vec3d previousRenderPosition = new Vec3d(0, 0, 0); //TODO make this serverside :/
    public Vec3d renderPosition = new Vec3d(0, 0, 0);
    private double launchDirection;

    public static final EntitySettings settings = new EntitySettings(
            "rocketentity",
            SpawnGroup.MISC,
            0.6f, 2f
    );

    public RocketEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        rocketState = new NbtCompound();
    }

    public void Launch(){
        getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1, Explosion.DestructionType.BREAK);
        launchDirection = Random.create().nextFloat() * Math.PI * 4;
        isLaunching = true;

        this.rocketState.putBoolean("isLaunching", true);



        //Takeoff Particles
        float smokeForce = rocketSettings.Power / 10;
        for(int i = 0; i < 360; i += (int)(60/rocketSettings.Power)) {
            float randomForce = Random.create().nextFloat();
            world.addParticle(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), Math.sin(i) * smokeForce * randomForce, 0, Math.cos(i) * smokeForce * randomForce);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.updateFuse();
        }
        if(rocketState.getBoolean("isLaunching")){
            //previousRenderPosition = renderPosition;
            //renderPosition = renderPosition.add(arcRotation.getX(), arcRotation.getY(), arcRotation.getZ());
            //float force = rocketSettings.Acceleration/20;
            //arcRotation = arcRotation.add((float) Math.sin(launchDirection) * force/3, force, (float) Math.cos(launchDirection) * force/3);

            Vec3d particlePosition = getPos().add(renderPosition);
            world.addParticle(ParticleTypes.FLAME, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
            for(int i = 0; i < rocketSettings.Power; i++) {
                world.addParticle(ParticleTypes.CLOUD, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
            }

//            if(renderPosition.y > 250){
//                kill();
//            }
        }
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
                    player.getInventory().removeStack(player.getInventory().getSlotWithStack(itemStack), 1);

                this.attachFuse(player, true);

                return ActionResult.success(this.world.isClient);
            }
        }
        return ActionResult.PASS;
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
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityLinkS2CPacket(this, (Entity)null));
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

    public void attachFuse(Entity entity, boolean sendPacket) {
        this.linkedEntity = entity;
        this.fuseNbt = null;
        if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityLinkS2CPacket(this, this.linkedEntity));
        }
    }

    public void setLinkedEntityId(int id) {
        this.linkedEntityId = id;
        this.detachFuse(false, false);
    }

    private void readFuseNbt() {
        if (this.fuseNbt != null && this.world instanceof ServerWorld) {
            if (this.fuseNbt.containsUuid("UUID")) {
                UUID uUID = this.fuseNbt.getUuid("UUID");
                Entity entity = ((ServerWorld)this.world).getEntity(uUID);
                if (entity != null) {
                    this.attachFuse(entity, true);
                    return;
                }
            } else if (this.fuseNbt.contains("X", 99) && this.fuseNbt.contains("Y", 99) && this.fuseNbt.contains("Z", 99)) {
                BlockPos blockPos = NbtHelper.toBlockPos(this.fuseNbt);
                this.attachFuse(LeashKnotEntity.getOrCreate(this.world, blockPos), true);
                return;
            }

            if (this.age > 100) {
                this.dropItem(Items.LEAD);
                this.fuseNbt = null;
            }
        }
    }

    @Override
    public boolean canHit() {
        return !isLaunching;
    }

    @Override
    public boolean isCollidable() {
        return !isLaunching;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Fuse", 10)) {
            this.fuseNbt = nbt.getCompound("Fuse");
        }
        if(nbt.contains("RocketState")){
            this.rocketState = nbt.getCompound("RocketState");
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put("RocketState", this.rocketState);

        NbtCompound nbtCompound;
        if (this.linkedEntity != null) {
            nbtCompound = new NbtCompound();
            if (this.linkedEntity instanceof LivingEntity) {
                UUID uUID = this.linkedEntity.getUuid();
                nbtCompound.putUuid("UUID", uUID);
            } else if (this.linkedEntity instanceof AbstractDecorationEntity) {
                BlockPos blockPos = ((AbstractDecorationEntity)this.linkedEntity).getDecorationBlockPos();
                nbtCompound.putInt("X", blockPos.getX());
                nbtCompound.putInt("Y", blockPos.getY());
                nbtCompound.putInt("Z", blockPos.getZ());
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