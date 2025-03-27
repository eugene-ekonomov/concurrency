package course.concurrency.m3_shared.testing;

import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExperiments {

    // Don't change this class
    public static class Counter {
        private volatile int counter = 0;

        public void increment() {
            counter++;
        }

        public int get() {
            return counter;
        }
    }

    @RepeatedTest(10)
    public void counterShouldFail() throws InterruptedException {
        int iterations = 1000000;
        int threads = 2;
        Counter counter = new Counter();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < iterations; i++) {
            executor.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                counter.increment();
            });
        }
        executor.awaitTermination(500, TimeUnit.MILLISECONDS);
        executor.shutdown();
        assertEquals(iterations, counter.get());
    }
}
