public class SuperEggDrop {

    // Fewest drops that are enough, in the worst case, to find the critical
    // floor of a building with floors 1..n when k identical eggs are available.
    //
    // The question is turned around. covered[j] is the number of floors that
    // the current number of moves can certainly settle with j eggs. One move is
    // added at a time until k eggs cover all n floors, and the number of moves
    // added is the answer.
    public static int solve(int k, int n) {
        // Index 0 is "no eggs", which can settle no floors, so it stays 0.
        int[] covered = new int[k + 1];
        int moves = 0;
        while (covered[k] < n) {
            moves++;
            // Walk j downward so that covered[j - 1] still holds the value
            // for moves - 1 at the moment it is read.
            for (int j = k; j >= 1; j--) {
                // One drop at the best floor splits the building into three
                // parts: the floor itself (+1), the floors below it if the egg
                // breaks (one egg fewer, one move fewer) and the floors above
                // it if the egg survives (same eggs, one move fewer).
                covered[j] = covered[j - 1] + covered[j] + 1;
            }
        }
        return moves;
    }

    private static void check(int caseNum, int k, int n, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(k, n);
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
