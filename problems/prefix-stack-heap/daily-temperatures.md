# Daily Temperatures
`ref: LC 739` · Difficulty: Medium · Pattern: Monotonic stack

## Problem
You are given a list of daily temperature readings, one integer per day, in day order. For each day, report how many days you would have to wait until a strictly warmer day occurs later in the list. If no warmer day ever occurs after a given day, report `0` for that day. The output is a list the same length as the input, where each position holds the wait count for the corresponding day. "Warmer" means strictly greater than — a day with the same temperature does not count as an answer.

## Constraints
- The list length can range from empty up to on the order of tens of thousands of days.
- Each temperature is a bounded integer (a realistic small range, well within `int`).
- Implication: with tens of thousands of days, an O(n^2) all-pairs comparison (check every later day for every day) runs into the hundreds of millions of comparisons — too slow. An O(n) single-pass approach is expected.

## Worked examples
1. `temps = [70,71,69,72,70,73]` -> `[1,2,1,2,1,0]`. Day 0 (70) waits 1 day for day 1 (71). Day 1 (71) has to skip past day 2 (69, not warmer) and wait 2 days for day 3 (72). Day 4 (70) waits 1 day for day 5 (73). Day 5 is the last day, so it waits `0`.
2. `temps = [80,80,80]` -> `[0,0,0]`. Every day has the same temperature, and "warmer" requires strictly greater, so no day ever finds a qualifying later day.
3. `temps = [60,65,70,75]` -> `[1,1,1,0]`. The list is strictly increasing, so every day except the last is immediately beaten by the very next day.
4. `temps = [90,80,70,60]` -> `[0,0,0,0]`. The list is strictly decreasing, so no day ever sees a warmer day afterward.

## Edge cases checklist
- Empty input list (output is also empty).
- Single-day input (always `0`, since there is no later day at all).
- All temperatures identical (every answer is `0`, since equal is not warmer).
- Strictly increasing sequence (every day but the last resolves on the very next day).
- Strictly decreasing sequence (every day is `0`).
- A plateau of equal values followed by a rise, where multiple equal days all resolve against the same later day.
- Alternating equal peaks and valleys, where a peak value never finds a strictly warmer day even though it repeats later.

## Approach

### Brute force
For each day `i`, scan forward through every later day `j > i` until one with a strictly higher temperature is found, and record `j - i`; if none is found, record `0`. This is O(n^2) time in the worst case (e.g., a strictly decreasing list, where every day scans all the way to the end and finds nothing) and O(1) extra space (ignoring the output array). At tens of thousands of days that is too slow for a fast solution, though it is a reasonable way to sanity-check the optimal approach on small inputs.

### Optimal
Walk the list once while maintaining a stack of day indices whose warmer day has not been found yet. For each new day, first pop every index on the stack whose temperature is strictly lower than today's temperature — today is the answer for each of those, at a distance of `today's index - popped index`. Then push today's own index onto the stack, since today itself might be someone else's future answer.

**Key invariant:** at every point in the scan, the stack holds day indices in strictly decreasing order of temperature from bottom to top, and every index still on the stack is still waiting for a strictly warmer day to its right.

Proof sketch: the stack starts empty, which trivially satisfies the invariant. Each step either pushes the current index (which is fine, since we only push after popping every index with a smaller temperature, so the new top is still smaller than everything below it, preserving the decreasing order) or pops indices whose temperature is less than the current one (which is exactly when they stop waiting, since the current day is the first later day that beats them — the earliest such later day, since the scan is left to right and this is the first time they're popped). Every index is pushed exactly once and popped at most once, so by the end, every resolved index has the correct answer and every index still on the stack correctly has `0` because the scan has ended before finding a warmer day.

### Step-by-step trace
Trace on `temps = [70,71,69,72,70,73]` (indices 0..5):

| Day (index) | temp | action | stack after (bottom to top, as index:temp) | answers fixed this step |
|---|---|---|---|---|
| 0 | 70 | stack empty, push | `0:70` | none |
| 1 | 71 | pop 0 (70<71) -> answer[0]=1-0=1; push 1 | `1:71` | answer[0]=1 |
| 2 | 69 | 69 not > 71, push | `1:71, 2:69` | none |
| 3 | 72 | pop 2 (69<72) -> answer[2]=3-2=1; pop 1 (71<72) -> answer[1]=3-1=2; push 3 | `3:72` | answer[2]=1, answer[1]=2 |
| 4 | 70 | 70 not > 72, push | `3:72, 4:70` | none |
| 5 | 73 | pop 4 (70<73) -> answer[4]=5-4=1; pop 3 (72<73) -> answer[3]=5-3=2; push 5 | `5:73` | answer[4]=1, answer[3]=2 |
| end | — | index 5 never popped | `5:73` | answer[5]=0 (default) |

Final answer array: `[1,2,1,2,1,0]`, matching worked example 1.

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;

public class DailyTemperatures {

    // For each day, returns how many days until a strictly warmer day; 0 if none exists.
    public static int[] solve(int[] temps) {
        int n = temps.length;
        int[] answer = new int[n];
        Deque<Integer> indexStack = new ArrayDeque<Integer>(); // ArrayDeque, never java.util.Stack, holds indices in decreasing temp order
        for (int i = 0; i < n; i++) {
            while (!indexStack.isEmpty() && temps[indexStack.peek()] < temps[i]) {
                int prevIndex = indexStack.pop();
                answer[prevIndex] = i - prevIndex; // strictly warmer day found, distance in days
            }
            indexStack.push(i);
        }
        // anything left on the stack never finds a warmer day; answer[] defaults to 0 already
        return answer;
    }
}
```

