package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.block.ClaimAnchorBlock;
import draylar.goml.item.GogglesItem;
import draylar.goml.item.UpgradeKitItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class Items {

    public static final Item REINFORCED_UPGRADE_KIT = registerUpgradeKit("reinforced_upgrade_kit", Blocks.MAKESHIFT_CLAIM_ANCHOR.getFirst(), Blocks.REINFORCED_CLAIM_ANCHOR.getFirst());
    public static final Item GLISTENING_UPGRADE_KIT = registerUpgradeKit("glistening_upgrade_kit", Blocks.REINFORCED_CLAIM_ANCHOR.getFirst(), Blocks.GLISTENING_CLAIM_ANCHOR.getFirst());
    public static final Item CRYSTAL_UPGRADE_KIT = registerUpgradeKit("crystal_upgrade_kit", Blocks.GLISTENING_CLAIM_ANCHOR.getFirst(), Blocks.CRYSTAL_CLAIM_ANCHOR.getFirst());
    public static final Item EMERADIC_UPGRADE_KIT = registerUpgradeKit("emeradic_upgrade_kit", Blocks.CRYSTAL_CLAIM_ANCHOR.getFirst(), Blocks.EMERADIC_CLAIM_ANCHOR.getFirst());
    public static final Item WITHERED_UPGRADE_KIT = registerUpgradeKit("withered_upgrade_kit", Blocks.EMERADIC_CLAIM_ANCHOR.getFirst(), Blocks.WITHERED_CLAIM_ANCHOR.getFirst());

    public static final Item GOGGLES = register("goggles", new GogglesItem());

    private static UpgradeKitItem registerUpgradeKit(String name, ClaimAnchorBlock from, ClaimAnchorBlock to) {
        return Registry.register(Registry.ITEM, GetOffMyLawn.id(name), new UpgradeKitItem(from, to));
    }

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(Registry.ITEM, GetOffMyLawn.id(name), item);
    }

    public static void init() {
        // NO-OP
    }

    private Items() {
        // NO-OP
    }
}
