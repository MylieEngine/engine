package mylie.engine.components;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.Core;
import mylie.engine.util.async.*;

@Slf4j
public class BaseComponent implements Component {
	@Getter(AccessLevel.PROTECTED)
	private ComponentManager componentManager;
	private Core core;
	@Accessors(chain = false)
	@Setter
	private boolean enabled = false;
	private boolean isEnabled = false;
	private boolean initialized = false;

	private final Mode mode;
	private final Target target;
	private final Cache cache;
	private Task<Boolean> updateTask;
	protected BaseComponent() {
		this(Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME);
	}

	protected BaseComponent(Mode mode, Target target, Cache cache) {
		this.mode = mode;
		this.target = target;
		this.cache = cache;
	}

	protected void onAdd(ComponentManager componentManager) {
		this.componentManager = componentManager;
		this.core = componentManager.component(Core.class);
		this.updateTask = new UpdateTask(this, mode, target, cache);
		log.trace("Component<{}> added", this.getClass().getSimpleName());
	}

	Result<Boolean> update() {
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
		this.componentManager = null;
		this.core = null;
		this.updateTask = null;
	}

	private static class UpdateTask extends Task<Boolean> {
		private final BaseComponent baseComponent;
		private final Scheduler scheduler;
		protected UpdateTask(BaseComponent baseComponent, Mode mode, Target target, Cache cache) {
			super("UpdateTask<" + baseComponent.getClass().getSimpleName() + ">", mode, target, cache);
			this.baseComponent = baseComponent;
			if (baseComponent.core != null) {
				this.scheduler = baseComponent.core.scheduler();
			} else {
				this.scheduler = null;
			}
		}

		@Override
		protected Result<Boolean> onExecute() {
			return Async.async(scheduler, super.mode(), super.target(), super.cache(), 0, UPDATE_FUNCTION,
					baseComponent);
		}
	}

	private static final Functions.One<BaseComponent, Boolean> UPDATE_FUNCTION = new Functions.One<>(
			"COMPONENT UPDATE") {
		@Override
		protected Boolean execute(BaseComponent baseComponent) {
			if (baseComponent.core.running()) {
				// update
				if (!baseComponent.initialized) {
					baseComponent.initialized = true;
					baseComponent.onInitialize();
				}
				if (baseComponent.enabled != baseComponent.isEnabled) {
					if (baseComponent.enabled) {
						baseComponent.isEnabled = true;
						baseComponent.onEnable();
					} else {
						baseComponent.isEnabled = false;
						baseComponent.onDisable();
					}
				}
				if (baseComponent.isEnabled) {
					baseComponent.onUpdate();
				}
			} else {
				// shutdown
				if (baseComponent.isEnabled) {
					baseComponent.isEnabled = false;
					baseComponent.onDisable();
				}
				if (baseComponent.initialized) {
					baseComponent.initialized = false;
					baseComponent.onDestroy();
				}
			}
			return true;
		}
	};

}
