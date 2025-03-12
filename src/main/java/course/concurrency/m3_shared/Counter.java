package course.concurrency.m3_shared;

import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
    private static AtomicInteger counter = new AtomicInteger(1);

    @SneakyThrows
    public static void first() {
        synchronized (Counter.class) {
            while (true) {
                if (counter.get() == 1) {
                    System.out.println("1");
                    counter.set(2);
                }
                Counter.class.notifyAll();
                Counter.class.wait(500);
            }
        }
    }

    @SneakyThrows
    public static void second() {
        synchronized (Counter.class) {
            while (true) {
                if (counter.get() == 2) {
                    System.out.println("2");
                    counter.set(3);
                }
                Counter.class.notifyAll();
                Counter.class.wait(500);
            }
        }
    }

    @SneakyThrows
    public static void third() {
        synchronized (Counter.class) {
            while (true) {
                if (counter.get() == 3) {
                    System.out.println("3");
                    counter.set(1);
                }
                Counter.class.notifyAll();
                Counter.class.wait(500);
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> first());
        Thread t2 = new Thread(() -> second());
        Thread t3 = new Thread(() -> third());
        t1.start();
        t2.start();
        t3.start();
    }
}
