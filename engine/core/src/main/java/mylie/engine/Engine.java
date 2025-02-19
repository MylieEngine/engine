package mylie.engine;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
public class Engine {
	private static Engine engine;
	private ShutdownReason shutdownReason;
	private final EngineConfiguration engineConfiguration;
	private Engine(EngineConfiguration configuration) {
		this.engineConfiguration = configuration;
	}

	ShutdownReason onStart() {
		return shutdownReason;
	}

	ShutdownReason onUpdate() {
		return shutdownReason;
	}

	void onShutdown(ShutdownReason reason) {
		shutdownReason = reason;
	}

	public static ShutdownReason start(EngineConfiguration config) {
		if (config == null) {
			throw new IllegalArgumentException("EngineConfiguration cannot be null");
		}
		checkEngineState(false);
		if (config.engineMode().equals(EngineConfiguration.EngineMode.MANUAL)) {
			engine = new Engine(config);
			return engine.onStart();
		}
		boolean running = true;
		ShutdownReason reason = null;
		while (running) {
			engine = new Engine(config);
			reason = engine.onStart();
			if (config.restartBehavior() == EngineConfiguration.RestartBehavior.MANUAL) {
				return reason;
			} else {
				if (reason.reason() != ShutdownReason.Reason.RESTART) {
					running = false;
				}
			}
		}
		engine = null;
		return reason;
	}

	public static void shutdown(String reason) {
		checkEngineState(true);
		engine.onShutdown(new ShutdownReason(ShutdownReason.Reason.REQUESTED, reason, null));
	}

	public static void shutdown(Exception e) {
		checkEngineState(true);
		engine.onShutdown(new ShutdownReason(ShutdownReason.Reason.ERROR, e.getMessage(), e));
	}

	public static void restart() {
		checkEngineState(true);
		engine.onShutdown(new ShutdownReason(ShutdownReason.Reason.RESTART, null, null));
	}

	public static ShutdownReason update() {
		checkEngineState(true);
		ShutdownReason shutdownReason = engine.onUpdate();
		if (engine.engineConfiguration.restartBehavior() == EngineConfiguration.RestartBehavior.MANAGED
				&& shutdownReason.reason() == ShutdownReason.Reason.RESTART) {
			engine = new Engine(engine.engineConfiguration());
			return engine.onStart();
		}
		if (shutdownReason != null) {
			engine = null;
		}
		return shutdownReason;
	}

	public static boolean running() {
		return engine != null;
	}

	private static void checkEngineState(boolean shouldBeRunning) {
		if ((engine == null) == (shouldBeRunning)) {
			throw new IllegalStateException(shouldBeRunning ? "Engine not started" : "Engine already started");
		}
	}

	public record ShutdownReason(Reason reason, String message, Exception exception) {
		public enum Reason {
			REQUESTED, ERROR, RESTART
		}
	}

}
