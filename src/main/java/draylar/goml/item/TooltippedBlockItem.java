package draylar.goml.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class TooltippedBlockItem extends BlockItem {

    private final int lines;

    public TooltippedBlockItem(Block block, Settings settings, int lines) {
        super(block, settings);
        this.lines = lines;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        for(int i = 1; i <= lines; i++) {
            tooltip.add(new TranslatableText(String.format("%s.description.%d", getTranslationKey(), i)).formatted(Formatting.GRAY));
        }
    }
}
