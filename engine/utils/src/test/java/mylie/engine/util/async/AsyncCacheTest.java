package mylie.engine.util.async;

import static mylie.engine.util.async.Async.*;
import static mylie.engine.util.async.AsyncTestSetup.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AsyncCacheTest {

	private void setupScheduler(Scheduler scheduler) {
		scheduler.register(Cache.NO);
		scheduler.register(Cache.ONE_FRAME);
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testNoOpCache(Scheduler scheduler) {
		setupScheduler(scheduler);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		List<Result<Boolean>> results = new LinkedList<>();
		results.add(
				async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, atomicInteger));
		results.add(
				async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, atomicInteger));
		scheduler.progress();
		results.add(
				async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.NO, 0, ATOMIC_INTEGER_INCREASE, atomicInteger));
		await(results);
		assertEquals(3, atomicInteger.get());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testOneFrameCache(Scheduler scheduler) {
		setupScheduler(scheduler);
		AtomicInteger atomicInteger = new AtomicInteger(0);
		List<Result<Boolean>> results = new LinkedList<>();
		results.add(async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, ATOMIC_INTEGER_INCREASE,
				atomicInteger));
		results.add(async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 1, ATOMIC_INTEGER_INCREASE,
				atomicInteger));
		await(results);
		scheduler.progress();
		results.add(async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 2, ATOMIC_INTEGER_INCREASE,
				atomicInteger));
		results.add(async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 2, ATOMIC_INTEGER_INCREASE,
				atomicInteger));
		await(results);
		assertEquals(2, atomicInteger.get());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testOneFrameCache2(Scheduler scheduler) {
		setupScheduler(scheduler);
		Integer integer = 1;
		Integer integer1 = 1;
		Result<Integer> result1 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, SUM_INTEGER,
				integer, integer1);
		Result<Integer> result2 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, SUM_INTEGER,
				integer, integer1);
		assertSame(result1, result2);
		Integer result = await(result1);
		assertEquals(2, result.intValue());
		scheduler.progress();
		Result<Integer> result3 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, SUM_INTEGER,
				integer, integer1);
		assertNotSame(result1, result3);
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testOneFrameCache3(Scheduler scheduler) {
		setupScheduler(scheduler);
		Integer integer = 1;
		Integer integer1 = 1;
		Integer integer2 = 2;
		Result<Integer> result1 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, MUL_ADD_INTEGER,
				integer, integer1, integer2);
		Result<Integer> result2 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, MUL_ADD_INTEGER,
				integer, integer1, integer2);
		assertSame(result1, result2);
		Integer result = await(result1);
		assertEquals(3, result.intValue());
		scheduler.progress();
		Result<Integer> result3 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, MUL_ADD_INTEGER,
				integer, integer1, integer2);
		assertNotSame(result1, result3);
		assertEquals(3, await(result3).intValue());
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testOneFrameCacheZeroArgument(Scheduler scheduler) {
		setupScheduler(scheduler);
		Result<Boolean> result = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, WAIT_100_MS);
		await(result);
		Result<Boolean> result1 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, WAIT_100_MS);
		assertSame(result, result1);
		scheduler.progress();
		Result<Boolean> result2 = async(scheduler, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME, 0, WAIT_100_MS);
		assertNotSame(result, result2);
	}
}
