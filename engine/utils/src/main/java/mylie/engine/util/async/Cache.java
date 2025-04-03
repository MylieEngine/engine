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

	static final class InvalidateEachStep extends Cache {
		private final Map<Hash, Result<?>> data = new HashMap<>();
		@Override
		void progress() {
			Cache parentCache = parent();
			if (parentCache != null) {
				for (Hash hash : data.keySet()) {
					parentCache.remove(hash);
				}
			}
			data.clear();
		}

		@Override
		void clear() {
			data.clear();
		}

		@Override
		void remove(Hash hash) {
			data.remove(hash);
		}

		@Override
		<R> void result(Result<R> result) {
			data.put(result.hash(), result);
			if (parent() != null) {
				parent().result(result);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		<R> Result<R> result(Hash hash, long version) {
			Result<R> result = (Result<R>) data.get(hash);
			if (result == null && parent() != null) {
				result = parent().result(hash, version);
			}
			return result;
		}

		@Override
		Cache createInstance() {
			return new InvalidateEachStep();
		}
	}

	static final class DoNotInvalidate extends Cache {
		private final Map<Hash, Result<?>> data = new HashMap<>();
		@Override
		void progress() {

		}

		@Override
		void clear() {
			data.clear();
		}

		@Override
		void remove(Hash hash) {
			data.remove(hash);
		}

		@Override
		<R> void result(Result<R> result) {
			data.put(result.hash(), result);
			if (parent() != null) {
				parent().result(result);
			}
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
		Cache createInstance() {
			return new DoNotInvalidate();
		}
	}
}
