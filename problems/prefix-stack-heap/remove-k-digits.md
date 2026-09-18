# Remove K Digits
`ref: LC 402` · Difficulty: Medium · Pattern: monotonic increasing stack

## Problem

You are given a non-negative integer written as a string of digits, `num`, and an integer `k`. Delete exactly `k` digits from `num` (the surviving digits keep their original left-to-right order; you may only delete, never reorder) so that the digits left over spell the smallest possible number. Return that number as a string, with no leading zeros unless the result is exactly `"0"`. If deleting `k` digits removes every digit, the result is `"0"`.

Input: a digit string `num` and an integer `k` with `0 <= k <= num.length`.
Output: the smallest possible string of digits obtainable by deleting exactly `k` digits from `num`, normalized to have no leading zeros (or exactly `"0"` if nothing meaningful survives).

## Constraints

- `1 <= num.length <= 10^5`
- `num` consists only of the digits `0`-`9`, and has no leading zero unless `num` itself is the single digit `"0"`.
- `0 <= k <= num.length`
- The result can have up to `10^5` digits, far too large to fit in any primitive numeric type. This must be solved purely with string/character manipulation, never by parsing into an `int` or `long`.
- With `num.length` up to `1e5`, repeatedly re-scanning the whole string for each of up to `1e5` removals is `O(n^2)` in the worst case, which is borderline-to-too-slow; an `O(n)` single pass is expected.

## Worked examples

1. `num = "1432219"`, `k = 3` -> `"1219"`. Removing the `4`, the `3`, and the trailing `9` (in that relative order) leaves `1,2,2,1,9`... but the smallest result actually comes from removing `4`, `3`, and one more digit; the mechanical process is easiest to see via the step-by-step trace below, which walks through exactly which three digits get dropped.
2. `num = "10200"`, `k = 1` -> `"200"`. Removing the leading `1` leaves `"0200"`, and stripping the resulting leading zero gives `"200"`.
3. `num = "10"`, `k = 2` -> `"0"`. Both digits are removed, so nothing survives and the result is `"0"`.
4. `num = "9"`, `k = 1` -> `"0"`. The only digit is removed, so the result is `"0"`.

## Edge cases checklist

- `k = 0` (return `num` unchanged, since no deletions are requested).
- `k = num.length` (delete everything, result is `"0"`).
- Removing digits exposes leading zeros that must be stripped (e.g. `"10200"`).
- Removing digits leaves only zeros, which must collapse to the single string `"0"`, not `""` or `"00"`.
- All digits identical (no digit is ever strictly greater than the next, so every deletion has to come from the tail end of the string).
- Strictly increasing digits (same as above: no descents exist, so all `k` deletions happen at the end).
- Strictly decreasing digits (every new digit triggers as many deletions as the remaining budget allows, all from the digits already placed).
- Single-digit input with `k = 1` (result is always `"0"`).
- `k` fully consumed partway through the string, so later digits are appended with no further deletions even if they would otherwise have triggered one.

## Approach

### Brute force

Repeat `k` times: scan the current string once, left to right, for the first "descent" (an index `i` where `digit[i] > digit[i+1]`), and delete `digit[i]`, since that digit is provably safe to remove (it is bigger than something to its immediate right, so removing it can only make the number smaller or equal). If no descent exists (the string is non-decreasing), delete the last digit instead. Each of the `k` rounds costs `O(n)`, for `O(k * n)` total, which is `O(n^2)` in the worst case when `k` is close to `n`.

### Optimal

Process the digits left to right while maintaining a stack that always holds a non-decreasing sequence of digits (bottom to top). For each new digit, while the stack is non-empty, its top digit is strictly greater than the new digit, and deletions remain (`k > 0`), pop the stack (that pop consumes one deletion). A bigger digit sitting immediately before a smaller one can always be profitably deleted. Then push the new digit. After the whole string is processed, if deletions still remain, remove them from the very end of the stack (the stack is non-decreasing at that point, so its largest, least useful digits are at the top/end). Finally, strip any leading zeros from the result, and return `"0"` if nothing is left.

