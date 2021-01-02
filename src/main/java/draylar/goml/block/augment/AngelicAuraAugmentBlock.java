package draylar.goml.block.augment;

import draylar.goml.block.ClaimAugmentBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class AngelicAuraAugmentBlock extends ClaimAugmentBlock {

    public AngelicAuraAugmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void playerTick(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 5, 0, true, false));
    }

    @Override
    public boolean ticks() {
        return true;
    }
}
