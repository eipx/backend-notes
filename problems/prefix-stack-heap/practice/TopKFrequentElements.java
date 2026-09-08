// Top K Frequent Elements
// ref: LC 347
// Given an array of integers and an integer k, return the k distinct values
// that occur most often. Order of the returned values does not matter.
// Required: O(n log k) time, O(n) space.
// Study page: ../top-k-frequent-elements.md
// Run: javac --release 8 TopKFrequentElements.java && java TopKFrequentElements

import java.util.*;

class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        // TODO: implement
        return null;
    }
}

public class TopKFrequentElements {

    // Order-independent comparison: convert both arrays to sorted sets, since the problem
    // guarantees a unique answer set of distinct values for every test case below.
    private static boolean sameSet(int[] a, int[] b) {
        TreeSet<Integer> setA = new TreeSet<Integer>();
        for (int x : a) setA.add(x);
        TreeSet<Integer> setB = new TreeSet<Integer>();
        for (int x : b) setB.add(x);
        return setA.equals(setB) && a.length == b.length;
    }

    private static void check(int caseNum, int[] nums, int k, int[] expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int[] got = new Solution().topKFrequent(nums, k);
            if (sameSet(got, expected)) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + Arrays.toString(expected) + " got " + Arrays.toString(got));
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + Arrays.toString(expected) + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1, 1, 1, 2, 2, 3}, 2, new int[]{1, 2}, fail, total);
        check(2, new int[]{1}, 1, new int[]{1}, fail, total);
        check(3, new int[]{4, 4, 4, 4, 5, 5, 6}, 1, new int[]{4}, fail, total);
        check(4, new int[]{7, 7, 8, 8, 8, 9, 9, 9, 9}, 2, new int[]{9, 8}, fail, total);
        check(5, new int[]{-1, -1, -2, -3, -3, -3}, 1, new int[]{-3}, fail, total);
        check(6, new int[]{5, 5, 5, 5, 5}, 1, new int[]{5}, fail, total);
        check(7, new int[]{1, 2, 2, 3, 3, 3, 4, 4, 4, 4}, 3, new int[]{4, 3, 2}, fail, total);
        check(8, new int[]{10, 20, 20, 30, 30, 30, 40, 40, 40, 40, 50}, 3, new int[]{40, 30, 20}, fail, total);
        check(9, new int[]{0, 0, 0, 1}, 2, new int[]{0, 1}, fail, total);
        check(10, new int[]{3, 3, 1, 1, 1, 2, 2, 2, 2}, 2, new int[]{2, 1}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
