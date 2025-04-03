package mylie.engine.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ComponentManagerTest {

	@Test
	void testAddComponent() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent2());
		Assertions.assertEquals(2, componentManager.components().size());
	}

	@Test
	void testRemoveComponent() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent2());
		Assertions.assertEquals(2, componentManager.components().size());
		componentManager.removeComponent(TestComponent.class);
		Assertions.assertEquals(1, componentManager.components().size());
	}

	@Test
	void testRemoveMany() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent());
		componentManager.addComponent(new TestComponent2());
		Assertions.assertEquals(3, componentManager.components().size());
		componentManager.removeComponent(TestComponent.class);
		Assertions.assertEquals(1, componentManager.components().size());
	}

	@Test
	void testAddMany() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponents(new TestComponent(), new TestComponent());
		Assertions.assertEquals(2, componentManager.components().size());
	}

	@Test
	void testGetComponent() {
		ComponentManager componentManager = new ComponentManager();
		TestComponent testComponent = new TestComponent();
		TestComponent2 testComponent2 = new TestComponent2();
		componentManager.addComponents(testComponent2, testComponent);
		Assertions.assertSame(testComponent, componentManager.component(TestComponent.class));
		Assertions.assertSame(testComponent2, componentManager.component(TestComponent2.class));
	}

	private static class TestComponent extends Component {

	}

	private static class TestComponent2 extends Component {

	}
}
