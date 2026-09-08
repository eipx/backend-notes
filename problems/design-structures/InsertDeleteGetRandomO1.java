import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * LC 380 - a set that supports insert, remove, and getRandom, each in average O(1) time,
 * with getRandom returning each currently-present value with equal probability.
 */
public class InsertDeleteGetRandomO1 {

    static class RandomizedSet {
        private final ArrayList<Integer> values;
        private final HashMap<Integer, Integer> valueToIndex; // value -> its index in "values"
        private final Random random;

        RandomizedSet() {
            values = new ArrayList<Integer>();
            valueToIndex = new HashMap<Integer, Integer>();
            random = new Random();
        }

        boolean insert(int val) {
            if (valueToIndex.containsKey(val)) return false;
            valueToIndex.put(val, values.size());
            values.add(val);
            return true;
        }

        boolean remove(int val) {
            Integer idx = valueToIndex.get(val);
            if (idx == null) return false;
            int lastIdx = values.size() - 1;
            int lastVal = values.get(lastIdx);
            // Move the last element into the hole left by val, then shrink from the end.
            // This keeps removal O(1) because ArrayList never has to shift elements.
            values.set(idx, lastVal);
            valueToIndex.put(lastVal, idx); // safe even when lastVal == val, see .md pitfalls
            values.remove(lastIdx); // ArrayList.remove(int index), NOT remove(Object)
            valueToIndex.remove(val);
            return true;
        }

        int getRandom() {
            int idx = random.nextInt(values.size());
            return values.get(idx);
        }
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int passed = 0;
    static int total = 0;

    static void check(int caseNum, String label, boolean got, boolean expected) {
        total++;
        if (got == expected) {
            passed++;
            System.out.println("PASS case " + caseNum + " (" + label + ")");
        } else {
            System.out.println("FAIL case " + caseNum + " (" + label + "): expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        // case 1: classic example - insert, remove, insert, getRandom membership
        RandomizedSet set1 = new RandomizedSet();
        check(1, "insert 1 first time", set1.insert(1), true);
        check(2, "remove 2 not present", set1.remove(2), false);
        check(3, "insert 2 first time", set1.insert(2), true);
        int r1 = set1.getRandom();
        total++;
        if (r1 == 1 || r1 == 2) { passed++; System.out.println("PASS case 4 (getRandom returns a member)"); }
        else System.out.println("FAIL case 4 (getRandom returns a member): got " + r1);
        check(5, "remove 1 present", set1.remove(1), true);
        check(6, "insert 2 duplicate", set1.insert(2), false);
        total++;
        if (set1.getRandom() == 2) { passed++; System.out.println("PASS case 7 (only member left is 2)"); }
        else System.out.println("FAIL case 7 (only member left is 2)");

        // case 8: remove the last element, leaving the set empty-safe for re-insert
        RandomizedSet set2 = new RandomizedSet();
        set2.insert(10);
        check(8, "remove only element", set2.remove(10), true);
        check(9, "insert after emptied", set2.insert(20), true);

        // case 10: remove the element that happens to already be last in the backing list
        RandomizedSet set3 = new RandomizedSet();
        set3.insert(1);
        set3.insert(2);
        set3.insert(3); // backing list is [1,2,3]; 3 is already last
        check(10, "remove element already at end", set3.remove(3), true);
        total++;
        int r2 = set3.getRandom();
        if (r2 == 1 || r2 == 2) { passed++; System.out.println("PASS case 11 (getRandom after removing tail element)"); }
        else System.out.println("FAIL case 11 (getRandom after removing tail element): got " + r2);

        // case 12: distribution sanity check over many calls - every inserted value must appear
        RandomizedSet set4 = new RandomizedSet();
        set4.insert(100);
        set4.insert(200);
        set4.insert(300);
        HashSet<Integer> seen = new HashSet<Integer>();
        for (int i = 0; i < 3000; i++) seen.add(set4.getRandom());
        total++;
        if (seen.size() == 3 && seen.contains(100) && seen.contains(200) && seen.contains(300)) {
            passed++;
            System.out.println("PASS case 12 (getRandom distribution covers all members over 3000 calls)");
        } else {
            System.out.println("FAIL case 12 (getRandom distribution covers all members over 3000 calls): saw " + seen);
        }

        System.out.println(passed + "/" + total + " passed");
        if (passed != total) System.exit(1);
    }
}
