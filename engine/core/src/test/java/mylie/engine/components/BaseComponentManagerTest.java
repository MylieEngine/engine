package mylie.engine.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BaseComponentManagerTest {

	@Test
	void testAddComponent() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(new TestBaseComponent());
		componentManager.addComponent(new TestBaseComponent2());
		Assertions.assertEquals(2, componentManager.components().size());
	}

	@Test
	void testRemoveComponent() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(new TestBaseComponent());
		componentManager.addComponent(new TestBaseComponent2());
		Assertions.assertEquals(2, componentManager.components().size());
		componentManager.removeComponent(TestBaseComponent.class);
		Assertions.assertEquals(1, componentManager.components().size());
	}

	@Test
	void testRemoveMany() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponent(new TestBaseComponent());
		componentManager.addComponent(new TestBaseComponent());
		componentManager.addComponent(new TestBaseComponent2());
		Assertions.assertEquals(3, componentManager.components().size());
		componentManager.removeComponent(TestBaseComponent.class);
		Assertions.assertEquals(1, componentManager.components().size());
	}

	@Test
	void testAddMany() {
		ComponentManager componentManager = new ComponentManager();
		componentManager.addComponents(new TestBaseComponent(), new TestBaseComponent());
		Assertions.assertEquals(2, componentManager.components().size());
	}

	@Test
	void testGetComponent() {
		ComponentManager componentManager = new ComponentManager();
		TestBaseComponent testComponent = new TestBaseComponent();
		TestBaseComponent2 testComponent2 = new TestBaseComponent2();
		componentManager.addComponents(testComponent2, testComponent);
		Assertions.assertSame(testComponent, componentManager.component(TestBaseComponent.class));
		Assertions.assertSame(testComponent2, componentManager.component(TestBaseComponent2.class));
	}

	private static class TestBaseComponent extends BaseComponent {

	}

	private static class TestBaseComponent2 extends BaseComponent {

	}
}
