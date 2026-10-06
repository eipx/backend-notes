// Freedom Trail
// ref: LC 514
// A circular ring has a lowercase letter at each of its n positions, numbered 0
// to n - 1 in clockwise order. A marker sits at the top, and at the start ring
// position 0 is at the marker. To spell the word key one letter at a time, turn the
// ring clockwise or anticlockwise (one step per position) until a position that
// holds the next letter is at the marker, then press the button (one more step).
// Return the fewest total steps (turns plus presses) needed to spell all of key.
// ring and key each have 1 to 100 lowercase letters, and every letter of key
// appears in ring.
// Required: O(m * n^2) time or better, where m = key.length and n = ring.length.
// Study page: ../freedom-trail.md
// Run: javac --release 8 FreedomTrail.java && java FreedomTrail

import java.util.*;

class Solution {
    public int findRotateSteps(String ring, String key) {
        // TODO: implement
        return 0;
    }
}

public class FreedomTrail {

    // Java 8 has no String.repeat, so build repeated text with a loop.
    private static String rep(String unit, int times) {
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t < times; t++) {
            sb.append(unit);
        }
        return sb.toString();
    }

    private static void check(int caseNum, String ring, String key, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().findRotateSteps(ring, key);
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

        check(1, "godding", "gd", 4, fail, total);
        check(2, "godding", "godding", 13, fail, total);
        check(3, "a", "a", 1, fail, total);
        check(4, "ab", "b", 2, fail, total);
        check(5, "abcde", "ea", 4, fail, total);
        check(6, "abcde", "aaa", 3, fail, total);
        check(7, "abcde", "edcba", 10, fail, total);
        check(8, "abcd", "c", 3, fail, total);
        check(9, "ccabaaa", "ab", 5, fail, total);
        check(10, "abcdd", "d", 2, fail, total);
        check(11, "aaaaa", "aaaaa", 5, fail, total);
        check(12, rep("ab", 50), rep("ab", 50), 199, fail, total);
        check(13, rep("a", 100), rep("a", 100), 100, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
