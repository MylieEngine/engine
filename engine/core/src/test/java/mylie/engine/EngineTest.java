package mylie.engine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EngineTest {

	@Test
	void testEngineWithoutConfiguration() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Engine.start(null));
		assertEquals("EngineConfiguration cannot be null", exception.getMessage());
	}

	@Test
	void testEngineLifecycle() {
		EngineConfiguration configuration = new EngineConfiguration();
		configuration.engineMode(EngineConfiguration.EngineMode.MANUAL);
		Engine.ShutdownReason shutdownReason = Engine.start(configuration);
		assertNull(shutdownReason);
		assertTrue(Engine.running());
		Engine.restart();
		shutdownReason = Engine.update();
		assertNull(shutdownReason);
		assertTrue(Engine.running());
		Engine.shutdown("OK");
		shutdownReason = Engine.update();
		assertEquals("OK", shutdownReason.message());
		assertFalse(Engine.running());
	}

	@Test
	void testEngineDoubleStart() {
		EngineConfiguration configuration = new EngineConfiguration();
		configuration.engineMode(EngineConfiguration.EngineMode.MANUAL);
		Engine.ShutdownReason start = Engine.start(configuration);
		assertNull(start);
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> Engine.start(configuration));
		assertEquals("Engine already started", illegalStateException.getMessage());
		Engine.shutdown("OK");
		Engine.ShutdownReason update = Engine.update();
		assertEquals("OK", update.message());
	}

	@Test
	void testNotRunningEngineShutdown() {
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> Engine.shutdown("A"));
		assertEquals("Engine not started", illegalStateException.getMessage());
	}

	@Test
	void testErrorShutdown() {
		EngineConfiguration configuration = new EngineConfiguration();
		configuration.engineMode(EngineConfiguration.EngineMode.MANUAL);
		Engine.ShutdownReason start = Engine.start(configuration);
		assertNull(start);
		Engine.shutdown(new RuntimeException("Exception"));
		Engine.ShutdownReason update = Engine.update();
		assertEquals("Exception", update.message());
		assertFalse(Engine.running());
	}
}
