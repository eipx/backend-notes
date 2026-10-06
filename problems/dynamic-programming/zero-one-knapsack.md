# 0-1 Knapsack
`ref: classic` · Difficulty: Medium · Pattern: classic 0-1 knapsack: dp over items and capacity, each item taken at most once, 1-D rolling array filled from high capacity to low

## Problem

You are given `n` items. Item `i` has a weight `weights[i]` and a value `values[i]`. You also have a bag that can carry a total weight of at most `capacity`. Choose a set of items, taking each item at most once and never a piece of one, so that their total weight is at most `capacity` and their total value is as large as possible. Return that largest total value.

Input: two integer arrays `weights` and `values` of the same length `n` (which may be 0), and an integer `capacity`.
Output: a single integer, the largest total value of any set of items that fits in the bag.

## Constraints

- `0 <= n <= 100` and `weights.length == values.length`.
- `1 <= weights[i] <= 10000`.
- `0 <= values[i] <= 1000000`.
- `0 <= capacity <= 10000`.
- The largest possible answer is `100 * 1000000 = 100000000`, which fits in an `int`. If values could reach `10^9`, the sum would overflow and a `long` array would be needed.
- Every item is in or out, so a plain search over all subsets looks at `2^n` sets, which is hopeless at `n = 100`. Only `(n + 1) * (capacity + 1)` different pairs (items considered, room left) exist, at most `1010101`, so a table with one cell per pair is fast.

## Worked examples

1. `weights = [1, 3, 4, 5]`, `values = [1, 4, 5, 7]`, `capacity = 7` -> `9`. Take the items of weight 3 and 4: weight 7, value `4 + 5 = 9`. The items of weight 5 and 1 give only `7 + 1 = 8`.
2. `weights = [2, 3, 4, 5]`, `values = [3, 4, 5, 6]`, `capacity = 5` -> `7`. Take the items of weight 2 and 3: weight 5, value `3 + 4 = 7`. No single item that fits is worth more than 6.
3. `weights = [10, 20, 30]`, `values = [60, 100, 120]`, `capacity = 50` -> `220`. Take the items of weight 20 and 30. The item of weight 10 has the best value per unit of weight, but it is not in the best set.
4. `weights = [2]`, `values = [5]`, `capacity = 10` -> `5`. There is one item and it can be taken once, so the answer is 5 and not 25.

## Edge cases checklist

- No items (`n = 0`): the answer is 0 and the array is never updated.
- `capacity = 0`: every weight is at least 1, so nothing fits and the answer is 0.
- One item whose weight equals the capacity exactly (the inner loop must still run for `c == capacity`).
- One item heavier than the capacity (the inner loop never runs, and the answer is 0).
- Every item fits together (the answer is the sum of all the values).
- A single cheap item and a huge capacity (`[2]`, `[5]`, `10` is 5, not 25). This is the check that each item is used once.
- Greedy traps: best value per weight first, biggest value first and lightest first each give a wrong answer on some input here.
- No set of items weighs exactly the capacity (`[3, 4]` with capacity 10). The bag is allowed to be partly empty, so the answer is the best value at weight at most the capacity.
- Weights equal to values (the problem becomes a subset-sum question: how close can the chosen weights get to the capacity).
- One heavy item worth more than all the light ones together.
- The upper bound `n = 100`, `capacity = 10000`, about a million cell updates (not among the tests here).

## Approach

### Brute force

Work through the items from the last one backwards. Let `f(i, c)` be the largest value using only the first `i` items with room `c`. Item `i` is either left out, giving `f(i - 1, c)`, or taken, which is only possible when `weights[i - 1] <= c` and gives `values[i - 1] + f(i - 1, c - weights[i - 1])`. Then `f(i, c)` is the larger of the two, and `f(0, c) = 0`. With no memory this makes up to `2^n` calls. Only `(n + 1) * (capacity + 1)` different pairs `(i, c)` exist, and the same pairs are reached again and again by different choices on the earlier items, so storing the answers removes the blow-up.

