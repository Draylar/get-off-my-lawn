package draylar.goml;

import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimUtils;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

public class EventHandlers {

    private EventHandlers() {
        // NO-OP
    }

    public static void init() {
        registerBreakBlockCallback();
        registerInteractBlockCallback();
        registerAttackEntityCallback();
        registerInteractEntityCallback();
    }

    private static void registerInteractEntityCallback() {
        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, entity.getBlockPos());

            if(!claimsFound.isEmpty()) {
                boolean noPermission = claimsFound.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission(playerEntity));

                if(noPermission || !playerEntity.hasPermissionLevel(3)) {
                    playerEntity.sendMessage(new TranslatableText("goml.entity_protected"), true);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }

    private static void registerAttackEntityCallback() {
        AttackEntityCallback.EVENT.register(((playerEntity, world, hand, entity, entityHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, entity.getBlockPos());

            if(!claimsFound.isEmpty()) {
                boolean noPermission = claimsFound.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission(playerEntity));

                if(noPermission || !playerEntity.hasPermissionLevel(3)) {
                    playerEntity.sendMessage(new TranslatableText("goml.entity_protected"), true);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        }));
    }

    private static void registerInteractBlockCallback() {
        UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockHitResult.getBlockPos());

            if(!claimsFound.isEmpty()) {
                boolean noPermission = claimsFound.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission(playerEntity));

                if(noPermission || !playerEntity.hasPermissionLevel(3)) {
                    playerEntity.sendMessage(new TranslatableText("goml.block_protected"), true);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        }));

        // handle placing blocks at side of block not in claim
        UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()));

            if(!claimsFound.isEmpty()) {
                boolean noPermission = claimsFound.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission(playerEntity));

                if(noPermission || !playerEntity.hasPermissionLevel(3)) {
                    playerEntity.sendMessage(new TranslatableText("goml.block_protected"), true);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        }));
    }

    private static void registerBreakBlockCallback() {
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            Selection<Entry<ClaimBox, Claim>> claimsFound = ClaimUtils.getClaimsAt(world, blockPos);

            if(!claimsFound.isEmpty()) {
                boolean noPermission = claimsFound.anyMatch((Entry<ClaimBox, Claim> boxInfo) -> !boxInfo.getValue().hasPermission(playerEntity));

                if(noPermission || !playerEntity.hasPermissionLevel(3)) {
                    playerEntity.sendMessage(new TranslatableText("goml.block_protected"), true);
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }
}
