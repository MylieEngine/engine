package mylie.engine.util.async;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter(AccessLevel.PROTECTED)
public abstract class Task<R> {
    final String id;
    final Scheduler scheduler;
    final Mode mode;
    final Target target;
    final Cache cache;

    @Getter
    final CopyOnWriteArrayList<Task<?>> dependencies;

    protected Task(String id, Scheduler scheduler, Mode mode, Target target, Cache cache) {
        this.id = id;
        this.scheduler = scheduler;
        this.mode = mode;
        this.target = target;
        this.cache = cache;
        this.dependencies = new CopyOnWriteArrayList<>();
    }

    public Result<R> execute(){
        List<Result<?>> results=new ArrayList<>(dependencies.size());
        for(Task<?> dependency:dependencies){
            results.add(dependency.execute());
        }
        Async.await(results);
        return onExecute();
    }

    protected abstract Result<R> onExecute();
}
