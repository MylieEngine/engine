package mylie.engine.util;

import lombok.Getter;

/**
 * Represents a versioned container that holds a value of type {@code T}.
 * The container associates a version number with the value and provides
 * methods to update the value and its version, ensuring version consistency.
 *
 * @param <T> the type of the value held by the container
 */
@Getter
public final class VersionedValue<T> implements Versioned<T> {
	private T value;
	private long version = 0;

	/**
	 * Constructs a new {@code Versioned} container with the specified initial value.
	 *
	 * @param value the initial value
	 */
	public VersionedValue(T value) {
		this.value = value;
	}

	/**
	 * Constructs a new {@code Versioned} container with a {@code null} initial value.
	 */
	public VersionedValue() {
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
	@Override
	public Ref<T> ref() {
		return new Ref<>(this);
	}
}
