package mylie.engine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.components.Component;
import mylie.engine.util.async.Cache;
import mylie.engine.util.async.Mode;
import mylie.engine.util.async.Scheduler;
import mylie.engine.util.async.Target;

public final class Core extends Component {
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
		Scheduler scheduler = schedulerSettings.getInstance();
		scheduler.register(Cache.NO);
		scheduler.register(Cache.ONE_FRAME);
		scheduler.register(Cache.FOREVER);
		return scheduler;
	}

	@Override
	public void enabled(boolean enabled) {
		throw new IllegalStateException("Core cannot be enabled or disabled");
	}
}
