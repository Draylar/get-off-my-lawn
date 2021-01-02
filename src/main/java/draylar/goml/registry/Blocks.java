package draylar.goml.registry;

import com.mojang.datafixers.util.Pair;
import draylar.goml.GetOffMyLawn;
import draylar.goml.block.ClaimAnchorBlock;
import draylar.goml.block.ClaimAugmentBlock;
import draylar.goml.block.augment.*;
import draylar.goml.item.TooltippedBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class Blocks {

    public static final List<ClaimAnchorBlock> ANCHORS = new ArrayList<>();
    public static final List<ClaimAugmentBlock> AUGMENTS = new ArrayList<>();

    public static final Pair<ClaimAnchorBlock, Item> MAKESHIFT_CLAIM_ANCHOR = register("makeshift_claim_anchor", GetOffMyLawn.CONFIG.makeshiftRadius, 10, FabricToolTags.AXES);
    public static final Pair<ClaimAnchorBlock, Item> REINFORCED_CLAIM_ANCHOR = register("reinforced_claim_anchor", GetOffMyLawn.CONFIG.reinforcedRadius, 10, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> GLISTENING_CLAIM_ANCHOR = register("glistening_claim_anchor", GetOffMyLawn.CONFIG.glisteningRadius, 15, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> CRYSTAL_CLAIM_ANCHOR = register("crystal_claim_anchor", GetOffMyLawn.CONFIG.crystalRadius, 20, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> EMERADIC_CLAIM_ANCHOR = register("emeradic_claim_anchor", GetOffMyLawn.CONFIG.emeradicRadius, 20, FabricToolTags.PICKAXES);
    public static final Pair<ClaimAnchorBlock, Item> WITHERED_CLAIM_ANCHOR = register("withered_claim_anchor", GetOffMyLawn.CONFIG.witheredRadius, 25, FabricToolTags.PICKAXES);

    public static final Pair<ClaimAugmentBlock, Item> ENDER_BINDING = register("ender_binding", new EnderBindingAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);
    public static final Pair<ClaimAugmentBlock, Item> LAKE_SPIRIT_GRACE = register("lake_spirit_grace", new LakeSpiritGraceAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);
    public static final Pair<ClaimAugmentBlock, Item> ANGELIC_AURA = register("angelic_aura", new AngelicAuraAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);
    public static final Pair<ClaimAugmentBlock, Item> HEAVEN_WINGS = register("heaven_wings", new HeavenWingsAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);
    public static final Pair<ClaimAugmentBlock, Item> DEFENDERS_RIGHT = register("defenders_right", new DefendersRightAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);
    public static final Pair<ClaimAugmentBlock, Item> VILLAGE_CORE = register("village_core", new ClaimAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);
    public static final Pair<ClaimAugmentBlock, Item> WITHERING_SEAL = register("withering_seal", new WitheringSealAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);
    public static final Pair<ClaimAugmentBlock, Item> CHAOS_ZONE = register("chaos_zone", new ChaosZoneAugmentBlock(FabricBlockSettings.of(Material.STONE).hardness(10).breakByTool(FabricToolTags.PICKAXES)), 2);

    private static Pair<ClaimAnchorBlock, Item> register(String name, int radius, float hardness, Tag<Item> toolTag) {
        ClaimAnchorBlock claimAnchorBlock;

        if(toolTag == null) {
            claimAnchorBlock = Registry.register(
                    Registry.BLOCK,
                    GetOffMyLawn.id(name),
                    new ClaimAnchorBlock(FabricBlockSettings.of(Material.STONE).strength(hardness, 3600000.0F), radius)
            );
        } else {
            claimAnchorBlock = Registry.register(
                    Registry.BLOCK,
                    GetOffMyLawn.id(name),
                    new ClaimAnchorBlock(FabricBlockSettings.of(Material.STONE).breakByTool(toolTag).strength(hardness, 3600000.0F), radius)
            );
        }

        Item registeredItem = Registry.register(Registry.ITEM, GetOffMyLawn.id(name), new BlockItem(claimAnchorBlock, new Item.Settings().group(GetOffMyLawn.GROUP)));
        ANCHORS.add(claimAnchorBlock);
        return Pair.of(claimAnchorBlock, registeredItem);
    }

    private static Pair<ClaimAugmentBlock, Item> register(String name, ClaimAugmentBlock augment) {
        ClaimAugmentBlock registered = Registry.register(
                Registry.BLOCK,
                GetOffMyLawn.id(name),
                augment
        );

        Item registeredItem = Registry.register(Registry.ITEM, GetOffMyLawn.id(name), new BlockItem(augment, new Item.Settings().group(GetOffMyLawn.GROUP)));
        AUGMENTS.add(registered);
        return Pair.of(augment, registeredItem);
    }

    private static Pair<ClaimAugmentBlock, Item> register(String name, ClaimAugmentBlock augment, int tooltipLines) {
        ClaimAugmentBlock registered = Registry.register(
                Registry.BLOCK,
                GetOffMyLawn.id(name),
                augment
        );

        Item registeredItem = Registry.register(Registry.ITEM, GetOffMyLawn.id(name), new TooltippedBlockItem(augment, new Item.Settings().group(GetOffMyLawn.GROUP), tooltipLines));
        AUGMENTS.add(registered);
        return Pair.of(augment, registeredItem);
    }

    public static void init() {
        // NO-OP
    }

    private Blocks() {
        // NO-OP
    }
}
