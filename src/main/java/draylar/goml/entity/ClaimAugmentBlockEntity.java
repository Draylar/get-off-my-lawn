package draylar.goml.entity;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Augment;
import draylar.goml.registry.PropertyTiles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ClaimAugmentBlockEntity extends BlockEntity {

    private static final String PARENT_POSITION_KEY = "ParentPosition";
    private ClaimAnchorBlockEntity parent;
    private BlockPos parentPosition;
    private Augment augment;

    public ClaimAugmentBlockEntity(BlockPos pos, BlockState state) {
        super(PropertyTiles.CLAIM_AUGMENT, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        if(parent != null) {
            tag.putLong(PARENT_POSITION_KEY, parent.getPos().asLong());
        }

        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        this.parentPosition = BlockPos.fromLong(tag.getLong(PARENT_POSITION_KEY));

        if(augment == null) {

            if(this.getCachedState().getBlock() instanceof Augment) {
                initialize((Augment) this.getCachedState().getBlock());
            }
        }

        super.readNbt(tag);
    }

    public void remove() {
        parent.removeChild(pos);
    }

    public void setParent(ClaimAnchorBlockEntity parent) {
        this.parent = parent;
        parent.addChild(pos, this);
    }

    public ClaimAnchorBlockEntity getParent() {
        return parent;
    }

    public void initialize(Augment augment) {
        this.augment = augment;
    }

    public Augment getAugment() {
        return augment;
    }

    public void tick() {
        assert world != null;

        if(world.isClient) {
            return;
        }

        // Parent is null and parent position is not null, assume we are just loading the augment from tags.
        if(parent == null && parentPosition != null) {
            BlockEntity blockEntity = world.getBlockEntity(parentPosition);

            if(blockEntity instanceof ClaimAnchorBlockEntity) {
                this.parent = (ClaimAnchorBlockEntity) blockEntity;
            } else {
                GetOffMyLawn.LOGGER.warn(String.format("An augment at %s tried to locate a parent at %s, but it could not be found!", pos.toString(), parentPosition.toString()));
                world.setBlockState(pos, Blocks.AIR.getDefaultState());

                // todo: drop block
            }
        }

        if (parent == null && parentPosition == null) {
            GetOffMyLawn.LOGGER.warn(String.format("An augment at %s has an invalid parent and parent position! Removing now.", pos.toString()));
            world.setBlockState(pos, Blocks.AIR.getDefaultState());

            // todo: drop block
        }
    }
}
