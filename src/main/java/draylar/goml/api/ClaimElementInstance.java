package draylar.goml.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class ClaimElementInstance {

    private final BlockState claimElement;
    private final BlockPos pos;

    public ClaimElementInstance(BlockState claimElement, BlockPos pos) {
        this.claimElement = claimElement;
        this.pos = pos;
    }

    public BlockState getClaimElement() {
        return claimElement;
    }

    public BlockPos getPos() {
        return pos;
    }
}
