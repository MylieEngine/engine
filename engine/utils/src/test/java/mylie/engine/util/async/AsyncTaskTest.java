package mylie.engine.util.async;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AsyncTaskTest {
	private void setupScheduler(Scheduler scheduler) {
		scheduler.register(Cache.ONE_FRAME);
	}

	@ParameterizedTest
	@MethodSource("mylie.engine.util.async.AsyncTestSetup#schedulerProvider")
	void testCacheNotRegistered(Scheduler scheduler) {
		setupScheduler(scheduler);
		List<String> list = new TestList();
		TestOrderTask taskA = new TestOrderTask("A", scheduler, list);
		TestOrderTask taskB = new TestOrderTask("B", scheduler, list);
		TestOrderTask taskC = new TestOrderTask("C", scheduler, list);

		taskC.dependencies().add(taskA);
		taskA.dependencies().add(taskB);
		taskC.dependencies().add(taskB);
		taskC.execute().result();
		Assertions.assertTrue(list.indexOf("B") < list.indexOf("A"));
		Assertions.assertTrue(list.indexOf("C") > list.indexOf("B"));
		Assertions.assertTrue(list.indexOf("A") < list.indexOf("C"));
		Assertions.assertEquals(3, list.size());
	}

	private static class TestList extends CopyOnWriteArrayList<String> implements Serializable {
		@Override
		public int hashCode() {
			return 1;
		}

	}

	private static class TestOrderTask extends Task<Boolean> {
		private final List<String> list;
		private final Scheduler scheduler;
		protected TestOrderTask(String id, Scheduler scheduler, List<String> list) {
			super(id, Mode.ASYNC, Target.BACKGROUND, Cache.ONE_FRAME);
			this.list = list;
			this.scheduler = scheduler;
		}

		@Override
		protected Result<Boolean> onExecute() {
			return Async.async(scheduler, mode(), target(), cache(), -1, ADD_STRING_FUNCTION, id(), list);
		}
	}

	private static final Functions.Two<String, List<String>, Boolean> ADD_STRING_FUNCTION = new Functions.Two<>(
			"ADD_STRING_FUNCTION") {
		@Override
		protected Boolean execute(String param0, List<String> param1) {
			param1.add(param0);
			return true;
		}

	};
}
