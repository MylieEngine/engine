package mylie.engine;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mylie.engine.util.Arguments;
import mylie.engine.util.Versioned;
import mylie.engine.util.VersionedValue;

@Setter
@Getter(AccessLevel.PACKAGE)
public class EngineConfiguration {
	private final Map<Property<?>, VersionedValue<?>> properties = new HashMap<>();
	public enum EngineMode {
		MANAGED, MANUAL
	}
	public enum RestartBehavior {
		MANAGED, MANUAL
	}
	private EngineMode engineMode = EngineMode.MANAGED;
	private RestartBehavior restartBehavior = RestartBehavior.MANAGED;
	@Setter(AccessLevel.PACKAGE)
	private Platform.Callback platformCallbacks;
	public static final Property<SchedulerSettings> SCHEDULER = new Property<>("SCHEDULER",
			SchedulerSettings.forkJoin());
	static final Property<Arguments> ARGUMENTS = new Property<>("ARGUMENTS", null);
	public static final Property<Feature.Level> FEATURE_LEVEL = new Property<>("FEATURE_LEVEL",
			new Feature.Level(false, Feature.Status.STABLE));

	@Getter(AccessLevel.PACKAGE)
	public static class Property<T> {
		private final String name;
		private final T defaultValue;

		Property(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
		}
	}

	public <T> void property(Property<T> property, T value) {
		versioned(property).value(value);
	}

	public <T> T property(Property<T> property) {
		return versioned(property).value();
	}

	public <T> Versioned<T> versionedProperty(Property<T> property) {
		return versioned(property);
	}

	@SuppressWarnings("unchecked")
	private <T> VersionedValue<T> versioned(Property<T> property) {
		VersionedValue<?> versioned = properties.computeIfAbsent(property,
				_ -> new VersionedValue<>(property.defaultValue));
		return (VersionedValue<T>) versioned;
	}
}
