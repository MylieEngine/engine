package mylie.engine.components;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.async.*;

@Slf4j
public class Component {
	@Getter(AccessLevel.PROTECTED)
	private ComponentManager componentManager;
	@Setter
	private boolean enabled = false;
	private boolean isEnabled = false;
	private boolean initialized = false;

	private final Mode mode;
	private final Target target;
	private final Cache cache;
	private Task<Boolean> updateTask;
	protected Component() {
		this(Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME);
	}

	protected Component(Mode mode, Target target, Cache cache) {
		this.mode = mode;
		this.target = target;
		this.cache = cache;
	}

	protected void onAdd(ComponentManager componentManager) {
		this.componentManager = componentManager;
		this.updateTask = new UpdateTask(this, mode, target, cache);
		log.trace("Component<{}> added", this.getClass().getSimpleName());
	}

	Result<?> update() {
		return updateTask.execute();
	}

	protected void onInitialize() {
		log.trace("Component<{}> initialized", this.getClass().getSimpleName());
	}

	protected void onEnable() {
		log.trace("Component<{}> enabled", this.getClass().getSimpleName());
	}

	protected void onUpdate() {
		log.trace("Component<{}> updated", this.getClass().getSimpleName());
	}

	protected void onDisable() {
		log.trace("Component<{}> disabled", this.getClass().getSimpleName());
	}

	protected void onDestroy() {
		log.trace("Component<{}> destroyed", this.getClass().getSimpleName());
	}

	protected void onRemoval() {
		log.trace("Component<{}> removed", this.getClass().getSimpleName());
	}

	private static class UpdateTask extends Task<Boolean> {
		private final Component component;
		private final Scheduler scheduler;
		protected UpdateTask(Component component, Mode mode, Target target, Cache cache) {
			super("UpdateTask<" + component.getClass().getSimpleName() + ">", mode, target, cache);
			this.component = component;
			this.scheduler = component.componentManager().engine().scheduler();
		}

		@Override
		protected Result<Boolean> onExecute() {
			return Async.async(scheduler, mode(), target(), cache(), 0, UPDATE_FUNCTION, component);
		}
	}

	private static final Functions.One<Component, Boolean> UPDATE_FUNCTION = new Functions.One<>("COMPONENT UPDATE") {
		@Override
		protected Boolean execute(Component component) {
			if (component.componentManager().engine().shutdownReason() == null) {
				// update
				if (!component.initialized) {
					component.initialized = true;
					component.onInitialize();
				}
				if (component.enabled != component.isEnabled) {
					if (component.enabled) {
						component.isEnabled = true;
						component.onEnable();
					} else {
						component.isEnabled = false;
						component.onDisable();
					}
				}
				if (component.isEnabled) {
					component.onUpdate();
				}
			} else {
				// shutdown
				if (component.isEnabled) {
					component.isEnabled = false;
					component.onDisable();
				}
				if (component.initialized) {
					component.initialized = false;
					component.onDestroy();
				}
			}
			return true;
		}
	};

}
