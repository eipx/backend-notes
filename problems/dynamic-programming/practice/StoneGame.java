// Stone Game
// ref: LC 877
// Alice and Bob play a game on a row of piles of stones, where piles[i] is the
// number of stones in the i-th pile. On each turn the player to move takes the
// whole pile at the left end or the whole pile at the right end of the row.
// Alice moves first, and the players alternate until no piles are left. The
// player with more stones wins. Return true if Alice wins when both players
// play perfectly, and false otherwise (a tie is not a win).
// 2 <= piles.length <= 500, the length is even, 1 <= piles[i] <= 500, and the
// total number of stones is odd (so a tie cannot happen). The tests also
// include a few inputs outside these limits.
// Required: O(n^2) time and space for the table over ranges of piles.
// Study page: ../stone-game.md
// Run: javac --release 8 StoneGame.java && java StoneGame

import java.util.*;

class Solution {
    public boolean stoneGame(int[] piles) {
        // TODO: implement
        return false;
    }
}

public class StoneGame {

    private static void check(int caseNum, int[] piles, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            boolean got = new Solution().stoneGame(piles);
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

        check(1, new int[]{5, 3, 4, 5}, true, fail, total);
        check(2, new int[]{3, 7, 2, 3}, true, fail, total);
        check(3, new int[]{1, 2}, true, fail, total);
        check(4, new int[]{2, 1}, true, fail, total);
        check(5, new int[]{1, 1, 3, 2}, true, fail, total);
        check(6, new int[]{4, 100, 3, 2}, true, fail, total);
        check(7, new int[]{6, 1, 1, 1, 1, 7}, true, fail, total);

        // 500 piles: 499 piles of 500 stones and a last pile of 499 (the
        // largest input the limits allow, with an odd total).
        int[] big = new int[500];
        for (int i = 0; i < big.length; i++) {
            big[i] = 500;
        }
        big[499] = 499;
        check(8, big, true, fail, total);

        check(9, new int[]{}, false, fail, total);
        check(10, new int[]{7}, true, fail, total);
        check(11, new int[]{2, 2}, false, fail, total);
        check(12, new int[]{1, 100, 1}, false, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
