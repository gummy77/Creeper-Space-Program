package org.gum.csp.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.gum.csp.datastructs.RocketSettings;
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

    private boolean isLaunching = false;

    public Vec3d arcRotation = new Vec3d(0, 0.1f, 0);
    public Vec3d previousRenderPosition = new Vec3d(0, 0, 0);
    public Vec3d renderPosition = new Vec3d(0, 0, 0);
    private double launchDirection;

    public static final EntitySettings settings = new EntitySettings(
            "rocketentity",
            SpawnGroup.MISC,
            0.6f, 2f
    );

    public RocketEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    public void Launch(){
        getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1, Explosion.DestructionType.BREAK);
        launchDirection = Random.create().nextFloat() * Math.PI * 4;
        isLaunching = true;

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
        if(isLaunching){
            previousRenderPosition = renderPosition;
            renderPosition = renderPosition.add(arcRotation.getX(), arcRotation.getY(), arcRotation.getZ());
            float force = rocketSettings.Acceleration/20;
            arcRotation = arcRotation.add((float) Math.sin(launchDirection) * force/3, force, (float) Math.cos(launchDirection) * force/3);

            Vec3d particlePosition = getPos().add(renderPosition);
            world.addParticle(ParticleTypes.FLAME, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
            for(int i = 0; i < rocketSettings.Power; i++) {
                world.addParticle(ParticleTypes.CLOUD, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
            }

            if(renderPosition.y > 250){
                kill();
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {

        if (this.getLinkedEntity() == player) {
            this.detachFuse(true, !player.getAbilities().creativeMode);
            return ActionResult.success(this.world.isClient);
        }

        ItemStack itemStack = new ItemStack(ItemRegistry.FUSE);
        if (player.isHolding(ItemRegistry.LAUNCH_KIT)) {
            if(player.getInventory().contains(itemStack)){
                if(!player.isCreative())
                    player.getInventory().removeStack(player.getInventory().getSlotWithStack(itemStack), 1);


                this.attachFuse(player, true);

                player.getStackInHand(hand).finishUsing(world, player);

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public void attachFuse(Entity entity, boolean sendPacket) {
        this.linkedEntity = entity;
        this.fuseNbt = null;
        if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.linkedEntity));
        }
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
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, (Entity)null));
            }
        }
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

    public boolean canBeLinkedBy(PlayerEntity player) {
        return !this.isLinked() && !(this instanceof Monster);
    }

    @Nullable
    public Entity getLinkedEntity() {
        if (this.linkedEntity == null && this.linkedEntityId != 0 && this.world.isClient) {
            this.linkedEntity = this.world.getEntityById(this.linkedEntityId);
        }

        return this.linkedEntity;
    }

    public boolean isLinked() {
        return this.linkedEntity != null;
    }

    public void setLinkedEntityId(int id) {
        this.linkedEntityId = id;
        this.detachFuse(false, false);
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
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
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

            nbt.put("Leash", nbtCompound);
        } else if (this.fuseNbt != null) {
            nbt.put("Leash", this.fuseNbt.copy());
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return new DefaultAttributeContainer.Builder()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

}