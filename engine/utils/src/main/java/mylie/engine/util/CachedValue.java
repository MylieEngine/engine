package mylie.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Getter;

@Getter
public final class CachedValue<T> implements Versioned<T> {
	private final List<Ref<?>> dependencies = new ArrayList<>();
	private final Supplier<T> function;
	T value;
	long version = 0;

	public CachedValue(Supplier<T> function, Ref<?>... dependencies) {
		if (function == null) {
			throw new IllegalArgumentException("Function cannot be null");
		}
		this.function = function;
		for (Ref<?> dependency : dependencies) {
			Objects.requireNonNull(dependency);
			this.dependencies.add(dependency);
		}
		value = function.get();
	}

	public long version() {
		value();
		return version;
	}

	@Override
	public T value() {
		boolean requiresUpdate = false;
		for (Ref<?> dependency : dependencies) {
			if (dependency.requiresUpdate()) {
				requiresUpdate = true;
				break;
			}
		}
		if (requiresUpdate) {
			for (Ref<?> dependency : dependencies()) {
				dependency.value(true);
			}
			value = function.get();
			version++;
		}
		return value;
	}

	@Override
	public Ref<T> ref() {
		return new Ref<>(this);
	}
}
