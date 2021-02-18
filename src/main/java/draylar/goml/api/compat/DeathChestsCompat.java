package draylar.goml.api.compat;

import com.therandomlabs.vanilladeathchest.deathchest.DeathChest;
import com.therandomlabs.vanilladeathchest.util.DeathChestBlockEntity;
import draylar.goml.api.event.ClaimEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.util.ActionResult;

import java.util.UUID;

public class DeathChestsCompat {

    public static void register() {
        ClaimEvents.PERMISSION_DENIED.register((player, world, hand, pos, reason) -> {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            // Handle UUID-check on server
            if(!world.isClient && blockEntity instanceof DeathChestBlockEntity) {
                DeathChest deathChest = ((DeathChestBlockEntity) blockEntity).getDeathChest();

                if(deathChest != null) {
                    UUID playerUUID = deathChest.getPlayerUUID();
                    if (playerUUID.equals(player.getUuid())) {
                        return ActionResult.FAIL;
                    }
                }
            }

            // Client should return failure to send to the server if the chest is potentially a death chest
            if(world.isClient && blockEntity instanceof LockableContainerBlockEntity) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }
}
