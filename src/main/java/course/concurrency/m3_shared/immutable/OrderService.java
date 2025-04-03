package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {

    private final Map<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(0);

    public long createOrder(List<Item> items) {
        var id = nextId.getAndIncrement();
        var order = new Order(items, id);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        var order = currentOrders.compute(orderId, (id, orderInner) -> orderInner.withPaymentInfo(paymentInfo));
        if (order.checkStatus()) {
            deliver(order);
        }
    }

    public void setPacked(long orderId) {
        var order = currentOrders.compute(orderId, (id, orderInner) -> orderInner.withPacked(true));
        if (order.checkStatus()) {
            deliver(order);
        }
    }

    private void deliver(Order order) {
        if (order.getStatus().equals(Order.Status.DELIVERED)) {
            return;
        }
        currentOrders.compute(order.getId(), (id, orderr) -> orderr.withStatus(Order.Status.DELIVERED));
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
