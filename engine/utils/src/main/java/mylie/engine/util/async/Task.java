package mylie.engine.util.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
public abstract class Task<R> {
	final String id;
	final Mode mode;
	final Target target;
	final Cache cache;

	@Getter
	final CopyOnWriteArrayList<Task<?>> dependencies;

	protected Task(String id, Mode mode, Target target, Cache cache) {
		this.id = id;
		this.mode = mode;
		this.target = target;
		this.cache = cache;
		this.dependencies = new CopyOnWriteArrayList<>();
	}

	public Result<R> execute() {
		List<Result<?>> results = new ArrayList<>(dependencies.size());
		for (Task<?> dependency : dependencies) {
			results.add(dependency.execute());
		}
		Async.await(results);
		return onExecute();
	}

	protected abstract Result<R> onExecute();
}
