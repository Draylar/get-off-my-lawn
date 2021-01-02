package draylar.goml.mixin.augment;

import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import draylar.goml.registry.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends HostileEntity {

    private EndermanEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "teleportTo(DDD)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void attemptTeleport(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        boolean b = ClaimUtils.getClaimsAt(world, getBlockPos()).anyMatch(claim -> {
            Claim foundClaim = claim.getValue();
            return ClaimUtils.getAnchor(world, foundClaim).hasAugment(Blocks.ENDER_BINDING.getFirst());
        });

        if(b) {
            cir.setReturnValue(false);
        }
    }
}
