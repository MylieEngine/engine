package mylie.engine.util.async;

import static mylie.engine.util.async.Async.*;
import static mylie.engine.util.async.AsyncTestSetup.ATOMIC_INTEGER_INCREASE;
import static mylie.engine.util.async.AsyncTestSetup.THROW_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AsyncBaseTest {

	private void setupScheduler(Scheduler scheduler) {
		scheduler.register(Cache.NO);
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCacheNotRegistered(Scheduler scheduler) {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> async(scheduler, Mode.ASYNC,
				Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, new AtomicInteger(0)));
		assertEquals("Cache not registered: " + Cache.NO, exception.getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCacheRegisterUnregister(Scheduler scheduler) {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> async(scheduler, Mode.ASYNC,
				Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, new AtomicInteger(0)));
		assertEquals("Cache not registered: " + Cache.NO, exception.getMessage());
		scheduler.register(Cache.NO);
		assertDoesNotThrow(() -> async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE,
				new AtomicInteger(0)));
		scheduler.unregister(Cache.NO);
		exception = assertThrows(IllegalStateException.class, () -> async(scheduler, Mode.ASYNC, Target.BACKGROUND,
				Cache.NO, 0, ATOMIC_INTEGER_INCREASE, new AtomicInteger(0)));
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
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testExecuteAsyncWithoutTarget(Scheduler scheduler) {
		setupScheduler(scheduler);
		Target target = new Target("TestTarget");
		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> async(scheduler, Mode.ASYNC, target, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, new AtomicInteger(0)));
		assertEquals("Target not registered: " + target, exception.getMessage());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testExecutionModeDirect(Scheduler scheduler) {
		setupScheduler(scheduler);
		String threadName = "TestThread";
		Thread.currentThread().setName(threadName);
		List<String> threadNames = new LinkedList<>();
		List<Result<Boolean>> results = new LinkedList<>();
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
		assertThrows(RuntimeException.class,
				() -> await(async(scheduler, Mode.DIRECT, Target.BACKGROUND, Cache.NO, 0, THROW_EXCEPTION)));
		assertThrows(RuntimeException.class,
				() -> await(async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, THROW_EXCEPTION)));
	}
}
