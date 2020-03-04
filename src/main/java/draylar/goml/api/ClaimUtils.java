package draylar.goml.api;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.GetOffMyLawn;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClaimUtils {

    public static Selection<Entry<Box, ClaimInfo>> getClaimsAt(WorldView world, BlockPos pos) {
        return GetOffMyLawn.CLAIM.get(world).getClaims().entries(box -> box.contains(Box.create(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)));
    }

    public static Selection<Entry<Box, ClaimInfo>> getClaimsInBox(WorldView world, BlockPos lower, BlockPos upper) {
        return GetOffMyLawn.CLAIM.get(world).getClaims().entries(box -> box.intersectsClosed(Box.create(lower.getX(), lower.getY(), lower.getZ(), upper.getX(), upper.getY(), upper.getZ())));
    }

    public static Selection<Entry<Box, ClaimInfo>> getClaimsInBox(WorldView world, BlockPos lower, BlockPos upper, Box ignore) {
        return GetOffMyLawn.CLAIM.get(world).getClaims().entries(box -> box.intersectsClosed(Box.create(lower.getX(), lower.getY(), lower.getZ(), upper.getX(), upper.getY(), upper.getZ())) && !box.equals(ignore));
    }

    public static boolean doesBoxHaveClaimsNotOwnedBy(WorldView world, BlockPos lower, BlockPos upper, PlayerEntity playerEntity) {
        Selection<Entry<Box, ClaimInfo>> claimsInBox = getClaimsInBox(world, lower, upper);
        AtomicBoolean valid = new AtomicBoolean(true);

        claimsInBox.forEach(boxClaimInfoEntry -> {
            if(!boxClaimInfoEntry.getValue().getOwner().equals(playerEntity.getUuid())) {
                valid.set(false);
            }
        });

        return valid.get();
    }

    public static boolean claimMatchesWith(Entry<Box, ClaimInfo> claim, PlayerEntity checkPlayer, BlockPos checkPos) {
        return playerHasPermission(claim, checkPlayer) && claim.getValue().getOrigin().equals(checkPos);
    }

    public static boolean playerHasPermission(Entry<Box, ClaimInfo> claim, PlayerEntity checkPlayer) {
        return claim.getValue().getOwner().equals(checkPlayer.getUuid());
    }
}
