# Longest Repeating Character Replacement
`ref: LC 424` · Difficulty: Medium · Pattern: variable-size sliding window with a frequency count and a "best-so-far" ceiling

## Problem

You are given a string `s` made up of uppercase English letters and an integer `k`. You may change up to `k` characters in the string (any characters, any number of times up to the limit `k`, each change swaps one character for any other letter) to make some contiguous slice of the string consist of a single repeated letter. Find the length of the longest contiguous slice that can be made uniform this way. You are choosing both the slice and which letter it becomes uniform to (implicitly, the letter that already appears most often inside that slice, since that minimizes how many changes are needed).

Input: a string `s` of uppercase letters, and a non-negative integer `k`.
Output: a single integer, the length of the longest contiguous slice achievable.

## Constraints

- `1 <= s.length <= 10^5`
- `s` consists only of uppercase English letters (`'A'`–`'Z'`), so 26 possible characters.
- `0 <= k <= s.length`
- With n up to 1e5, checking every possible slice explicitly (O(n^2) slices) and counting character frequencies inside each one from scratch would be at least O(n^2), roughly 1e10 operations at the top end — too slow. An O(n) or O(26n) sliding window is required.

## Worked examples

1. Input: `s = "ABAB"`, `k = 2` → Output: `4`. Change both `B`s to `A` (or both `A`s to `B`); either way exactly 2 changes make the whole 4-character string uniform, and 2 changes is within the budget `k = 2`.
2. Input: `s = "AABABBA"`, `k = 1` → Output: `4`. Take the slice `"ABBA"` (or `"BABB"`); it contains one character that differs from the majority letter in that slice, so a single change suffices, and no slice of length 5 can be fixed with only 1 change.
3. Input: `s = "AAAA"`, `k = 2` → Output: `4`. The whole string is already uniform, so 0 changes are needed; the budget `k = 2` is simply unused.
4. Input: `s = "ABCDE"`, `k = 1` → Output: `2`. Every character is distinct, so any slice of length 2 needs exactly 1 change to make both characters match; a slice of length 3 would need 2 changes (only 1 of the 3 characters can stay), which exceeds `k = 1`.

## Edge cases checklist

- `k = 0`: no changes allowed at all; answer is simply the length of the longest run of one repeated letter already present in `s`.
- `k >= s.length`: the entire string can always be made uniform; answer is `s.length()`.
- String already entirely one repeated letter: answer is `s.length()` regardless of `k`.
- String with no repeated letters at all (all 26 distinct, or fewer than 26 distinct but none repeating locally): answer depends purely on `k + 1`.
- Single-character string: answer is always `1`.
- `k` larger than needed for the whole string (wasted budget).
- Very skewed frequency (one letter dominates, e.g. `"AAAAB"`).
- The "stale max frequency" trap: the window's tracked max-frequency-of-any-letter is allowed to be out of date after a shrink, and a solver must understand *why* that is still correct rather than a bug.

## Approach

### Brute force

For every pair of start and end indices defining a possible slice (O(n^2) slices), count the frequency of each of the 26 letters inside that slice (O(26) with a running count, or O(n) if recomputed from scratch each time) and check whether `sliceLength - maxFrequencyInSlice <= k` (i.e., the number of characters that are NOT the majority letter — and therefore need replacing — fits the budget). This is O(n^2) to O(n^3) depending on how the inner count is computed. At `n = 10^5`, even the best O(n^2) version is around `10^10` operations — far too slow.

### Optimal

Slide a window `[windowStart, windowEnd]` across the string while maintaining a count of how many times each of the 26 letters appears inside the current window, and the maximum such count seen so far *for any window of the current best size* (`maxFreqInWindow`). At each step, extend the window by one character on the right and update its frequency count. If the number of characters in the window that are not the majority letter (`windowSize - maxFreqInWindow`) exceeds `k`, shrink the window by one from the left. Track the largest window size seen.

**Key invariant:** the window never needs to shrink by more than one character per step, and the tracked window length only ever grows or stays the same over the course of the algorithm — so it is enough to track a single scalar "best size" rather than re-validating every window.

