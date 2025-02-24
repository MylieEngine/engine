package mylie.engine.util;

import java.lang.reflect.Constructor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BlockingTest {

	@Test
	void testCreateInstance() {
		Constructor<?> declaredConstructor = Blocking.class.getDeclaredConstructors()[0];
		declaredConstructor.setAccessible(true);
		Exception e = Assertions.assertThrows(Exception.class, declaredConstructor::newInstance);
		Assertions.assertEquals(IllegalStateException.class, e.getCause().getClass());
		Assertions.assertEquals("Utility class", e.getCause().getMessage());
	}

	@Test
	void testBlockingInterrupt() {
		Thread thread = Thread.currentThread();
		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			thread.interrupt();
		}).start();
		LinkedBlockingQueue<Object> objects = new LinkedBlockingQueue<>();
		Assertions.assertThrows(IllegalStateException.class, () -> Blocking.poll(objects, 1, TimeUnit.SECONDS));
	}

}
