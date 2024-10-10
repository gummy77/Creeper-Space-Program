package org.gum.csp.entity;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.gum.csp.datastructs.Payload;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.datastructs.RocketSettings;
import org.gum.csp.item.PayloadItem;
import org.gum.csp.item.PayloadTrackingCompass;
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
    private float launchTime = 0;
    private double launchDirection = 0;

    private boolean shouldRenderInfo = false;
    private PlayerEntity infoLinkedPlayer;
    private int infoRenderTime = 0;

    private int health = 4;

    private boolean isAboutToExplode = false;

    public static final EntitySettings settings = new EntitySettings(
            "rocket_entity",
            SpawnGroup.MISC,
            0.6f, 2f,
            true
    );

    public RocketEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    public void Launch(){
        if(!isLaunching) {
            this.isLaunching = true;
            this.launchTime = 0;

            this.launchParticles();
        }
    }

    public float calculateMaxHeight(){
        float P = this.getRocketSettings().Power;
        float M = this.getRocketSettings().Mass;
        float T = this.getRocketSettings().burnTime;

        float log = (float) (  1f / (Math.pow(P*T, 3f) * 20f)  );
        float H = (float) -(  Math.log10(log) * ((300f*P*T)/M)  );

        return H;
    }

    public static float calculateMaxHeight(RocketSettings settings){
        float f = settings.Power;
        float m = settings.Mass;
        float t = settings.burnTime;

        float a = f/m;
        float vi = a*t;
        float h = (vi*vi) / (2 * 9.8f);

        return h;
    }

    public boolean hasLaunched(){
        return this.isLaunching;
    }

    public void haveFailure(int type) {
        System.out.println((this.world.isClient ? "Client" : "Server") + " - fails");
        this.getRocketSettings().isFailing = true;
        if(type == 0) {
            this.isAboutToExplode = true;
        }
    }

    public boolean networkFailure() {
        return networkFailure(random.nextBetween(0, 1));
    }

    public boolean networkFailure(int failureType) {

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIntList(IntList.of(
                this.getId(),
                failureType
        ));

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.getBlockPos())) {
            ServerPlayNetworking.send(player, NetworkingConstants.ROCKET_FAILURE_PACKET_ID, buf);
        }

        this.haveFailure(failureType);

        if(failureType == 0) {
            return true;
        }
        return false;
    }

    public void networkLaunch() {
        boolean failedOnLaunch = false;
        if(Random.create().nextFloat() * 100f < getRocketSettings().Volatility) { //TODO network a failure event
            failedOnLaunch = networkFailure();
        }

        playSound(SoundRegistry.WOODEN_ROCKET_LAUNCH, 3f, 1f);

        if(!failedOnLaunch){
            getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1, Explosion.DestructionType.BREAK);

            Launch();

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIntList(IntList.of(
                    this.getId()
            ));

            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.getBlockPos())) {
                ServerPlayNetworking.send(player, NetworkingConstants.LAUNCH_ROCKET_PACKET_ID, buf);
            }

            if (this.getRocketSettings().payload != null) {
                Payload payload = PayloadRegistry.getPayload(this.getRocketSettings().payload);
                payload.Deploy(world, this, this.getPayloadPosition(), this.calculateMaxHeight());
            }
        }
    }

    protected BlockPos getPayloadPosition(){
        float payloadDistance = this.calculateMaxHeight() * (2f/3f);


        Vec3d payloadPosition = new Vec3d(Math.cos(launchDirection), 0, -Math.sin(launchDirection));
        payloadPosition = payloadPosition.multiply(payloadDistance);

        payloadPosition = payloadPosition.add(this.getPos());

        return new BlockPos(payloadPosition.x, payloadPosition.y, payloadPosition.z);
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
        } else {
            if(this.shouldRenderInfo) {
                //this.infoRenderTime -= 1;
                if(this.infoRenderTime <= 0) {
                    this.shouldRenderInfo = false;
                }
            }
        }

        if(this.isAboutToExplode) {
            this.launchTime += this.world.isClient ? 1.2f : 1f;
            Vec3d particlePosition = getPos().add(0, 0.75f, 0);
            for (int i = 0; i < 10; i++) {
                Vec3d dir = new Vec3d(Math.sin(random.nextFloat() * Math.PI * 2), 0, Math.cos(random.nextFloat() * Math.PI * 2));
                dir = dir.multiply(0.025f);
                world.addImportantParticle(ParticleTypes.FLAME, true, particlePosition.x, particlePosition.y, particlePosition.z, dir.x, 0, dir.z);
            }

            if(this.launchTime >= random.nextBetween(30, 60)){
                //System.out.println(this.world.isClient + " - fails");
                this.explode();
            }
        }

        if (this.isLaunching) {
            launchTime += 1;

            if(launchTime < 90) {
                enginesActive();
            } else {
                if(Math.abs(getVelocity().y) < 0.1f) {
                    kill();
                    return;
                }
            }

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

    private void explode() {
        System.out.println("explodes with: " + this.getRocketSettings().blocks.length + " blocks");
        world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 1, 1, true);

        for(RocketPart part : this.getRocketSettings().blocks) {
            Vec3d particlePosition = getPos().add(part.offset.getX(), part.offset.getY(), part.offset.getZ());
            for(int i = 0; i < 20; i++) {
                Vec3d vel = new Vec3d(random.nextFloat()-0.5f, random.nextFloat()-0.5f, random.nextFloat()-0.5f);
                vel = vel.multiply(Math.sqrt(vel.length()) * 0.25f);
                world.addImportantParticle(ParticleTypes.FLAME, true, particlePosition.x, particlePosition.y, particlePosition.z, -vel.x * 3, -vel.y * 3, -vel.z * 3);
                world.addImportantParticle(ParticleRegistry.EXHAUST, true, particlePosition.x, particlePosition.y, particlePosition.z, vel.x, vel.y, vel.z);
            }
        }

        if(!world.isClient) {
            getEntityWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2, true, Explosion.DestructionType.BREAK);
            kill();
        }
        this.isAboutToExplode = false;
    }

    private void enginesActive(){
        float force = 0.075f;
        this.addVelocity(rocketRotation.x * force, rocketRotation.y * force, rocketRotation.z * force);
        rocketRotation = rocketRotation.rotateX((float) Math.sin(this.launchDirection) * 0.005f);
        rocketRotation = rocketRotation.rotateZ((float) Math.cos(this.launchDirection) * 0.005f);

        if(launchTime % 10 == 0) {
            playSound(SoundRegistry.WOODEN_ROCKET_LAUNCH, 3f, 1f);
        }

        //TODO foreach engine present
        Vec3d particlePosition = getPos();
        world.addParticle(ParticleTypes.FLAME, true, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
        for (int i = 0; i < 3; i++) {
            world.addImportantParticle(ParticleRegistry.EXHAUST, true, particlePosition.x, particlePosition.y, particlePosition.z, 0, 0, 0);
        }
    }

    @Override
    public void checkDespawn() {
        if(this.isLaunching && (this.launchTime > this.getRocketSettings().burnTime)) {
            Entity entity = this.world.getClosestPlayer(this, -1.0);
            if (entity != null) {
                Vec3d checkposition = new Vec3d(this.getX(), entity.getY(), this.getZ());
                double d = entity.squaredDistanceTo(checkposition);
                int i = this.getType().getSpawnGroup().getImmediateDespawnRange();
                int j = i * i * i;
                if (d > (double) j) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if(attacker instanceof PlayerEntity) {
            for (RocketPart part : getRocketSettings().blocks) {
                Vec3d partPos = this.getPos().add(part.offset.getX() - (part.radius * 0.0625f * 0.5f), part.offset.getY(), part.offset.getZ() - (part.radius * 0.0625f * 0.5f));
                BlockState blockState = part.Block;
                for (int i = 0; i < 5; i++) {
                    Vec3d randomPos = partPos.add(random.nextFloat() * part.radius * 0.0625f, random.nextFloat(), random.nextFloat() * part.radius * 0.0625f);
                    world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), randomPos.x, randomPos.y, randomPos.z, 0, 0, 0);
                }

                playSound(blockState.getSoundGroup().getBreakSound(), 1, 1);
            }
            if (this.health <= 0) {
                if(this.getRocketSettings().payload != null) {
                    dropStack(PayloadRegistry.getPayloadStack(this.getRocketSettings().payload), getRocketSettings().blocks.length);
                }
                if (attacker instanceof ServerPlayerEntity) {
                    if (!((PlayerEntity) attacker).isCreative()) {
                        for (RocketPart part : getRocketSettings().blocks) {
                            dropStack(part.Block.getBlock().asItem().getDefaultStack(), part.offset.getY());
                        }
                    }
                } else {
                    for (RocketPart part : getRocketSettings().blocks) {
                        dropStack(part.Block.getBlock().asItem().getDefaultStack(), part.offset.getY());
                    }
                }
                kill();
                return true;
            }
            this.health -= 1;
        }
        return false;
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
            if(!this.world.isClient) {
                this.networkFailure(0);
            }
//            if (this.getRocketSettings().payload != null) {
//                Payload payload = PayloadRegistry.getPayload(this.getRocketSettings().payload);
//                payload.Deploy(world, this, this.getBlockPos(), this.calculateMaxHeight());
//                kill();
//            }
        } else if(itemStack.getItem().getClass() == PayloadItem.class) {
            return addPayload(itemStack, player, hand);
        } else if(itemStack.isOf(ItemRegistry.PAYLOAD_COMPASS)) {
            return addPayloadTracker(this, itemStack, player, hand);
        } else if(itemStack.isOf(ItemRegistry.ROCKET_INSPECTOR)) {
            return displayStats(player);
        }
        return ActionResult.PASS;
    }

    public int getRenderInfo(){
        return this.shouldRenderInfo ? this.infoRenderTime : 0;
    }

    public PlayerEntity getInfoLinkedPlayer(){
        return infoLinkedPlayer;
    }

    public ActionResult displayStats(PlayerEntity player) {
        if(world.isClient) {
            this.infoRenderTime = 90; // Time delay for text
            this.shouldRenderInfo = true;
            this.infoLinkedPlayer = player;
        }
        return ActionResult.SUCCESS;
    }

    public ActionResult addPayloadTracker(RocketEntity entity, ItemStack itemStack, PlayerEntity player, Hand hand) {
        BlockPos blockPos = this.getPayloadPosition();
        World world = entity.getWorld();

        if(this.getRocketSettings().payload != null && this.getRocketSettings().payload.canBeTracked()) {
            world.playSound((PlayerEntity)null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            PayloadTrackingCompass.writeNbt(world.getRegistryKey(), this.getId(), blockPos, itemStack.getOrCreateNbt());
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public ActionResult addPayload(ItemStack payloadItem, PlayerEntity player, Hand hand) {
        if(this.getRocketSettings().payload == null) {

            PayloadRegistry.PAYLOADS payload = PayloadRegistry.payloadFromStack(payloadItem);
            if(payload == null) return ActionResult.FAIL;

            this.getRocketSettings().payload = payload;

            for (int i = 0; i < 10; i++) {
                world.addParticle(ParticleTypes.GLOW, this.getPos().x + random.nextFloat() - 0.5f, this.getPos().y + getRocketSettings().blocks.length - random.nextFloat(), this.getPos().z + random.nextFloat() - 0.5f, 0, 0, 0);
            }

            player.getStackInHand(hand).decrement(1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public RocketSettings getRocketSettings() {
        if(rocketSettings == null)
            return RocketSettings.SIMPLE_ROCKET;
        return rocketSettings;
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
            buf.writeDouble(this.launchDirection);
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

    public int getHealth(){
        return this.health;
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
    protected Box calculateBoundingBox() {
        EntityDimensions dimensions;
        if(this.getRocketSettings().blocks.length == 0) {
            dimensions = EntityDimensions.fixed(0.8f, 2);
        } else {
            float width = this.getRocketSettings().getMaxWidth();
            dimensions = EntityDimensions.fixed(width/16f, this.getRocketSettings().blocks.length);
        }
        return dimensions.getBoxAt(this.getPos());
    }

    @Override
    protected void initDataTracker() {
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Fuse", 10)) {
            this.fuseNbt = nbt.getCompound("Fuse");
        }

        if(nbt.contains("isLaunching")) this.isLaunching = nbt.getBoolean("isLaunching");
        if(nbt.contains("launchDirection")) this.launchDirection = nbt.getDouble("launchDirection");
        if(nbt.contains("launchTime")) this.launchTime = nbt.getInt("launchTime");

        if(nbt.contains("RocketSettings")) {
            this.rocketSettings = RocketSettings.fromNbt(nbt.getCompound("RocketSettings"));
            this.setBoundingBox(calculateBoundingBox());
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        NbtCompound nbtCompound;

        if(rocketSettings != null) {
            nbt.put("RocketSettings", this.rocketSettings.toNbt());
        }

        nbt.putBoolean("isLaunching", this.isLaunching);
        nbt.putDouble("launchDirection", this.launchDirection);
        nbt.putFloat("launchTime", this.launchTime);

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