package mylie.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import mylie.engine.components.Component;
import mylie.engine.components.ComponentManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ComponentTest {
	static Stream<SchedulerSettings> schedulerProvider() {
		return Stream.of(SchedulerSettings.singleThreaded(), SchedulerSettings.forkJoin());
	}

	@ParameterizedTest
	@MethodSource("schedulerProvider")
	void testComponents(SchedulerSettings schedulerSettings) {
		EngineConfiguration configuration = new EngineConfiguration();
		configuration.property(EngineConfiguration.SCHEDULER, schedulerSettings);
		configuration.engineMode(EngineConfiguration.EngineMode.MANAGED);
		TestComponent component = new TestComponent();
		configuration.platformCallbacks(new Platform.Callback() {
			int counter = 0;
			@Override
			public void onInitialize(ComponentManager componentManager) {
				componentManager.addComponent(component);
			}

			@Override
			public void onUpdate() {
				counter++;
				if (counter == 10) {
					Engine.shutdown("OK");
				}
			}

			@Override
			public void onShutdown() {
				// nothing to do here
			}
		});
		Engine.start(configuration);
		assertEquals(10, component.update);
	}

	static class TestComponent extends Component {
		boolean add = false;
		boolean init = false;
		boolean enable = false;
		int update = 0;

		public TestComponent() {
			enabled(true);
		}

		@Override
		protected void onAdd(ComponentManager componentManager) {
			super.onAdd(componentManager);
			assertFalse(add);
			add = true;
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			assertTrue(add);
			assertFalse(init);
			assertEquals(0, update);
			init = true;
		}

		@Override
		protected void onEnable() {
			super.onEnable();
			assertFalse(enable);
			assertTrue(init);
			assertEquals(0, update);
			enable = true;
		}

		@Override
		protected void onUpdate() {
			super.onUpdate();
			update++;
			assertTrue(enable);
		}

		@Override
		protected void onDisable() {
			super.onDisable();
			assertTrue(enable);
			enable = false;
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
			assertFalse(enable);
			assertTrue(init);
			init = false;
			assertTrue(add);
		}

		@Override
		protected void onRemoval() {
			super.onRemoval();
			assertTrue(add);
			assertFalse(init);
			assertFalse(enable);
			add = false;
		}
	}
}
