// Kth Largest Element
// ref: LC 215
// Given an unsorted array and an integer k, return the kth largest value
// (k = 1 means the maximum); duplicates count by position, not distinct value.
// Required: O(n log k) time, O(k) space.
// Study page: ../kth-largest-element.md
// Run: javac --release 8 KthLargestElement.java && java KthLargestElement

import java.util.*;

class Solution {
    public int findKthLargest(int[] nums, int k) {
        // TODO: implement
        return -1;
    }
}

public class KthLargestElement {

    private static void check(int caseNum, int[] nums, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().findKthLargest(nums, k);
            if (Integer.compare(got, expected) == 0) {
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

        check(1, new int[]{3, 2, 1, 5, 6, 4}, 2, 5, fail, total);
        check(2, new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4, 4, fail, total);
        check(3, new int[]{1}, 1, 1, fail, total);
        check(4, new int[]{7, 6, 5, 4, 3, 2, 1}, 1, 7, fail, total);
        check(5, new int[]{7, 6, 5, 4, 3, 2, 1}, 7, 1, fail, total);
        check(6, new int[]{2, 2, 2, 2}, 2, 2, fail, total);
        check(7, new int[]{-1, -2, -3, -4}, 2, -2, fail, total);
        check(8, new int[]{5, 5, 5, 1, 1}, 3, 5, fail, total);
        check(9, new int[]{1, 2}, 2, 1, fail, total);
        check(10, new int[]{9, 3, 2, 4, 8}, 3, 4, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
