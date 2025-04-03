package mylie.engine.util.async;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class Cache {
	public static final Cache NO = new NullCache();
	public static final Cache ONE_FRAME = new InvalidateEachStep();
	public static final Cache FOREVER = new DoNotInvalidate();
	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.PROTECTED)
	private Cache parent;

	abstract void progress();

	abstract void clear();

	abstract void remove(Hash hash);

	abstract <R> void result(Result<R> result);

	abstract <R> Result<R> result(Hash hash, long version);

	abstract Cache createInstance();

	static final class NullCache extends Cache {
		@Override
		void progress() {
			// NoOp intentional
		}

		@Override
		void clear() {
			// NoOp intentional
		}

		@Override
		void remove(Hash hash) {
			// NoOp intentional
		}

		@Override
		<R> void result(Result<R> result) {
			// NoOp intentional
		}

		@Override
		<R> Result<R> result(Hash hash, long version) {
			return null;
		}

		@Override
		Cache createInstance() {
			return new NullCache();
		}

		@Override
		public String toString() {
			return "NoOpCache";
		}
	}

	/**
	 * Helper base class for caches that store data.
	 */
	static abstract class AbstractDataCache extends Cache {
		protected final Map<Hash, Result<?>> data = new HashMap<>();

		@Override
		void clear() {
			data.clear();
		}

		@Override
		void remove(Hash hash) {
			data.remove(hash);
		}

		@SuppressWarnings("unchecked")
		@Override
		<R> Result<R> result(Hash hash, long version) {
			Result<R> result = (Result<R>) data.get(hash);
			if (result == null && parent() != null) {
				return parent().result(hash, version);
			}
			return result;
		}

		@Override
		<R> void result(Result<R> result) {
			data.put(result.hash(), result);
			if (parent() != null) {
				parent().result(result);
			}
		}
	}

	static final class InvalidateEachStep extends AbstractDataCache {
		@Override
		void progress() {
			Cache parentCache = parent();
			if (parentCache != null) {
				for (Hash hash : data.keySet()) {
					parentCache.remove(hash);
				}
			}
			clear();
		}

		@Override
		Cache createInstance() {
			return new InvalidateEachStep();
		}
	}

	static final class DoNotInvalidate extends AbstractDataCache {
		@Override
		void progress() {
			// Nothing to do on progress
		}

		@Override
		Cache createInstance() {
			return new DoNotInvalidate();
		}
	}
}
