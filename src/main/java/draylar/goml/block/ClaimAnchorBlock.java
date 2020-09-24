package draylar.goml.block;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.Claim;
import draylar.goml.api.ClaimUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Collections;

public class ClaimAnchorBlock extends Block {

    private final int radius;

    public ClaimAnchorBlock(Block.Settings settings, int radius) {
        super(settings);
        this.radius = radius;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if(world == null) {
            return;
        }

        if(!world.isClient()) {
            Claim claimInfo = new Claim(Collections.singleton(placer.getUuid()), pos);
            GetOffMyLawn.CLAIM.get(world).add(new ClaimBox(pos, radius), claimInfo);
            GetOffMyLawn.CLAIM.get(world).sync();
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(world == null) {
            return;
        }

        if(!world.isClient()) {
            ClaimUtils.getClaimsAt(world, pos).forEach(claimedArea -> {
                if (ClaimUtils.claimMatchesWith(claimedArea, player, pos)) {
                    GetOffMyLawn.CLAIM.get(world).remove(claimedArea.getKey());
                }
            });
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldView, BlockPos pos) {
        if(worldView == null) {
            return true;
        }

        if(worldView instanceof World) {
            World world = (World) worldView;
            if (GetOffMyLawn.CONFIG.dimensionBlacklist.contains(world.getRegistryKey().getValue().toString())) {
                return false;
            }
        }

        return ClaimUtils.getClaimsInBox(worldView, pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)).isEmpty();
    }

    public int getRadius() {
        return radius;
    }
}
