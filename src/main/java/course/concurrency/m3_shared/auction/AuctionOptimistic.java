package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBid = new AtomicReference<>();

    public boolean propose(Bid bid) {

        var oldBid = latestBid.getAndUpdate(currentBid -> {
            if (currentBid == null || bid.getPrice() > currentBid.getPrice()) {
                return bid;
            }
            return currentBid;
        });
        var changed = bid == latestBid.get();
        if (changed) {
            notifier.sendOutdatedMessage(oldBid);
        }
        return changed;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