**Key invariant:** at every point during the scan, the digits currently on the stack form a non-decreasing sequence from bottom to top, built using the fewest deletions necessary so far to guarantee that property, and every deletion used was applied to a digit that was strictly greater than some digit immediately to its right in the original string (which is always a safe, non-regretful deletion for minimizing the numeric value).

Proof sketch: a number written with more digits in non-decreasing order from left to right (all else equal) is never improved by swapping in a larger leading digit, so keeping the stack non-decreasing is exactly the right target shape. Whenever a new digit `d` would break that shape (some digit above it on the stack is larger than `d`), removing that larger, earlier digit strictly decreases the number's value at that digit position (a smaller digit now appears earlier, which dominates the comparison), and it is legal because we still owe exactly `k` deletions in total. Using one now on a digit we can prove is not needed is never worse than deferring it. Once `k` deletions are exhausted, the remaining digits are appended without further changes, which is correct because with no deletions left, every remaining digit must be kept regardless of local order.

### Step-by-step trace

Trace on `num = "1432219"`, `k = 3`:

| digit read | action | stack after (bottom to top) | k remaining |
|---|---|---|---|
| `1` | stack empty, push | `1` | 3 |
| `4` | top `1` not > `4`, push | `1,4` | 3 |
| `3` | top `4` > `3` and k>0: pop `4` (k=2); top `1` not > `3`, push `3` | `1,3` | 2 |
| `2` | top `3` > `2` and k>0: pop `3` (k=1); top `1` not > `2`, push `2` | `1,2` | 1 |
| `2` | top `2` not > `2` (equal, not greater), push | `1,2,2` | 1 |
| `1` | top `2` > `1` and k>0: pop `2` (k=0); top `2` still > `1` but k=0 now, stop; push `1` | `1,2,1` | 0 |
| `9` | k=0, no pop possible, push | `1,2,1,9` | 0 |
| end | k=0, no trailing trim needed | `1,2,1,9` | 0 |

Result string: `"1219"`. No leading zero to strip. Final answer: `"1219"`, matching worked example 1.

