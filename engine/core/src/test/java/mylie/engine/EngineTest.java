package mylie.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
		assertNotNull(shutdownReason);
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
		assertNotNull(update);
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
		assertNotNull(update);
		assertEquals("Exception", update.message());
		assertFalse(Engine.running());
	}

	static Stream<SchedulerSettings> schedulerProvider() {
		return Stream.of(SchedulerSettings.singleThreaded(), SchedulerSettings.forkJoin());
	}

	@ParameterizedTest
	@MethodSource("schedulerProvider")
	void testEngineManagedMode(SchedulerSettings schedulerSettings) {
		EngineConfiguration configuration = new EngineConfiguration();
		configuration.schedulerSettings(schedulerSettings);
		configuration.engineMode(EngineConfiguration.EngineMode.MANAGED);
		AtomicInteger initCounter = new AtomicInteger(0);
		AtomicInteger shutdownCounter = new AtomicInteger(0);
		AtomicInteger updateCounter = new AtomicInteger(0);
		configuration.platformCallbacks(new Platform.Callback() {
			@Override
			public void onInitialize() {
				initCounter.incrementAndGet();
			}

			@Override
			public void onUpdate() {
				updateCounter.incrementAndGet();
				if (updateCounter.get() == 10) {
					Engine.shutdown("OK");
				}
			}

			@Override
			public void onShutdown() {
				shutdownCounter.incrementAndGet();
			}
		});
		Engine.start(configuration);
		assertEquals(1, initCounter.get());
		assertEquals(1, shutdownCounter.get());
		assertEquals(10, updateCounter.get());
	}

	@ParameterizedTest
	@MethodSource("schedulerProvider")
	void testEngineManualMode(SchedulerSettings schedulerSettings) {
		EngineConfiguration configuration = new EngineConfiguration();
		configuration.schedulerSettings(schedulerSettings);
		configuration.engineMode(EngineConfiguration.EngineMode.MANUAL);
		AtomicInteger initCounter = new AtomicInteger(0);
		AtomicInteger shutdownCounter = new AtomicInteger(0);
		AtomicInteger updateCounter = new AtomicInteger(0);
		configuration.platformCallbacks(new Platform.Callback() {
			@Override
			public void onInitialize() {
				initCounter.incrementAndGet();
			}

			@Override
			public void onUpdate() {
				updateCounter.incrementAndGet();
				if (updateCounter.get() == 10) {
					Engine.shutdown("OK");
				}
			}

			@Override
			public void onShutdown() {
				shutdownCounter.incrementAndGet();
			}
		});
		Engine.ShutdownReason shutdownReason = Engine.start(configuration);
		while (shutdownReason == null) {
			shutdownReason = Engine.update();
		}
		assertEquals(1, initCounter.get());
		assertEquals(1, shutdownCounter.get());
		assertEquals(10, updateCounter.get());
	}
}
