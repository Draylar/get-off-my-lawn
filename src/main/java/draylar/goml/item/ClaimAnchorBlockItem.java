package draylar.goml.item;

import java.util.List;

import draylar.goml.block.ClaimAnchorBlock;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class ClaimAnchorBlockItem extends BlockItem {
	public ClaimAnchorBlockItem(ClaimAnchorBlock block, Settings settings) {
		super(block, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText("tooltip.goml.claim_range", ((ClaimAnchorBlock)this.getBlock()).getRadius()).formatted(Formatting.DARK_GRAY));
		super.appendTooltip(stack, world, tooltip, context);
	}
}
