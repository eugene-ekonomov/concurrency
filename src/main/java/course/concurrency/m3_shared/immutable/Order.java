package course.concurrency.m3_shared.immutable;

import java.util.Collections;
import java.util.List;

import static course.concurrency.m3_shared.immutable.Order.Status.NEW;

public final class Order {

    public enum Status {NEW, IN_PROGRESS, DELIVERED}

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public Order(List<Item> items, Long id) {
        this.items = List.copyOf(items);
        this.status = NEW;
        this.id = id;
        this.paymentInfo = null;
        this.isPacked = false;
    }

    private Order(List<Item> items, Long id, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.items = Collections.unmodifiableList(items);
        this.id = id;
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public boolean checkStatus() {
        return items != null && !items.isEmpty() && paymentInfo != null && isPacked;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        return new Order(this.items, this.id, paymentInfo, this.isPacked, Status.IN_PROGRESS);
    }

    public boolean isPacked() {
        return isPacked;
    }

    public Order withPacked(boolean packed) {
        return new Order(this.items, this.id, this.paymentInfo, packed, Status.IN_PROGRESS);
    }

    public Status getStatus() {
        return status;
    }

    public Order withStatus(Status status) {
        return new Order(this.items, this.id, this.paymentInfo, this.isPacked, status);
    }
}
