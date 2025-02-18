package mylie.engine.util.async;

final class Hash {
	final Functions.Base function;
	final Object[] args;
	final int value;
	Hash(Functions.Base function, Object... args) {
		this.function = function;
		this.args = args;
		int tmp = function.hashCode();
		for (Object arg : this.args) {
			if (arg instanceof Custom custom) {
				tmp = tmp * 31 + custom.value();
			} else {
				tmp = tmp * 31 + arg.hashCode();
			}
		}
		this.value = tmp;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public int hashCode() {
		return value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Hash hash) {
			return hash.value == value;
		}
		return false;
	}

	public interface Custom {
		int value();
	}
}
