package mylie.engine.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CachedValueTest {

	@Test
	void testConstructor() {
		VersionedValue<Integer> a = new VersionedValue<>(1);
		VersionedValue<Integer> b = new VersionedValue<>(2);
		CachedValue<Integer> sum = new CachedValue<>(() -> a.value() + b.value(), a.ref(), b.ref());
		assertEquals(3, sum.value());
		assertEquals(0, sum.version());
	}

	@Test
	void testEmptyConstructor() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new CachedValue<>(null));
		assertEquals("Function cannot be null", exception.getMessage());
	}

	@Test
	void testChange() {
		VersionedValue<Integer> a = new VersionedValue<>(1);
		VersionedValue<Integer> b = new VersionedValue<>(2);
		CachedValue<Integer> sum = new CachedValue<>(() -> a.value() + b.value(), a.ref(), b.ref());
		a.value(2);
		assertEquals(4, sum.value());
		assertEquals(1, sum.version());
	}

	@Test
	void testReference() {
		VersionedValue<Integer> a = new VersionedValue<>(1);
		VersionedValue<Integer> b = new VersionedValue<>(2);
		CachedValue<Integer> sum = new CachedValue<>(() -> a.value() + b.value(), a.ref(), b.ref());
		Versioned.Ref<Integer> ref = sum.ref();
		assertEquals(3, ref.value(false));
		a.value(2);
		assertNotEquals(4, ref.value(false));
		assertTrue(ref.requiresUpdate());
		assertEquals(4, ref.value(true));
	}
}
