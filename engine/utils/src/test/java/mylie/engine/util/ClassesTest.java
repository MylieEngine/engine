package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClassesTest {

	@Test
	void testCreateInstance() {
		Constructor<?> declaredConstructor = Classes.class.getDeclaredConstructors()[0];
		declaredConstructor.setAccessible(true);
		Exception e = Assertions.assertThrows(Exception.class, declaredConstructor::newInstance);
		Assertions.assertEquals(IllegalStateException.class, e.getCause().getClass());
		Assertions.assertEquals("Utility class", e.getCause().getMessage());
	}

	@Test
	void shouldCreateNewInstanceForValidClass() {
		MyTestObject instance = Classes.newInstance(MyTestObject.class);
		assertNotNull(instance);
	}

	@Test
	void shouldThrowIllegalStateExceptionForClassWithoutDefaultConstructor() {
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Classes.newInstance(ClassWithoutDefaultConstructor.class));
		assertTrue(exception.getMessage().contains("Failed to instantiate class"));
	}

	@Test
	void shouldThrowIllegalStateExceptionForAbstractClass() {
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Classes.newInstance(AbstractClass.class));
		assertTrue(exception.getMessage().contains("Failed to instantiate class"));
	}

	@Test
	void shouldThrowIllegalStateExceptionForInterface() {
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Classes.newInstance(MyInterface.class));
		assertTrue(exception.getMessage().contains("Failed to instantiate class"));
	}

	@Test
	void shouldHandlePrivateClassInstantiation() {
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> Classes.newInstance(PrivateTestClass.class));
		assertTrue(exception.getMessage().contains("Failed to instantiate class"));
	}

	// Test classes
	public static class MyTestObject {
	}

	@SuppressWarnings("unused")
	public static class ClassWithoutDefaultConstructor {
		public ClassWithoutDefaultConstructor(String arg) {
		}
	}

	public abstract static class AbstractClass {
	}

	public interface MyInterface {
	}

	private static class PrivateTestClass {
	}
}