### Optimal

Let `dp[i][c]` be the largest value that can be built from the first `i` items with total weight at most `c`. The table has `n + 1` rows and `capacity + 1` columns:

- `dp[0][c] = 0` for every `c`: with no items there is no value. (Starting every column at 0 means "weight at most `c`", so a partly empty bag is allowed.)

For `i >= 1`, let `w = weights[i - 1]` and `v = values[i - 1]`:

- If `w > c`, the item does not fit: `dp[i][c] = dp[i - 1][c]`.
- Otherwise there are two choices. Leave the item out and keep `dp[i - 1][c]`. Take it, which spends `w` of the room and leaves `dp[i - 1][c - w] + v`. So `dp[i][c] = max(dp[i - 1][c], dp[i - 1][c - w] + v)`.

The answer is `dp[n][capacity]`. Row `i` reads only row `i - 1`, and only the cells in the same column or to its left. That allows a single array `best` of length `capacity + 1` that is reused for every item, as long as each item is folded in with `c` running from `capacity` down to `w`. When the loop reaches column `c`, every column below `c` has not been touched yet for this item, so `best[c - w]` still holds the row `i - 1` value, which is exactly what the recurrence needs.

**Key invariant:** after the first `i` items have been folded in, `best[c]` is the exact largest value for those `i` items at weight at most `c`, for every `c`. While item `i` is being folded in and the loop is at column `c`, all columns below `c` still hold their values from before item `i`. That is why item `i` is counted at most once. A loop that runs from low to high breaks this: `best[c - w]` would already include item `i`, and the item could be taken again.

### Step-by-step trace

State of `best` for `weights = [2, 3, 4, 5]`, `values = [3, 4, 5, 6]`, `capacity = 5`. Columns are the capacity `c` from 0 to 5. Each row is the array after one more item has been folded in.

| after | c=0 | c=1 | c=2 | c=3 | c=4 | c=5 |
|---|---|---|---|---|---|---|
| no items | 0 | 0 | 0 | 0 | 0 | 0 |
| item (w 2, v 3) | 0 | 0 | 3 | 3 | 3 | 3 |
| item (w 3, v 4) | 0 | 0 | 3 | 4 | 4 | 7 |
| item (w 4, v 5) | 0 | 0 | 3 | 4 | 5 | 7 |
| item (w 5, v 6) | 0 | 0 | 3 | 4 | 5 | 7 |

A few cells worked out. For the first item the loop runs `c = 5, 4, 3, 2`, and each cell becomes `max(0, 0 + 3) = 3`. For the second item (weight 3, value 4), `c = 5` gives `max(3, best[2] + 4) = max(3, 3 + 4) = 7`, where `best[2] = 3` is the value from the first item alone. Then `c = 4` gives `max(3, best[1] + 4) = 4` and `c = 3` gives `max(3, best[0] + 4) = 4`. For the third item, `c = 5` gives `max(7, best[1] + 5) = 7` and `c = 4` gives `max(4, best[0] + 5) = 5`. The fourth item changes nothing: `c = 5` gives `max(7, best[0] + 6) = 7`. The answer is `best[5] = 7`.

Walking back: `best[5]` became 7 at the second item by adding that item to `best[2] = 3`, and `best[2]` became 3 at the first item. So the chosen items are the ones of weight 2 and 3.

For contrast, run the first item with `c` going upward. Then `c = 4` reads `best[2]`, which already holds this item's value 3, and writes 6. The same item has been counted twice (two items of weight 2 for a total weight of 4). That is the unbounded knapsack, not this problem.

## Java 8 solution
```java
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
```

## Complexity

