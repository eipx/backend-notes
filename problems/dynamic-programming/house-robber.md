# House Robber
`ref: LC 198` · Difficulty: Medium · Pattern: linear DP with the choice take or skip, kept in two rolling variables

## Problem

A row of houses stands along a street, and `nums[i]` is the amount of money in house `i`. You may take the money from any set of houses, but you may never take from two houses that stand next to each other. Return the largest total you can take.

Input: an integer array `nums`, where entry `i` is the money in the `i`-th house of the row.
Output: a single integer, the largest total over all sets of houses in which no two houses are neighbors.

## Constraints

- `1 <= nums.length <= 100`
- `0 <= nums[i] <= 400`
- A search over every set of houses looks at `2^n` candidates, which is `2^100` at the limit. Even the plain take-or-skip recursion makes about `1.6^n` calls (thirty houses already need over two million). Only `n + 1` different prefixes of the row exist, so one stored value per prefix is enough, and a single pass does it.

## Worked examples

1. `nums = [1, 2, 3, 1]` -> `4`. Take house 0 and house 2, which gives `1 + 3`. The other legal pairs give less: houses 1 and 3 give `2 + 1 = 3`, and houses 0 and 3 give `1 + 1 = 2`.
2. `nums = [2, 7, 9, 3, 1]` -> `12`. Take houses 0, 2 and 4, which gives `2 + 9 + 1`. Taking the 7 and the 3 gives only 10.
3. `nums = [5]` -> `5`. A lone house has no neighbor, so it is always taken.
4. `nums = [2, 3, 2]` -> `4`. The middle house is the largest, but taking it blocks both of its neighbors and gives 3. The two outer houses are not neighbors of each other, and together they give 4.

## Edge cases checklist

- One house (the answer is that house, even when its value is 0).
- Two houses, in both orders (`[2, 1]` and `[1, 2]`). Only one of the two can be taken, so the answer is the larger one.
- All values zero (the answer is 0 whatever is chosen).
- All values equal (`[4, 4, 4, 4, 4]` is 12, because three houses at positions 0, 2 and 4 fit).
- The largest house sits in the middle and taking it blocks two others (`[2, 3, 2]` and `[1, 100, 1]`).
- The best set leaves a gap of two houses between neighbors in the set (`[2, 1, 1, 2]` and `[5, 1, 1, 5, 1, 1, 5]`). Taking every other house cannot find these.
- The best set ends before the last house (`[1, 2, 3, 1]`).
- The upper bound, 100 houses worth 400 each (the total is 20000, far inside `int` range).
- An empty row is outside the constraints. The loop below returns 0 for it, but it is not among the tests here.

## Approach

### Brute force

Work from the last house. Let `best(i)` be the largest total that uses only the first `i` houses. Look at house `i - 1`, the last one of that prefix. If it is taken, its left neighbor is off limits, so the rest of the total comes from the first `i - 2` houses: `nums[i - 1] + best(i - 2)`. If it is skipped, the rest comes from the first `i - 1` houses: `best(i - 1)`. So `best(i)` is the larger of the two, and the recursion stops at `best(0) = 0` and `best(1) = nums[0]`. With no memory this recursion calls `best(i - 1)` and `best(i - 2)` from every call, which is the Fibonacci pattern and grows like `1.6^n`. Only the values `best(0)` to `best(n)` ever occur, and each one is asked for over and over, so storing each answer once removes the blow-up.

### Optimal

Let `best[i]` be the largest total using only the first `i` houses. The table has `n + 1` cells, and the extra cell `best[0]` stands for the empty prefix:

- `best[0] = 0`: with no houses there is nothing to take.
- `best[1] = nums[0]`: with one house, take it. Money is never negative, so taking can only help.

For `i >= 2`, the last house of the prefix is `nums[i - 1]`, and there are two choices:

- Take it. Its neighbor `nums[i - 2]` cannot be taken, so what remains is the best over the first `i - 2` houses: `nums[i - 1] + best[i - 2]`.
- Skip it. The best over the first `i - 1` houses carries over: `best[i - 1]`.

So `best[i] = max(best[i - 1], best[i - 2] + nums[i - 1])`. The answer is `best[n]`. The table is filled from left to right, and each cell reads only the two cells just before it.

Because of that, the table is not needed. Two variables are enough. Before the loop looks at house `i`, `prev1` holds `best[i]` and `prev2` holds `best[i - 1]`, and the loop computes `best[i + 1]` as `max(prev1, prev2 + nums[i])`. Both start at 0: `prev1` is `best[0]`, and `prev2` stands for the prefix before the empty one, which is also worth 0. That start also covers a single house without a special case, because `0 + nums[0]` is never less than `0`.

