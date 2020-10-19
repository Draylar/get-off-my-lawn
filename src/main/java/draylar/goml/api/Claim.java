package draylar.goml.api;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a claim on land with an origin {@link BlockPos}, owners, and other allowed players.
 * <p>While this class stores information about the origin of a claim, the actual bounding box is stored by the world.
 */
public class Claim {

    @Deprecated public static final String OWNER_KEY = "Owner"; // Legacy key for single-UUID owner

    public static final String POSITION_KEY = "Pos";
    public static final String OWNERS_KEY = "Owners";
    public static final String TRUSTED_KEY = "Trusted";

    private final Set<UUID> owners;
    private final Set<UUID> trusted = new HashSet<>();
    private final BlockPos origin;

    /**
     * Returns a {@link Claim} instance with the given owner and origin position.
     *
     * @param owners  list of {@link UUID}s of owners of the claim
     * @param origin  origin {@link BlockPos} of claim
     */
    public Claim(Set<UUID> owners, BlockPos origin) {
        this.owners = owners;
        this.origin = origin;
    }

    public Claim(Set<UUID> owners, Set<UUID> trusted, BlockPos origin) {
        this.owners = owners;
        this.trusted.addAll(trusted);
        this.origin = origin;
    }

    public boolean isOwner(PlayerEntity player) {
        return isOwner(player.getUuid());
    }

    protected boolean isOwner(UUID uuid) {
        return owners.contains(uuid);
    }

    public void addOwner(PlayerEntity player) {
        owners.add(player.getUuid());
    }

    public boolean hasPermission(PlayerEntity player) {
        return hasPermission(player.getUuid());
    }

    protected boolean hasPermission(UUID uuid) {
        return owners.contains(uuid) || trusted.contains(uuid);
    }

    public void trust(PlayerEntity player) {
        trusted.add(player.getUuid());
    }

    public void untrust(PlayerEntity player) {
        trusted.remove(player.getUuid());
    }

    /**
     * Returns the {@link UUID}s of the owners of the claim.
     *
     * <p>The owner is defined as the player who placed the claim block, or someone added through the goml command.
     *
     * @return  claim owner's UUIDs
     */
    public Set<UUID> getOwners() {
        return owners;
    }

    public Set<UUID> getTrusted() {
        return trusted;
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
     * Serializes this {@link Claim} to a {@link CompoundTag} and returns it.
     *
     * <p>The following tags are stored at the top level of the tag:
     * <ul>
     * <li>"Owners" - list of {@link UUID}s of claim owners
     * <li>"Pos" - origin {@link BlockPos} of claim
     *
     * @return  this object serialized to a {@link CompoundTag}
     */
    public CompoundTag asTag() {
        CompoundTag tag = new CompoundTag();

        // collect owner UUIDs into list
        ListTag ownersTag = new ListTag();
        owners.forEach(ownerUUID -> {
            ownersTag.add(NbtHelper.fromUuid(ownerUUID));
        });

        // collect trusted UUIDs into list
        ListTag trustedTag = new ListTag();
        trusted.forEach(trustedUUID -> {
            trustedTag.add(NbtHelper.fromUuid(trustedUUID));
        });

        tag.put(OWNERS_KEY, ownersTag);
        tag.put(TRUSTED_KEY, trustedTag);
        tag.putLong(POSITION_KEY, origin.asLong());

        return tag;
    }

    /**
     * Uses the top level information in the given {@link CompoundTag} to construct a {@link Claim}.
     *
     * <p>This method expects to find the following tags at the top level of the tag:
     * <ul>
     * <li>"Owners" - {@link UUID}s of claim owners
     * <li>"Pos" - origin {@link BlockPos} of claim
     *
     * @param tag  tag to deserialize information from
     * @return  {@link Claim} instance with information from tag
     */
    public static Claim fromTag(CompoundTag tag) {
        // Handle legacy data stored in "Owner" key, which is a single UUID
        if(tag.containsUuid(OWNER_KEY)) {
            return new Claim(Collections.singleton(tag.getUuid(OWNER_KEY)), BlockPos.fromLong(tag.getLong(POSITION_KEY)));
        }

        // Collect UUID of owners
        Set<UUID> ownerUUIDs = new HashSet<>();
        ListTag ownersTag = tag.getList(OWNERS_KEY, NbtType.INT_ARRAY);
        ownersTag.forEach(ownerUUID -> ownerUUIDs.add(NbtHelper.toUuid(ownerUUID)));

        // Collect UUID of trusted
        Set<UUID> trustedUUIDs = new HashSet<>();
        ListTag trustedTag = tag.getList(TRUSTED_KEY, NbtType.INT_ARRAY);
        trustedTag.forEach(trustedUUID -> trustedUUIDs.add(NbtHelper.toUuid(trustedUUID)));

        return new Claim(ownerUUIDs, trustedUUIDs, BlockPos.fromLong(tag.getLong(POSITION_KEY)));
    }
}
