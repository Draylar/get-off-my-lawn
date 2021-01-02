package draylar.goml.block.augment;

import draylar.goml.block.ClaimAugmentBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class LakeSpiritGraceAugmentBlock extends ClaimAugmentBlock {

    public LakeSpiritGraceAugmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void playerTick(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 5, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 5, 0, true, false));
    }

    @Override
    public boolean ticks() {
        return true;
    }
}
