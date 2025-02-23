package mylie.engine;

import java.util.concurrent.ForkJoinPool;
import mylie.engine.util.async.MultiThreadedScheduler;
import mylie.engine.util.async.Scheduler;
import mylie.engine.util.async.SingleThreadedScheduler;

public abstract class SchedulerSettings {
	abstract Scheduler getInstance();

	public static SchedulerSettings singleThreaded() {
		return new SchedulerSettings() {
			@Override
			Scheduler getInstance() {
				return new SingleThreadedScheduler();
			}
		};
	}

	public static SchedulerSettings forkJoin() {
		return new SchedulerSettings() {
			@Override
			Scheduler getInstance() {
				return new MultiThreadedScheduler.ExecutorBased(ForkJoinPool.commonPool());
			}
		};
	}
}
