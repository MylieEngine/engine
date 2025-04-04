package mylie.engine.util.async;

import static mylie.engine.util.async.Async.*;
import static mylie.engine.util.async.AsyncTestSetup.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AsyncBaseTest {

	private void setupScheduler(Scheduler scheduler) {
		scheduler.register(Cache.NO);
	}

	@Test
	void testCreateInstance() {
		Constructor<?> declaredConstructor = Async.class.getDeclaredConstructors()[0];
		declaredConstructor.setAccessible(true);
		Exception e = Assertions.assertThrows(Exception.class, declaredConstructor::newInstance);
		Assertions.assertEquals(IllegalStateException.class, e.getCause().getClass());
		Assertions.assertEquals("Utility class", e.getCause().getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCacheNotRegistered(Scheduler scheduler) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> async(scheduler, Mode.ASYNC,
				Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, atomicInteger));
		assertEquals("Cache not registered: " + Cache.NO, exception.getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCacheRegisterUnregister(Scheduler scheduler) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> async(scheduler, Mode.ASYNC,
				Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, atomicInteger));
		assertEquals("Cache not registered: " + Cache.NO, exception.getMessage());
		scheduler.register(Cache.NO);
		assertDoesNotThrow(() -> async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE,
				atomicInteger));
		scheduler.unregister(Cache.NO);
		exception = assertThrows(IllegalStateException.class, () -> async(scheduler, Mode.ASYNC, Target.BACKGROUND,
				Cache.NO, 0, ATOMIC_INTEGER_INCREASE, atomicInteger));
		assertEquals("Cache not registered: " + Cache.NO, exception.getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCacheDoubleRegister(Scheduler scheduler) {
		scheduler.register(Cache.NO);
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> scheduler.register(Cache.NO));
		assertEquals("Cache already registered: " + Cache.NO, illegalStateException.getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCacheDoubleUnRegister(Scheduler scheduler) {
		scheduler.register(Cache.NO);
		assertDoesNotThrow(() -> scheduler.unregister(Cache.NO));
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> scheduler.unregister(Cache.NO));
		assertEquals("Cache not registered: " + Cache.NO, illegalStateException.getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testRegisterUnregisterTarget(Scheduler scheduler) {
		Target target = new Target("TestTarget");
		Queue<Runnable> drain = new LinkedList<>();
		assertDoesNotThrow(() -> scheduler.register(target, drain::add));
		assertDoesNotThrow(() -> scheduler.unregister(target));
		assertDoesNotThrow(() -> scheduler.register(target, drain::add));
		IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
				() -> scheduler.register(target, drain::add));
		assertEquals("Target already registered: " + target, illegalStateException.getMessage());
		assertDoesNotThrow(() -> scheduler.unregister(target));
		IllegalStateException illegalStateException1 = assertThrows(IllegalStateException.class,
				() -> scheduler.unregister(target));
		assertEquals("Target not registered: " + target, illegalStateException1.getMessage());
		assertEquals(0, drain.size());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testExecuteAsyncWithoutTarget(Scheduler scheduler) {
		setupScheduler(scheduler);
		Target target = new Target("TestTarget");
		AtomicInteger atomicInteger = new AtomicInteger(0);
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> async(scheduler, Mode.ASYNC, target, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, atomicInteger));
		assertEquals("Target not registered: " + target, exception.getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testExecutionModeDirect(Scheduler scheduler) {
		setupScheduler(scheduler);
		String threadName = "TestThread";
		Thread.currentThread().setName(threadName);
		List<String> threadNames = new LinkedList<>();
		List<Result<?>> results = new LinkedList<>();
		results.add(async(scheduler, Mode.DIRECT, Target.BACKGROUND, Cache.NO, 0, AsyncTestSetup.WRITE_THREAD_NAME,
				threadNames));
		results.add(async(scheduler, Mode.DIRECT, Target.BACKGROUND, Cache.NO, 0, AsyncTestSetup.WRITE_THREAD_NAME,
				threadNames));
		await(results);
		assertEquals(2, threadNames.size());
		assertEquals(threadName, threadNames.get(0));
		assertEquals(threadName, threadNames.get(1));
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testExecuteException(Scheduler scheduler) {
		setupScheduler(scheduler);
		Result<Boolean> async = async(scheduler, Mode.DIRECT, Target.BACKGROUND, Cache.NO, 0, THROW_EXCEPTION);
		assertThrows(RuntimeException.class, () -> await(async));
		Result<Boolean> async1 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, THROW_EXCEPTION);
		assertThrows(RuntimeException.class, () -> await(async1));
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testSubmitAlways(Scheduler scheduler) {
		setupScheduler(scheduler);
		Target target = new Target("TestTarget", true);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		Queue<Runnable> drain = new LinkedList<>();
		assertDoesNotThrow(() -> scheduler.register(target, drain::add));
		Result<Boolean> result = async(scheduler, Mode.ASYNC, target, Cache.NO, 0, ATOMIC_INTEGER_INCREASE,
				atomicInteger);
		assertEquals(1, drain.size());
		assertFalse(result.future.isDone());
		assertEquals(0, atomicInteger.get());
		Runnable poll = drain.poll();
		assertNotNull(poll);
		poll.run();
		assertTrue(result.future.isDone());
		assertEquals(1, atomicInteger.get());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCustomHashObject(Scheduler scheduler) {
		setupScheduler(scheduler);
		AsyncTestSetup.CustomHashObject customHashObject = new AsyncTestSetup.CustomHashObject(1);
		Result<Boolean> result = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, CUSTOM_HASH_OBJECT,
				customHashObject);
		assertTrue(result.result());
	}

	@SuppressWarnings({"SimplifiableAssertion", "EqualsBetweenInconvertibleTypes"})
	@Test
	void testHashEquals() {
		Hash hash = new Hash(THROW_EXCEPTION);
		Assertions.assertNotEquals(new Hash(WAIT_100_MS), hash);
		Assertions.assertFalse(hash.equals("adsf"));
	}
}
