package draylar.goml.mixin;

import draylar.goml.api.event.ModifyEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(at = @At("TAIL"), method="canPlayerModifyAt", cancellable = true)
    public void canPlayerModifyAt(PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            ServerWorld world = ((ServerWorld) (Object) this);

            if (ModifyEvents.PLAYER_MODIFY_AT.invoker().check(world, player, pos) != ActionResult.PASS) {
                cir.setReturnValue(false);
            }
        }
    }
}
