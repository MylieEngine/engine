package mylie.engine.util;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Represents a versioned container that holds a value of type {@code T}.
 * The container associates a version number with the value and provides
 * methods to update the value and its version, ensuring version consistency.
 *
 * @param <T> the type of the value held by the container
 */
@Getter
public final class Versioned<T> {
	private T value;
	private long version = 0;

	/**
	 * Constructs a new {@code Versioned} container with the specified initial value.
	 *
	 * @param value the initial value
	 */
	public Versioned(T value) {
		this.value = value;
	}

	/**
	 * Constructs a new {@code Versioned} container with a {@code null} initial value.
	 */
	public Versioned() {
		this(null);
	}

	/**
	 * Updates the value and the version of the container.
	 *
	 * @param value   the new value
	 * @param version the new version number, which must be greater than the current version
	 * @throws IllegalArgumentException if the specified version is less than or equal to the current version
	 */
	public void value(T value, long version) {
		if (version <= this.version) {
			throw new IllegalArgumentException("Version cannot be lower than current version");
		}
		this.value = value;
		this.version = version;
	}

	/**
	 * Updates the value and increments the version by one.
	 *
	 * @param value the new value to update
	 */
	public void value(T value) {
		this.value = value;
		this.version++;
	}

	/**
	 * Creates a reference to the current versioned container.
	 *
	 * @return a new {@code Ref} object that references this container
	 */
	public Ref<T> ref() {
		return new Ref<>(this);
	}

	/**
	 * Represents a reference to a {@code Versioned} container. The reference
	 * tracks the current state of the associated {@code Versioned} instance
	 * and can be updated on demand to reflect the latest value and version.
	 *
	 * @param <T> the type of the value held by the referenced {@code Versioned} container
	 */
	@Getter
	public static final class Ref<T> {
		@Getter(AccessLevel.NONE)
		private final Versioned<T> versioned;
		private T value;
		private long version;

		/**
		 * Constructs a new reference to the specified {@code Versioned} container.
		 *
		 * @param versioned the {@code Versioned} container to reference
		 */
		private Ref(Versioned<T> versioned) {
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

		/**
		 * Retrieves the current value held by the reference.
		 * If {@code update} is {@code true}, the reference will be updated
		 * with the latest value and version from the associated {@code Versioned} container.
		 *
		 * @param update a flag indicating whether the reference should be updated
		 * @return the current value held by the reference
		 */
		public T value(boolean update) {
			if (update) {
				updateValue();
			}
			return value;
		}

		/**
		 * Checks whether the reference requires an update. This occurs if the version
		 * of the reference is less than the version in the associated {@code Versioned} container.
		 *
		 * @return {@code true} if the reference needs an update, {@code false} otherwise
		 */
		public boolean requiresUpdate() {
			return version < versioned.version();
		}
	}
}
