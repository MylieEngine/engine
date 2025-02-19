package mylie.engine.util.async;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MultiThreadedScheduler extends Scheduler implements Scheduler.Executor {
	protected MultiThreadedScheduler() {
		super(true, new SingleThreadedScheduler.SingleThreadedCache().createInstance());
		register(Target.BACKGROUND, this);
	}

	@Override
	public void register(Target target, Consumer<Runnable> drain) {
		register(target, new SubmitExecutor(this, drain));
	}

	static class ExecutorBased extends MultiThreadedScheduler {
		final ExecutorService executor;

		ExecutorBased(ExecutorService executor) {
			this.executor = executor;
		}

		@Override
		public <R> Result<R> executeFunction(Target target, Cache cache, long version, Hash hash,
				Supplier<R> function) {
			Result<R> result = new Result<>(hash, version, target, function);
			cache(cache).result(result);
			Async.unlock();
			executor.execute(() -> {
				if (result.running().compareAndSet(false, true)) {
					AsyncUtil.checkedExecute(function, result);
				}
			});
			return result;
		}
	}
}
