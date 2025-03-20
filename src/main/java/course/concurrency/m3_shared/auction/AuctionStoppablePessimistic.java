package course.concurrency.m3_shared.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;
    private volatile boolean stopped;

    private Object lock = new Object();

    public boolean propose(Bid bid) {
        if (stopped) {
            return false;
        }
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

    public Bid stopAuction() {
        synchronized (lock) {
            stopped = true;
            return latestBid;
        }
    }
}
