package course.concurrency.m2_async.minPrice;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    private final Executor executor = Executors.newFixedThreadPool(128);

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10L, 45L, 66L, 345L, 234L, 333L, 67L, 123L, 768L);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {

        var prices = shopIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, id), executor)
                        .exceptionally(e -> 0.0d))
                .toList();
        try {
            CompletableFuture.allOf(prices.toArray(new CompletableFuture[0])).get(2900, TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {
        }

        return prices.stream()
                .filter(CompletableFuture::isDone)
                .map(f -> f.getNow(0.0d))
                .filter(d -> d > 0.0d)
                .min(Double::compareTo)
                .orElse(Double.NaN);
    }
}
