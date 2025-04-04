package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArgumentsTest {

	@Test
	void testFromString_testKeys() {
		Arguments arguments = new Arguments();

		arguments.fromString("-key value -key2 value2 -key4");

		assertTrue(arguments.isSet("key"));
		assertEquals("value", arguments.value("key"));
		assertTrue(arguments.isSet("key2"));
		assertEquals("value2", arguments.value("key2"));
		assertFalse(arguments.isSet("key3"));
		assertThrows(IllegalArgumentException.class, () -> arguments.value("key3"));
		assertTrue(arguments.isSet("key4"));
		assertEquals("", arguments.value("key4"));
	}

	@Test
	void testFromString_overwritingSameKey() {
		Arguments arguments = new Arguments();

		arguments.fromString("-key value1 -key value2");

		assertTrue(arguments.isSet("key"));
		assertEquals("value2", arguments.value("key"));
	}

	@Test
	void testFromString_unexpectedFormatValueBeforeKeyThrowsException() {
		Arguments arguments = new Arguments();
		arguments.fromString("-key1 -key2");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> arguments.value("key3"));
		assertEquals("Argument key3 is not set", exception.getMessage());
	}

	@Test
	void testFromString_unexpectedFormatValueAfterKeyThrowsException() {
		Arguments arguments = new Arguments();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> arguments.fromString("-key value value"));
		assertEquals("Argument value does not start with -", exception.getMessage());
	}

	public enum Type {
		TYPE1, TYPE2
	}

	@Test
	void testFromString_typedValue() {
		Arguments arguments = new Arguments();
		arguments.fromString("-key1 TYPE1 -key2 TYPE2 -key3 TYPE3");
		assertTrue(arguments.isSet("key1"));
		assertTrue(arguments.isSet("key2"));
		assertTrue(arguments.isSet("key3"));
		Arguments.Typed<Type> key1 = Arguments.Typed.ofEnum(Type.class, "key1");
		Arguments.Typed<Type> key2 = Arguments.Typed.ofEnum(Type.class, "key2");
		Arguments.Typed<Type> key3 = Arguments.Typed.ofEnum(Type.class, "key3");
		Arguments.Typed<Type> key4 = Arguments.Typed.ofEnum(Type.class, "key4");
		assertTrue(arguments.isSet(key1));
		assertTrue(arguments.isSet(key2));
		assertTrue(arguments.isSet(key3));
		assertEquals(Type.TYPE1, arguments.value(key1));
		assertEquals(Type.TYPE2, arguments.value(key2));
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> arguments.value(key3));
		assertEquals("Failed to parse typed argument key3", illegalStateException.getMessage());
		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> arguments.value(key4));
		assertEquals("Typed argument key4 is not set", illegalArgumentException.getMessage());
	}

	@Test
	void testFromArray_null() {
		Arguments arguments = new Arguments();
		assertDoesNotThrow(() -> arguments.fromArray(null));
	}

	@Test
	void testFromArray_empty() {
		Arguments arguments = new Arguments();
		assertDoesNotThrow(() -> arguments.fromArray(new String[0]));
	}

	@Test
	void testFromArray_valid() {
		Arguments arguments = new Arguments();
		assertDoesNotThrow(() -> arguments.fromArray(new String[]{"-key1", "value1", "-key2", "value2"}));
		assertTrue(arguments.isSet("key1"));
		assertTrue(arguments.isSet("key2"));
		assertEquals("value1", arguments.value("key1"));
		assertEquals("value2", arguments.value("key2"));
	}
}
