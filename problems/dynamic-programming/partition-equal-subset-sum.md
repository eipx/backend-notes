# Partition Equal Subset Sum
`ref: LC 416` · Difficulty: Medium · Pattern: 0/1 subset-sum table over reachable sums (1-D boolean array, inner loop from high sum down to low sum)

## Problem

Given an array of positive integers, decide whether the elements can be divided into two groups so that both groups have the same total. Every element must go into exactly one of the two groups. Return `true` if such a division exists, and `false` otherwise.

Input: an integer array `nums` of positive values.
Output: a boolean, `true` when the array can be split into two groups with equal sums.

## Constraints

- `1 <= nums.length <= 200`
- `1 <= nums[i] <= 100`
- The total is at most `200 * 100 = 20000`, so half of it is at most `10000`. Trying every split means looking at `2^200` subsets, which is impossible. A table indexed by sum has at most `10001` cells and needs about `200 * 10000 = 2 million` steps.

## Worked examples

1. `nums = [1, 5, 11, 5]` -> `true`. The total is 22, so each group must sum to 11. One group is `{11}` and the other is `{1, 5, 5}`.
2. `nums = [1, 2, 3, 5]` -> `false`. The total is 11, which is odd. Two equal whole-number sums would add up to an even total, so no split can exist.
3. `nums = [2, 2, 3, 5]` -> `false`. The total is 12, so each group would need to sum to 6. The sums that subsets can reach are 0, 2, 3, 4, 5, 7, 8, 9, 10 and 12, and 6 is not among them.
4. `nums = [99, 1, 100]` -> `true`. The total is 200. The element `100` alone is one group, and `{99, 1}` is the other. A single element can be a whole group.

## Edge cases checklist

- `n == 1` (a lone positive element cannot be split into two equal groups, because the other group would be empty with sum 0).
- `n == 2` with equal values (`[2, 2]`, true) and with different values (`[1, 2]`, false).
- An odd total, which is false at once. This holds even when half of the total rounded down is reachable: in `[1, 2, 3, 5]` the total is 11 and `5 = 2 + 3` is reachable, yet the answer is false.
- An even total whose half cannot be reached (`[1, 2, 5]` and `[2, 2, 3, 5]`).
- An element larger than half of the total (`5` in `[1, 2, 5]`). It can never fit in a group whose sum is only half of the total.
- An element exactly equal to half of the total (`100` in `[99, 1, 100]`), which forms one group by itself.
- All values equal. An even count is true (`[1, 1, 1, 1]`, eight `100`s). An odd count is false (for example `[2, 2, 2]`, where half of 6 is 3).
- A group that needs several small elements (`{3, 3, 3}` in `[3, 3, 3, 4, 5]`, and `{14, 4, 2}` in `[14, 9, 8, 4, 3, 2]`).
- The upper bound: 200 values of 100 gives a total of 20000 and a table of 10001 cells. The last test case is a small version of it (eight values of 100).

## Approach

### Brute force

For every one of the `2^n` subsets, add up its elements and check whether the sum equals half of the total. A recursive "put this element in the group or leave it out" search with a remaining target is the same idea and is also `O(2^n)`. At `n = 200` this can never finish, so it is only useful for checking tiny inputs.

### Optimal

Turn the question into a subset-sum question. If the total is odd, return `false` at once. Otherwise let `target = total / 2` and ask: is there a subset whose sum is exactly `target`? If there is, the elements left over also sum to `target`, and the split exists.

Let `dp[s]` be `true` when some subset of the elements processed so far sums to exactly `s`, for `s` from `0` to `target`. Only the empty subset exists at the start, so `dp[0]` is `true` and every other cell is `false`. Now take the elements one at a time. A sum `s` becomes reachable with the new element `x` when `s - x` was reachable without it:

`dp[s] = dp[s] || dp[s - x]`

After all elements are processed, the answer is `dp[target]`. An element larger than `target` changes nothing, and the inner loop below simply does not run for it.

**Direction matters.** For each element the inner loop must run from `target` down to `x`. When the loop reaches sum `s`, all the cells below `s` have not been visited yet in this pass, so `dp[s - x]` still means "reachable without `x`". That is what limits each element to one use. If the loop ran upward instead, `dp[s - x]` could already have been set by the same element `x`, and `x` would be counted again and again, as if it could be used any number of times.

Counterexample for the wrong direction: `nums = [1, 2, 5]` (total 8, `target = 4`, correct answer `false`). Take the first element, `x = 1`.

- Upward loop (`s = 1, 2, 3, 4`): `dp[1] = dp[0]` is true; then `dp[2] = dp[1]` is true because `dp[1]` was just set; then `dp[3]` and `dp[4]` follow the same way. The single element `1` has been used four times, and the upward version returns `true`.
- Downward loop (`s = 4, 3, 2, 1`): `dp[4]`, `dp[3]` and `dp[2]` read cells below them that are still false, and only `dp[1] = dp[0]` becomes true. The single element `1` is used once. After `x = 2` the reachable sums are `0, 1, 2, 3`, and `x = 5` changes nothing, so `dp[4]` stays false and the answer is `false`, which is correct.

