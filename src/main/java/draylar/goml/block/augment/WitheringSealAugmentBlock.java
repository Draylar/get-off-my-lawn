package draylar.goml.block.augment;

import draylar.goml.block.ClaimAugmentBlock;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class WitheringSealAugmentBlock extends ClaimAugmentBlock {

    public WitheringSealAugmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean ticks() {
        return true;
    }

    @Override
    public void playerTick(PlayerEntity player) {
        player.removeStatusEffect(StatusEffects.WITHER);
    }
}
