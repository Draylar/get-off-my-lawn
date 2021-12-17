package draylar.goml.registry;

import draylar.goml.GetOffMyLawn;
import draylar.goml.entity.ClaimAnchorBlockEntity;
import draylar.goml.entity.ClaimAugmentBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class PropertyTiles {

    public static final BlockEntityType<ClaimAnchorBlockEntity> CLAIM_ANCHOR = register(
            "claim_anchor",
            FabricBlockEntityTypeBuilder.create(
                    ClaimAnchorBlockEntity::new,
                    GOMLBlocks.ANCHORS.toArray(new Block[0])).build(null));

    public static final BlockEntityType<ClaimAugmentBlockEntity> CLAIM_AUGMENT = register(
            "claim_augment",
            FabricBlockEntityTypeBuilder.create(
                    ClaimAugmentBlockEntity::new,
                    GOMLBlocks.AUGMENTS.toArray(new Block[0])).build(null));

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> entity) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, GetOffMyLawn.id(name), entity);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> entity) {
        return Registry.register(Registry.ENTITY_TYPE, GetOffMyLawn.id(name), entity);
    }

    public static void init() {
        // NO-OP
    }

    private PropertyTiles() {
        // NO-OP
    }
}
