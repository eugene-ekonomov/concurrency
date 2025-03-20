package course.concurrency.m3_shared.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    private Object lock = new Object();

    public boolean propose(Bid bid) {
        synchronized (lock) {
            try {
                if (latestBid == null) {
                    latestBid = bid;
                    return true;
                } else if (bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
                return false;
            } finally {
                lock.notifyAll();
            }
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
