package draylar.goml.api;

import draylar.goml.entity.ClaimAugmentBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Defines behavior for a claim Augment, which handles events for players inside claims.
 */
public interface Augment {

    default void onPlayerEnter(Claim claim, PlayerEntity player) {

    }

    default void onPlayerExit(Claim claim, PlayerEntity player) {

    }

    default void tick(Claim claim, World world, ClaimAugmentBlockEntity be) {

    }

    default void playerTick(PlayerEntity player) {

    }

    default boolean ticks() {
        return false;
    }
}
