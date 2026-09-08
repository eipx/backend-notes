import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * LC 981 - a key/value store where each key can hold many (timestamp, value) versions.
 * get(key, timestamp) must return the value stored at the largest recorded timestamp
 * that is less than or equal to the requested timestamp, or "" if none qualifies.
 * Two implementations: a TreeMap-per-key version, and an ArrayList + binary search version.
 */
public class TimeBasedKeyValueStore {

    interface ITimeMap {
        void set(String key, String value, int timestamp);
        String get(String key, int timestamp);
    }

    // ---------------------------------------------------------------
    // Version A: HashMap<String, TreeMap<Integer,String>> + floorEntry
    // ---------------------------------------------------------------
    static class TimeMapTreeMap implements ITimeMap {
        // The variable type must be TreeMap/NavigableMap, not plain Map,
        // because floorEntry/floorKey are not on the Map interface.
        private final HashMap<String, TreeMap<Integer, String>> store;

        TimeMapTreeMap() {
            store = new HashMap<String, TreeMap<Integer, String>>();
        }

        @Override
        public void set(String key, String value, int timestamp) {
            TreeMap<Integer, String> versions = store.get(key);
            if (versions == null) {
                versions = new TreeMap<Integer, String>();
                store.put(key, versions);
            }
            versions.put(timestamp, value); // TreeMap keeps sorted order regardless of insertion order
        }

        @Override
        public String get(String key, int timestamp) {
            NavigableMap<Integer, String> versions = store.get(key);
            if (versions == null) return "";
            Map.Entry<Integer, String> floor = versions.floorEntry(timestamp); // largest key <= timestamp
            return floor == null ? "" : floor.getValue();
        }
    }

    // ---------------------------------------------------------------
    // Version B: HashMap<String, parallel ArrayLists> + manual binary search
    // Assumes timestamps for a given key arrive in non-decreasing order (as LC 981
    // guarantees); see .md for what breaks if that assumption does not hold.
    // ---------------------------------------------------------------
    static class TimeMapBinarySearch implements ITimeMap {
        private final HashMap<String, List<Integer>> keyToTimes;
        private final HashMap<String, List<String>> keyToValues;

        TimeMapBinarySearch() {
            keyToTimes = new HashMap<String, List<Integer>>();
            keyToValues = new HashMap<String, List<String>>();
        }

        @Override
        public void set(String key, String value, int timestamp) {
            List<Integer> times = keyToTimes.get(key);
            List<String> values = keyToValues.get(key);
            if (times == null) {
                times = new ArrayList<Integer>();
                values = new ArrayList<String>();
                keyToTimes.put(key, times);
                keyToValues.put(key, values);
            }
            times.add(timestamp);
            values.add(value);
        }

        @Override
        public String get(String key, int timestamp) {
            List<Integer> times = keyToTimes.get(key);
            if (times == null || times.isEmpty()) return "";
            List<String> values = keyToValues.get(key);
            int lo = 0, hi = times.size() - 1, resultIdx = -1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (times.get(mid) <= timestamp) {
                    resultIdx = mid; // possible match found, keep searching to the right for a closer one
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            return resultIdx == -1 ? "" : values.get(resultIdx);
        }
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int[] counters = new int[2];

    static void runCase(int caseNum, String label, String[] ops, Object[][] args, String[] expected) {
        runOnImpl(caseNum, label + " [TreeMap]", ops, args, expected, new TimeMapTreeMap());
        runOnImpl(caseNum, label + " [BinarySearch]", ops, args, expected, new TimeMapBinarySearch());
    }

    static void runOnImpl(int caseNum, String label, String[] ops, Object[][] args, String[] expected, ITimeMap map) {
        counters[1]++;
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
