package draylar.goml.registry;

import com.mojang.datafixers.util.Pair;
import draylar.goml.GetOffMyLawn;
import draylar.goml.block.ClaimAnchorBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

public class Blocks {

    public static final Pair<ClaimAnchorBlock, Item> MAKESHIFT_CLAIM_ANCHOR = register("makeshift_claim_anchor", 10, 10, FabricToolTags.AXES);
    public static final Pair<ClaimAnchorBlock, Item> REINFORCED_CLAIM_ANCHOR = register("reinforced_claim_anchor", 25, 10, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> GLISTENING_CLAIM_ANCHOR = register("glistening_claim_anchor", 50, 15, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> CRYSTAL_CLAIM_ANCHOR = register("crystal_claim_anchor", 75, 20, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> EMERADIC_CLAIM_ANCHOR = register("emeradic_claim_anchor", 125, 20, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> WITHERED_CLAIM_ANCHOR = register("withered_claim_anchor", 200, 25, FabricToolTags.PICKAXES);


    private static Pair<ClaimAnchorBlock, Item> register(String name, int radius, float hardness, Tag<Item> toolTag) {
        ClaimAnchorBlock claimAnchorBlock;

        if(toolTag == null) {
            claimAnchorBlock = Registry.register(
                    Registry.BLOCK,
                    GetOffMyLawn.id(name),
                    new ClaimAnchorBlock(FabricBlockSettings.of(Material.STONE).strength(hardness, 3600000.0F).build(), radius)
            );
        } else {
            claimAnchorBlock = Registry.register(
                    Registry.BLOCK,
                    GetOffMyLawn.id(name),
                    new ClaimAnchorBlock(FabricBlockSettings.of(Material.STONE).breakByTool(toolTag).strength(hardness, 3600000.0F).build(), radius)
            );
        }

        Item registeredItem = Registry.register(Registry.ITEM, GetOffMyLawn.id(name), new BlockItem(claimAnchorBlock, new Item.Settings().group(GetOffMyLawn.GROUP)));
        return Pair.of(claimAnchorBlock, registeredItem);
    }

    public static void init() {
        // NO-OP
    }

    private Blocks() {
        // NO-OP
    }
}
