package mylie.engine.util;

import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Classes {
	private Classes() {}
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			log.error("Failed to instantiate class: {}", clazz.getName(), e);
			throw new IllegalStateException("Failed to instantiate class: " + clazz.getName(), e);
		}
	}
}