## Complexity
Time O(n): each index is pushed onto the stack exactly once and popped at most once across the entire scan, so the total number of push/pop operations is bounded by `2n` — this is the classic amortized argument for monotonic-stack algorithms, even though a single step can pop many elements. Space O(n) for the stack in the worst case (a strictly decreasing list, where nothing ever gets popped until the scan ends).

## Java 8 pitfalls for this problem
- Using `java.util.Stack` instead of `ArrayDeque` works but is legacy, synchronized (slower for single-threaded use), and extends `Vector` — prefer `Deque<Integer> stack = new ArrayDeque<Integer>()` with `push`/`pop`/`peek`.
- `indexStack.peek()` returns a boxed `Integer`; using it directly as an array index (`temps[indexStack.peek()]`) auto-unboxes, which is fine as long as the stack is never empty at that point — the `!indexStack.isEmpty()` check in the `while` condition must come first, and Java short-circuits `&&` left to right so this is safe, but swapping the order would risk a `NoSuchElementException` from `peek()` on an empty deque (or `NullPointerException` if using `peek()` on an empty `ArrayDeque`-backed structure that returns null and then gets unboxed).
- Comparing temperatures with `<` on primitive `int` is correct here; if you ever store temperatures as boxed `Integer` and compare with `<`, Java still auto-unboxes for `<`/`>` (unlike `==`), so that particular operator is safe — but it is easy to instead reach for `.equals()` or `==` out of habit when checking "is this day resolved yet," and `==` on boxed values outside the `-128..127` cache range silently misbehaves.
- The answer array is created with `new int[n]`, which Java zero-initializes automatically — relying on this default of `0` for "never found a warmer day" is intentional and correct here, not an oversight.

## Wrong approaches and why they fail
1. **Only compare each day to the very next day.** Counterexample: `temps = [70,71,69,72,70,73]` — day 2 (69) is not warmer than day 1 (71), but a "check only the immediate neighbor" rule would either wrongly report `0` for day 1 or would need to keep scanning anyway, which just reduces to the brute force approach without the stack's efficiency gain.
2. **Use a stack but never pop, only push, then scan the stack for a match at the end.** This loses the day-order information needed to compute a distance and cannot tell you which specific later day resolved which earlier day — the pop step is the mechanism that assigns a correct answer to a specific earlier index.
3. **Track only the single maximum temperature seen so far, from the right or left, instead of a full monotonic stack.** Counterexample: `temps = [73,71,69,72,70,73]` (a high value early, then smaller values, then a moderate value, then a final high value) — a "compare to the running max" idea confuses "warmer than everything before" with "warmer than the immediately preceding unresolved days," and cannot correctly assign different wait distances to days 71, 69, and 70, which each need their own distinct nearest warmer day, not the global maximum.

## Variants
1. **Return the index of the next warmer day instead of the distance.** Identical algorithm, except when popping, store the current index itself (`i`) rather than `i - poppedIndex`.
2. **Find the next warmer day using a circular list** (the array wraps around once). Run the same monotonic-stack scan twice over the (conceptually doubled) list, only recording answers during the first pass, so days near the end can still find a warmer day that wraps to the beginning.
3. **Next smaller element instead of next warmer.** Flip the comparison in the `while` condition from `<` to `>`, so the stack now holds indices in increasing order of temperature and pops whenever a strictly smaller value appears.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[70,71,69,72,70,73]` | `[1,2,1,2,1,0]` | general case, multiple pops in a single step |
| 2 | `[80,80,80]` | `[0,0,0]` | equal temperatures, never strictly warmer |
| 3 | `[100]` | `[0]` | single day, no later day exists |
| 4 | `[60,65,70,75]` | `[1,1,1,0]` | strictly increasing, resolved every day but the last |
| 5 | `[90,80,70,60]` | `[0,0,0,0]` | strictly decreasing, never resolved |
| 6 | `[]` | `[]` | empty input |
| 7 | `[50,50,51]` | `[2,1,0]` | plateau then rise, tied days share the same resolving day |
| 8 | `[1,2,1,2,1,2]` | `[1,0,1,0,1,0]` | alternating equal peaks, strict inequality required |
| 9 | `[40,50]` | `[1,0]` | minimal increasing pair |
| 10 | `[50,40]` | `[0,0]` | minimal decreasing pair |
