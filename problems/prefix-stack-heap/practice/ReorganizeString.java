// Reorganize String
// ref: LC 767
// Given a string s of lowercase letters, rearrange its characters so no two
// adjacent characters are equal. Return any valid rearrangement, or "" if
// none exists. Several valid outputs can exist, so this runner validates the
// proposed answer (same letter multiset, no adjacent repeats, or exactly ""
// when impossible) instead of comparing to one fixed string.
// Study page: ../reorganize-string.md
// Run: javac --release 8 ReorganizeString.java && java ReorganizeString

import java.util.*;

class Solution {
    public String reorganizeString(String s) {
        // TODO: implement
        return "";
    }
}

public class ReorganizeString {

    private static boolean isValidRearrangement(String original, String attempt) {
        if (attempt.length() != original.length()) {
            return false;
        }
        int[] originalCounts = new int[26];
        int[] attemptCounts = new int[26];
        for (int i = 0; i < original.length(); i++) {
            originalCounts[original.charAt(i) - 'a']++;
        }
        for (int i = 0; i < attempt.length(); i++) {
            attemptCounts[attempt.charAt(i) - 'a']++;
        }
        for (int i = 0; i < 26; i++) {
            if (originalCounts[i] != attemptCounts[i]) {
                return false;
            }
        }
        for (int i = 1; i < attempt.length(); i++) {
            if (attempt.charAt(i) == attempt.charAt(i - 1)) {
                return false;
            }
        }
        return true;
    }

    private static void check(int caseNum, String input, boolean expectFeasible, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            String got = new Solution().reorganizeString(input);
            boolean ok;
            if (!expectFeasible) {
                ok = got.equals("");
            } else {
                ok = isValidRearrangement(input, got);
            }
            if (ok) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                String expectedDesc = expectFeasible ? ("a valid rearrangement of \"" + input + "\"") : "\"\" (impossible)";
                System.out.println("FAIL case " + caseNum + ": expected " + expectedDesc + " got \"" + got + "\"");
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected a validated result got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "aab", true, fail, total);
        check(2, "aaab", false, fail, total);
        check(3, "a", true, fail, total);
        check(4, "aa", false, fail, total);
        check(5, "aabb", true, fail, total);
        check(6, "aaabbbcc", true, fail, total);
        check(7, "vvvlo", true, fail, total);
        check(8, "abc", true, fail, total);
        check(9, "aaaa", false, fail, total);
        check(10, "ab", true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
