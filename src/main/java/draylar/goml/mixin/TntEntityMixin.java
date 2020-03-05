package draylar.goml.mixin;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.ClaimInfo;
import draylar.goml.api.ClaimUtils;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Mixin which prevents TNT primed by player A from going off in player B's claim.
 */
@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity {

    @Shadow private LivingEntity causingEntity;

    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "explode", cancellable = true)
    private void attemptExplosion(CallbackInfo ci) {
        if (causingEntity instanceof PlayerEntity) {
            Selection<Entry<Box, ClaimInfo>> sel = ClaimUtils.getClaimsAt(world, getBlockPos());

            if (!sel.isEmpty()) {
                AtomicBoolean hasPermission = new AtomicBoolean(true);

                sel.forEach(claim -> {
                    if (!ClaimUtils.playerHasPermission(claim, (PlayerEntity) this.causingEntity)) {
                        hasPermission.set(false);
                    }
                });

                if (!hasPermission.get()) {
                    ci.cancel();
                }
            }
        }
    }
}
