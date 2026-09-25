public class MaximumPointsYouCanObtainFromCards {

    // Take exactly k cards from the two ends of the row; return the largest total.
    public static int solve(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int front = 0;
        for (int j = 0; j < k; j++) {
            front += cardPoints[j];              // start with all k cards from the front
        }
        int back = 0;
        int best = front;
        for (int j = k - 1; j >= 0; j--) {       // keep j front cards, take k - j from the back
            front -= cardPoints[j];              // give back front card j
            back += cardPoints[n - k + j];       // take one more card from the back
            best = Math.max(best, front + back);
        }
        return best;
    }

    private static void check(int caseNum, int[] cardPoints, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(cardPoints, k);
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

        check(1, new int[]{1,2,3,4,5,6,1}, 3, 12, fail, total);
        check(2, new int[]{2,2,2}, 2, 4, fail, total);
        check(3, new int[]{9,7,7,9,7,7,9}, 7, 55, fail, total);
        check(4, new int[]{1,1000,1}, 1, 1, fail, total);
        check(5, new int[]{1,79,80,1,1,1,200,1}, 3, 202, fail, total);
        check(6, new int[]{5}, 1, 5, fail, total);
        check(7, new int[]{10,1,1,1,10}, 2, 20, fail, total);
        check(8, new int[]{3,9,1,1,1,1,9,3}, 2, 12, fail, total);
        check(9, new int[]{100,40,17,9,73,1,1,1,1,1}, 3, 157, fail, total);
        check(10, new int[]{1,2,3,4,5}, 5, 15, fail, total);
        check(11, new int[]{1,2}, 1, 2, fail, total);
        check(12, new int[]{5,1,100,4}, 2, 104, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
