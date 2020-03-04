package draylar.goml.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class ClaimInfo {

    private final UUID owner;
    private final BlockPos origin;

    public ClaimInfo(UUID owner, BlockPos origin) {
        this.owner = owner;
        this.origin = origin;
    }

    public UUID getOwner() {
        return owner;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public CompoundTag asTag() {
        CompoundTag tag = new CompoundTag();

        tag.putUuid("Owner", owner);
        tag.putLong("Pos", origin.asLong());

        return tag;
    }

    public static ClaimInfo fromTag(CompoundTag tag) {
        return new ClaimInfo(tag.getUuid("Owner"), BlockPos.fromLong(tag.getLong("Pos")));
    }
}
