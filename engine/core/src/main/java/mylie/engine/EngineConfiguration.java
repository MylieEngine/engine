package mylie.engine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter(AccessLevel.PACKAGE)
public class EngineConfiguration {
	public enum EngineMode {
		MANAGED, MANUAL
	}
	public enum RestartBehavior {
		MANAGED, MANUAL
	}
	private EngineMode engineMode = EngineMode.MANAGED;
	private RestartBehavior restartBehavior = RestartBehavior.MANAGED;
	private Feature.Level featureLevel = new Feature.Level(false, Feature.Status.STABLE);
	@Setter(AccessLevel.PACKAGE)
	private Platform.Callback platformCallbacks;
	private SchedulerSettings schedulerSettings = SchedulerSettings.forkJoin();
}
