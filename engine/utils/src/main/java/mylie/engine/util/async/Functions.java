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
	protected abstract static class Zero<R> extends Base {
		public Zero(String id) {
			super(id);
		}

		protected abstract R execute();
	}

	protected abstract static class One<P0, R> extends Base {
		public One(String id) {
			super(id);
		}

		protected abstract R execute(P0 param0);
	}

	protected abstract static class Two<P0, P1, R> extends Base {
		public Two(String id) {
			super(id);
		}
		protected abstract R execute(P0 param0, P1 param1);
	}

	protected abstract static class Three<P0, P1, P2, R> extends Base {
		public Three(String id) {
			super(id);
		}
		protected abstract R execute(P0 param0, P1 param1, P2 param2);
	}
}
