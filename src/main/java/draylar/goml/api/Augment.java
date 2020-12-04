package draylar.goml.api;

import net.minecraft.entity.player.PlayerEntity;

public interface Augment {
    void onPlayerEnter(Claim claim, PlayerEntity player);
}
