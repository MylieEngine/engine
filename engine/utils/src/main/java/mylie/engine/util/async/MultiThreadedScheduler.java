package mylie.engine.util.async;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MultiThreadedScheduler extends Scheduler {
	protected MultiThreadedScheduler() {
		super(true, new SingleThreadedScheduler.SingleThreadedCache().createInstance());
	}

	@Override
	public void register(Target target, Consumer<Runnable> drain) {
		register(target, new SubmitExecutor(this, drain));
	}

	public static class ExecutorBased extends MultiThreadedScheduler {
		final ExecutorService executor;
		final Executor executorMapper;
		public ExecutorBased(ExecutorService executor) {
			this.executor = executor;
			ExecutorBased self = this;
			executorMapper = new Executor() {
				@Override
				<R> Result<R> executeFunction(Target target, Cache cache, long version, Hash hash,
						Supplier<R> function) {
					return self.executeFunction(target, cache, version, hash, function);
				}
			};
			register(Target.BACKGROUND, executorMapper);
		}

		<R> Result<R> executeFunction(Target target, Cache cache, long version, Hash hash, Supplier<R> function) {
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
