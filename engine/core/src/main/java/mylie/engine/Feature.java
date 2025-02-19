package mylie.engine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Feature {

	enum Status {
		DEPRECATED, STABLE, RELEASE_CANDIDATE, EXPERIMENTAL

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	@interface State {
		Status status() default Status.STABLE;
		String since() default "";
	}

	record Level(boolean deprecated, Status level) {
		public Level(boolean deprecated, Status level) {
			this.deprecated = deprecated;
			this.level = level;
			if (level == Status.DEPRECATED) {
				throw new IllegalArgumentException("Deprecated cannot be used as a level");
			}
		}
	}

	static boolean isAllowed(Level level, Class<?> type) {
		State annotation = type.getAnnotation(State.class);
		return annotation == null || isAllowed(level, annotation);
	}

	static boolean isAllowed(Level level, State state) {
		if (state.status() == Status.DEPRECATED) {
			return level.deprecated();
		} else {
			return level.level().ordinal() >= state.status().ordinal();
		}
	}
}
