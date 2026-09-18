# Reorganize String
`ref: LC 767` · Difficulty: Medium · Pattern: greedy max-heap by remaining frequency

## Problem

You are given a string `s` made only of lowercase English letters. Rearrange its characters, using every character exactly once (the result is some permutation of `s`), so that no two adjacent characters in the result are the same. Return any one valid rearrangement. If no such rearrangement exists, return an empty string.

Because more than one valid rearrangement can exist for the same input, there is no single "correct" output string to compare against. A checker for this problem has to verify that a proposed answer actually satisfies the rules, not that it matches one fixed expected string.

Input: a string `s` of lowercase letters.
Output: a permutation of `s` with no two adjacent equal characters, or `""` if that is impossible.

## Constraints

- `1 <= s.length <= 500`
- `s` consists only of lowercase English letters `'a'`-`'z'`.
- A rearrangement is possible if and only if no letter's count exceeds `(s.length() + 1) / 2` (integer division). If one letter is more than half (rounding up) of the string, it cannot avoid sitting next to a copy of itself somewhere.

## Worked examples

1. `s = "aab"` -> feasible; one valid rearrangement is `"aba"` (letters `a:2, b:1`, and `2 <= (3+1)/2 = 2`, so it is right at the feasibility boundary).
2. `s = "aaab"` -> impossible, so the answer is `""` (letter `a` appears `3` times, but `(4+1)/2 = 2`, and `3 > 2`).
3. `s = "a"` -> feasible trivially; the only rearrangement is `"a"` itself.
4. `s = "aabb"` -> feasible; one valid rearrangement is `"abab"` (or `"baba"`).

## Edge cases checklist

- A single character (`length 1`), trivially feasible.
- Every character distinct (always trivially feasible, any order works).
- Exactly at the feasibility boundary: the most frequent letter's count equals `(n+1)/2` exactly.
- One count over the boundary: the most frequent letter's count is `(n+1)/2 + 1`, which must be reported as impossible.
- All characters identical with `length > 1` (always impossible except for `length 1`).
- Two distinct letters with equal counts (straightforward alternation).
- Several letters with varied counts, where the greedy choice of "most frequent remaining" changes over the course of building the result.
- Even-length vs. odd-length input, since the feasibility formula `(n+1)/2` behaves differently by parity (for even `n` it equals `n/2`; for odd `n` it is `(n+1)/2`, one more than the floor).

## Approach

### Brute force

Generate every permutation of `s` (there are up to `n!` of them, with duplicates from repeated letters) and check each one for the no-adjacent-repeat property, stopping at the first valid one found (or reporting impossible if none qualify). This is combinatorially explosive and unusable beyond tiny strings; it is only useful as a slow, obviously-correct oracle for testing a real solution on small inputs.

### Optimal

Count how many times each letter occurs. If any letter's count exceeds `(n+1)/2`, no rearrangement is possible, so return `""` immediately. Otherwise, build the result greedily: repeatedly take the currently most frequent remaining letter and append it, as long as it is not the exact letter that was just appended (to guarantee no immediate repeat). The clean way to enforce "not the same as the previous letter" without extra bookkeeping is to hold the just-used letter out of the max-heap for exactly one round: pop the most frequent letter, append it and decrement its count, then (only if the *previously* appended letter still has a positive remaining count) push that previous letter back into the heap, and finally remember the letter/count just appended as the new "previous" pair for the next iteration.

**Key invariant:** at every step, deferring the just-appended letter's re-entry into the heap by exactly one iteration guarantees that no two identical letters are ever appended back to back, because a letter can only be chosen again once at least one other distinct letter has been appended in between (the deferral forces exactly that gap). Combined with the upfront feasibility check, greedily choosing the highest remaining count at every other step never runs out of distinct letters to alternate with before the string is exhausted.

### Step-by-step trace

Trace on `s = "aab"` (counts: `a:2, b:1`; feasible since `2 <= (3+1)/2 = 2`):

| step | heap before (count:letter, sorted by count desc) | pop | append | prevCount>0? re-push | prev after this step | result so far |
|---|---|---|---|---|---|---|
| 1 | `2:a, 1:b` | `2:a` | `a` | no (prevCount starts at 0) | `count=1, letter=a` | `"a"` |
| 2 | `1:b` | `1:b` | `b` | yes, re-push `1:a` | `count=0, letter=b` | `"ab"` |
| 3 | `1:a` | `1:a` | `a` | no (prevCount=0) | `count=0, letter=a` | `"aba"` |
| end | empty | - | - | - | - | `"aba"` |

Final result `"aba"`: same multiset as the input (`a:2, b:1`) and no two adjacent letters are equal, so it is a valid answer.

## Java 8 solution
```java
import java.util.PriorityQueue;

public class ReorganizeString {

    // Returns a permutation of s with no two adjacent equal characters, or ""
    // if no such permutation exists.
    public static String solve(String s) {
        int n = s.length();
        int[] counts = new int[26];
        for (int i = 0; i < n; i++) {
            counts[s.charAt(i) - 'a']++;
        }

        int maxFreq = 0;
        for (int i = 0; i < 26; i++) {
            maxFreq = Math.max(maxFreq, counts[i]);
        }
        if (maxFreq > (n + 1) / 2) {
            return ""; // some letter would be forced to sit next to itself
        }

        // Max-heap of {count, letterIndex}, ordered by count descending.
        PriorityQueue<int[]> heap = new PriorityQueue<int[]>((a, b) -> b[0] - a[0]);
        for (int i = 0; i < 26; i++) {
            if (counts[i] > 0) {
                heap.add(new int[]{counts[i], i});
            }
        }

        StringBuilder result = new StringBuilder(n);
        int prevCount = 0;
        int prevLetter = -1;
        while (!heap.isEmpty()) {
            int[] top = heap.poll();
            result.append((char) ('a' + top[1]));
            if (prevCount > 0) {
                heap.add(new int[]{prevCount, prevLetter});
            }
            prevCount = top[0] - 1;
            prevLetter = top[1];
        }
        return result.toString();
    }

    // Validates a proposed answer instead of comparing to one fixed string,
    // since this problem can have many different valid outputs.
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
        String got = solve(input);
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
```

