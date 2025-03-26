package course.concurrency.m3_shared.homework;

import java.util.concurrent.ConcurrentHashMap;

public class MapDeadlock {
    public static void main(String[] args) throws InterruptedException {
        var map = new ConcurrentHashMap<String, Integer>();
        Thread t1 = new Thread(() ->
                map.computeIfAbsent("A", (key) -> {
                    sleep();
                    return map.computeIfAbsent("B", key2 -> 1);
                }));
        Thread t2 = new Thread(() ->
                map.computeIfAbsent("B", (key2) -> {
                    sleep();
                    return map.computeIfAbsent("A", key -> 1);
                }));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}