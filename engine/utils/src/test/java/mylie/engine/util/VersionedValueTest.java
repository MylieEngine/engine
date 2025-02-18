package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VersionedValueTest {

	@Test
	void testConstructor() {
		VersionedValue<String> versionedValue = new VersionedValue<>("test");
		assertEquals("test", versionedValue.value());
		assertEquals(0, versionedValue.version());
	}

	@Test
	void testEmptyConstructor() {
		VersionedValue<String> versionedValue = new VersionedValue<>();
		assertNull(versionedValue.value());
		assertEquals(0, versionedValue.version());
	}

	@Test
	void testReferenceUpdate() {
		VersionedValue<String> versionedValue = new VersionedValue<>("test");
		VersionedValue.Ref<String> ref1 = versionedValue.ref();
		assertEquals("test", ref1.value(false));
		assertEquals("test", ref1.value(true));
		assertFalse(ref1.requiresUpdate());
		versionedValue.value("test2");
		assertTrue(ref1.requiresUpdate());
		assertEquals("test", ref1.value(false));
		assertEquals("test2", ref1.value(true));
	}

	@Test
	void testUpdateValue() {
		VersionedValue<String> versionedValue = new VersionedValue<>("test");
		assertEquals("test", versionedValue.value());
		assertEquals(0, versionedValue.version());
		assertDoesNotThrow(() -> versionedValue.value("test2", 1));
		assertEquals("test2", versionedValue.value());
		assertEquals(1, versionedValue.version());
		assertThrows(IllegalArgumentException.class, () -> versionedValue.value("test3", 1));
	}
}
