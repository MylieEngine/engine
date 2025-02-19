package mylie.engine.util.async;

import lombok.AccessLevel;
import lombok.Getter;

public class Functions {
	private Functions() {
	}
	@Getter(AccessLevel.PACKAGE)
	abstract static class Base {
		private final String id;
		Base(String id) {
			this.id = id;
		}
	}
	public abstract static class Zero<R> extends Base {
		protected Zero(String id) {
			super(id);
		}

		protected abstract R execute();
	}

	public abstract static class One<P0, R> extends Base {
		protected One(String id) {
			super(id);
		}

		protected abstract R execute(P0 param0);
	}

	public abstract static class Two<P0, P1, R> extends Base {
		protected Two(String id) {
			super(id);
		}
		protected abstract R execute(P0 param0, P1 param1);
	}

	public abstract static class Three<P0, P1, P2, R> extends Base {
		protected Three(String id) {
			super(id);
		}
		protected abstract R execute(P0 param0, P1 param1, P2 param2);
	}
}
