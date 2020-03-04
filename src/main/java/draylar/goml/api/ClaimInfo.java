package draylar.goml.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Contains information about a single claim in the world.
 * While this class stores information about the origin of a claim, the actual bounding box is stored by the world.
 */
public class ClaimInfo {

    private final UUID owner;
    private final BlockPos origin;

    /**
     * Returns a {@link ClaimInfo} instance with the given owner and origin position.
     *
     * @param owner  {@link UUID} of owner of the claim
     * @param origin  origin {@link BlockPos} of claim
     */
    public ClaimInfo(UUID owner, BlockPos origin) {
        this.owner = owner;
        this.origin = origin;
    }

    /**
     * Returns the {@link UUID} of the owner of the claim.
     *
     * <p>The owner is defined as the player who placed the claim block.
     *
     * @return  claim owner's UUID
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Returns the origin position of the claim as a {@link BlockPos}.
     *
     * <p>The origin position of a claim is the position the center Claim Anchor was placed at.
     *
     * @return  origin position of this claim
     */
    public BlockPos getOrigin() {
        return origin;
    }

    /**
     * Serializes this {@link ClaimInfo} to a {@link CompoundTag} and returns it.
     *
     * <p>The following tags are stored at the top level of the tag:
     * <ul>
     * <li>"Owner" - {@link UUID} of claim owner
     * <li>"Pos" - origin {@link BlockPos} of claim
     *
     * @return  this object serialized to a {@link CompoundTag}
     */
    public CompoundTag asTag() {
        CompoundTag tag = new CompoundTag();

        tag.putUuid("Owner", owner);
        tag.putLong("Pos", origin.asLong());

        return tag;
    }

    /**
     * Uses the top level information in the given {@link CompoundTag} to construct a {@link ClaimInfo}.
     *
     * <p>This method expects to find the following tags at the top level of the tag:
     * <ul>
     * <li>"Owner" - {@link UUID} of claim owner
     * <li>"Pos" - origin {@link BlockPos} of claim
     *
     * @param tag  tag to deserialize information from
     * @return  {@link ClaimInfo} instance with information from tag
     */
    public static ClaimInfo fromTag(CompoundTag tag) {
        return new ClaimInfo(tag.getUuid("Owner"), BlockPos.fromLong(tag.getLong("Pos")));
    }
}
