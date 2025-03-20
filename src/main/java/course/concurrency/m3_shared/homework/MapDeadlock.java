package course.concurrency.m3_shared.homework;

import java.util.concurrent.ConcurrentHashMap;

public class MapDeadlock {
    public static void main(String[] args) {
        var map = new ConcurrentHashMap<String, Integer>();
        var count = map.compute("key", (k, oldValue) -> {
            return map.putIfAbsent(k, 0) + 1;
        });
    }
}
