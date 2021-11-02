package draylar.goml;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import draylar.goml.api.PermissionReason;
import draylar.goml.api.event.ClaimEvents;
import draylar.goml.api.event.ModifyEvents;
import draylar.goml.block.ClaimAnchorBlock;
import draylar.goml.entity.ClaimAnchorBlockEntity;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class EventHandlers {

    private EventHandlers() {
        // NO-OP
    }

    public static void init() {
        registerBreakBlockCallback();
        registerInteractBlockCallback();
        registerAttackEntityCallback();
        registerInteractEntityCallback();
        registerPlayerModifyCallback();
        registerAnchorAttackCallback();
    }

    private static void registerInteractEntityCallback() {
        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, entity.getBlockPos());
            return testPermission(claimsFound, playerEntity, hand, entity.getBlockPos(), PermissionReason.ENTITY_PROTECTED);
        });
    }

    private static void registerAttackEntityCallback() {
        AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, entity.getBlockPos());
            return testPermission(claimsFound, playerEntity, hand, entity.getBlockPos(), PermissionReason.ENTITY_PROTECTED);
        });
    }

    private static void registerInteractBlockCallback() {
        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockHitResult.getBlockPos());
            return testPermission(claimsFound, playerEntity, hand, blockHitResult.getBlockPos(), PermissionReason.AREA_PROTECTED);
        });

        // handle placing blocks at side of block not in claim
        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()));
            return testPermission(claimsFound, playerEntity, hand, blockHitResult.getBlockPos(), PermissionReason.AREA_PROTECTED);
        });
    }

    private static void registerBreakBlockCallback() {
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockPos);
            return testPermission(claimsFound, playerEntity, hand, blockPos, PermissionReason.BLOCK_PROTECTED);
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, pos);
            ActionResult result = testPermission(claimsFound, player, Hand.MAIN_HAND, pos, PermissionReason.BLOCK_PROTECTED);
            return !result.equals(ActionResult.FAIL);
        });
    }

    private static void registerPlayerModifyCallback() {
        ModifyEvents.PLAYER_MODIFY_AT.register(((world, player, pos) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, pos);
            return testPermission(claimsFound, player, Hand.MAIN_HAND, pos, PermissionReason.BLOCK_PROTECTED);
        }));
    }

    private static void registerAnchorAttackCallback() {
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            if(world.getBlockState(blockPos).getBlock() instanceof ClaimAnchorBlock) {
                BlockEntity be = world.getBlockEntity(blockPos);

                if(be instanceof ClaimAnchorBlockEntity) {
                    if(((ClaimAnchorBlockEntity) be).hasAugment()) {
                        return ActionResult.FAIL;
                    }
                }
            }

            return ActionResult.PASS;
        });
    }

    private static ActionResult testPermission(Selection<Entry<ClaimBox, Claim>> claims, PlayerEntity player, Hand hand, BlockPos pos, PermissionReason reason) {
        if(!claims.isEmpty()) {
            boolean noPermission = claims.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission(player));

            if(noPermission && !player.hasPermissionLevel(3)) {
                ActionResult check = ClaimEvents.PERMISSION_DENIED.invoker().check(player, player.world, hand, pos, reason);

                if(check.isAccepted() || check.equals(ActionResult.PASS)) {
                    player.sendMessage(reason.getReason(), true);
                    return ActionResult.FAIL;
                }
            }
        }

        return ActionResult.PASS;
    }
}
