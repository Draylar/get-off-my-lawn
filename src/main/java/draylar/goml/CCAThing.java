package draylar.goml;

import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import draylar.goml.cca.WorldClaimComponent;

public class CCAThing implements WorldComponentInitializer {
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(GetOffMyLawn.CLAIM, WorldClaimComponent::new);
	}
}
