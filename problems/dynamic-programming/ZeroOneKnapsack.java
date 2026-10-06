public class ZeroOneKnapsack {

    // Largest total value of a set of items whose total weight is at most
    // capacity, where every item is taken at most once.
    //
    // best[c] is the largest value reachable with total weight at most c,
    // using only the items folded in so far. It starts at all zeros, which is
    // right for "at most": an empty bag is always allowed. Each item is folded
    // in with the capacity loop running from high to low, so that best[c - w]
    // still holds its value from before this item. That is what stops the item
    // from being counted twice.
    public static int solve(int[] weights, int[] values, int capacity) {
        int[] best = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            int w = weights[i];
            int v = values[i];
            // c >= w keeps c - w from going below zero. An item heavier than
            // the capacity never enters the loop.
            for (int c = capacity; c >= w; c--) {
                // Skip the item (keep best[c]) or take it (best[c - w] + v).
                best[c] = Math.max(best[c], best[c - w] + v);
            }
        }
        return best[capacity];
    }

    private static void check(int caseNum, int[] weights, int[] values, int capacity, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(weights, values, capacity);
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

        check(1, new int[]{1, 3, 4, 5}, new int[]{1, 4, 5, 7}, 7, 9, fail, total);
        check(2, new int[]{2, 3, 4, 5}, new int[]{3, 4, 5, 6}, 5, 7, fail, total);
        check(3, new int[]{10, 20, 30}, new int[]{60, 100, 120}, 50, 220, fail, total);
        check(4, new int[]{6, 5, 5}, new int[]{10, 7, 7}, 10, 14, fail, total);
        check(5, new int[]{}, new int[]{}, 10, 0, fail, total);
        check(6, new int[]{3, 4}, new int[]{5, 6}, 0, 0, fail, total);
        check(7, new int[]{5}, new int[]{10}, 5, 10, fail, total);
        check(8, new int[]{6}, new int[]{10}, 5, 0, fail, total);
        check(9, new int[]{1, 2, 3}, new int[]{6, 10, 12}, 6, 28, fail, total);
        check(10, new int[]{2}, new int[]{5}, 10, 5, fail, total);
        check(11, new int[]{3, 4}, new int[]{4, 5}, 10, 9, fail, total);
        check(12, new int[]{3, 34, 4, 12, 5, 2}, new int[]{3, 34, 4, 12, 5, 2}, 9, 9, fail, total);
        check(13, new int[]{1, 2, 3, 10}, new int[]{10, 10, 10, 100}, 10, 100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