## Java 8 solution
```java
public class RemoveKDigits {

    // Deletes exactly k digits from num (order preserved) to make the smallest
    // possible remaining number, with no leading zeros (or "0" if empty).
    public static String solve(String num, int k) {
        StringBuilder stack = new StringBuilder(); // used as a stack: append = push, deleteCharAt(end) = pop
        for (int i = 0; i < num.length(); i++) {
            char digit = num.charAt(i);
            while (k > 0 && stack.length() > 0 && stack.charAt(stack.length() - 1) > digit) {
                stack.deleteCharAt(stack.length() - 1);
                k--;
            }
            stack.append(digit);
        }
        // Any deletions not yet used must come off the end: the stack is
        // non-decreasing at this point, so the tail holds the least useful digits.
        stack.setLength(stack.length() - k);

        // Strip leading zeros, but keep at least one character.
        int start = 0;
        while (start < stack.length() - 1 && stack.charAt(start) == '0') {
            start++;
        }
        String result = stack.substring(start);
        return result.isEmpty() ? "0" : result;
    }

    private static void check(int caseNum, String num, int k, String expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        String got = solve(num, k);
        if (got.equals(expected)) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got \"" + got + "\"");
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "1432219", 3, "1219", fail, total);
        check(2, "10200", 1, "200", fail, total);
        check(3, "10", 2, "0", fail, total);
        check(4, "9", 1, "0", fail, total);
        check(5, "112", 1, "11", fail, total);
        check(6, "1234567890", 9, "0", fail, total);
        check(7, "10", 1, "0", fail, total);
        check(8, "5337", 2, "33", fail, total);
        check(9, "100", 1, "0", fail, total);
        check(10, "1111111", 3, "1111", fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): each character is pushed onto the stack exactly once and popped at most once across the entire scan (the classic monotonic-stack amortized argument), plus a final `O(n)` pass to strip leading zeros. Space O(n) for the stack/result buffer.

## Java 8 pitfalls for this problem

- Using a `StringBuilder` as the stack (`append`/`deleteCharAt(length()-1)`) avoids boxing every character into a `Character` object, which a `Deque<Character>` would otherwise do; either works, but `StringBuilder` is the more natural fit here since the end goal is a string anyway.
- `stack.setLength(stack.length() - k)` truncates from the end in O(1)-amortized without shifting anything, which is exactly the right operation for "the remaining budget always gets removed from the least significant end of a non-decreasing stack". Using `deleteCharAt` in a loop `k` times works too but is more code for the same result.
- Never parse `num` (or any prefix/suffix of it) into an `int` or `long` for comparison or arithmetic. At up to `1e5` digits it does not fit in any primitive numeric type, and this problem is a pure string-manipulation exercise.
- Comparing `char` values with `>` compares their numeric (Unicode) code points directly, which is exactly digit order for the ASCII digits `'0'`-`'9'`, so no conversion to `int` is needed, but it is worth being able to state explicitly why `'4' > '3'` is true as a `char` comparison.
- Leading-zero stripping must stop one character before the end (`start < stack.length() - 1`) so that a result which is entirely zeros collapses to a single `"0"` rather than being stripped down to an empty string; the final `result.isEmpty() ? "0" : result` check is a second safety net for the case where `k` consumed every character.

## Wrong approaches and why they fail

1. **Remove the `k` digits with the largest individual values anywhere in the string, regardless of position.** Counterexample: `num = "1432219"`, `k = 3`. The three largest-valued digits are `9`, `4`, and one of the `3`s; removing exactly those leaves `"1221"`, which is numerically larger than the true answer `"1219"`. A digit's position (and what comes after it) matters more than its raw value; a trailing `9` contributes far less harm than an early `4` sitting in front of smaller digits.
2. **Always remove the last `k` digits.** Counterexample: `num = "1432219"`, `k = 3`. Removing the last three digits gives `"1432"`, nowhere close to the true minimum `"1219"`.
3. **Sort all the digits into ascending order and keep the smallest `n - k` of them.** This ignores the rule that surviving digits must keep their original relative order (only deletions are allowed, not reordering). Counterexample: `num = "1432219"`. Sorting all its digits gives `1,1,2,2,3,4,9`, and taking the smallest four (`"1122"`) is not even achievable by deleting three digits from the original string while preserving order (the original only has two `1`s, at positions 0 and 5, with both `2`s appearing strictly between them, so no order-preserving deletion can produce two `1`s followed by two `2`s).

## Variants

1. **Keep exactly `n - k` digits instead of framing it as removing `k`.** Identical algorithm; only the bookkeeping variable changes from "deletions remaining" to "keeps remaining."
2. **Produce the largest possible number instead of the smallest.** Flip the stack's comparison from "pop while top is greater" to "pop while top is smaller," turning the monotonic increasing stack into a monotonic decreasing one.
3. **The input arrives as a stream of digits (you must decide whether to delete before seeing the rest).** This is a fundamentally different, online version of the problem; the offline monotonic-stack approach requires seeing the whole string (or at least enough lookahead) to know which digits are safe to keep.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `num="1432219", k=3` | `"1219"` | general case, multiple pops across two positions |
| 2 | `num="10200", k=1` | `"200"` | leading-zero stripping after a single deletion |
| 3 | `num="10", k=2` | `"0"` | deleting every digit |
| 4 | `num="9", k=1` | `"0"` | single digit fully removed |
| 5 | `num="112", k=1` | `"11"` | no descents, trailing trim only |
| 6 | `num="1234567890", k=9` | `"0"` | strictly increasing then a trailing 0 forces a cascade of pops |
| 7 | `num="10", k=1` | `"0"` | one deletion leaves a bare zero |
| 8 | `num="5337", k=2` | `"33"` | one pop, then trailing trim |
| 9 | `num="100", k=1` | `"0"` | result collapses from "00" to "0" |
| 10 | `num="1111111", k=3` | `"1111"` | all-equal digits, no pops ever trigger |
