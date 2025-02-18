package mylie.engine.util;

import lombok.AccessLevel;
import lombok.Getter;

public interface Versioned<T> {
	long version();
	T value();
	Ref<T> ref();
	@Getter
	final class Ref<T> {
		@Getter(AccessLevel.NONE)
		private final Versioned<T> versioned;
		private T value;
		private long version;

		Ref(Versioned<T> versioned) {
			this.versioned = versioned;
			this.value = versioned.value();
			this.version = versioned.version();
		}

		private void updateValue() {
			if (version < versioned.version()) {
				version = versioned.version();
				value = versioned.value();
			}
		}

		public T value(boolean update) {
			if (update) {
				updateValue();
			}
			return value;
		}

		public boolean requiresUpdate() {
			return version < versioned.version();
		}
	}
}
