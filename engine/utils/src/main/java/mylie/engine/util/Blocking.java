package mylie.engine.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public final class Blocking {
	private Blocking() {
		throw new IllegalStateException("Utility class");
	}
	public static <T> T poll(BlockingQueue<T> queue, long timeout, TimeUnit unit) {
		try {
			return queue.poll(timeout, unit);
		} catch (InterruptedException e) {
			log.warn("Interrupted while polling queue", e);
			throw new IllegalStateException("Interrupted while polling queue", e);
		}
	}
}
