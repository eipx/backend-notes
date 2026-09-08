import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * LC 460 - fixed-capacity cache that evicts the Least Frequently Used key.
 * On a tie in frequency, evicts the Least Recently Used among the tied keys.
 * get() and put() both run in O(1) average time.
 */
public class LfuCache {

    static class LFUCache {
        private final int capacity;
        private int minFreq;
        private final HashMap<Integer, Integer> keyToVal;
        private final HashMap<Integer, Integer> keyToFreq;
        // LinkedHashSet preserves insertion order, so within one frequency bucket the
        // first element is the least recently used and is the eviction target.
        private final HashMap<Integer, LinkedHashSet<Integer>> freqToKeys;

        LFUCache(int capacity) {
            this.capacity = capacity;
            this.minFreq = 0;
            this.keyToVal = new HashMap<Integer, Integer>();
            this.keyToFreq = new HashMap<Integer, Integer>();
            this.freqToKeys = new HashMap<Integer, LinkedHashSet<Integer>>();
        }

        private void bumpFreq(int key) {
            int freq = keyToFreq.get(key);
            LinkedHashSet<Integer> bucket = freqToKeys.get(freq);
            bucket.remove(key);
            if (bucket.isEmpty()) {
                freqToKeys.remove(freq);
                if (freq == minFreq) minFreq++; // this freq bucket is gone; minFreq must move up
            }
            keyToFreq.put(key, freq + 1);
            LinkedHashSet<Integer> nextBucket = freqToKeys.get(freq + 1);
            if (nextBucket == null) {
                nextBucket = new LinkedHashSet<Integer>();
                freqToKeys.put(freq + 1, nextBucket);
            }
            nextBucket.add(key); // re-inserted at the tail -> most recently used within freq+1
        }

        int get(int key) {
            if (!keyToVal.containsKey(key)) return -1;
            bumpFreq(key);
            return keyToVal.get(key);
        }

        void put(int key, int value) {
            if (capacity <= 0) return;
            if (keyToVal.containsKey(key)) {
                keyToVal.put(key, value);
                bumpFreq(key);
                return;
            }
            if (keyToVal.size() == capacity) {
                LinkedHashSet<Integer> bucket = freqToKeys.get(minFreq);
                int evictKey = bucket.iterator().next(); // oldest entry in the lowest-freq bucket
                bucket.remove(evictKey);
                if (bucket.isEmpty()) freqToKeys.remove(minFreq);
                keyToVal.remove(evictKey);
                keyToFreq.remove(evictKey);
            }
            keyToVal.put(key, value);
            keyToFreq.put(key, 1);
            LinkedHashSet<Integer> bucket = freqToKeys.get(1);
            if (bucket == null) {
                bucket = new LinkedHashSet<Integer>();
                freqToKeys.put(1, bucket);
            }
            bucket.add(key);
            minFreq = 1; // a brand new key always starts at frequency 1
        }
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int[] counters = new int[2];

    static void runCase(int caseNum, String label, int capacity, String[] ops, int[][] args, Integer[] expected) {
        counters[1]++;
        LFUCache cache = new LFUCache(capacity);
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
