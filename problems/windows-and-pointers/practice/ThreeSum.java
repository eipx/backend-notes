// 3Sum
// ref: LC 15
// Given an array of integers nums, find every distinct triplet of values
// (a, b, c) such that a + b + c == 0. No duplicate triplet may be reported
// by value, even if it arises from different index combinations.
// Required complexity: O(n^2) time, O(log n) to O(n) space (sort + output aside).
// Study page: ../three-sum.md
// Run: javac --release 8 ThreeSum.java && java ThreeSum

import java.util.*;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        // TODO: implement
        return new ArrayList<List<Integer>>();
    }
}

public class ThreeSum {
    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{-1,0,1,2,-1,-4}, tripletList(intList(-1,-1,2), intList(-1,0,1))},
            {new int[]{}, tripletList()},
            {new int[]{0}, tripletList()},
            {new int[]{0,0,0}, tripletList(intList(0,0,0))},
            {new int[]{0,0,0,0}, tripletList(intList(0,0,0))},
            {new int[]{-2,0,0,2,2}, tripletList(intList(-2,0,2))},
            {new int[]{1,2,-2,-1}, tripletList()},
            {new int[]{3,-2,1,0}, tripletList()},
            {new int[]{-1,0,1,0}, tripletList(intList(-1,0,1))},
            {new int[]{-5,-4,-3,-2,-1}, tripletList()}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            @SuppressWarnings("unchecked")
            List<List<Integer>> expected = (List<List<Integer>>) cases[i][1];
            try {
                List<List<Integer>> got = new Solution().threeSum(nums);
                if (expected.equals(got)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }

    private static List<Integer> intList(int... vals) {
        List<Integer> l = new ArrayList<Integer>();
        for (int v : vals) {
            l.add(v);
        }
        return l;
    }

    @SafeVarargs
    private static List<List<Integer>> tripletList(List<Integer>... triplets) {
        List<List<Integer>> l = new ArrayList<List<Integer>>();
        for (List<Integer> t : triplets) {
            l.add(t);
        }
        return l;
    }
}
