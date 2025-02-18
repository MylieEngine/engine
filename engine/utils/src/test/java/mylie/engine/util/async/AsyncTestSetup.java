package mylie.engine.util.async;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class AsyncTestSetup {
    static Stream<Scheduler> schedulerProvider() {
        return Stream.of(new SingleThreadedScheduler());
    }

    final static Functions.Zero<Boolean> THROW_EXCEPTION=new Functions.Zero<>("ThrowException") {
        @Override
        protected Boolean execute() {
            throw new RuntimeException("Test exception");
        }
    };

    final static Functions.Zero<Boolean> WAIT_100_MS=new Functions.Zero<>("Wait100ms") {
        @Override
        protected Boolean execute() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    };

    final static Functions.One<Collection<String>,Boolean> WRITE_THREAD_NAME=new Functions.One<>("WriteThreadName") {
        @Override
        protected Boolean execute(Collection<String> param0) {
            param0.add(Thread.currentThread().getName());
            return true;
        }
    };

    final static Functions.One<AtomicInteger,Boolean> ATOMIC_INTEGER_INCREASE=new Functions.One<AtomicInteger, Boolean>("AtomicInteger::incrementAndGet") {
        @Override
        protected Boolean execute(AtomicInteger param0) {
            param0.incrementAndGet();
            return true;
        }
    };

    final static Functions.Two<Integer,Integer,Integer> SUM_INTEGER=new Functions.Two<>("SumInteger") {
        @Override
        protected Integer execute(Integer param0, Integer param1) {
            return param0+param1;
        }
    };

    final static Functions.Three<Integer,Integer,Integer,Integer> MUL_ADD_INTEGER=new Functions.Three<>("MulAddInteger") {
        @Override
        protected Integer execute(Integer param0, Integer param1, Integer param2) {
            return param0*param1+param2;
        }
    };


}
