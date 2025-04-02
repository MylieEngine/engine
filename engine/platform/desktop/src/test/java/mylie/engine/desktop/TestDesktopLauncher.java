package mylie.engine.desktop;

import mylie.engine.Engine;
import mylie.engine.EngineConfiguration;
import mylie.engine.Platform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestDesktopLauncher {
	@Test
	void testLaunchEngine() {
		EngineConfiguration engineConfiguration = Platform.initialize(Desktop.class);
		engineConfiguration.engineMode(EngineConfiguration.EngineMode.MANUAL);
		Engine.ShutdownReason start = Engine.start(engineConfiguration);
		Assertions.assertNull(start);
		Engine.shutdown("Ok");
		Engine.ShutdownReason result = Engine.update();
		Assertions.assertNotNull(result);
	}
}
