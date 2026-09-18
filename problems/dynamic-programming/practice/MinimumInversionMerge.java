// Minimum Inversion Merge
// ref: custom (no public reference)
// Merge two lowercase strings, keeping each string's own letter order, so that the
// number of pairs (i < j) with merged[i] > merged[j] is as small as possible.
// Required: O(n*m) time with O(1) work per state.
// Study page: ../minimum-inversion-merge.md
// Run: javac --release 8 MinimumInversionMerge.java && java MinimumInversionMerge

class Solution {
    public long minConflicts(String a, String b) {
        // TODO: implement
        return -1L;
    }
}

public class MinimumInversionMerge {

    private static void check(int caseNum, String a, String b, long expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            long got = new Solution().minConflicts(a, b);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": threw " + e);
        }
    }

    private static String generated(boolean first) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append(first ? (char) ('a' + (i * 7) % 26) : (char) ('z' - (i * 11) % 26));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int[] fail = new int[1], total = new int[1];
        check(1, "zdc", "", 3L, fail, total);
        check(2, "", "", 0L, fail, total);
        check(3, "a", "a", 0L, fail, total);
        check(4, "ab", "cd", 0L, fail, total);
        check(5, "cd", "ab", 0L, fail, total);
        check(6, "ba", "ab", 1L, fail, total);
        check(7, "zz", "aa", 0L, fail, total);
        check(8, "az", "za", 1L, fail, total);
        check(9, "dcba", "dcba", 18L, fail, total);
        check(10, "aaa", "aaa", 0L, fail, total);
        check(11, "bca", "acb", 4L, fail, total);
        check(12, "zyx", "abc", 3L, fail, total);
        check(13, "caa", "b", 3L, fail, total);
        check(14, generated(true), generated(false), 3841316L, fail, total);
        System.out.println((total[0] - fail[0]) + "/" + total[0] + " passed");
        if (fail[0] > 0) System.exit(1);
    }
}
