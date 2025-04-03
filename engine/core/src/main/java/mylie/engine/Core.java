package mylie.engine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.components.BaseComponent;
import mylie.engine.util.async.Cache;
import mylie.engine.util.async.Mode;
import mylie.engine.util.async.Scheduler;
import mylie.engine.util.async.Target;

public final class Core extends BaseComponent {
	private final EngineConfiguration engineConfiguration;
	@Getter(AccessLevel.PUBLIC)
	private final Scheduler scheduler;
	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PUBLIC)
	private volatile boolean running = true;
	public Core(EngineConfiguration engineConfiguration) {
		super(Mode.DIRECT, Target.BACKGROUND, Cache.FOREVER);
		this.engineConfiguration = engineConfiguration;
		this.scheduler = initScheduler();
	}

	private Scheduler initScheduler() {
		SchedulerSettings schedulerSettings = engineConfiguration.property(EngineConfiguration.SCHEDULER);
		Scheduler engineScheduler = schedulerSettings.getInstance();
		engineScheduler.register(Cache.NO);
		engineScheduler.register(Cache.ONE_FRAME);
		engineScheduler.register(Cache.FOREVER);
		return engineScheduler;
	}

	@Override
	public void enabled(boolean enabled) {
		throw new IllegalStateException("Core cannot be enabled or disabled");
	}
}
