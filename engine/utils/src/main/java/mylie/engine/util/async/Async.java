package mylie.engine.util.async;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Async {
	private Async() {
	}
	private static final Lock lock = new ReentrantLock();

	public static <T extends Collection<Result<R>>, R> void await(T results) {
		for (Result<R> result : results) {
			result.future().join();
		}
	}

	public static <R> R await(Result<R> result) {
		return result.future().join();
	}

	public static <R> Result<R> async(Scheduler scheduler, Mode mode, Target target, Cache cache, long version,
			Functions.Zero<R> function) {
		Hash hash = new Hash(function);
		lock();
		Result<R> result = scheduler.cache(cache).result(hash, version);
		logAsyncCall(target, hash, result, function);
		if (result == null) {
			result = executeFunction(scheduler, mode, target, cache, version, hash, function::execute);
		} else {
			unlock();
		}
		return result;
	}

	public static <P0, R> Result<R> async(Scheduler scheduler, Mode mode, Target target, Cache cache, long version,
			Functions.One<P0, R> function, P0 p0) {
		Hash hash = new Hash(function, p0);
		lock();
		Result<R> result = scheduler.cache(cache).result(hash, version);
		logAsyncCall(target, hash, result, function, p0);
		if (result == null) {
			result = executeFunction(scheduler, mode, target, cache, version, hash, () -> function.execute(p0));
		} else {
			unlock();
		}
		return result;
	}

	public static <P0, P1, R> Result<R> async(Scheduler scheduler, Mode mode, Target target, Cache cache, long version,
			Functions.Two<P0, P1, R> function, P0 p0, P1 p1) {
		Hash hash = new Hash(function, p0, p1);
		lock();
		Result<R> result = scheduler.cache(cache).result(hash, version);
		logAsyncCall(target, hash, result, function, p0, p1);
		if (result == null) {
			result = executeFunction(scheduler, mode, target, cache, version, hash, () -> function.execute(p0, p1));
		} else {
			unlock();
		}
		return result;
	}

	public static <P0, P1, P2, R> Result<R> async(Scheduler scheduler, Mode mode, Target target, Cache cache,
			long version, Functions.Three<P0, P1, P2, R> function, P0 p0, P1 p1, P2 p2) {
		Hash hash = new Hash(function, p0, p1, p2);
		lock();
		Result<R> result = scheduler.cache(cache).result(hash, version);
		logAsyncCall(target, hash, result, function, p0, p1, p2);
		if (result == null) {
			result = executeFunction(scheduler, mode, target, cache, version, hash, () -> function.execute(p0, p1, p2));
		} else {
			unlock();
		}
		return result;
	}

	private static <R> Result<R> executeFunction(Scheduler scheduler, Mode mode, Target target, Cache cache,
			long version, Hash hash, Supplier<R> function) {
		Result<R> result;
		if (executeDirectly(mode, target)) {
			result = new Result<>(hash, version);
			cache.result(result);
			unlock();
			try {
				result.future().complete(function.get());
			} catch (Exception e) {
				result.future().completeExceptionally(e);
			}
		} else {
			result = scheduler.execute(target, cache, version, hash, function);
		}
		return result;
	}

	private static boolean executeDirectly(Mode mode, Target target) {
		if (mode == Mode.ASYNC) {
			return false;
		} else {
			return Target.BACKGROUND == target || target.isCurrent();
		}
	}

	private static void logAsyncCall(Target target, Hash hash, Result<?> result, Functions.Base function,
			Object... args) {
		if (log.isTraceEnabled()) {
			log.trace("ASYNC CALL: {}<{}> Target: {}, Hash: {}, Cached:{}", function.id(), Arrays.toString(args),
					target, hash, result != null);
		}
	}

	/**
	 * Acquires the global lock for thread-safe operations.
	 */
	static void lock() {
		lock.lock();
	}

	/**
	 * Releases the previously acquired global lock.
	 *
	 * @throws IllegalMonitorStateException if the current thread has not acquired the lock.
	 */
	static void unlock() {
		lock.unlock();
	}
}
