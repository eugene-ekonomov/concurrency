package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference<>(null, false);

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
        latestBid.attemptMark(latestBid.getReference(), true);
        return latestBid.getReference();
    }
}
