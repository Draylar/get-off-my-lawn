package draylar.goml.entity;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.Augment;
import draylar.goml.registry.GOMLEntities;
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
        super(GOMLEntities.CLAIM_AUGMENT, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        if(parent != null) {
            tag.putLong(PARENT_POSITION_KEY, parent.getPos().asLong());
        }

        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        this.parentPosition = BlockPos.fromLong(tag.getLong(PARENT_POSITION_KEY));

        if(augment == null) {

            if(getCachedState().getBlock() instanceof Augment) {
                initialize((Augment) getCachedState().getBlock());
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

    public static void tick(ClaimAugmentBlockEntity entity) {
        assert entity.world != null;

        if(entity.world.isClient) {
            return;
        }

        // Parent is null and parent position is not null, assume we are just loading the augment from tags.
        if(entity.parent == null && entity.parentPosition != null) {
            BlockEntity blockEntity = entity.world.getBlockEntity(entity.parentPosition);

            if(blockEntity instanceof ClaimAnchorBlockEntity) {
                entity.parent = (ClaimAnchorBlockEntity) blockEntity;
            } else {
                GetOffMyLawn.LOGGER.warn(String.format("An augment at %s tried to locate a parent at %s, but it could not be found!", entity.pos.toString(), entity.parentPosition.toString()));
                entity.world.setBlockState(entity.pos, Blocks.AIR.getDefaultState());

                // todo: drop block
            }
        }

        if (entity.parent == null && entity.parentPosition == null) {
            GetOffMyLawn.LOGGER.warn(String.format("An augment at %s has an invalid parent and parent position! Removing now.", entity.pos.toString()));
            entity.world.setBlockState(entity.pos, Blocks.AIR.getDefaultState());

            // todo: drop block
        }
    }
}