**Key invariant:** after the first `k` elements have been processed with the downward loop, `dp[s]` is `true` exactly when some subset of those `k` elements sums to `s`. While element `k + 1` is being processed and the loop stands at sum `s`, every cell below `s` still holds its value from after `k` elements, so `dp[s - x]` describes subsets that do not contain the new element. Once a cell becomes `true` it never becomes `false` again.

### Step-by-step trace

Trace for `nums = [2, 2, 3, 5]`. The total is 12, so `target = 6`. A `T` marks a reachable sum.

| after processing | s=0 | s=1 | s=2 | s=3 | s=4 | s=5 | s=6 |
|---|---|---|---|---|---|---|---|
| start (empty subset) | T | . | . | . | . | . | . |
| first 2 | T | . | T | . | . | . | . |
| second 2 | T | . | T | . | T | . | . |
| 3 | T | . | T | T | T | T | . |
| 5 | T | . | T | T | T | T | . |

While processing `3`, the loop visits `s = 6, 5, 4, 3`. At `s = 6` it reads `dp[3]`, which is still false (it is set later in the same pass), so `dp[6]` stays false. Then `dp[5]` takes `dp[2]`, `dp[4]` was already true, and `dp[3]` takes `dp[0]`. While processing `5`, only `s = 6` and `s = 5` are visited: `dp[6]` reads `dp[1]` (false), and `dp[5]` was already true. Because `dp[6]` is false at the end, the answer is `false`, matching worked example 3.

