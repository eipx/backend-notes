// Super Egg Drop
// ref: LC 887
// You have k identical eggs and a building with floors numbered 1 to n. There
// is an unknown critical floor f, with 0 <= f <= n: an egg dropped from any
// floor above f breaks, and an egg dropped from floor f or below survives and
// can be dropped again. In one move you take an unbroken egg and drop it from
// any floor. Return the smallest number of moves that is enough to find f for
// certain, even in the worst case.
// 1 <= k <= 100; 1 <= n <= 10000. One test uses n = 0, outside these
// limits.
// Required: O(k * m) time, where m is the answer, and O(k) space (the table
// over floors, with a binary search for the drop floor, is the slower
// fallback).
// Study page: ../super-egg-drop.md
// Run: javac --release 8 SuperEggDrop.java && java SuperEggDrop

import java.util.*;

class Solution {
    public int superEggDrop(int k, int n) {
        // TODO: implement
        return 0;
    }
}

public class SuperEggDrop {

    private static void check(int caseNum, int k, int n, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().superEggDrop(k, n);
            if (got == expected) {
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

        check(1, 1, 2, 2, fail, total);
        check(2, 2, 6, 3, fail, total);
        check(3, 3, 14, 4, fail, total);
        check(4, 1, 1, 1, fail, total);
        check(5, 1, 0, 0, fail, total);
        check(6, 1, 10, 10, fail, total);
        check(7, 2, 1, 1, fail, total);
        check(8, 2, 100, 14, fail, total);
        check(9, 3, 25, 5, fail, total);
        check(10, 3, 26, 6, fail, total);
        check(11, 2, 10000, 141, fail, total);
        check(12, 100, 10000, 14, fail, total);
        check(13, 4, 5000, 19, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
