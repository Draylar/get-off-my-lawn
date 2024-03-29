package draylar.goml.mixin;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private World world;
    @Shadow public abstract LivingEntity getCausingEntity();
    @Unique private BlockPos goml_contextPos = null;

    @Inject(
            method = "affectWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void storePosition(boolean particles, CallbackInfo ci, boolean bl, ObjectArrayList objectArrayList, Iterator var4, BlockPos blockPos) {
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
        if(getCausingEntity() instanceof PlayerEntity) {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockPos);

            if (!claimsFound.isEmpty()) {
                return !claimsFound.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission((PlayerEntity) getCausingEntity()));
            }
        }

        return true;
    }
}
