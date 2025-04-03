package mylie.engine.util;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class Arguments {
	private final Map<String, String> data = new HashMap<>();

	public void fromString(String arguments) {
		String key = null;
		String value = "";
		for (String argument : arguments.split(" ")) {
			if (key == null) {
				if (argument.startsWith("-")) {
					key = argument.substring(1);
					value = "";
				} else {
					log.warn("Argument {} does not start with -", argument);
					throw new IllegalArgumentException("Argument " + argument + " does not start with -");
				}
			} else {
				if (argument.startsWith("-")) {
					data.put(key, value);
					key = argument.substring(1);
				} else {
					value = argument;
					data.put(key, value);
					key = null;
				}
				value = "";
			}
		}
		if (key != null) {
			data.put(key, value);
		}
	}

	public void fromArray(String[] arguments) {
		if (arguments == null || arguments.length == 0) {
			return;
		}
		fromString(String.join(" ", arguments));
	}

	public boolean isSet(String argument) {
		return data.containsKey(argument);
	}

	public <T> boolean isSet(Typed<T> argument) {
		return isSet(argument.id);
	}

	public String value(String argument) {
		if (isSet(argument)) {
			return data.get(argument);
		}
		log.warn("Argument {} is not set", argument);
		throw new IllegalArgumentException("Argument " + argument + " is not set");
	}

	public <T> T value(Typed<T> argument) {
		if (isSet(argument.id)) {
			try {
				return argument.parse(value(argument.id));
			} catch (Exception e) {
				log.error("Failed to parse typed argument {}", argument.id, e);
				throw new IllegalStateException("Failed to parse typed argument " + argument.id, e);
			}
		}
		log.warn("Typed argument {} is not set", argument.id);
		throw new IllegalArgumentException("Typed argument " + argument.id + " is not set");
	}

	public abstract static class Typed<T> {
		final String id;

		protected Typed(String id) {
			this.id = id;
		}

		protected abstract T parse(String value);

		public static <T extends Enum<T>> Typed<T> ofEnum(Class<T> enumClass, String id) {
			return new Typed<>(id) {
				@Override
				protected T parse(String value) {
					try {
						T t = Enum.valueOf(enumClass, value);
						return t;
					} catch (Exception e) {
						log.error("Failed to parse enum value {}", value, e);
						throw e;
					}
				}
			};
		}
	}
}