## Java 8 solution
```java
public class PartitionEqualSubsetSum {

    // True when the positive integers in nums can be split into two groups with
    // the same sum. Every element goes into exactly one group.
    public static boolean solve(int[] nums) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        // Two equal integer halves need an even total. Checking this first also
        // keeps the integer division below from rounding an odd total down.
        if (total % 2 != 0) {
            return false;
        }
        int target = total / 2;

        // dp[s] is true when some subset of the elements processed so far
        // sums to exactly s. The empty subset makes dp[0] true from the start.
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int x : nums) {
            // Walk the sums from high to low. dp[s - x] sits at a lower index
            // and has not been touched during this element's pass yet, so it
            // still describes the elements before x. That is what limits each
            // element to one use.
            for (int s = target; s >= x; s--) {
                dp[s] = dp[s] || dp[s - x];
            }
        }
        return dp[target];
    }

    private static void check(int caseNum, int[] nums, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        boolean got = solve(nums);
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

        check(1, new int[]{1, 5, 11, 5}, true, fail, total);
        check(2, new int[]{1, 2, 3, 5}, false, fail, total);
        check(3, new int[]{1}, false, fail, total);
        check(4, new int[]{2, 2}, true, fail, total);
        check(5, new int[]{1, 2}, false, fail, total);
        check(6, new int[]{1, 1, 1, 1}, true, fail, total);
        check(7, new int[]{3, 3, 3, 4, 5}, true, fail, total);
        check(8, new int[]{1, 2, 5}, false, fail, total);
        check(9, new int[]{2, 2, 3, 5}, false, fail, total);
        check(10, new int[]{14, 9, 8, 4, 3, 2}, true, fail, total);
        check(11, new int[]{1, 2, 3, 4, 5}, false, fail, total);
        check(12, new int[]{99, 1, 100}, true, fail, total);
        check(13, new int[]{100, 100, 100, 100, 100, 100, 100, 100}, true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n * target), where `target` is half of the total: each of the `n` elements triggers one pass over at most `target` cells. With the stated bounds that is at most `200 * 10000 = 2 million` steps. This is called pseudo-polynomial, because it grows with the size of the numbers and not only with `n`. Space O(target): one boolean array of `target + 1` cells, at most 10001.

## Java 8 pitfalls for this problem

- Dividing an odd total by 2 without checking the parity first. Integer division rounds down, so `[1, 2, 3, 5]` (total 11) would search for `5`, find `2 + 3`, and wrongly answer `true`. Test `total % 2 != 0` first.
- Forgetting `dp[0] = true`. A new `boolean[]` is all `false`, so nothing can ever become reachable and every answer is `false`.
- Allocating `new boolean[target]` instead of `new boolean[target + 1]`. Index `target` must exist, and the failure is an `ArrayIndexOutOfBoundsException`.
- Writing the loop bound as `s > x` instead of `s >= x`. That skips `s == x`, the case where the element alone forms the sum, and `[99, 1, 100]` would return `false`.
- Running the inner loop upward. It compiles and passes some inputs, but it treats every element as reusable (see the `[1, 2, 5]` counterexample above).
- Letting the loop go below `x`, for example `s >= 0`. Then `s - x` is negative and the array access throws.
- Using `Boolean[]` or `List<Boolean>` for the table. Unset boxed entries are `null`, not `false`, so reading one and unboxing it throws a `NullPointerException`. A primitive `boolean[]` starts as all `false`.
- Summing into an `int` is safe here (the total is at most 20000), but a version with larger values would need `long`.

## Wrong approaches and why they fail

1. **Return `total % 2 == 0`, treating an even total as enough.** An even total is necessary but not sufficient. Counterexample: `nums = [1, 2, 5]`. The total is 8, which is even, but no subset sums to 4, so the correct answer is `false`.
2. **Skip the parity check and search for `total / 2` with integer division.** An odd total rounds down to a value that can be reachable even though no equal split exists. Counterexample: `nums = [1, 2, 3, 5]`. The total is 11, the rounded-down half is 5, and `2 + 3 = 5` is reachable, so this approach says `true`. The correct answer is `false`.
3. **Run the inner loop upward (from `x` to `target`).** Each element can then be used many times. Counterexample: `nums = [2, 2, 3, 5]`. With `target = 6`, the first `2` sets `dp[2]`, then `dp[4]`, then `dp[6]` in the same pass, so the approach says `true`. The correct answer is `false`.
4. **Greedy: sort in descending order and put each element into the group with the smaller total so far.** Local balancing can lock in a bad early choice. Counterexample: `nums = [3, 3, 3, 4, 5]`. The order is 5, 4, 3, 3, 3: the group totals go 5 and 4, then 5 and 7, then 8 and 7, then 8 and 10. The greedy answer is `false`, but `{4, 5}` and `{3, 3, 3}` both sum to 9, so the correct answer is `true`.
5. **Only try splitting the array at one position (a prefix that sums to half).** The two groups do not have to be contiguous. Counterexample: `nums = [1, 5, 11, 5]`. The prefix sums are 1, 6, 17 and 22, and none of them equals 11, so this approach says `false`. The correct answer is `true` (`{11}` and `{1, 5, 5}`).

## Variants

1. **Two-dimensional table.** `dp[i][s]` means "some subset of the first `i` elements sums to `s`", with `dp[i][s] = dp[i - 1][s] || dp[i - 1][s - x]`. The one-dimensional array used here is this table rolled into a single row, and the downward loop is exactly what makes that legal. Keep the 2-D table when you need to rebuild the groups.
2. **Return the two groups, not just true or false.** With the 2-D table, walk back from `dp[n][target]`. If `dp[i - 1][s]` is true, element `i - 1` is not needed and the walk goes up. Otherwise element `i - 1` is in the subset, and the walk goes to `dp[i - 1][s - nums[i - 1]]`.
3. **Count the subsets that reach the target.** Make `dp` an array of `long` counts with `dp[0] = 1` and `dp[s] += dp[s - x]`. The loop still runs downward.
4. **Target Sum (LC 494).** Put a plus or minus sign in front of each number to reach a given value. If `P` is the sum of the numbers given a plus sign, then `P = (total + target) / 2`, so the task becomes counting subsets with sum `P`.
5. **Minimum difference between the two groups (LC 1049).** Fill the same table up to `total / 2`, take the largest reachable sum `s`, and the answer is `total - 2 * s`.
6. **Unlimited reuse of each number (coin-style).** The upward loop is the correct one there. Choosing the direction is the whole difference between the two problems.
7. **Split into `k` equal groups (LC 698).** This does not reduce to one sum table. It needs backtracking or a bitmask over which elements are already used.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[1,5,11,5]` | `true` | one element (11) equals half the total, the other group is several small ones |
| 2 | `nums=[1,2,3,5]` | `false` | odd total 11; the rounded-down half 5 is reachable, so only the parity check makes this false |
| 3 | `nums=[1]` | `false` | n == 1 |
| 4 | `nums=[2,2]` | `true` | n == 2 with equal values |
| 5 | `nums=[1,2]` | `false` | n == 2 with different values, odd total |
| 6 | `nums=[1,1,1,1]` | `true` | all equal, even count |
| 7 | `nums=[3,3,3,4,5]` | `true` | the groups are {4,5} and {3,3,3}; the greedy "add to the smaller group" gives 8 and 10 |
| 8 | `nums=[1,2,5]` | `false` | even total 8, half 4 unreachable, an element larger than half; the upward loop wrongly says true |
| 9 | `nums=[2,2,3,5]` | `false` | even total 12, half 6 unreachable; the upward loop reuses a 2 three times and says true |
| 10 | `nums=[14,9,8,4,3,2]` | `true` | a group of three elements ({14,4,2}); the largest element is not alone |
| 11 | `nums=[1,2,3,4,5]` | `false` | odd total 15; the rounded-down half 7 is reachable (3+4) |
| 12 | `nums=[99,1,100]` | `true` | one element equals half the total; also needs the `s >= x` loop bound |
| 13 | `nums=[100,100,100,100,100,100,100,100]` | `true` | all equal at the maximum value, a small version of the upper bound |
