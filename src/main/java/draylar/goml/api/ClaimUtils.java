package draylar.goml.api;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.GetOffMyLawn;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class ClaimUtils {

    /**
     * Returns all claims at the given position in the given world.
     *
     * <p>Under normal circumstances, only 1 claim will exist at a location, but multiple may still be returned.
     *
     * @param world  world to check for claim in
     * @param pos  position to check at
     * @return  claims at the given position in the given world
     */
    public static Selection<Entry<ClaimBox, Claim>> getClaimsAt(WorldView world, BlockPos pos) {
        Box checkBox = Box.create(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        return GetOffMyLawn.CLAIM.get(world).getClaims().entries(box -> box.contains(checkBox));
    }

    /**
     * Returns all claims that intersect with a box created by the 2 given positions.
     *
     * @param world  world to check for claim in
     * @param lower  lower corner of claim
     * @param upper  upper corner of claim
     * @return  claims that intersect with a box created by the 2 positions in the given world
     */
    public static Selection<Entry<ClaimBox, Claim>> getClaimsInBox(WorldView world, BlockPos lower, BlockPos upper) {
        Box checkBox = Box.create(lower.getX(), lower.getY(), lower.getZ(), upper.getX(), upper.getY(), upper.getZ());
        return GetOffMyLawn.CLAIM.get(world).getClaims().entries(box -> box.intersectsClosed(checkBox));
    }

    /**
     * Returns all claims that intersect with a box created by the 2 given positions.
     * If the found box is equal to the ignore box, it is not included.
     *
     * @param world  world to check for claim in
     * @param lower  lower corner of claim
     * @param upper  upper corner of claim
     * @param ignore  box to ignore
     * @return  claims that intersect with a box created by the 2 positions in the given world
     */
    public static Selection<Entry<ClaimBox, Claim>> getClaimsInBox(WorldView world, BlockPos lower, BlockPos upper, Box ignore) {
        Box checkBox = Box.create(lower.getX(), lower.getY(), lower.getZ(), upper.getX(), upper.getY(), upper.getZ());
        return GetOffMyLawn.CLAIM.get(world).getClaims().entries(box -> box.intersectsClosed(checkBox) && !box.equals(ignore));
    }

    /**
     * Returns whether or not the information about a claim matches with a {@link PlayerEntity} and {@link BlockPos}.
     *
     * @param claim  claim to check
     * @param checkPlayer  player to check against
     * @param checkPos  position to check against
     * @return  whether or not the claim information matches up with the player and position
     */
    public static boolean claimMatchesWith(Entry<ClaimBox, Claim> claim, PlayerEntity checkPlayer, BlockPos checkPos) {
        return playerHasPermission(claim, checkPlayer) && claim.getValue().getOrigin().equals(checkPos);
    }

    /**
     * Returns whether the given {@link PlayerEntity} has permission to do anything in the given claim.
     *
     * @param claim  claim to check for permisisons
     * @param checkPlayer  player to check
     * @return  whether the checkPlayer can build/interact in the claim
     */
    public static boolean playerHasPermission(Entry<ClaimBox, Claim> claim, PlayerEntity checkPlayer) {
        return claim.getValue().getOwners().contains(checkPlayer.getUuid()) || checkPlayer.hasPermissionLevel(3);
    }
}
