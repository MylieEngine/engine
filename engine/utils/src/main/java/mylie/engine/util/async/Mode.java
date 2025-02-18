package mylie.engine.util.async;

/**
 * Defines the execution mode for tasks in the {@code mylie.engine.util.async} package.
 * <p>
 * - {@link #DIRECT} executes tasks immediately in the caller's thread.
 * - {@link #ASYNC} executes tasks asynchronously in a separate thread.
 */
public enum Mode {

	/**
	 * Represents a mode where tasks are executed immediately on the calling thread.
	 */
	DIRECT,

	/**
	 * Represents a mode where tasks are executed asynchronously, typically on a different thread.
	 */
	ASYNC
}
