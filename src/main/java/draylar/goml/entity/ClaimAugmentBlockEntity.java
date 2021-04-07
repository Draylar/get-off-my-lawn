package draylar.goml.entity;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Augment;
import draylar.goml.registry.GOMLEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

public class ClaimAugmentBlockEntity extends BlockEntity implements Tickable {

    private static final String PARENT_POSITION_KEY = "ParentPosition";
    private ClaimAnchorBlockEntity parent;
    private BlockPos parentPosition;
    private Augment augment;

    public ClaimAugmentBlockEntity() {
        super(GOMLEntities.CLAIM_AUGMENT);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if(parent != null) {
            tag.putLong(PARENT_POSITION_KEY, parent.getPos().asLong());
        }

        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        this.parentPosition = BlockPos.fromLong(tag.getLong(PARENT_POSITION_KEY));

        if(augment == null) {

            if(state.getBlock() instanceof Augment) {
                initialize((Augment) state.getBlock());
            }
        }

        super.fromTag(state, tag);
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

    @Override
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