Time O(n * capacity): each of the `n` items makes one pass over at most `capacity + 1` cells with constant work per cell, at most about a million updates here. The running time depends on the numeric size of `capacity`, not only on the length of the input, which is why this is called pseudo-polynomial. Space O(capacity) for the single array (at most `10001` ints). The full table with one row per item would use O((n + 1) * (capacity + 1)), about a million ints, and is only needed to recover which items were chosen (see Variants).

## Java 8 pitfalls for this problem

- Running the capacity loop from low to high. The code still compiles and runs, and it answers a different problem: every item may then be used any number of times. `[2]`, `[5]`, capacity 10 returns 25 instead of 5.
- Writing the loop bound as `c >= 0` instead of `c >= w`. Then `best[c - w]` is read at a negative index when `c < w`, and the failure is an `ArrayIndexOutOfBoundsException`.
- Allocating `new int[capacity]` instead of `new int[capacity + 1]`. Index `capacity` must exist, because it holds the answer.
- Summing values in an `int` when they can be large. Here the largest answer is `10^8`, which is safe. With values up to `10^9`, the sum silently wraps to a wrong value (sometimes negative, sometimes a wrong positive), and the array should be `long[]`.
- Marking unreachable weights with `Integer.MIN_VALUE` in an exact-fill version and then adding `v` to it without testing. The sum is no longer equal to the marker (it is `MIN_VALUE + v`), so a later check for "unreachable" misses it and the cell is treated like a real value. Test for the marker before adding.
- Relying on the zero default of a new `int[]`. Zero is correct here because the question is "weight at most `c`", so an empty bag is allowed. For an "exactly `c`" question zero is wrong for every `c > 0`.
- Using a memo table whose default value looks like a real answer. In a top-down version, a new `int[][]` is all zeros, and 0 is also a legitimate stored answer, so use `-1` as the "not computed" mark. The recursion itself is only `n + 1 = 101` frames deep, so depth is not the problem.
- Keeping the array in a `static` field and not clearing it between calls. The next call would start from the previous call's values. Allocate it inside `solve`.
- Assuming `values` is as long as `weights`. The loop bound is `weights.length`, so a shorter `values` array fails with an `ArrayIndexOutOfBoundsException`.

## Wrong approaches and why they fail

1. **Take items in order of value per unit of weight, while they fit.** That is the right plan when an item can be cut into pieces, and a wrong one when it cannot. Counterexample: `weights = [10, 20, 30]`, `values = [60, 100, 120]`, `capacity = 50`. The ratios are 6, 5 and 4, so it takes the first two items (weight 30, value 160), and the third item (weight 30) no longer fits in the 20 that is left. The correct answer is `220`. For `weights = [1, 3, 4, 5]`, `values = [1, 4, 5, 7]`, `capacity = 7` it takes the items of weight 5 and 1 and gives `8`, and the correct answer is `9`.
2. **Take the most valuable item first, or the lightest item first, while they fit.** Counterexample for most valuable first: `weights = [6, 5, 5]`, `values = [10, 7, 7]`, `capacity = 10`. It takes the value-10 item, leaves room 4, and nothing else fits, so it gives `10`. The correct answer is `14` (the two items of weight 5). Counterexample for lightest first: `weights = [1, 2, 3, 10]`, `values = [10, 10, 10, 100]`, `capacity = 10`. It packs the three light items (weight 6, value 30), and the heavy item no longer fits. The correct answer is `100`.
3. **Run the capacity loop from low to high.** Cell `best[c]` is then built from a cell that already includes the current item, so one item is counted many times. Counterexample: `weights = [2]`, `values = [5]`, `capacity = 10` returns `25` (five copies), and the correct answer is `5`. For `weights = [3, 4]`, `values = [4, 5]`, `capacity = 10` it returns `13` (two items of weight 3 and one of weight 4), and the correct answer is `9`.
4. **Insist that the bag is filled exactly.** Start with `best[0] = 0` and every other cell marked impossible, then return `best[capacity]`. Counterexample: `weights = [3, 4]`, `values = [4, 5]`, `capacity = 10`. No set of these two items weighs exactly 10, so `best[10]` stays impossible, while the correct answer is `9` (both items, weight 7). Likewise `weights = [6]`, `values = [10]`, `capacity = 5` has no exact fill, and the correct answer is `0`. Start all cells at 0, or take the maximum over all `c`.

