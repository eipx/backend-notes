// Maximum Points You Can Obtain from Cards
// ref: LC 1423
// Cards lie in a row with point values. Take exactly k cards, each one from
// the left end or the right end of the row. Return the largest total.
// Required: O(k) time, O(1) extra space.
// Study page: ../maximum-points-you-can-obtain-from-cards.md
// Run: javac --release 8 MaximumPointsYouCanObtainFromCards.java && java MaximumPointsYouCanObtainFromCards

import java.util.*;

class Solution {
    public int maxScore(int[] cardPoints, int k) {
        // TODO: implement
        return 0;
    }
}

public class MaximumPointsYouCanObtainFromCards {

    private static void check(int caseNum, int[] cardPoints, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxScore(cardPoints, k);
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
