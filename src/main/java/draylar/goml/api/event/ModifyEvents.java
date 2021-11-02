package draylar.goml.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifyEvents {

    /**
     *  This callback is triggered when a player attempts to modify a block
     *  Callback handlers can return deny this by not returning pass {@link ActionResult}.
     */

    public static final Event<PlayerModifyHandler> PLAYER_MODIFY_AT = EventFactory.createArrayBacked(PlayerModifyHandler.class,
            (listeners) -> (world, player, pos) -> {
                for (PlayerModifyHandler event : listeners) {
                    ActionResult result = event.check(world, player, pos);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    public interface PlayerModifyHandler {
        ActionResult check(World world, PlayerEntity player, BlockPos pos);
    }
}
