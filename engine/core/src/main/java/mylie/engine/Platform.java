package mylie.engine;

import mylie.engine.util.Classes;

public abstract class Platform {
	private Platform.Callback callbacks;
	protected Platform() {

	}

	protected abstract void initialize(EngineConfiguration engineConfiguration);

	public static EngineConfiguration initialize(Class<? extends Platform> platformClass, Platform.Callback callbacks) {
		Platform platform = Classes.newInstance(platformClass);
		EngineConfiguration engineConfiguration = new EngineConfiguration();
		engineConfiguration.platformCallbacks(callbacks);
		platform.initialize(engineConfiguration);
		return engineConfiguration;
	}

	public static EngineConfiguration initialize(Class<? extends Platform> platformClass) {
		return initialize(platformClass, null);
	}

	public interface Callback {
		void onInitialize();
		void onUpdate();
		void onShutdown();
	}
}
