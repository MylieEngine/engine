package mylie.engine.util.async;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TargetTest {

	/**
	 * Test class for {@link Target#bind()}.
	 * The bind() method ensures that the current Target instance is marked as bound and stored
	 * in a thread-local storage. It throws exceptions when invoked in an invalid state.
	 */

	@Test
	void testBind_SuccessfulBind() {
		Target target = new Target("TestTarget");
		target.bind();
		assertTrue(target.isCurrent());
	}

	@Test
	void testBind_AlreadyBound() {
		Target target = new Target("TestTarget");
		target.bind();
		IllegalStateException exception = assertThrows(IllegalStateException.class, target::bind);
		assertEquals("Target is already bound", exception.getMessage());
	}

	@Test
	void testBind_NotBindable() {
		Target target = Target.BACKGROUND;
		IllegalStateException exception = assertThrows(IllegalStateException.class, target::bind);
		assertEquals("Target is not bindable", exception.getMessage());
	}

	@Test
	void testUnbind_SuccessfulUnbind() {
		Target target = new Target("TestTarget");
		target.bind();
		target.unbind();
		assertFalse(target.isCurrent());
	}

	@Test
	void testUnbind_NotBound() {
		Target target = new Target("TestTarget");
		IllegalStateException exception = assertThrows(IllegalStateException.class, target::unbind);
		assertEquals("Target is not bound", exception.getMessage());
	}
}
