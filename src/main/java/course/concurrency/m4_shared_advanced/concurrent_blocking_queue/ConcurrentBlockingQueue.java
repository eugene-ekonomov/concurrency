package course.concurrency.m4_shared_advanced.concurrent_blocking_queue;

import java.util.LinkedList;
import java.util.List;

public class ConcurrentBlockingQueue<T> {

    private final int maxSize;
    private final List<T> queue;

    public ConcurrentBlockingQueue(int maxSize) {
        this.maxSize = maxSize;
        queue = new LinkedList<>();
    }

    public void enqueue(T value) {
        synchronized (queue) {
            while (maxSize == queue.size()) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.add(value);
            queue.notifyAll();
        }

    }

    public T dequeue() throws InterruptedException {
        synchronized (queue) {
            while (true) {
                if (queue.isEmpty()) {
                    queue.wait();
                } else {
                    var value = queue.removeFirst();
                    queue.notifyAll();
                    return value;
                }
            }
        }
    }

    public int getSize() {
        synchronized (queue) {
            queue.notifyAll();
            return queue.size();
        }
    }
}