## Variants

1. **Unbounded knapsack.** Each item may be used any number of times. The only change is the loop direction: run `c` from `w` up to `capacity`. Coin Change (LC 322) and Coin Change II (LC 518) have this form.
2. **Recover the chosen items.** Keep the whole `(n + 1) x (capacity + 1)` table. Walk back from `dp[n][capacity]`: if `dp[i][c] == dp[i - 1][c]`, item `i - 1` was left out. Otherwise it was taken, and `c` drops by its weight.
3. **Yes-or-no and counting forms.** Partition Equal Subset Sum (LC 416, see `partition-equal-subset-sum.md`) keeps a boolean array and the same high-to-low loop. Target Sum (LC 494) counts subsets, replacing `max` by a sum of counts. Last Stone Weight II (LC 1049) becomes "get as close to half the total as possible".
4. **Several copies of each item.** Give each item a count. Either loop over the copies, or split the count into powers of two (1, 2, 4, ... and a remainder) and treat each piece as its own 0-1 item.
5. **Two limits at once.** If each item uses both weight and volume, the table gets two capacity dimensions and both inner loops run from high to low. Ones and Zeroes (LC 474) has this shape.
6. **Huge capacity, small values.** When `capacity` is around `10^9` but the total of all values is small, swap the roles: let `best[v]` be the smallest weight that reaches value `v`, and the time becomes O(n * total value).
7. **Exactly full bag.** Start `best[0] = 0` and every other cell at a marker for impossible, and skip an update when the source cell is the marker. The answer is `best[capacity]`, or "impossible" if it is still the marker.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `weights=[1,3,4,5], values=[1,4,5,7], capacity=7` | `9` | takes the items of weight 3 and 4; best-ratio-first and biggest-value-first give 8, lightest-first gives 5 |
| 2 | `weights=[2,3,4,5], values=[3,4,5,6], capacity=5` | `7` | two items beat any single item; the trace example; biggest-value-first gives 6 |
| 3 | `weights=[10,20,30], values=[60,100,120], capacity=50` | `220` | best-ratio-first gives 160; a low-to-high loop gives 300 |
| 4 | `weights=[6,5,5], values=[10,7,7], capacity=10` | `14` | two smaller items beat the single most valuable one; biggest-value-first and best-ratio-first give 10 |
| 5 | `weights=[], values=[], capacity=10` | `0` | no items |
| 6 | `weights=[3,4], values=[5,6], capacity=0` | `0` | zero capacity, nothing fits |
| 7 | `weights=[5], values=[10], capacity=5` | `10` | one item whose weight equals the capacity (the loop must reach `c == w`) |
| 8 | `weights=[6], values=[10], capacity=5` | `0` | one item too heavy, the loop never runs |
| 9 | `weights=[1,2,3], values=[6,10,12], capacity=6` | `28` | every item fits, the answer is the sum of all values; a low-to-high loop gives 36 |
| 10 | `weights=[2], values=[5], capacity=10` | `5` | each item at most once; a low-to-high loop gives 25 |
| 11 | `weights=[3,4], values=[4,5], capacity=10` | `9` | no set weighs exactly 10, so "at most" matters; an exact-fill version has no answer, a low-to-high loop gives 13 |
| 12 | `weights=[3,34,4,12,5,2], values=[3,34,4,12,5,2], capacity=9` | `9` | weights equal to values (subset-sum shape), reachable as 4 + 5 or 4 + 3 + 2 |
| 13 | `weights=[1,2,3,10], values=[10,10,10,100], capacity=10` | `100` | one heavy item worth more than all the light ones; lightest-first gives 30 |
