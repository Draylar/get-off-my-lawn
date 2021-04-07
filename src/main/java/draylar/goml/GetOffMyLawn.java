package draylar.goml;

import draylar.goml.api.compat.DeathChestsCompat;
import draylar.goml.cca.ClaimComponent;
import draylar.goml.cca.WorldClaimComponent;
import draylar.goml.command.ClaimCommand;
import draylar.goml.config.GOMLConfig;
import draylar.goml.registry.GOMLBlocks;
import draylar.goml.registry.GOMLEntities;
import draylar.goml.registry.GOMLItems;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.WorldComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetOffMyLawn implements ModInitializer {

	public static final ComponentType<ClaimComponent> CLAIM = ComponentRegistry.INSTANCE.registerIfAbsent(
			id("claims"),
			ClaimComponent.class
	).attach(WorldComponentCallback.EVENT, WorldClaimComponent::new);

	public static final GOMLConfig CONFIG = AutoConfig.register(GOMLConfig.class, JanksonConfigSerializer::new).getConfig();
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("group"), () -> new ItemStack(GOMLItems.GOGGLES));
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		GOMLBlocks.init();
		GOMLItems.init();
		GOMLEntities.init();
		EventHandlers.init();
		ClaimCommand.init();

		// Register compat hooks
		if(FabricLoader.getInstance().isModLoaded("vanilladeathchest")) {
			DeathChestsCompat.register();
		}
	}

	public static Identifier id(String name) {
		return new Identifier("goml", name);
	}
}