**Key invariant:** before house `i` is looked at, `prev1` is the exact largest total over houses `0` to `i - 1`, and `prev2` is the exact largest total over houses `0` to `i - 2` (0 when that range is empty). An optimal set for houses `0` to `i` either leaves house `i` out, in which case it is a legal set among houses `0` to `i - 1` and the best such set is worth `prev1`, or it contains house `i`, in which case it cannot contain house `i - 1`, so the rest is a legal set among houses `0` to `i - 2`, worth at most `prev2`. Any legal set among houses `0` to `i - 2` can be joined with house `i` without breaking the rule, so the second case is worth exactly `nums[i] + prev2`. The larger of the two is the exact answer for houses `0` to `i`, and by induction `prev1` after the last house is the answer for the whole row.

### Step-by-step trace

Run on `nums = [2, 7, 9, 3, 1]`. Each step compares `take = prev2 + nums[i]` with `skip = prev1`, keeps the larger as `cur`, and then shifts: `prev2 = prev1`, `prev1 = cur`.

| i | nums[i] | prev2 before | prev1 before | take = prev2 + nums[i] | skip = prev1 | cur | prev2 after | prev1 after |
|---|---|---|---|---|---|---|---|---|
| 0 | 2 | 0 | 0 | 2 | 0 | 2 | 0 | 2 |
| 1 | 7 | 0 | 2 | 7 | 2 | 7 | 2 | 7 |
| 2 | 9 | 2 | 7 | 11 | 7 | 11 | 7 | 11 |
| 3 | 3 | 7 | 11 | 10 | 11 | 11 | 11 | 11 |
| 4 | 1 | 11 | 11 | 12 | 11 | 12 | 11 | 12 |

The answer is `12`. At `i = 3` skipping wins (`11` against `10`), so the 3 is left out and the best total stays 11. At `i = 4`, taking wins (`11 + 1 = 12`).

Walking back to find the houses: the last step took house 4, and the `11` it added to came from `prev2`, the best over houses 0 to 2. At `i = 2` taking won (`11` against `7`), so house 2 was taken, and the `2` it added to is the best over house 0 alone, which is house 0. The houses are 0, 2 and 4, worth `2 + 9 + 1 = 12`, which is worked example 2.

