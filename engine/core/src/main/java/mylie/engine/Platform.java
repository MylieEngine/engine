package mylie.engine;

import mylie.engine.components.ComponentManager;
import mylie.engine.util.Arguments;
import mylie.engine.util.Classes;

public abstract class Platform {
	protected Platform() {

	}

	protected abstract void initialize(EngineConfiguration engineConfiguration);

	public static EngineConfiguration initialize(Class<? extends Platform> platformClass, Platform.Callback callbacks,
			String... args) {
		Platform platform = Classes.newInstance(platformClass);
		EngineConfiguration engineConfiguration = new EngineConfiguration();
		Arguments arguments = new Arguments();
		arguments.fromArray(args);
		engineConfiguration.property(EngineConfiguration.ARGUMENTS, arguments);
		engineConfiguration.platformCallbacks(callbacks);
		platform.initialize(engineConfiguration);
		return engineConfiguration;
	}

	public static EngineConfiguration initialize(Class<? extends Platform> platformClass) {
		return initialize(platformClass, null);
	}

	public interface Callback {
		void onInitialize(ComponentManager componentManager);
		void onUpdate();
		void onShutdown();
	}
}
