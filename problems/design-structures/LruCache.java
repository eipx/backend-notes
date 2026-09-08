import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LC 146 - two implementations of a fixed-capacity cache that evicts the
 * least recently used entry once it is full:
 *   1) LRUCacheLinkedHashMap - relies on LinkedHashMap's built-in access-order mode
 *   2) LRUCacheManual        - HashMap + doubly linked list with two sentinel nodes
 */
public class LruCache {

    interface ICache {
        int get(int key);
        void put(int key, int value);
    }

    // ---------------------------------------------------------------
    // Version A: LinkedHashMap with accessOrder = true
    // ---------------------------------------------------------------
    static class LRUCacheLinkedHashMap implements ICache {
        private final int capacity;
        private final LinkedHashMap<Integer, Integer> map;

        LRUCacheLinkedHashMap(int capacity) {
            this.capacity = capacity;
            // The 3-arg constructor (initialCapacity, loadFactor, accessOrder) is required.
            // accessOrder = true reorders the internal linked list on every get() AND put()
            // that touches an existing key, so the head is always least-recently-used.
            // The anonymous-subclass diamond below is NOT allowed pre Java 9, so the
            // generic types are spelled out on both sides.
            this.map = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    // This method is protected and is invoked BY the map itself, right
                    // after a put(), never by our own code.
                    return size() > capacity;
                }
            };
        }

        @Override
        public int get(int key) {
            Integer value = map.get(key); // get() also counts as an access, reorders the list
            return value == null ? -1 : value;
        }

        @Override
        public void put(int key, int value) {
            // Putting an existing key updates its value AND moves it to most-recently-used,
            // because accessOrder = true makes put() on an existing key an access too.
            map.put(key, value);
        }
    }

    // ---------------------------------------------------------------
    // Version B: hand-rolled HashMap + doubly linked list, two sentinels
    // ---------------------------------------------------------------
    static class LRUCacheManual implements ICache {
        private static class Node {
            int key, value;
            Node prev, next;
            Node(int key, int value) { this.key = key; this.value = value; }
        }

        private final int capacity;
        private final HashMap<Integer, Node> map;
        private final Node head; // sentinel; head.next is the most-recently-used real node
        private final Node tail; // sentinel; tail.prev is the least-recently-used real node

        LRUCacheManual(int capacity) {
            this.capacity = capacity;
            this.map = new HashMap<Integer, Node>();
            head = new Node(-1, -1);
            tail = new Node(-1, -1);
            head.next = tail;
            tail.prev = head;
        }

        private void unlink(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void insertAtFront(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        @Override
        public int get(int key) {
            Node node = map.get(key);
            if (node == null) return -1;
            unlink(node);
            insertAtFront(node);
            return node.value;
        }

        @Override
        public void put(int key, int value) {
            Node existing = map.get(key);
            if (existing != null) {
                existing.value = value; // put on an existing key must refresh recency, not just value
                unlink(existing);
                insertAtFront(existing);
                return;
            }
            if (map.size() == capacity) {
                Node lru = tail.prev;
                unlink(lru);
                map.remove(lru.key);
            }
            Node fresh = new Node(key, value);
            map.put(key, fresh);
            insertAtFront(fresh);
        }
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int[] counters = new int[2]; // {passed, total}

    static void runCase(int caseNum, String label, int capacity, String[] ops, int[][] args, Integer[] expected) {
        runOnImpl(caseNum, label + " [LinkedHashMap]", ops, args, expected, new LRUCacheLinkedHashMap(capacity));
        runOnImpl(caseNum, label + " [Manual DLL]", ops, args, expected, new LRUCacheManual(capacity));
    }

    static void runOnImpl(int caseNum, String label, String[] ops, int[][] args, Integer[] expected, ICache cache) {
        counters[1]++;
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