Proof sketch: `maxFreqInWindow` is only ever updated upward (via `Math.max`), never recomputed downward when the window shrinks. This looks unsafe at first glance — after a shrink, the true maximum frequency inside the *current* window might be lower than the stale `maxFreqInWindow` value — but it is harmless: the shrink only fires when `windowSize - maxFreqInWindow > k`, i.e., the window has become one character too big for the majority letter it once had. Shrinking by exactly one keeps the window at its previous best size, and any future extension either finds a genuinely bigger majority-letter count (correctly updating `maxFreqInWindow` upward) or the window stays at the same size without ever growing on stale information. In other words, the algorithm never reports a length larger than what was truly achievable, because it never uses a stale `maxFreqInWindow` to justify a *larger* window than the data supports — it only uses it to decide *not* to shrink further, which can never overstate the final answer, only (at worst) keep the window at its already-proven-valid size for one extra step.

### Step-by-step trace

Trace on `s = "AABABBA"`, `k = 1` (expected answer `4`). State shown after processing each `windowEnd`:

| windowEnd | char | freq (A,B) | maxFreqInWindow | windowSize | shrink? | window after step | best |
|---|---|---|---|---|---|---|---|
| 0 | A | A:1,B:0 | 1 | 1 | no (1-1=0≤1) | "A" | 1 |
| 1 | A | A:2,B:0 | 2 | 2 | no (2-2=0≤1) | "AA" | 2 |
| 2 | B | A:2,B:1 | 2 | 3 | no (3-2=1≤1) | "AAB" | 3 |
| 3 | A | A:3,B:1 | 3 | 4 | no (4-3=1≤1) | "AABA" | 4 |
| 4 | B | A:3,B:2 | 3 | 5 | yes (5-3=2>1) → drop leftmost `A`, A:2,B:2, start=1 | "ABAB" | 4 |
| 5 | B | A:2,B:3 | 3 | 5 | yes (5-3=2>1) → drop leftmost `A`, A:1,B:3, start=2 | "BABB" | 4 |
| 6 | A | A:2,B:3 | 3 | 5 | yes (5-3=2>1) → drop leftmost `B`, A:2,B:2, start=3 | "ABBA" | 4 |

Final best: `4`.

## Java 8 solution

```java
public class LongestRepeatingCharacterReplacement {

    public static int solve(String s, int k) {
        int[] freq = new int[26];
        int windowStart = 0;
        int maxFreqInWindow = 0;
        int best = 0;
        for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
            char c = s.charAt(windowEnd);
            freq[c - 'A']++;
            maxFreqInWindow = Math.max(maxFreqInWindow, freq[c - 'A']);
            int windowSize = windowEnd - windowStart + 1;
            // chars needing replacement = window size - count of the most frequent char in it;
            // maxFreqInWindow is allowed to be stale (never decreased on shrink) because a
            // window that was valid at this size can only be beaten by a strictly larger one
            if (windowSize - maxFreqInWindow > k) {
                char leaving = s.charAt(windowStart);
                freq[leaving - 'A']--;
                windowStart++;
            }
            best = Math.max(best, windowEnd - windowStart + 1);
        }
        return best;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {"ABAB", 2, 4},
            {"AABABBA", 1, 4},
            {"", 2, 0},
            {"A", 0, 1},
            {"AAAA", 2, 4},
            {"ABCDE", 1, 2},
            {"ABBB", 2, 4},
            {"AAAB", 0, 3},
            {"AABA", 0, 2},
            {"AB", 5, 2}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            String s = (String) cases[i][0];
            int k = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            int got = solve(s, k);
            if (Integer.compare(expected, got) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
```

## Complexity

- Time: O(n). Each index is visited once as `windowEnd`, and `windowStart` only ever moves forward, advancing at most n times total across the whole run (an amortized argument, not n moves per step).
- Space: O(1), since the frequency array is a fixed 26 slots regardless of `n`.

## Java 8 pitfalls for this problem