## Complexity

Time O(n log 26), which is effectively O(n): the heap never holds more than 26 entries (one per letter of the alphabet), so every heap operation is O(log 26) = O(1), and there are O(n) of them (one append per input character). Space O(n) for the output, plus O(26) = O(1) for the counts array and heap.

## Java 8 pitfalls for this problem

- `PriorityQueue<Integer>`'s default ordering is a min-heap; here we need a max-heap by count, so the constructor takes an explicit comparator (`(a, b) -> b[0] - a[0]`). Forgetting this and using the no-argument constructor silently builds the wrong ordering rather than throwing an error.
- The feasibility check `maxFreq > (n + 1) / 2` uses integer division on purpose; writing it as `(n + 1) / 2.0` or `Math.ceil(n / 2.0)` also works but mixes in unnecessary floating-point comparisons for what is fundamentally an integer condition.
- `PriorityQueue` does not iterate in sorted order; only repeated `poll()` calls return elements in comparator order. Iterating the heap directly (a for-each loop, or `toArray()`) returns them in internal array order, which is not sorted, a common source of confusion when debugging.
- Use a `StringBuilder` to build the result instead of repeated `String` concatenation (`result = result + ch`) in the loop; string concatenation in a loop is O(n) per append due to copying, giving O(n^2) overall.
- The "hold the previous letter out for one round" trick only works if the re-push check (`prevCount > 0`) happens on *every* iteration, including the very first one (where `prevCount` starts at `0` and correctly causes no re-push, since there is no "previous" letter yet).

## Wrong approaches and why they fail

1. **Sort the string's characters directly (e.g. group all identical letters together) and try to patch up any adjacent duplicates with a single local forward swap.** Counterexample: sorted `"aabbcc"` (as an array `[a,a,b,b,c,c]`), a single left-to-right pass that swaps each duplicate one position to the right produces `[a,b,a,b,c,c]` after fixing the first pair, but then hits the trailing duplicate `c,c` at the very end of the array with no further element to swap into. The local fix cannot resolve a duplicate pair that ends up at the tail, since sorting pushes all copies of the most frequent late-alphabet letter together at the end.
2. **Always append the most frequent remaining letter, without ever checking it against the previously appended letter.** This is equivalent to sorting by frequency and reading off letters in that fixed order, which clusters every copy of the top letter together. Counterexample: `s = "aab"` (`a:2, b:1`). Blindly appending the most frequent letter first every time produces `"aab"`, which has adjacent `'a','a'` at the start, even though a valid arrangement (`"aba"`) exists.
3. **Only run the feasibility check (max count `<= (n+1)/2`) and, if it passes, return the original string unmodified, assuming feasibility alone implies the input is already valid.** Counterexample: `s = "aabb"` is feasible (max count `2`, threshold `(4+1)/2 = 2`), but the original string `"aabb"` itself has adjacent `'a','a'` and adjacent `'b','b'`. Feasibility only proves *some* rearrangement exists, it says nothing about whether the given order already satisfies the rule.

## Variants

1. **Generalize "no two adjacent equal" to "no two equal letters within distance `k`".** This is exactly the Task Scheduler pattern: instead of deferring the previous letter for one round, hold recently used letters in a cooldown queue of size `k` before they become eligible again.
2. **Report every distinct valid rearrangement, or count how many exist**, instead of just one. This is a fundamentally harder combinatorial counting problem, not a small tweak to the greedy construction.
3. **Generalize from a fixed 26-letter alphabet to arbitrary objects with arbitrary counts** (e.g. rearranging a multiset of general items). Replace the fixed-size `counts[26]` array with a `HashMap<T, Integer>` and heap entries of `{count, item}`, at the cost of hashing overhead instead of direct array indexing.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `"aab"` | valid rearrangement of `a:2,b:1` | boundary feasibility (max count exactly `(n+1)/2`) |
| 2 | `"aaab"` | `""` (impossible) | one letter over the feasibility threshold |
| 3 | `"a"` | valid rearrangement of `a:1` | single character, trivially feasible |
| 4 | `"aa"` | `""` (impossible) | two identical characters, always impossible |
| 5 | `"aabb"` | valid rearrangement of `a:2,b:2` | two letters, equal counts |
| 6 | `"aaabbbcc"` | valid rearrangement of `a:3,b:3,c:2` | three letters, greedy choice shifts partway through |
| 7 | `"vvvlo"` | valid rearrangement of `v:3,l:1,o:1` | boundary feasibility with three distinct letters |
| 8 | `"abc"` | valid rearrangement of `a:1,b:1,c:1` | all distinct letters, trivially feasible |
| 9 | `"aaaa"` | `""` (impossible) | all identical characters, length 4 |
| 10 | `"ab"` | valid rearrangement of `a:1,b:1` | minimal two-distinct-letter case |
