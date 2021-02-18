package draylar.goml.block.augment;

import draylar.goml.api.Claim;
import draylar.goml.block.ClaimAugmentBlock;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HeavenWingsAugmentBlock extends ClaimAugmentBlock {

    public static final AbilitySource HEAVEN_WINGS = Pal.getAbilitySource("goml", "heaven_wings");

    public HeavenWingsAugmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlayerEnter(Claim claim, PlayerEntity player) {
        HEAVEN_WINGS.grantTo(player, VanillaAbilities.ALLOW_FLYING);
    }

    @Override
    public void onPlayerExit(Claim claim, PlayerEntity player) {
        HEAVEN_WINGS.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);

        if(!world.isClient) {
            HEAVEN_WINGS.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if(!world.isClient && placer instanceof PlayerEntity) {
            HEAVEN_WINGS.grantTo((PlayerEntity) placer, VanillaAbilities.ALLOW_FLYING);
        }
    }
}
