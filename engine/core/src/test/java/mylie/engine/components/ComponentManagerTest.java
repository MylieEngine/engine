package mylie.engine.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ComponentManagerTest {

	@Test
	void testAddComponent() {
		ComponentManager componentManager = new ComponentManager(null);
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent2());
		Assertions.assertEquals(2, componentManager.components().size());
	}

	@Test
	void testRemoveComponent() {
		ComponentManager componentManager = new ComponentManager(null);
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent2());
		Assertions.assertEquals(2, componentManager.components().size());
		componentManager.removeComponent(TestComponent.class);
		Assertions.assertEquals(1, componentManager.components().size());
	}

	@Test
	void testRemoveMany() {
		ComponentManager componentManager = new ComponentManager(null);
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent2());
		Assertions.assertEquals(3, componentManager.components().size());
		componentManager.removeComponent(TestComponent.class);
		Assertions.assertEquals(1, componentManager.components().size());
	}

	private static class TestComponent extends Component {

	}

	private static class TestComponent2 extends Component {

	}
}
