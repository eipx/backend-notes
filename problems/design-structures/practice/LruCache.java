/**
 * LRU Cache
 * ref: LC 146
 *
 * Implement class LRUCache(capacity): get(key) returns the value stored for
 * key and marks it as most recently used, or returns -1 if key is not
 * present; put(key, value) inserts a new key or updates an existing key's
 * value and marks it as most recently used, evicting the least recently
 * used entry first if adding a new key would exceed capacity. Both get and
 * put must run in average O(1) time.
 *
 * Note: this file's filesystem is case-insensitive, so a class named
 * "LRUCache" and a public class named "LruCache" cannot exist as separate
 * .class files side by side - the fill-in methods below and the test runner
 * therefore live in one class. get/put below (plus the constructor) are
 * exactly the methods to implement.
 *
 * study page: ../lru-cache.md
 * run: javac --release 8 LruCache.java && java LruCache
 */
import java.util.*;

public class LruCache {

    public LruCache(int capacity) {
        // TODO: implement
    }

    public int get(int key) {
        // TODO: implement
        return -1;
    }

    public void put(int key, int value) {
        // TODO: implement
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int[] counters = new int[2]; // {passed, total}

    static void runCase(int caseNum, String label, int capacity, String[] ops, int[][] args, Integer[] expected) {
        counters[1]++;
        try {
            LruCache cache = new LruCache(capacity);
            for (int i = 0; i < ops.length; i++) {
                if (ops[i].equals("put")) {
                    cache.put(args[i][0], args[i][1]);
                } else {
                    int got = cache.get(args[i][0]);
                    int exp = expected[i];
                    if (got != exp) {
                        System.out.println("FAIL case " + caseNum + " (" + label + ") step " + i + ": expected " + exp + " got " + got);
                        return;
                    }
                }
            }
            counters[0]++;
            System.out.println("PASS case " + caseNum + " (" + label + ")");
        } catch (Exception e) {
            System.out.println("FAIL case " + caseNum + " (" + label + "): threw " + e);
        }
    }

    public static void main(String[] args) {
        // case 1: classic example
        runCase(1, "classic example", 2,
                new String[]{"put", "put", "get", "put", "get", "put", "get", "get", "get"},
                new int[][]{{1, 1}, {2, 2}, {1, 0}, {3, 3}, {2, 0}, {4, 4}, {1, 0}, {3, 0}, {4, 0}},
                new Integer[]{null, null, 1, null, -1, null, -1, 3, 4});

        // case 2: capacity 1 - every put evicts the previous single entry
        runCase(2, "capacity 1", 1,
                new String[]{"put", "get", "put", "get", "get"},
                new int[][]{{1, 10}, {1, 0}, {2, 20}, {1, 0}, {2, 0}},
                new Integer[]{null, 10, null, -1, 20});

        // case 3: get on a key that was never inserted
        runCase(3, "get missing key", 2,
                new String[]{"get", "put", "get"},
                new int[][]{{5, 0}, {5, 50}, {5, 0}},
                new Integer[]{-1, null, 50});

        // case 4: put on an existing key must refresh recency, changing who gets evicted
        runCase(4, "put on existing key refreshes recency", 2,
                new String[]{"put", "put", "put", "put", "get", "get"},
                new int[][]{{1, 1}, {2, 2}, {1, 100}, {3, 3}, {2, 0}, {1, 0}},
                new Integer[]{null, null, null, null, -1, 100});

        // case 5: reading a key also refreshes recency (get counts as an access)
        runCase(5, "get refreshes recency", 2,
                new String[]{"put", "put", "get", "put", "get", "get"},
                new int[][]{{1, 1}, {2, 2}, {1, 0}, {3, 3}, {2, 0}, {1, 0}},
                new Integer[]{null, null, 1, null, -1, 1});

        // case 6: overwriting the same key repeatedly should not grow the cache
        runCase(6, "repeated overwrite same key", 1,
                new String[]{"put", "put", "put", "get"},
                new int[][]{{7, 1}, {7, 2}, {7, 3}, {7, 0}},
                new Integer[]{null, null, null, 3});

        System.out.println(counters[0] + "/" + counters[1] + " passed");
        if (counters[0] != counters[1]) System.exit(1);
    }
}
