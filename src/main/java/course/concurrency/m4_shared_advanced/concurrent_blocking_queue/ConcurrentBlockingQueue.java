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
        if (maxSize > queue.size()) {
            synchronized (queue) {
                if (maxSize > queue.size()) {
                    queue.add(value);
                }
                queue.notifyAll();
            }
        }
    }

    public T dequeue() throws InterruptedException {
        synchronized (queue) {
            while (true) {
                if (queue.isEmpty()) {
                    queue.wait();
                } else {
                    return queue.removeFirst();
                }
            }
        }
    }

    public int getSize() {
        synchronized (queue) {
            return queue.size();
        }
    }
}
