package mylie.engine.util.async;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SingleThreadedScheduler extends Scheduler {
	private final SingleThreadedExecutor executor;
	public SingleThreadedScheduler() {
		super(false, new SingleThreadedCache().createInstance());
		primaryCache().progress();
		primaryCache().clear();
		executor = new SingleThreadedExecutor(this);
		register(Target.BACKGROUND, executor);
	}

	@Override
	public void register(Target target, Consumer<Runnable> drain) {
		register(target, executor);
	}

	private static final class SingleThreadedExecutor implements Executor {
		final Scheduler scheduler;

		private SingleThreadedExecutor(Scheduler scheduler) {
			this.scheduler = scheduler;
		}

		@Override
		public <R> Result<R> executeFunction(Target target, Cache cache, long version, Hash hash,
				Supplier<R> function) {
			Result<R> result = new Result<>(hash, version);
			scheduler.cache(cache).result(result);
			Async.unlock();
			AsyncUtil.checkedExecute(function, result);
			return result;
		}
	}

	private static final class SingleThreadedCache extends Cache {
		private final Map<Hash, Result<?>> data = new HashMap<>();

		@Override
		void progress() {
			// Nothing to do
		}

		@Override
		void clear() {
			data.clear();
		}

		@Override
		void remove(Hash hash) {
			data.remove(hash);
		}

		@Override
		<R> void result(Result<R> result) {
			data.put(result.hash(), result);
		}

		@SuppressWarnings("unchecked")
		@Override
		<R> Result<R> result(Hash hash, long version) {
			Result<?> result = data.get(hash);
			return (Result<R>) result;
		}

		@Override
		Cache createInstance() {
			return new SingleThreadedCache();
		}
	}
}
