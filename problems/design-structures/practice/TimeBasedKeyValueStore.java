/**
 * Time Based Key-Value Store
 * ref: LC 981
 *
 * Implement class TimeMap: set(key, value, timestamp) stores a version of
 * key at the given timestamp (timestamps for a given key arrive in
 * non-decreasing order); get(key, timestamp) returns the value stored at the
 * largest recorded timestamp that is less than or equal to the requested
 * timestamp, or "" if no such version exists. Both set and get should run in
 * O(log n) time or better per call.
 *
 * study page: ../time-based-key-value-store.md
 * run: javac --release 8 TimeBasedKeyValueStore.java && java TimeBasedKeyValueStore
 */
import java.util.*;

class TimeMap {

    public TimeMap() {
        // TODO: implement
    }

    public void set(String key, String value, int timestamp) {
        // TODO: implement
    }

    public String get(String key, int timestamp) {
        // TODO: implement
        return "";
    }
}

public class TimeBasedKeyValueStore {

    static int[] counters = new int[2]; // {passed, total}

    static void runCase(int caseNum, String label, String[] ops, Object[][] args, String[] expected) {
        counters[1]++;
        try {
            TimeMap map = new TimeMap();
            for (int i = 0; i < ops.length; i++) {
                if (ops[i].equals("set")) {
                    map.set((String) args[i][0], (String) args[i][1], (Integer) args[i][2]);
                } else {
                    String got = map.get((String) args[i][0], (Integer) args[i][1]);
                    String exp = expected[i];
                    if (!got.equals(exp)) {
                        System.out.println("FAIL case " + caseNum + " (" + label + ") step " + i + ": expected \"" + exp + "\" got \"" + got + "\"");
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
        // case 1: classic example, one set then two gets straddling the timestamp
        runCase(1, "classic single version",
                new String[]{"set", "get", "get"},
                new Object[][]{{"foo", "bar", 1}, {"foo", 1}, {"foo", 2}},
                new String[]{null, "bar", "bar"});

        // case 2: two versions, get exactly on a stored timestamp returns that exact version
        runCase(2, "two versions exact match",
                new String[]{"set", "set", "get", "get", "get"},
                new Object[][]{{"foo", "bar", 1}, {"foo", "bar2", 4}, {"foo", 4}, {"foo", 5}, {"foo", 3}},
                new String[]{null, null, "bar2", "bar2", "bar"});

        // case 3: get before any set timestamp returns ""
        runCase(3, "get before first timestamp",
                new String[]{"set", "get"},
                new Object[][]{{"k", "v", 10}, {"k", 5}},
                new String[]{null, ""});

        // case 4: get on a key that was never set at all
        runCase(4, "get on unknown key",
                new String[]{"set", "get"},
                new Object[][]{{"k", "v", 1}, {"other", 1}},
                new String[]{null, ""});

        // case 5: many versions, binary search must land on the correct floor
        runCase(5, "many versions floor search",
                new String[]{"set", "set", "set", "set", "get", "get", "get", "get"},
                new Object[][]{{"k", "v1", 1}, {"k", "v2", 3}, {"k", "v3", 5}, {"k", "v4", 7},
                        {"k", 0}, {"k", 4}, {"k", 6}, {"k", 100}},
                new String[]{null, null, null, null, "", "v2", "v3", "v4"});

        // case 6: two different keys never interfere with each other
        runCase(6, "independent keys",
                new String[]{"set", "set", "get", "get"},
                new Object[][]{{"a", "va", 1}, {"b", "vb", 1}, {"a", 5}, {"b", 5}},
                new String[]{null, null, "va", "vb"});

        System.out.println(counters[0] + "/" + counters[1] + " passed");
        if (counters[0] != counters[1]) System.exit(1);
    }
}
