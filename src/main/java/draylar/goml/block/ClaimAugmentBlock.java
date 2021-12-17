package draylar.goml.block;

import draylar.goml.api.Augment;
import draylar.goml.entity.ClaimAnchorBlockEntity;
import draylar.goml.entity.ClaimAugmentBlockEntity;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ClaimAugmentBlock extends Block implements Augment, BlockEntityProvider {

    public ClaimAugmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            BlockState offsetState = world.getBlockState(pos.offset(direction));

            // todo: interface
            if(offsetState.getBlock() instanceof ClaimAugmentBlock || offsetState.getBlock() instanceof ClaimAnchorBlock) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if(world == null) {
            return;
        }

        if(!world.isClient()) {
            ClaimAugmentBlockEntity thisBE = (ClaimAugmentBlockEntity) world.getBlockEntity(pos);

            if(thisBE == null) {
                return;
            }

            thisBE.initialize(this);

            for(Direction direction : Direction.values()) {
                BlockPos offsetPos = pos.offset(direction);
                BlockState offsetState = world.getBlockState(offsetPos);
                Block offsetBlock = offsetState.getBlock();

                // Neighbor is a core element, set parent directly
                if(offsetBlock instanceof ClaimAnchorBlock) {
                    ClaimAnchorBlockEntity coreBE = (ClaimAnchorBlockEntity) world.getBlockEntity(offsetPos);
                    thisBE.setParent(coreBE);
                    return;
                }

                // Neighbor is another augment, grab parent from it
                if(offsetBlock instanceof ClaimAugmentBlock) {
                    ClaimAugmentBlockEntity otherAugmentBE = (ClaimAugmentBlockEntity) world.getBlockEntity(offsetPos);

                    if(otherAugmentBE != null) {
                        thisBE.setParent(otherAugmentBE.getParent());
                    }

                    return;
                }
            }
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(!world.isClient) {
            ClaimAugmentBlockEntity be = (ClaimAugmentBlockEntity) world.getBlockEntity(pos);
            be.remove();
        }

        super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClaimAugmentBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(world.isClient) {
            return null;
        }
        return (w, pos, state1, blockEntity) -> ((ClaimAugmentBlockEntity)blockEntity).tick();
    }
}
