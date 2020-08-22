package draylar.goml.block;

import com.jamieswhiteshirt.rtree3i.Box;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimInfo;
import draylar.goml.api.ClaimUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

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
            ClaimInfo claimInfo = new ClaimInfo(placer.getUuid(), pos);
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
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if(world == null) {
            return true;
        }

        return ClaimUtils.getClaimsInBox(world, pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)).isEmpty();
    }

    public int getRadius() {
        return radius;
    }
}
