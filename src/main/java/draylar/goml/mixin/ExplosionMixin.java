package draylar.goml.mixin;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import com.mojang.datafixers.util.Pair;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private World world;

    @Shadow @Final private double x;

    @Shadow @Final private double y;

    @Shadow @Final private double z;

    @Shadow @Final private float power;

    @Shadow @Final private List<BlockPos> affectedBlocks;

    @Shadow
    private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList, ItemStack itemStack, BlockPos blockPos) {
    }

    @Shadow @Final private boolean createFire;

    @Shadow @Final private Random random;

    @Shadow @Final private Entity entity;

    @Shadow public abstract LivingEntity getCausingEntity();

    @Shadow @Final private Explosion.DestructionType destructionType;

    @Inject(at = @At("HEAD"), method = "affectWorld", cancellable = true)
    private void affectWorld(boolean bl, CallbackInfo ci) {
        runCustomAffectWorld(bl);
        ci.cancel();
    }

    @Unique
    private void runCustomAffectWorld(boolean bl) {
        if (this.world.isClient) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        boolean bl2 = this.destructionType != Explosion.DestructionType.NONE;
        if (bl) {
            if (this.power >= 2.0F && bl2) {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            } else {
                this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            }
        }

        if (bl2) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList();
            Collections.shuffle(this.affectedBlocks, this.world.random);
            Iterator var4 = this.affectedBlocks.iterator();

            while(var4.hasNext()) {
                BlockPos blockPos = (BlockPos)var4.next();
                BlockState blockState = this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();

                if(isValid(blockPos)) {

                    if (!blockState.isAir()) {
                        BlockPos blockPos2 = blockPos.toImmutable();
                        this.world.getProfiler().push("explosion_blocks");
                        if (block.shouldDropItemsOnExplosion(((Explosion) (Object) this)) && this.world instanceof ServerWorld) {
                            BlockEntity blockEntity = block instanceof BlockEntityProvider ? this.world.getBlockEntity(blockPos) : null;
                            LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).random(this.world.random).parameter(LootContextParameters.ORIGIN, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ())).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity).optionalParameter(LootContextParameters.THIS_ENTITY, entity);
                            if (this.destructionType == Explosion.DestructionType.DESTROY) {
                                builder.parameter(LootContextParameters.EXPLOSION_RADIUS, this.power);
                            }

                            blockState.getDroppedStacks(builder).forEach((itemStack) -> {
                                tryMergeStack(objectArrayList, itemStack, blockPos2);
                            });
                        }

                        this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
                        block.onDestroyedByExplosion(this.world, blockPos, ((Explosion) (Object) this));
                        this.world.getProfiler().pop();
                    }
                }
            }

            ObjectListIterator var12 = objectArrayList.iterator();

            while(var12.hasNext()) {
                Pair<ItemStack, BlockPos> pair = (Pair)var12.next();
                Block.dropStack(this.world, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
            }
        }

        if (this.createFire) {
            Iterator var11 = this.affectedBlocks.iterator();

            while(var11.hasNext()) {
                BlockPos blockPos3 = (BlockPos)var11.next();
                if (this.random.nextInt(3) == 0 && this.world.getBlockState(blockPos3).isAir() && this.world.getBlockState(blockPos3.down()).isOpaqueFullCube(this.world, blockPos3.down())) {
                    this.world.setBlockState(blockPos3, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    @Unique
    private boolean isValid(BlockPos blockPos) {
        if(getCausingEntity() instanceof PlayerEntity) {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockPos);

            if (!claimsFound.isEmpty()) {
                return !claimsFound.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission((PlayerEntity) getCausingEntity()));
            }
        }

        return true;
    }
}
