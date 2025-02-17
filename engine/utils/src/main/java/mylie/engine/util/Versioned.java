package mylie.engine.util;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class Versioned<T> {
	private T value;
	private long version = 0;

	public Versioned(T value) {
		this.value = value;
	}

	public Versioned() {
		this(null);
	}

	public void value(T value, long version) {
		assert version > this.version : "Version cannot be lower than current version";
		this.value = value;
		this.version = version;
	}

	public void value(T value) {
		this.value = value;
		this.version++;
	}

    public Ref<T> ref() {
        return new Ref<>(this);
    }

	@Getter
	public static class Ref<T> {
		@Getter(AccessLevel.NONE)
		private final Versioned<T> versioned;
		private T value;
		private long version;

		public Ref(Versioned<T> versioned) {
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
