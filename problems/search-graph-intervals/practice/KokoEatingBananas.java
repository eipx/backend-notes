// Koko Eating Bananas
// ref: LC 875
// Given piles of bananas and a number of hours before a guard returns, find the
// smallest constant eating speed k such that every pile can be finished in time,
// where each hour Koko eats up to k bananas from a single chosen pile.
// Required complexity: O(n log(max(piles))) time, O(1) extra space
// Study page: ../koko-eating-bananas.md
// Run: javac --release 8 KokoEatingBananas.java && java KokoEatingBananas

import java.util.*;

class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        // TODO: implement
        return 0;
    }
}

public class KokoEatingBananas {
    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{3,6,7,11}, 8, 4 },
            { new int[]{30,11,23,4,20}, 5, 30 },
            { new int[]{30,11,23,4,20}, 6, 23 },
            { new int[]{1,1,1,1}, 4, 1 },
            { new int[]{1000000000}, 2, 500000000 },
            { new int[]{1}, 1, 1 },
            { new int[]{312884470}, 968709470, 1 },
            { new int[]{3,6,7,11}, 4, 11 },
            { new int[]{5,5,5,5,5}, 5, 5 },
            { new int[]{2,10,3}, 3, 10 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] piles = (int[]) cases[i][0];
            int h = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            try {
                int actual = new Solution().minEatingSpeed(piles, h);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (piles=" + Arrays.toString(piles) + ", h=" + h + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
