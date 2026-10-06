// Dungeon Game
// ref: LC 174
// A knight starts in the top-left room of a dungeon with m rows and n columns
// and must reach the princess in the bottom-right room. Each room holds an
// integer that is added to the knight's health when he enters it: a negative
// number is demons that take health, zero changes nothing, and a positive
// number is an orb that gives health. He moves one room right or one room down
// at each step. His health must never be 0 or below, in any room, including the
// first and the last. Return the smallest starting health that lets him reach
// the princess along some path.
// 1 <= m, n <= 200, -1000 <= dungeon[i][j] <= 1000.
// Required: O(m * n) time.
// Study page: ../dungeon-game.md
// Run: javac --release 8 DungeonGame.java && java DungeonGame

import java.util.*;

class Solution {
    public int calculateMinimumHP(int[][] dungeon) {
        // TODO: implement
        return 0;
    }
}

public class DungeonGame {

    private static void check(int caseNum, int[][] dungeon, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().calculateMinimumHP(dungeon);
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

        check(1, new int[][]{{-2, -3, 3}, {-5, -10, 1}, {10, 30, -5}}, 7, fail, total);
        check(2, new int[][]{{0}}, 1, fail, total);
        check(3, new int[][]{{-3}}, 4, fail, total);
        check(4, new int[][]{{5}}, 1, fail, total);
        check(5, new int[][]{{-1, 10, -6}}, 2, fail, total);
        check(6, new int[][]{{-1}, {-2}, {3}, {-4}}, 5, fail, total);
        check(7, new int[][]{{0, 0}, {0, 0}}, 1, fail, total);
        check(8, new int[][]{{1, 2}, {3, 4}}, 1, fail, total);
        check(9, new int[][]{{1, -3, 3}, {0, -2, 0}, {-3, -3, -3}}, 3, fail, total);
        check(10, new int[][]{{0, 0}, {0, -10}}, 11, fail, total);
        check(11, new int[][]{{-1000, -1000}, {-1000, -1000}}, 3001, fail, total);
        check(12, new int[][]{{-3, 5}, {-2, -1}, {4, -7}}, 7, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
