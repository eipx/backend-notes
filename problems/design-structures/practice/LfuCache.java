/**
 * LFU Cache
 * ref: LC 460
 *
 * Implement class LFUCache(capacity): get(key) returns the value for key and
 * bumps its use frequency, or returns -1 if key is absent; put(key, value)
 * inserts a new key or updates an existing key's value and bumps its
 * frequency, evicting the least frequently used key (ties broken by least
 * recently used) if adding a new key would exceed capacity. Both get and put
 * must run in average O(1) time.
 *
 * Note: this file's filesystem is case-insensitive, so a class named
 * "LFUCache" and a public class named "LfuCache" cannot exist as separate
 * .class files side by side - the fill-in methods below and the test runner
 * therefore live in one class. get/put below (plus the constructor) are
 * exactly the methods to implement.
 *
 * study page: ../lfu-cache.md
 * run: javac --release 8 LfuCache.java && java LfuCache
 */
import java.util.*;

public class LfuCache {

    public LfuCache(int capacity) {
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
            LfuCache cache = new LfuCache(capacity);
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
        // case 1: classic example - tie-break by recency inside the same frequency
        runCase(1, "classic example", 2,
                new String[]{"put", "put", "get", "put", "get", "get", "put", "get", "get", "get"},
                new int[][]{{1, 1}, {2, 2}, {1, 0}, {3, 3}, {2, 0}, {3, 0}, {4, 4}, {1, 0}, {3, 0}, {4, 0}},
                new Integer[]{null, null, 1, null, -1, 3, null, -1, 3, 4});

        // case 2: capacity 0 - nothing is ever stored
        runCase(2, "capacity zero", 0,
                new String[]{"put", "get"},
                new int[][]{{1, 1}, {1, 0}},
                new Integer[]{null, -1});

        // case 3: capacity 1 - every put replaces the only slot
        runCase(3, "capacity one", 1,
                new String[]{"put", "put", "get", "put", "get"},
                new int[][]{{1, 1}, {2, 2}, {1, 0}, {3, 3}, {2, 0}},
                new Integer[]{null, null, -1, null, -1});

        // case 4: get on a missing key returns -1 without changing any frequency
        runCase(4, "get missing key", 2,
                new String[]{"get", "put", "get"},
                new int[][]{{9, 0}, {9, 90}, {9, 0}},
                new Integer[]{-1, null, 90});

        // case 5: put on an existing key both updates the value and bumps its frequency
        runCase(5, "put existing key bumps frequency", 2,
                new String[]{"put", "put", "put", "put", "get", "get"},
                new int[][]{{1, 1}, {2, 2}, {1, 100}, {3, 3}, {2, 0}, {1, 0}},
                new Integer[]{null, null, null, null, -1, 100});

        // case 6: minFreq must climb when the last key at the current minFreq is evicted or promoted
        runCase(6, "minFreq climbs after bucket empties", 2,
                new String[]{"put", "put", "get", "get", "put", "get"},
                new int[][]{{1, 1}, {2, 2}, {1, 0}, {2, 0}, {3, 3}, {1, 0}},
                new Integer[]{null, null, 1, 2, null, -1});

        System.out.println(counters[0] + "/" + counters[1] + " passed");
        if (counters[0] != counters[1]) System.exit(1);
    }
}
