package draylar.goml.item;

import com.jamieswhiteshirt.rtree3i.Box;
import com.jamieswhiteshirt.rtree3i.Entry;
import com.jamieswhiteshirt.rtree3i.Selection;
import draylar.goml.GetOffMyLawn;
import draylar.goml.api.ClaimBox;
import draylar.goml.api.ClaimInfo;
import draylar.goml.api.ClaimUtils;
import draylar.goml.block.ClaimAnchorBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UpgradeKitItem extends Item {

    private final ClaimAnchorBlock from;
    private final ClaimAnchorBlock to;

    public UpgradeKitItem(ClaimAnchorBlock from, ClaimAnchorBlock to) {
        super(new Item.Settings().group(GetOffMyLawn.GROUP));

        this.from = from;
        this.to = to;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if(context == null || context.getPlayer() == null || context.getWorld().isClient) {
            return ActionResult.PASS;
        }

        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockState block = world.getBlockState(pos);

        if(block.getBlock().equals(from)) {
            // get claims at block position
            Selection<Entry<ClaimBox, ClaimInfo>> claimsFound =  GetOffMyLawn.CLAIM.get(world).getClaims().entries(box ->
                    box.contains(Box.create(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1))
            );

            if(!claimsFound.isEmpty()) {
                boolean noPermission = claimsFound.anyMatch((Entry<ClaimBox, ClaimInfo> boxInfo) -> !boxInfo.getValue().getOwner().equals(context.getPlayer().getUuid()));

                // get claim at location
                AtomicReference<Entry<ClaimBox, ClaimInfo>> currentClaim = new AtomicReference<>();
                claimsFound.forEach(claim -> {
                    if (claim.getValue().getOrigin().equals(pos) && claim.getValue().getOwner().equals(context.getPlayer().getUuid())) {
                        currentClaim.set(claim);
                    }
                });


                // if we have permission
                if(!noPermission) {

                    // if we don't overlap with another claim
                    if(ClaimUtils.getClaimsInBox(world, pos.add(-to.getRadius(), -to.getRadius(), -to.getRadius()), pos.add(to.getRadius(), to.getRadius(), to.getRadius()), currentClaim.get().getKey().toBox()).isEmpty()) {

                        // remove claim
                        GetOffMyLawn.CLAIM.get(world).remove(currentClaim.get().getKey());

                        // set block
                        world.setBlockState(pos, to.getDefaultState());

                        // new claim
                        ClaimInfo claimInfo = new ClaimInfo(context.getPlayer().getUuid(), pos);
                        GetOffMyLawn.CLAIM.get(world).add(new ClaimBox(pos, to.getRadius()), claimInfo);
                    }
                }
            }
        }

        return ActionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(tooltip == null) {
            return;
        }

        tooltip.add(new LiteralText(new TranslatableText(from.getTranslationKey()).asString() + " -> " + new TranslatableText(to.getTranslationKey()).asString()).formatted(Formatting.GRAY));
    }
}
