package mylie.engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import mylie.engine.components.ComponentManager;
import mylie.engine.util.Arguments;
import mylie.engine.util.Blocking;
import mylie.engine.util.async.*;

@Getter(AccessLevel.PACKAGE)
public class Engine {
	public static final Target TARGET = new Target("Engine");
	private static Engine engine;
	@Getter
	private ShutdownReason shutdownReason;
	private ShutdownReason shutdownNextFrame;
	private final EngineConfiguration engineConfiguration;
	private final BlockingQueue<Runnable> asyncQueue = new LinkedBlockingQueue<>();
	private final Scheduler scheduler;
	private final ComponentManager componentManager;
	private boolean updateLoopRunning = false;
	private Engine(EngineConfiguration configuration) {
		Thread.currentThread().setName("Engine");
		this.engineConfiguration = configuration;
		checkArguments();
		this.componentManager = new ComponentManager();
		Core core = new Core(engineConfiguration);
		this.scheduler = core.scheduler();
		this.scheduler.register(TARGET, asyncQueue::add);
		this.componentManager.addComponent(core);
	}

	private void checkArguments() {
		Arguments arguments = engineConfiguration.property(EngineConfiguration.ARGUMENTS);
		if (arguments != null) {
			Feature.Status status = Feature.Status.STABLE;
			boolean allowDeprecation = false;
			Arguments.Typed<Feature.Status> featureLevel = Arguments.Typed.ofEnum(Feature.Status.class,
					"allowFeatures");
			if (arguments.isSet(featureLevel)) {
				status = arguments.value(featureLevel);
			}
			if (arguments.isSet("allowDeprecation")) {
				allowDeprecation = true;
			}
			Feature.Level level = new Feature.Level(allowDeprecation, status);
			engineConfiguration.property(EngineConfiguration.FEATURE_LEVEL, level);
		}
	}

	ShutdownReason onStart() {
		if (engineConfiguration.platformCallbacks() != null) {
			engineConfiguration.platformCallbacks().onInitialize(componentManager);
		}
		if (engineConfiguration.engineMode() == EngineConfiguration.EngineMode.MANAGED) {
			if (scheduler instanceof SingleThreadedScheduler) {
				runSingleThreaded();
			} else {
				runMultiThreaded();
			}
			shutdownProcedure();
		}
		return shutdownReason;
	}

	ShutdownReason onUpdate() {
		scheduler.progress();
		executeQueueTasks(false);
		runUpdateLoop();
		if (shutdownReason != null) {
			shutdownProcedure();
		}
		return shutdownReason;
	}

	private void shutdownProcedure() {
		componentManager().shutdown();
		if (engineConfiguration.platformCallbacks() != null) {
			engineConfiguration.platformCallbacks().onShutdown();
		}
	}

	void onShutdown(ShutdownReason reason) {
		shutdownNextFrame = reason;
	}

	private void runUpdateLoop() {
		shutdownReason = shutdownNextFrame;
		if (shutdownReason != null) {
			componentManager.component(Core.class).running(false);
		}
		scheduler.progress();
		componentManager.update();
		if (engineConfiguration.platformCallbacks() != null && shutdownReason == null) {
			engineConfiguration.platformCallbacks().onUpdate();
		}
	}

	private void runMultiThreaded() {
		updateLoopRunning = true;
		Thread updateThread = new Thread(() -> {
			while (shutdownReason == null) {
				runUpdateLoop();
			}
			runUpdateLoop();
			updateLoopRunning = false;
		}, "Update Thread");
		updateThread.start();
		executeQueueTasks(true);
	}

	private void runSingleThreaded() {
		while (shutdownReason == null) {
			runUpdateLoop();
		}
		runUpdateLoop();
	}

	private void executeQueueTasks(boolean blocking) {
		if (blocking) {
			while (shutdownReason == null || updateLoopRunning) {
				Runnable poll = Blocking.poll(asyncQueue, 10, TimeUnit.MILLISECONDS);
				if (poll != null) {
					poll.run();
				}
			}
		} else {
			Runnable task;
			while ((task = asyncQueue.poll()) != null) {
				task.run();
			}
		}
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
		if (shutdownReason == null) {
			// update ok
			return null;
		}
		if (engine.engineConfiguration.restartBehavior() == EngineConfiguration.RestartBehavior.MANAGED
				&& shutdownReason.reason() == ShutdownReason.Reason.RESTART) {
			engine = new Engine(engine.engineConfiguration());
			return engine.onStart();
		}
		engine = null;
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
