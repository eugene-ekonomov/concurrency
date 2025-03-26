package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference<>(null, false);

    public boolean propose(Bid bid) {
        while (true) {
            var mark = latestBid.isMarked();
            if (mark) {
                return false;
            }
            var oldBid = latestBid.getReference();

            if (oldBid == null || bid.getPrice() > oldBid.getPrice()) {
                if (latestBid.compareAndSet(oldBid, bid, false, false)) {
                    notifier.sendOutdatedMessage(oldBid);
                    return true;
                }
            } else {
                return false;
            }
        }

    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid latest;
        do {
            latest = latestBid.getReference();
        } while(!latestBid.attemptMark(latest, true));
        return latest;
    }
}
