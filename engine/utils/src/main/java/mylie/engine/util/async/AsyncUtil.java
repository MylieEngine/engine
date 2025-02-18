package mylie.engine.util.async;

import java.util.function.Supplier;

public interface AsyncUtil {
	static <R> void checkedExecute(Supplier<R> function, Result<R> result) {
		try {
			result.future().complete(function.get());
		} catch (Exception e) {
			result.future().completeExceptionally(e);
		}
	}
}
