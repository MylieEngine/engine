package mylie.engine.util.async;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a logical execution context target that allows binding and unbinding
 * to a thread-local storage for managing the current execution target. Targets
 * can be either bindable or non-bindable.
 */
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public final class Target {
	/**
	 * A predefined non-bindable target representing background execution.
	 */
	public static final Target BACKGROUND = new Target("Background", false);

	private static final ThreadLocal<Target> THREAD_LOCAL_TARGET = new ThreadLocal<>();
	@Getter
	private final String id;
	private final boolean bindable;
	private boolean bound;

	/**
	 * Constructor for creating a target with the specified name and bindable property.
	 *
	 * @param id     the name of the target, used for identification.
	 * @param bindable whether this target can be bound.
	 */
	private Target(String id, boolean bindable) {
		this.id = id;
		this.bindable = bindable;
		bound = false;
	}

	/**
	 * Constructor for creating a bindable target with the specified name.
	 *
	 * @param id the name of the target, used for identification.
	 */
	public Target(String id) {
		this(id, true);
	}

	/**
	 * Binds the target to the current thread. Only bindable targets can be bound,
	 * and a target cannot be bound more than once.
	 *
	 * @throws IllegalStateException if the target is not bindable or is already bound.
	 */
	public void bind() {
		if (!bindable) {
			throw new IllegalStateException("Target is not bindable");
		}
		if (bound) {
			throw new IllegalStateException("Target is already bound");
		}
		bound = true;
		THREAD_LOCAL_TARGET.set(this);
	}

	/**
	 * Unbinds the target from the current thread. The target must be bound before
	 * it can be unbound.
	 *
	 * @throws IllegalStateException if the target is not currently bound.
	 */
	public void unbind() {
		if (!bound) {
			throw new IllegalStateException("Target is not bound");
		}
		bound = false;
		THREAD_LOCAL_TARGET.remove();
	}

	/**
	 * Returns a string representation of this target, which is its unique identifier.
	 *
	 * @return the unique identifier of this target as a {@code String}.
	 */
	@Override
	public String toString() {
		return id;
	}

	/**
	 * Checks if the target is the current execution target for the thread.
	 *
	 * @return {@code true} if this target is the current target for the thread,
	 * {@code false} otherwise.
	 */
	public boolean isCurrent() {
		return THREAD_LOCAL_TARGET.get() == this;
	}
}
