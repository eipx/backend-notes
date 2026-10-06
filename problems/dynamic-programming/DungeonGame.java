public class DungeonGame {

    // Smallest starting health that lets a knight walk from the top-left room
    // to the bottom-right room, moving only right or down, without his health
    // ever dropping to 0 or below. Every room's number is added to his health
    // when he enters it (negative means demons, positive means a health orb).
    //
    // need[i][j] is the smallest health the knight must have just before
    // entering room (i, j) so that he can still finish the walk alive. The
    // table is filled from the bottom-right corner back to the top-left,
    // because the requirement depends on what lies ahead, not behind.
    public static int solve(int[][] dungeon) {
        if (dungeon.length == 0 || dungeon[0].length == 0) {
            return 1;
        }
        int m = dungeon.length;
        int n = dungeon[0].length;
        int[][] need = new int[m][n];

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                // The health the knight must carry out of this room: the
                // smaller requirement of the rooms he can walk into next.
                int next;
                if (i == m - 1 && j == n - 1) {
                    // Nothing comes after the princess room, but he must be
                    // alive (health at least 1) when he leaves it.
                    next = 1;
                } else if (i == m - 1) {
                    // Last row: only a move to the right is possible.
                    next = need[i][j + 1];
                } else if (j == n - 1) {
                    // Last column: only a move down is possible.
                    next = need[i + 1][j];
                } else {
                    next = Math.min(need[i + 1][j], need[i][j + 1]);
                }
                // Entering with health h leaves h + dungeon[i][j], which must be
                // at least next. A big orb can push this below 1, but the knight
                // still needs at least 1 to be alive on entering.
                need[i][j] = Math.max(1, next - dungeon[i][j]);
            }
        }
        return need[0][0];
    }

    private static void check(int caseNum, int[][] dungeon, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(dungeon);
        if (got == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
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
