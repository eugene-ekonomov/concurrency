package course.concurrency.m4_shared_advanced.concurrent_blocking_queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentBlockingQueueAdditionalTests {

    @Test
    @DisplayName("Elements should be ordered correctly for one thread")
    public void elementsShouldBeOrderedOneThread() throws InterruptedException {
        var count = 5;
        ConcurrentBlockingQueue<Integer> queue = new ConcurrentBlockingQueue<>(count);

        for (int i = 0; i < count; i++) {
            queue.enqueue(i);
        }

        for (int i = 0; i < count; i++) {
            var res = queue.dequeue();
            assertEquals(i, res);
        }
    }

    @Test
    @DisplayName("All initial elements should be retrieved")
    public void elementsShouldBeRetrieved() throws InterruptedException {
        var count = 500;
        var queue = new ConcurrentBlockingQueue<Integer>(count);

        var latch = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(count*2);

        // enqueue
        for (int i = 0; i < count; i++) {
            final var element = i;
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                queue.enqueue(element);
            });
        }
        // dequeue
        var resultQueue = new ConcurrentLinkedQueue<Integer>();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Integer res;
                try {
                    res = queue.dequeue();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                resultQueue.add(res);
            });
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }

    @Test
    @DisplayName("Full queue should block incoming requests")
    public void shouldBlockOnPut() throws InterruptedException {
        var count = 100;
        var capacity = 2;
        var queue = new ConcurrentBlockingQueue<Integer>(capacity);

        var poolSize = capacity*3;
        var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

        // enqueue
        for (int i = 0; i < count; i++) {
            final var element = i;
            executor.submit(() -> queue.enqueue(element));
        }

        assertEquals(capacity, queue.getSize());
        assertEquals(count, executor.getTaskCount());
        // only {capacity} tasks are done, others are blocked
        assertEquals(capacity, executor.getCompletedTaskCount());

        // check if everything works as expected after blocking

        // dequeue
        var resultQueue = new ConcurrentLinkedQueue<Integer>();
        for (int i = 0; i < count; i++) {
            var res = queue.dequeue();
            resultQueue.add(res);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }

    @Test
    @DisplayName("Empty queue should block on dequeue")
    public void shouldBlockOnEmpty() throws InterruptedException {
        var count = 100;
        var capacity = 2;
        var queue = new ConcurrentBlockingQueue<Integer>(capacity);

        var poolSize = capacity*3;
        var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

        // dequeue
        var resultQueue = new ConcurrentLinkedQueue<Integer>();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                Integer res;
                try {
                    res = queue.dequeue();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                resultQueue.add(res);
            });
        }

        assertEquals(0, queue.getSize());
        assertEquals(0, executor.getCompletedTaskCount());
        assertEquals(count, executor.getTaskCount());

        // check if everything works as expected after blocking

        // enqueue
        for (int i = 0; i < count; i++) {
            queue.enqueue(i);
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }
}