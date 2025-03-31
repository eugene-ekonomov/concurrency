package course.concurrency.m4_shared_advanced.concurrent_blocking_queue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrentBlockingQueueTest {

    @Test
    void singleThreadTest() throws InterruptedException {
        var queue = new ConcurrentBlockingQueue<>(3);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);
        queue.enqueue(5);
        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertEquals(0, queue.getSize());
    }

    @Test
    void concurrencyTest() throws InterruptedException {
        var maxSize = 1000000;
        var threadsCount = 10;
        var queue = new ConcurrentBlockingQueue<Integer>(maxSize);
        try (var executor = Executors.newFixedThreadPool(threadsCount);
             var executor2 = Executors.newFixedThreadPool(threadsCount)) {
            for (int i = 0; i < maxSize + 1000; i++) {
                int finalI = i;
                executor.submit(() -> queue.enqueue(finalI));
            }

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
            assertEquals(maxSize, queue.getSize());

            for (int i = 0; i < maxSize - 1; i++) {
                executor2.submit(() -> {
                    try {
                        queue.dequeue();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            executor2.shutdown();
            executor2.awaitTermination(10, TimeUnit.SECONDS);
        }
        assertEquals(1, queue.getSize());
        queue.dequeue();
        assertEquals(0, queue.getSize());
    }

    @Test
    void blockingTest() throws InterruptedException {
        var maxSize = 1000;
        var queue = new ConcurrentBlockingQueue<Integer>(maxSize);
        try (var executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> {
                try {
                    var result = queue.dequeue();
                    assertEquals(1, result);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            assertEquals(0, queue.getSize());
            for (int i = 1; i <= maxSize; i++) {
                int finalI = i;
                executor.submit(() -> queue.enqueue(finalI));
            }
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
        assertEquals(999, queue.getSize());
    }

    @Test
    void simultaneouslyTest() throws InterruptedException {
        var maxSize = 1000000;
        var queue = new ConcurrentBlockingQueue<Integer>(maxSize);
        try (var executor = Executors.newFixedThreadPool(8);
             var executor2 = Executors.newFixedThreadPool(8)
        ) {
            for (int i = 0; i < maxSize; i++) {
                executor.submit(() -> {
                    try {
                        queue.dequeue();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            for (int i = 0; i < maxSize; i++) {
                int finalI = i;
                executor2.submit(() -> queue.enqueue(finalI));
            }
            Thread.sleep(2000);
            executor.shutdown();
            executor2.shutdown();
        }
        assertEquals(0, queue.getSize());
    }
}