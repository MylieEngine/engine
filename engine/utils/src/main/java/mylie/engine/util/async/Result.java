package mylie.engine.util.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
@Getter(AccessLevel.PACKAGE)
public class Result<R> {
	final Hash hash;
	final long version;
	final Target target;
	final Supplier<R> function;
	final CompletableFuture<R> future;
	final AtomicBoolean running = new AtomicBoolean(false);
	Result(Hash hash, long version, Target target, Supplier<R> function) {
		this.hash = hash;
		this.version = version;
		this.target = target;
		this.function = function;
		this.future = new CompletableFuture<>();
	}

	public boolean completed() {
		return future.isDone();
	}

	public R result() {
		if (!completed() && (target == Target.BACKGROUND || target.isCurrent())) {
			if (running.compareAndSet(false, true)) {
				AsyncUtil.checkedExecute(function, this);
			}
		}
		return future.join();
	}
}