## Java 8 solution
```java
public class HouseRobber {

    // Largest total that can be taken from a row of houses when no two
    // neighboring houses may both be taken.
    //
    // After house i has been looked at, prev1 is the best total over houses
    // 0..i and prev2 is the best total over houses 0..i-1. Before any house
    // has been looked at, both are 0 (an empty row is worth nothing).
    public static int solve(int[] nums) {
        int prev2 = 0;
        int prev1 = 0;
        for (int i = 0; i < nums.length; i++) {
            // Take house i: its neighbor i - 1 is off limits, so the rest of
            // the total comes from houses 0..i-2.
            int take = prev2 + nums[i];
            // Skip house i: the best total over houses 0..i-1 carries over.
            int skip = prev1;
            int cur = Math.max(take, skip);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // A row of n houses that all hold the same amount.
    private static int[] filled(int n, int value) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = value;
        }
        return nums;
    }

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums);
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

        check(1, new int[]{1, 2, 3, 1}, 4, fail, total);
        check(2, new int[]{2, 7, 9, 3, 1}, 12, fail, total);
        check(3, new int[]{5}, 5, fail, total);
        check(4, new int[]{0}, 0, fail, total);
        check(5, new int[]{2, 1}, 2, fail, total);
        check(6, new int[]{1, 2}, 2, fail, total);
        check(7, new int[]{0, 0, 0, 0}, 0, fail, total);
        check(8, new int[]{4, 4, 4, 4, 4}, 12, fail, total);
        check(9, new int[]{2, 3, 2}, 4, fail, total);
        check(10, new int[]{1, 100, 1}, 100, fail, total);
        check(11, new int[]{2, 1, 1, 2}, 4, fail, total);
        check(12, new int[]{5, 1, 1, 5, 1, 1, 5}, 15, fail, total);
        check(13, filled(100, 400), 20000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): the loop looks at each house once, with constant work per house. Space O(1): two integer variables and no table. The version with a full `best` table uses O(n) space, and it is the one to keep if you also need to recover which houses were taken (see Variants).

## Java 8 pitfalls for this problem

- Seeding a table with `nums[1]` or reading `nums[i - 2]` without a guard. A row of one house has no index 1, and the failure is an `ArrayIndexOutOfBoundsException`. The two-variable loop starts from `0` and `0`, so it needs no seeding and no special case for short rows.
- Prefix index against house index. In the table form, `best[i]` covers the first `i` houses, so it pairs with `nums[i - 1]`. Using `nums[i]` there takes the wrong house and runs off the end of the array when `i == n`. The table also needs `n + 1` cells, not `n`.
- Shifting the two variables in the wrong order. `prev1 = cur; prev2 = prev1;` copies the new value into both of them. The order is `prev2 = prev1; prev1 = cur;`.
- Using `0` to mean "not computed yet" in a memo array. Houses can hold `0`, so a stored answer of `0` looks the same as an empty cell. Fill the memo with `-1`. A memo kept in a `static` field also carries stale answers from one run into the next, so it must be created fresh for every call.
- Overflow. Here the total is at most `50 * 400 = 20000`, so `int` is safe. If the limits grow (money up to a billion, a long row), the running totals need `long`.
- Recursion depth. A top-down version goes `n` calls deep. That is harmless at 100 houses, but a row of 100000 overflows the default stack. The loop has no depth at all.
- `Math.max` takes exactly two arguments in Java 8, which is all this recurrence needs. Writing the take and skip values into named locals first keeps the two choices readable.

## Wrong approaches and why they fail

1. **Add up the even-position houses, add up the odd-position houses, and return the larger sum.** This assumes the best set always takes every other house. It fails when a gap of two houses is better. Counterexample: `nums = [2, 1, 1, 2]`. The even positions give `2 + 1 = 3` and the odd positions give `1 + 2 = 3`, so the method returns `3`. Taking the two ends gives `4`, which is the correct answer.
2. **Greedy: take the largest remaining house, remove its neighbors, repeat.** A big house in the middle can block two good ones. Counterexample: `nums = [2, 3, 2]`. The method takes the 3, removes both 2s, and returns `3`. The correct answer is `4`.
3. **Always take the current house and add the best from two houses back (`best[i] = nums[i] + best[i - 2]`), then return the value at the last house.** There is no skip choice, so the answer is forced to end with the last house. Counterexample: `nums = [1, 100, 1]`. The last value is `1 + 1 = 2`. The correct answer is `100`, from the middle house alone.
4. **Seed a table with `best[0] = nums[0]` and `best[1] = nums[1]`.** With two houses the second is not necessarily better than the first. Counterexample: `nums = [2, 1]`. The method returns `1`. The correct answer is `2`, and the right seed is `max(nums[0], nums[1])`.

## Variants

1. **Return the houses, not just the total.** Keep the full `best` table and walk back from `best[n]`. At each `i`, if `best[i] == best[i - 1]` the house was skipped and the walk moves to `i - 1`. Otherwise the house was taken and the walk moves to `i - 2`.
2. **House Robber II (LC 213).** The houses form a circle, so the first and last are neighbors. Run this same loop twice, once without the first house and once without the last, and keep the larger. See `house-robber-ii.md`.
3. **House Robber III (LC 337).** The houses form a binary tree, and a parent and its child are neighbors. Each node reports two values, the best total with it taken and the best total with it skipped. See `house-robber-iii.md`.
4. **Delete and Earn (LC 740).** Sum the points for each distinct value, and two values that differ by 1 conflict. After that grouping, lay the sums out by value from the smallest to the largest, with 0 for a value that does not occur, and it is this problem on that row.
5. **Taken houses must have at least `d` houses between them** (`d = 1` is this problem). Replace the look-back of two with a look-back of `d + 1`: `best[i] = max(best[i - 1], best[i - d - 1] + nums[i - 1])`, treating cells before the start as 0.
6. **At most `k` houses may be taken.** Add the count taken so far to the state, which makes the table `n` by `k` and the time `O(n * k)`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[1,2,3,1]` | `4` | first example; best set ends before the last house |
| 2 | `nums=[2,7,9,3,1]` | `12` | second example; three houses taken, the 3 skipped |
| 3 | `nums=[5]` | `5` | one house (a table seeded from `nums[1]` fails here) |
| 4 | `nums=[0]` | `0` | one house worth nothing |
| 5 | `nums=[2,1]` | `2` | two houses, the first is larger (a seed of `nums[1]` gives 1) |
| 6 | `nums=[1,2]` | `2` | two houses, the second is larger |
| 7 | `nums=[0,0,0,0]` | `0` | zeros everywhere |
| 8 | `nums=[4,4,4,4,4]` | `12` | all equal values, positions 0, 2 and 4 |
| 9 | `nums=[2,3,2]` | `4` | the largest house blocks two others (greedy gives 3) |
| 10 | `nums=[1,100,1]` | `100` | middle house alone beats both ends (a forced take of the last house gives 2) |
| 11 | `nums=[2,1,1,2]` | `4` | gap of two houses between the taken ones (even/odd sums give 3) |
| 12 | `nums=[5,1,1,5,1,1,5]` | `15` | repeated gaps of two houses (even/odd sums give 12) |
| 13 | `nums=[400, 400, ..., 400]` (100 entries) | `20000` | upper bound, 50 houses taken |
