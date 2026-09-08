/**
 * Insert Delete GetRandom O(1)
 * ref: LC 380
 *
 * Implement class RandomizedSet: insert(val) adds val to the set if it is
 * not already present and returns whether it was added; remove(val) removes
 * val if present and returns whether it was removed; getRandom() returns a
 * uniformly random element currently in the set. insert, remove, and
 * getRandom must each run in average O(1) time.
 *
 * study page: ../insert-delete-getrandom-o1.md
 * run: javac --release 8 InsertDeleteGetRandomO1.java && java InsertDeleteGetRandomO1
 */
import java.util.*;

class RandomizedSet {

    public RandomizedSet() {
        // TODO: implement
    }

    public boolean insert(int val) {
        // TODO: implement
        return false;
    }

    public boolean remove(int val) {
        // TODO: implement
        return false;
    }

    public int getRandom() {
        // TODO: implement
        return 0;
    }
}

public class InsertDeleteGetRandomO1 {

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

    static void fail(int caseNum, String label, Exception e) {
        total++;
        System.out.println("FAIL case " + caseNum + " (" + label + "): threw " + e);
    }

    public static void main(String[] args) {
        // case 1-7: classic example - insert, remove, insert, getRandom membership
        RandomizedSet set1 = new RandomizedSet();
        try {
            check(1, "insert 1 first time", set1.insert(1), true);
        } catch (Exception e) { fail(1, "insert 1 first time", e); }

        try {
            check(2, "remove 2 not present", set1.remove(2), false);
        } catch (Exception e) { fail(2, "remove 2 not present", e); }

        try {
            check(3, "insert 2 first time", set1.insert(2), true);
        } catch (Exception e) { fail(3, "insert 2 first time", e); }

        try {
            int r1 = set1.getRandom();
            total++;
            if (r1 == 1 || r1 == 2) {
                passed++;
                System.out.println("PASS case 4 (getRandom returns a member)");
            } else {
                System.out.println("FAIL case 4 (getRandom returns a member): got " + r1);
            }
        } catch (Exception e) { fail(4, "getRandom returns a member", e); }

        try {
            check(5, "remove 1 present", set1.remove(1), true);
        } catch (Exception e) { fail(5, "remove 1 present", e); }

        try {
            check(6, "insert 2 duplicate", set1.insert(2), false);
        } catch (Exception e) { fail(6, "insert 2 duplicate", e); }

        try {
            total++;
            if (set1.getRandom() == 2) {
                passed++;
                System.out.println("PASS case 7 (only member left is 2)");
            } else {
                System.out.println("FAIL case 7 (only member left is 2)");
            }
        } catch (Exception e) { fail(7, "only member left is 2", e); }

        // case 8-9: remove the last element, leaving the set empty-safe for re-insert
        RandomizedSet set2 = new RandomizedSet();
        try {
            set2.insert(10);
            check(8, "remove only element", set2.remove(10), true);
        } catch (Exception e) { fail(8, "remove only element", e); }

        try {
            check(9, "insert after emptied", set2.insert(20), true);
        } catch (Exception e) { fail(9, "insert after emptied", e); }

        // case 10-11: remove the element that happens to already be last in the backing list
        RandomizedSet set3 = new RandomizedSet();
        try {
            set3.insert(1);
            set3.insert(2);
            set3.insert(3); // backing list is [1,2,3]; 3 is already last
            check(10, "remove element already at end", set3.remove(3), true);
        } catch (Exception e) { fail(10, "remove element already at end", e); }

        try {
            int r2 = set3.getRandom();
            total++;
            if (r2 == 1 || r2 == 2) {
                passed++;
                System.out.println("PASS case 11 (getRandom after removing tail element)");
            } else {
                System.out.println("FAIL case 11 (getRandom after removing tail element): got " + r2);
            }
        } catch (Exception e) { fail(11, "getRandom after removing tail element", e); }

        // case 12: distribution sanity check over many calls - every inserted value must appear
        RandomizedSet set4 = new RandomizedSet();
        try {
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
        } catch (Exception e) { fail(12, "getRandom distribution covers all members over 3000 calls", e); }

        System.out.println(passed + "/" + total + " passed");
        if (passed != total) System.exit(1);
    }
}
