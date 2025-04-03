package mylie.engine.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import mylie.engine.util.async.Async;
import mylie.engine.util.async.Result;

public class ComponentManager {
	@Getter(AccessLevel.PACKAGE)
	private final List<Component> components;
	public ComponentManager() {
		this.components = new CopyOnWriteArrayList<>();
	}

	public void addComponent(Component component) {
		components.add(component);
		component.onAdd(this);
	}

	public void addComponents(Component... components) {
		for (Component component : components) {
			addComponent(component);
		}
	}

	public void removeComponent(Component component) {
		components.remove(component);
		component.onRemoval();
	}

	public void removeComponent(Class<? extends Component> componentClass) {
		for (Component component : components) {
			if (componentClass.isInstance(component)) {
				removeComponent(component);
			}
		}
	}

	public <T extends Component> T component(Class<T> componentClass) {
		for (Component component : components) {
			if (componentClass.isInstance(component)) {
				return componentClass.cast(component);
			}
		}
		return null;
	}

	public void update() {
		List<Result<?>> results = new ArrayList<>(components.size());
		for (Component component : components) {
			results.add(component.update());
		}
		Async.await(results);
	}

	public void shutdown() {
		components.forEach(Component::onRemoval);
		components.clear();
	}
}
