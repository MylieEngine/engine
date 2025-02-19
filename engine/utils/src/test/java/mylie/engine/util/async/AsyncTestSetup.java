package mylie.engine.util.async;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;

class AsyncTestSetup {
	private AsyncTestSetup() {
	}

	static Stream<Scheduler> schedulerProvider() {
		return Stream.of(new SingleThreadedScheduler(),
				new MultiThreadedScheduler.ExecutorBased(ForkJoinPool.commonPool()),
				new MultiThreadedScheduler.ExecutorBased(Executors.newVirtualThreadPerTaskExecutor()),
				new MultiThreadedScheduler.ExecutorBased(Executors.newCachedThreadPool()),
				new MultiThreadedScheduler.ExecutorBased(Executors.newSingleThreadExecutor()),
				new MultiThreadedScheduler.ExecutorBased(Executors.newFixedThreadPool(4)));
	}

	static final Functions.Zero<Boolean> THROW_EXCEPTION = new Functions.Zero<>("ThrowException") {
		@Override
		protected Boolean execute() {
			throw new RuntimeException("Test exception");
		}
	};

	static final Functions.Zero<Boolean> WAIT_100_MS = new Functions.Zero<>("Wait100ms") {
		@Override
		protected Boolean execute() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			return true;
		}
	};

	static final Functions.One<Collection<String>, Boolean> WRITE_THREAD_NAME = new Functions.One<>("WriteThreadName") {
		@Override
		protected Boolean execute(Collection<String> param0) {
			param0.add(Thread.currentThread().getName());
			return true;
		}
	};

	static final Functions.One<AtomicInteger, Boolean> ATOMIC_INTEGER_INCREASE = new Functions.One<>(
			"AtomicInteger::incrementAndGet") {
		@Override
		protected Boolean execute(AtomicInteger param0) {
			param0.incrementAndGet();
			return true;
		}
	};

	static final Functions.Two<Integer, Integer, Integer> SUM_INTEGER = new Functions.Two<>("SumInteger") {
		@Override
		protected Integer execute(Integer param0, Integer param1) {
			return param0 + param1;
		}
	};

	static final Functions.Three<Integer, Integer, Integer, Integer> MUL_ADD_INTEGER = new Functions.Three<>(
			"MulAddInteger") {
		@Override
		protected Integer execute(Integer param0, Integer param1, Integer param2) {
			return param0 * param1 + param2;
		}
	};

	static final Functions.One<CustomHashObject, Boolean> CUSTOM_HASH_OBJECT = new Functions.One<>("CustomHashObject") {
		@Override
		protected Boolean execute(CustomHashObject param0) {
			Assertions.assertEquals(param0.value, param0.hashCode());
			return param0.value == param0.hashCode();
		}
	};

	static class CustomHashObject implements Hash.Custom {
		private final int value;
		public CustomHashObject(int value) {
			this.value = value;
		}
		@Override
		public int hashCode() {
			return value;
		}

		@Override
		public int value() {
			return value;
		}
	}

}
