package draylar.goml;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import draylar.goml.cca.ClaimComponent;
import draylar.goml.command.ClaimCommand;
import draylar.goml.config.GOMLConfig;
import draylar.goml.registry.GOMLBlocks;
import draylar.goml.registry.PropertyTiles;
import draylar.goml.registry.GOMLItems;
import draylar.omegaconfig.OmegaConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

public class GetOffMyLawn implements ModInitializer {

	public static final ComponentKey<ClaimComponent> CLAIM = ComponentRegistryV3.INSTANCE.getOrCreate(
			id("claims"),
			ClaimComponent.class
	);

	public static final GOMLConfig CONFIG = OmegaConfig.register(GOMLConfig.class);
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("group"), () -> new ItemStack(GOMLItems.GOGGLES));
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		GOMLBlocks.init();
		GOMLItems.init();
		PropertyTiles.init();
		EventHandlers.init();
		ClaimCommand.init();
	}

	public static Identifier id(String name) {
		return new Identifier("goml", name);
	}
}