- Char arithmetic `c - 'A'`: this only works correctly if the input is guaranteed uppercase `'A'`–`'Z'`; mixing in lowercase would silently produce an out-of-range or wrong index — worth a defensive comment or assertion if input cleanliness isn't guaranteed elsewhere.
- Recomputing `maxFreqInWindow` from scratch (scanning all 26 counts) on every shrink "to be safe" is not wrong, but it changes the complexity to O(26n) — still fine here, but it's worth recognizing that the stale-max trick is what gets you the tighter O(n) bound without the 26 factor, and is the detail solvers most often get nervous about and "fix" unnecessarily.
- Off-by-one in the replacement-count formula: it is `windowSize - maxFreqInWindow`, using the *current* window size (`windowEnd - windowStart + 1`), not the size from before this character was added.
- Comparing `Integer` boxed counts with `==` — not an issue here since `freq` is a primitive `int[]`, but a common mistake if someone rewrites this with a `Map<Character, Integer>` instead.
- Java 8 has no `Map.getOrDefault` pitfall here since we use a primitive array, but if switched to a `HashMap<Character,Integer>`, forgetting the default-zero case on first sight of a letter is a classic bug.

## Wrong approaches and why they fail

- **Shrink the window back to size 0 (or restart entirely) whenever the replacement budget is exceeded, instead of shrinking by exactly one from the left.** Counterexample: `"AABABBA"`, `k = 1` — restarting the window at `windowEnd + 1` every time the budget is blown would never let the window recover to size 4 (`"ABAB"`/`"ABBA"`), since it throws away all the progress instead of sliding incrementally; the correct approach recognizes the window only ever needs to lose its single leftmost character to become valid again.
- **Track the true maximum frequency in the current window at all times (recomputing on every shrink) and assume that is required for correctness.** This is not wrong, just unnecessary — but a common misconception is the reverse: assuming the *stale* max approach (used above) must be a bug because "it doesn't decrease when the window shrinks." Concrete check: on `"AABABBA"`, `k=1`, after shrinking at `windowEnd=4`, the true max frequency in `"ABAB"` is 2 (both letters), but the stale value is 3 (left over from `"AABA"`); despite this the algorithm still lands on the correct final answer of 4, because the stale value only prevents unnecessary further shrinking, it never inflates the reported best length.
- **Assume the answer is always `k + 1` when the string has enough distinct characters.** Counterexample: `s = "AAAA"`, `k = 2` — even though `k + 1 = 3`, the answer is `4`, because the string is already uniform and no replacement was needed at all; the `k+1` shortcut only applies when the string has no usable existing repeats within reach, which is not something you can assume without checking.

## Variants

- **Only two distinct letters allowed in the input (e.g. just `'A'` and `'B'`).** The same sliding window works unchanged; a common simplification is tracking just `count of the more frequent of the two letters` instead of a general 26-slot array, since there's only one other letter to swap.
- **Report the actual resulting string (which characters were changed), not just the length.** Track `bestStart`/`bestLength` the same way as the length-only version, then after finding the best window, identify the majority letter and construct the output by scanning that slice and swapping any non-majority letter.
- **Lowercase letters, or full mixed-case/Unicode alphabet, instead of a 26-letter fixed set.** Swap the fixed 26-slot `int[]` frequency array for a `HashMap<Character, Integer>` (or a 128/256-slot array for extended ASCII); the sliding window logic itself is unchanged, only the frequency container's size and indexing scheme change.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `s="ABAB", k=2` | 4 | exact budget makes whole string uniform |
| 2 | `s="AABABBA", k=1` | 4 | classic mixed example, stale-max trace case |
| 3 | `s="", k=2` | 0 | empty string |
| 4 | `s="A", k=0` | 1 | single character, zero budget |
| 5 | `s="AAAA", k=2` | 4 | already uniform, budget unused |
| 6 | `s="ABCDE", k=1` | 2 | all distinct letters, budget limits window to k+1 |
| 7 | `s="ABBB", k=2` | 4 | majority letter present, budget covers the rest |
| 8 | `s="AAAB", k=0` | 3 | zero budget, answer is longest existing run |
| 9 | `s="AABA", k=0` | 2 | zero budget forces window to shrink around a lone different letter |
| 10 | `s="AB", k=5` | 2 | budget far exceeds string length, capped at string length |
