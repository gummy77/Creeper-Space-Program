package org.gum.csp.entity;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.gum.csp.datastructs.PayloadSettings;
import org.gum.csp.datastructs.RocketPart;
import org.gum.csp.registries.ItemRegistry;
import org.gum.csp.registries.NetworkingConstants;

public class PayloadEntity extends Entity {

    public static final EntitySettings settings = new EntitySettings(
            "payload_entity",
            SpawnGroup.MISC,
            1f, 1f,
            true
    );

    private PayloadSettings payloadSettings;
    private boolean hasParachuteDeployed = false;
    private float parachuteAngle = 0;

    public PayloadEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if(!world.isClient) {
            if(payloadSettings != null) {
                PacketByteBuf buf = PacketByteBufs.create(); //TODO move to its own function and update to work with saving
                buf.writeInt(this.getId());
                buf.writeNbt(payloadSettings.toNbt());
                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, this.getBlockPos())) {
                    ServerPlayNetworking.send(player, NetworkingConstants.DEPLOY_PAYLOAD_PACKET_ID, buf);
                }
            }
        }

        if(!onGround) {
            this.addVelocity(0, -0.02, 0);
            this.setVelocity(this.getVelocity().multiply(0.98f));
            if (getBlockPos().getY() - world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, getBlockPos()).getY() < 40) {
                this.setVelocity(this.getVelocity().multiply(0.925f));
                hasParachuteDeployed = true;
            }
            this.move(MovementType.SELF, this.getVelocity());
        }
    }

    public boolean hasParachuteDeployed() {
        return hasParachuteDeployed;
    }

    public float getParachuteAngle() { return parachuteAngle; }
    public void setParachuteAngle(float parachuteAngle) { this.parachuteAngle = parachuteAngle; }

    public PayloadSettings getPayloadSettings(){
        return this.payloadSettings;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ActionResult actionResult = this.interactWithItem(player, hand);
        if (actionResult.isAccepted()) {
            return actionResult;
        } else {
            return super.interact(player, hand);
        }
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if(attacker instanceof PlayerEntity) {
            if(this.getPayloadSettings().payload != null) {
                this.getPayloadSettings().payload.onInteract(world, this, this.getBlockPos(), attacker);
            }

            if(attacker instanceof ServerPlayerEntity) {
                if(!((PlayerEntity) attacker).isCreative()) {
                    for (RocketPart part : getPayloadSettings().blocks) {
                        dropStack(part.block.getBlock().asItem().getDefaultStack());
                    }
                }
            } else {
                for (RocketPart part : getPayloadSettings().blocks) {
                    dropStack(part.block.getBlock().asItem().getDefaultStack());
                }
            }

            for (RocketPart part : getPayloadSettings().blocks) {
                Vec3d partPos = this.getPos().add(part.offset.getX() - 0.5f, part.offset.getY(), part.offset.getZ() - 0.5f);
                BlockState blockState = part.block;
                for (int i = 0; i < 10; i++) {
                    Vec3d randomPos = partPos.add(random.nextFloat(), random.nextFloat(), random.nextFloat());
                    world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), randomPos.x, randomPos.y, randomPos.z, 0, 0, 0);
                }

                playSound(blockState.getSoundGroup().getBreakSound(), 1, 1);
            }

            kill();
        }
        return false;
    }

    private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(ItemRegistry.DEV_WAND)) {
            this.setParachuteAngle(0);

        }
        return ActionResult.PASS;
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
        if(this.getPayloadSettings() == null) return EntityDimensions.fixed(1, 1).getBoxAt(this.getPos());
        EntityDimensions dimensions;
        if(this.getPayloadSettings().blocks.length == 0) {
            dimensions = EntityDimensions.fixed(0.8f, 2);
        } else {
            float width = this.getPayloadSettings().getMaxWidth();
            dimensions = EntityDimensions.fixed(width/16f, this.getPayloadSettings().blocks.length);
        }
        return dimensions.getBoxAt(this.getPos());
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if(nbt.contains("PayloadSettings")) {
            this.payloadSettings = PayloadSettings.fromNbt(nbt.getCompound("PayloadSettings"));
            this.setBoundingBox(calculateBoundingBox());
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if(this.payloadSettings != null) {
            nbt.put("PayloadSettings", this.payloadSettings.toNbt());
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
