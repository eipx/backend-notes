// Find K Closest Elements
// ref: LC 658
// Given a sorted integer array arr, an integer k and an integer x, return the
// k integers in arr closest to x, in ascending order.
// Required: O(log(n - k) + k) time, O(k) output.
// Study page: ../find-k-closest-elements.md
// Run: javac --release 8 FindKClosestElements.java && java FindKClosestElements

import java.util.*;

class Solution {
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        // TODO: implement
        return new ArrayList<Integer>();
    }
}

public class FindKClosestElements {

    private static void check(int caseNum, int[] arr, int k, int x, List<Integer> expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            List<Integer> got = new Solution().findClosestElements(arr, k, x);
            if (got.equals(expected)) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1,2,3,4,5}, 4, 3, Arrays.asList(1,2,3,4), fail, total);
        check(2, new int[]{1,2,3,4,5}, 2, 3, Arrays.asList(2,3), fail, total);
        check(3, new int[]{3,4,5,6,7}, 3, 0, Arrays.asList(3,4,5), fail, total);
        check(4, new int[]{3,4,5,6,7}, 3, 20, Arrays.asList(5,6,7), fail, total);
        check(5, new int[]{1,2,3,4,5}, 5, 3, Arrays.asList(1,2,3,4,5), fail, total);
        check(6, new int[]{1,3}, 1, 2, Arrays.asList(1), fail, total);
        check(7, new int[]{1,1,1,10,10,10}, 3, 1, Arrays.asList(1,1,1), fail, total);
        check(8, new int[]{1,1,2,2,3,3}, 2, 2, Arrays.asList(2,2), fail, total);
        check(9, new int[]{1,4,6,8,9}, 3, 5, Arrays.asList(4,6,8), fail, total);
        check(10, new int[]{-4,-3,-2,-1,0,1,2,3,4}, 5, 0, Arrays.asList(-2,-1,0,1,2), fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
