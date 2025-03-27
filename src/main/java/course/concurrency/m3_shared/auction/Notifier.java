package course.concurrency.m3_shared.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {
    ExecutorService pool = Executors.newSingleThreadExecutor();

    public void sendOutdatedMessage(Bid bid) {
        imitateSending();
    }

    private void imitateSending() {
        // don't remove this delay, deal with it properly
        pool.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

        });
    }

    public void shutdown() {
        pool.shutdown();
    }
}
