package course.concurrency.m6_streams;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 1L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque() {
                    @Override
                    public Object take() throws InterruptedException {
                        return super.takeLast();
                    }
                });
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(8, 8, 1, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.DiscardPolicy());
    }
}