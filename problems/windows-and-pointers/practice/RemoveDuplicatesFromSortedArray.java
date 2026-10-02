// Remove Duplicates from Sorted Array
// ref: LC 26
// nums is sorted in non-decreasing order. Rewrite it in place so that each distinct
// value appears exactly once, in the original order, at the front of the array, and
// return the count k of distinct values. Only the first k positions are examined;
// whatever is stored after position k - 1 is ignored. Do not build a second array
// to hold the answer.
// Array length is from 1 to 30000; each value is from -100 to 100.
// Example: [0,0,1,1,1,2] returns 3 and its first 3 elements must be [0,1,2].
// Required: O(n) time, O(1) extra space.
// Run: javac --release 8 RemoveDuplicatesFromSortedArray.java && java RemoveDuplicatesFromSortedArray

import java.util.*;

class Solution {
    public int removeDuplicates(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class RemoveDuplicatesFromSortedArray {

    private static String preview(int[] a, int count) {
        int shown = Math.max(0, Math.min(count, a.length));
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < shown && i < 12; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(a[i]);
        }
        if (shown > 12) {
            sb.append(", ...");
        }
        sb.append("]");
        return sb.toString();
    }

    // The solution gets a copy of the input. The check passes when the returned count
    // equals the number of distinct values and the first k elements of the copy are
    // exactly those distinct values in order.
    private static void check(int caseNum, int[] nums, int[] unique, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        String expected = "k=" + unique.length + " " + preview(unique, unique.length);
        int[] work = Arrays.copyOf(nums, nums.length);
        try {
            int k = new Solution().removeDuplicates(work);
            boolean ok = (k == unique.length);
            for (int i = 0; ok && i < k; i++) {
                if (work[i] != unique[i]) {
                    ok = false;
                }
            }
            if (ok) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected
                        + " got k=" + k + " " + preview(work, k));
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    private static int[] filled(int value, int count) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    private static int[] range(int lo, int hi) {
        int[] a = new int[hi - lo + 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = lo + i;
        }
        return a;
    }

    // 30000 values that climb from -100 to 100, every value present, many repeats.
    private static int[] stepped() {
        int[] a = new int[30000];
        for (int i = 0; i < a.length; i++) {
            a[i] = -100 + (int) ((long) i * 201 / a.length);
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1,1,2}, new int[]{1,2}, fail, total);
        check(2, new int[]{0,0,1,1,1,2,2,3,3,4}, new int[]{0,1,2,3,4}, fail, total);
        check(3, new int[]{5}, new int[]{5}, fail, total);
        check(4, new int[]{2,2,2,2,2,2}, new int[]{2}, fail, total);
        check(5, new int[]{1,2,3,4,5}, new int[]{1,2,3,4,5}, fail, total);
        check(6, new int[]{-3,-3,-2,-1,-1,0}, new int[]{-3,-2,-1,0}, fail, total);
        check(7, new int[]{-100,-100,100,100}, new int[]{-100,100}, fail, total);
        check(8, new int[]{-100,100}, new int[]{-100,100}, fail, total);
        check(9, new int[]{1,2,3,3,3}, new int[]{1,2,3}, fail, total);
        check(10, new int[]{1,1,1,2,3}, new int[]{1,2,3}, fail, total);
        check(11, new int[]{-5,-5,0,0,0,5,5,5,5}, new int[]{-5,0,5}, fail, total);
        check(12, stepped(), range(-100, 100), fail, total);
        check(13, filled(-100, 30000), new int[]{-100}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
