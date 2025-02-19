package mylie.engine.util.async;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public abstract class Scheduler {
	@Getter
	private final boolean multiThreaded;
	@Getter(AccessLevel.PACKAGE)
	private final Cache primaryCache;
	private final Map<Target, Executor> targetExecutors = new HashMap<>();
	private final Map<Cache, Cache> caches = new IdentityHashMap<>();

	protected Scheduler(boolean multiThreaded, Cache primaryCache) {
		this.multiThreaded = multiThreaded;
		this.primaryCache = primaryCache;
	}

	public void progress() {
		for (Cache cache : caches.values()) {
			cache.progress();
		}
	}

	void register(Target target, Executor executor) {
		if (targetExecutors.containsKey(target)) {
			throw new IllegalStateException("Target already registered: " + target);
		}
		log.trace("Registering target {} with executor {}", target, executor);
		targetExecutors.put(target, executor);
	}

	public abstract void register(Target target, Consumer<Runnable> drain);

	public void unregister(Target target) {
		if (!targetExecutors.containsKey(target)) {
			throw new IllegalStateException("Target not registered: " + target);
		}
		log.trace("Unregistering target {}", target);
		targetExecutors.remove(target);
	}

	public void register(Cache cache) {
		if (caches.containsKey(cache)) {
			throw new IllegalStateException("Cache already registered: " + cache);
		}
		log.trace("Registering cache {}", cache);
		Cache cacheInstance = cache.createInstance();
		cacheInstance.clear();
		cacheInstance.parent(primaryCache);
		caches.put(cache, cacheInstance);
		cache.parent(primaryCache);
	}

	public void unregister(Cache cache) {
		if (!caches.containsKey(cache)) {
			throw new IllegalStateException("Cache not registered: " + cache);
		}
		log.trace("Unregistering cache {}", cache);
		Cache removedCache = caches.remove(cache);
		removedCache.clear();
		removedCache.parent(null);
	}

	public Cache cache(Cache cache) {
		if (!caches.containsKey(cache)) {
			throw new IllegalStateException("Cache not registered: " + cache);
		}
		return caches.get(cache);
	}

	<R> Result<R> execute(Target target, Cache cache, long version, Hash hash, Supplier<R> function) {
		Executor executor = targetExecutors.get(target);
		if (executor == null) {
			throw new IllegalStateException("Target not registered: " + target);
		}
		return executor.executeFunction(target, cache, version, hash, function);
	}

	interface Executor {
		<R> Result<R> executeFunction(Target target, Cache cache, long version, Hash hash, Supplier<R> function);
	}

	static final class SubmitExecutor implements Executor {
		final Scheduler scheduler;
		final Consumer<Runnable> drain;

		public SubmitExecutor(Scheduler scheduler, Consumer<Runnable> drain) {
			this.scheduler = scheduler;
			this.drain = drain;
		}

		@Override
		public <R> Result<R> executeFunction(Target target, Cache cache, long version, Hash hash,
				Supplier<R> function) {
			Result<R> result = new Result<>(hash, version, target, function);
			scheduler.cache(cache).result(result);
			Async.unlock();
			drain.accept(() -> AsyncUtil.checkedExecute(function, result));
			return result;
		}
	}
}
