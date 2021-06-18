package draylar.goml.mixin.compat;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import ladysnake.blast.common.world.CustomExplosion;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(CustomExplosion.class)
public abstract class BlastExplosionMixin extends Explosion {

    @Unique private BlockPos goml_contextPos = null;

    private BlastExplosionMixin(World world, @Nullable Entity entity, double x, double y, double z, float power) {
        super(world, entity, x, y, z, power);
    }

    @Inject(
            method = "affectWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void storePosition(boolean boolean_1, CallbackInfo ci, boolean boolean_2, Iterator var3, BlockPos blockPos, ObjectArrayList objectArrayList, BlockState blockState) {
        goml_contextPos = blockPos;
    }

    @Redirect(
            method = "affectWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isAir()Z", ordinal = 0))
    private boolean isAirAndInvalid(BlockState blockState) {
        return blockState.isAir() || !isValid(goml_contextPos);
    }

    @Unique
    private boolean isValid(BlockPos blockPos) {
        Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(super.world, blockPos);
        return claimsFound.isEmpty();
    }
}
