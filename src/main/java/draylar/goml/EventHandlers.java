package draylar.goml;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.api.ClaimInfo;
import draylar.goml.api.ClaimUtils;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;

import java.util.concurrent.atomic.AtomicBoolean;

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
            Selection<Entry<Box, ClaimInfo>> sel = ClaimUtils.getClaimsAt(world, entity.getBlockPos());

            if(!sel.isEmpty()) {
                AtomicBoolean hasPermission = new AtomicBoolean(true);

                sel.forEach(claim -> {
                    if(!ClaimUtils.playerHasPermission(claim, playerEntity)) {
                        hasPermission.set(false);
                    }
                });

                if(!hasPermission.get()) {
                    // TODO: translatable text
                    playerEntity.addChatMessage(new LiteralText("This entity is protected by a claim."), true);

                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }

    private static void registerAttackEntityCallback() {
        AttackEntityCallback.EVENT.register(((playerEntity, world, hand, entity, entityHitResult) -> {
            Selection<Entry<Box, ClaimInfo>> sel = ClaimUtils.getClaimsAt(world, entity.getBlockPos());

            if(!sel.isEmpty()) {
                AtomicBoolean hasPermission = new AtomicBoolean(true);

                sel.forEach(claim -> {
                    if(!ClaimUtils.playerHasPermission(claim, playerEntity)) {
                        hasPermission.set(false);
                    }
                });

                if(!hasPermission.get()) {
                    // TODO: translatable text
                    playerEntity.addChatMessage(new LiteralText("This entity is protected by a claim."), true);

                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        }));
    }

    private static void registerInteractBlockCallback() {
        UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
            Selection<Entry<Box, ClaimInfo>> sel = ClaimUtils.getClaimsAt(world, blockHitResult.getBlockPos());

            if(!sel.isEmpty()) {
                AtomicBoolean hasPermission = new AtomicBoolean(true);

                sel.forEach(claim -> {
                    if(!ClaimUtils.playerHasPermission(claim, playerEntity)) {
                        hasPermission.set(false);
                    }
                });

                if(!hasPermission.get()) {
                    // TODO: translatable text
                    playerEntity.addChatMessage(new LiteralText("This block is protected by a claim."), true);

                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        }));

        // handle placing blocks at side of block not in claim
        UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
            Selection<Entry<Box, ClaimInfo>> sel = ClaimUtils.getClaimsAt(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()));

            if(!sel.isEmpty()) {
                AtomicBoolean hasPermission = new AtomicBoolean(true);

                sel.forEach(claim -> {
                    if(!ClaimUtils.playerHasPermission(claim, playerEntity)) {
                        hasPermission.set(false);
                    }
                });

                if(!hasPermission.get()) {
                    // TODO: translatable text
                    playerEntity.addChatMessage(new LiteralText("This block is protected by a claim."), true);

                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        }));
    }

    private static void registerBreakBlockCallback() {
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            Selection<Entry<Box, ClaimInfo>> sel = ClaimUtils.getClaimsAt(world, blockPos);

            if(!sel.isEmpty()) {
                AtomicBoolean hasPermission = new AtomicBoolean(true);

                sel.forEach(claim -> {
                    if(!ClaimUtils.playerHasPermission(claim, playerEntity)) {
                        hasPermission.set(false);
                    }
                });

                if(!hasPermission.get()) {
                    // TODO: translatable text
                    playerEntity.addChatMessage(new LiteralText("This block is protected by a claim."), true);

                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }
}
