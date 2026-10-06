# Super Egg Drop
`ref: LC 887` · Difficulty: Hard · Pattern: DP on moves and eggs, `f(m, k)` = floors that `m` moves and `k` eggs can certainly cover; or the slower DP on floors with binary search

## Problem

You have `k` identical eggs and a building with floors numbered `1` to `n`. Somewhere in the building there is a critical floor `f`, with `0 <= f <= n`. An egg dropped from any floor above `f` breaks. An egg dropped from floor `f` or lower does not break and can be dropped again. A move is one drop: you take an egg that has not broken and drop it from any floor you choose. You do not know `f` in advance. Return the smallest number of moves that is guaranteed to be enough to find `f`, even if the building behaves as badly as possible.

Input: two integers, `k` (the number of eggs) and `n` (the number of floors).
Output: a single integer, the minimum number of moves that settles `f` for certain in the worst case.

## Constraints

- `1 <= k <= 100`
- `1 <= n <= 10000`
- There are `n + 1` possible values of `f`, not `n`: `f = 0` means the egg breaks even on floor 1, and `f = n` means it never breaks.
- The table over floors (see Brute force) has `k * n` cells and tries up to `n` drop floors in each, which is about `5 * 10^9` steps at the limits. The table over moves below does at most `10000` steps at the limits.

## Worked examples

1. `k = 1`, `n = 2` -> `2`. With one egg, any risky drop can end the experiment. Drop from floor 1. If it breaks, `f = 0`. If it survives, drop from floor 2: a break means `f = 1` and a survival means `f = 2`. Two moves.
2. `k = 2`, `n = 6` -> `3`. Drop the first egg from floor 3. If it breaks, `f` is `0`, `1` or `2`, and the second egg settles it in two more moves (drop from floor 1; if it survives, drop from floor 2). If it survives, `f` is between `3` and `6`: drop from floor 5. A break means `f` is `3` or `4`, and one more drop from floor 4 settles it. A survival means `f` is `5` or `6`, and one more drop from floor 6 settles it. Three moves are enough, and two moves are not (a table below shows two moves reach only 3 floors).
3. `k = 3`, `n = 14` -> `4`. Four moves with three eggs cover exactly 14 floors, so 14 floors need four moves and 15 floors would need five.
4. `k = 2`, `n = 100` -> `14`. With two eggs, drop the first from floor 14, then 27, 39, 50, and so on, going up by one floor less each time (`14 + 13 + 12 + ... + 1 = 105`, which is at least 100). Whenever the first egg breaks, the second one scans the gap below it one floor at a time, and the total number of drops stays at most 14.
5. `k = 1`, `n = 0` -> `0`. This is outside the limits. A building with no floors has `f = 0` and nothing to find out.

## Edge cases checklist

- One egg (the only safe plan is to go up one floor at a time, so the answer is `n`).
- One floor (`n = 1`): one drop settles it, however many eggs there are.
- No floors (`n = 0`, outside the limits): no move is needed, and the loop must not run even once.
- Far more eggs than needed (`k = 100`, `n = 10000`): spare eggs add nothing and the answer is the number of halvings, `14`.
- Exactly two eggs: the answer is the smallest `m` with `m * (m + 1) / 2 >= n` (`n = 100` gives `14`, `n = 10000` gives `141`).
- An `n` that is exactly the most that some number of moves can cover, and an `n` that is one more (`k = 3`: `n = 25` needs 5 moves, `n = 26` needs 6).
- A critical floor of `0` (the egg breaks everywhere) or `n` (it never breaks). These are the two ends of the `n + 1` outcomes, and forgetting them is the usual off-by-one.
- More eggs than moves: an egg that is never needed adds nothing, so with `k >= m` the cover is `2^m - 1` floors.
- The upper bounds `k = 100` and `n = 10000`, both among the tests. The slowest loop is `k = 1`, `n = 10000`, which is 10000 moves (not among the tests here).

## Approach

### Brute force

Think in floors. Let `g(k, n)` be the fewest moves that settle `n` floors in the worst case with `k` eggs. Drop an egg from floor `x`, with `1 <= x <= n`. If it breaks, the critical floor is below `x`, so the `x - 1` floors under it are left and one egg is gone: `g(k - 1, x - 1)`. If it survives, the critical floor is at or above `x`, so the `n - x` floors above it are left and all `k` eggs remain: `g(k, n - x)`. The building decides which of the two happens, and it picks the worse one for you, so you choose the `x` that makes the worse of the two smallest:

`g(k, n) = 1 + min over x in 1..n of max(g(k - 1, x - 1), g(k, n - x))`.

The base cases are `g(k, 0) = 0` and `g(1, n) = n`. With no memory this recursion repeats the same pairs `(k, n)` over and over. With a table it is `k * n` cells and up to `n` choices of `x` per cell, which is `O(k * n^2)`. The first term of the maximum grows with `x` and the second shrinks with `x`, so a binary search for the crossing point finds the best `x` in `O(log n)` and the table costs `O(k * n * log n)`. That is fast enough, but the next idea is simpler and faster.

### Optimal

Turn the question around. Instead of asking how many moves `n` floors need, ask how many floors `m` moves can settle. Let `f(m, k)` be the largest number of floors that `m` moves and `k` eggs can settle for certain. The building gets no easier as it grows, and extra moves or eggs never hurt, so the answer is the smallest `m` with `f(m, k) >= n`.

- `f(0, k) = 0`: with no moves, no floor can be settled.
- `f(m, 0) = 0`: with no eggs, no floor can be settled.
- For `m >= 1` and `k >= 1`: `f(m, k) = f(m - 1, k - 1) + f(m - 1, k) + 1`.

Here is why the recurrence holds. Spend one move on a drop from some floor `x`. If the egg breaks, the floors below `x` must be settled with `m - 1` moves and `k - 1` eggs, so there can be at most `f(m - 1, k - 1)` of them. If the egg survives, the floors above `x` must be settled with `m - 1` moves and `k` eggs, so there can be at most `f(m - 1, k)` of them. Add the floor `x` itself and the total is at most `f(m - 1, k - 1) + f(m - 1, k) + 1`. Choosing `x` so that exactly that many floors lie below it, `x = f(m - 1, k - 1) + 1`, reaches the limit on both sides.

The table is filled one move at a time, and the row for `m` reads only the row for `m - 1`. One array `covered` of length `k + 1` is enough: after the `m`-th pass, `covered[j]` is `f(m, j)`. The loop adds a move and updates the array until `covered[k] >= n`, and the number of passes is the answer.

**Key invariant:** after `m` passes, `covered[j]` is exactly `f(m, j)` for every `j` from 0 to `k`. The base row is correct, and each pass turns the row for `m - 1` into the row for `m` by the recurrence. So the loop stops at the first `m` for which `m` moves cover at least `n` floors, and that `m` is the minimum.

### Step-by-step trace

Filled table of `f(m, j)` for `k = 2` eggs and `n = 6` floors. Row `m` is the number of moves and column `j` is the number of eggs.

| m \ eggs | 0 | 1 | 2 |
|---|---|---|---|
| 0 | 0 | 0 | 0 |
| 1 | 0 | 1 | 1 |
| 2 | 0 | 2 | 3 |
| 3 | 0 | 3 | 6 |

A few cells worked out. `f(1, 1) = f(0, 0) + f(0, 1) + 1 = 1`: one move and one egg settle one floor. `f(2, 1) = f(1, 0) + f(1, 1) + 1 = 0 + 1 + 1 = 2`: one egg climbs one floor per move. `f(2, 2) = f(1, 1) + f(1, 2) + 1 = 1 + 1 + 1 = 3`. `f(3, 2) = f(2, 1) + f(2, 2) + 1 = 2 + 3 + 1 = 6`.

The loop stops when `covered[2] >= 6`. After the second pass `covered[2]` is `3`, which is less than 6, and after the third pass it is `6`, so the answer is `3` moves. The first drop of the plan is at floor `f(2, 1) + 1 = 3`: if the egg breaks, the 2 floors below are settled by `f(2, 1) = 2` (one egg, two moves), and if it survives, the 3 floors above are settled by `f(2, 2) = 3` (two eggs, two moves). That is the plan of worked example 2.

The in-place update must go from the largest egg count down. In the second pass, `j = 2` is updated first: `covered[2] = covered[1] + covered[2] + 1 = 1 + 1 + 1 = 3`, using the value of `covered[1]` from the first pass. Then `j = 1`: `covered[1] = covered[0] + covered[1] + 1 = 0 + 1 + 1 = 2`. Going upward instead would update `covered[1]` to `2` first and then compute `covered[2] = 2 + 1 + 1 = 4`, mixing two different move counts.

## Java 8 solution
```java
public class SuperEggDrop {

    // Fewest drops that are enough, in the worst case, to find the critical
    // floor of a building with floors 1..n when k identical eggs are available.
    //
    // The question is turned around. covered[j] is the number of floors that
    // the current number of moves can certainly settle with j eggs. One move is
    // added at a time until k eggs cover all n floors, and the number of moves
    // added is the answer.
    public static int solve(int k, int n) {
        // Index 0 is "no eggs", which can settle no floors, so it stays 0.
        int[] covered = new int[k + 1];
        int moves = 0;
        while (covered[k] < n) {
            moves++;
            // Walk j downward so that covered[j - 1] still holds the value
            // for moves - 1 at the moment it is read.
            for (int j = k; j >= 1; j--) {
                // One drop at the best floor splits the building into three
                // parts: the floor itself (+1), the floors below it if the egg
                // breaks (one egg fewer, one move fewer) and the floors above
                // it if the egg survives (same eggs, one move fewer).
                covered[j] = covered[j - 1] + covered[j] + 1;
            }
        }
        return moves;
    }

    private static void check(int caseNum, int k, int n, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(k, n);
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

        check(1, 1, 2, 2, fail, total);
        check(2, 2, 6, 3, fail, total);
        check(3, 3, 14, 4, fail, total);
        check(4, 1, 1, 1, fail, total);
        check(5, 1, 0, 0, fail, total);
        check(6, 1, 10, 10, fail, total);
        check(7, 2, 1, 1, fail, total);
        check(8, 2, 100, 14, fail, total);
        check(9, 3, 25, 5, fail, total);
        check(10, 3, 26, 6, fail, total);
        check(11, 2, 10000, 141, fail, total);
        check(12, 100, 10000, 14, fail, total);
        check(13, 4, 5000, 19, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(k * m), where `m` is the number of moves returned: each pass of the outer loop costs `k` steps, and there are `m` passes. Since `m <= n` (one egg needs `n` moves), the worst case at the limits is `k = 1`, `n = 10000`, which is 10000 steps. With `k = 100`, `n = 10000` the answer is 14, so about `100 * 14 = 1400` steps. Space O(k) for the one array of `k + 1` ints. The slower floors table from the Brute force section is O(k * n * log n) time (with the binary search) and O(k * n) space.

## Java 8 pitfalls for this problem

- Updating the array in the wrong direction. Each entry must be computed from the previous pass. Going from `j = 1` upward makes `covered[j - 1]` already new when `covered[j]` reads it, and the cover is overstated. `k = 3`, `n = 14` then returns `3`, and the correct answer is `4`. Go from `j = k` down to `1`.
- Array sizing. Index `0` stands for "no eggs", so the array is `new int[k + 1]`. `new int[k]` throws an `ArrayIndexOutOfBoundsException` at `covered[k]`.
- Forgetting the `+ 1` for the floor that was dropped from. Without it every entry stays `0` forever, the loop condition never becomes false, and the program hangs instead of printing a wrong answer. The loop has no other exit.
- Using `<=` in the loop condition. The loop must run while `covered[k] < n`. With `<=` the loop runs one pass too many whenever `n` equals exactly what some number of moves can cover. `k = 3`, `n = 14` returns `5` instead of `4`, and `n = 0` returns `1` instead of `0`.
- `k = 0`. Nothing grows, so the loop would never end. The limits promise `k >= 1`.
- Integer overflow. When the loop ends, `covered[k]` is below `2 * n`, because both values added in the last pass were below `n`. That is safe for `n = 10000` (and up to about a billion), but a larger `n` needs `long`. A closed-form version with binomial coefficients overflows much sooner, since each coefficient can be astronomically larger than `n`. It needs `long` and an early exit as soon as the running sum reaches `n`.
- Recursion depth. A recursive version over floors goes up to `n = 10000` calls deep when `k >= 2` (the choice `x = 1` calls `g(k, n - 1)` again), which risks a `StackOverflowError` on the default stack. The loop version has no recursion.
- Zero as a memo sentinel. A memo table `new int[k + 1][n + 1]` is full of zeros, and `0` is a valid answer (for `n = 0`), so a check "is it 0?" cannot tell "not computed" from "computed as 0". Use `-1`. The full table is also `100 * 10001` ints, about 4 MB.
- Floating-point logarithms. Computing the many-eggs answer as `Math.ceil(Math.log(n + 1) / Math.log(2))` can be off by one near a power of two. For example, `Math.log(1 << 29) / Math.log(2)` is `29.000000000000004`, so `Math.ceil` returns `30` instead of `29`. That example is far outside the limits here, and the formula happens to be exact for every `n` up to `10000`, but an exact answer should not depend on floating-point rounding. Count halvings with an integer loop.

## Wrong approaches and why they fail

1. **Binary search on the floors, then scan when one egg is left.** Always drop from the middle of the floors that are still open, and when only one egg remains, go up one floor at a time. The first drop at the middle risks the first egg, and if it breaks the second egg has to scan half the building. Counterexample: `k = 2`, `n = 100`. A break at floor 50 leaves 49 floors for the last egg, so the worst case is `1 + 49 = 50` moves, and the correct answer is `14`.
2. **Ignore the eggs and use `ceil(log2(n + 1))`.** That is the answer only when eggs never run out. Counterexamples: `k = 1`, `n = 10` gives `4`, and the correct answer is `10`. `k = 2`, `n = 100` gives `7`, and the correct answer is `14` (no plan with two eggs can finish in 7 moves, because 7 moves with two eggs cover only `7 * 8 / 2 = 28` floors).
3. **Use the two-egg answer, the smallest `m` with `m * (m + 1) / 2 >= n`, for every `k`.** In general it is right only for `k = 2`. Counterexamples: `k = 3`, `n = 14` gives `5`, and the correct answer is `4`. `k = 100`, `n = 10000` gives `141`, and the correct answer is `14`. `k = 1`, `n = 10` gives `4`, and the correct answer is `10`.
4. **Add the two outcomes instead of taking the worse one.** In the table over floors, writing `1 + g(k - 1, x - 1) + g(k, n - x)` pays for the break and the survival together, but only one of them happens in any one run. Counterexample: `k = 2`, `n = 6` gives `6`, and the correct answer is `3`.

## Variants

1. **Floors table with a binary search for the drop floor.** The recurrence from the Brute force section, filled bottom-up, with a binary search over `x` in each cell because the two branches move in opposite directions. O(k * n * log n) time, enough for the limits here.
2. **Closed form with binomial coefficients.** `f(m, k) = C(m, 1) + C(m, 2) + ... + C(m, k)`. Each outcome of a plan is a string of "broke" and "survived" results with at most `k` breaks, and `n` floors have `n + 1` possible values of the critical floor, so the smallest `m` with `C(m, 0) + C(m, 1) + ... + C(m, k) >= n + 1` is the answer. Build each term from the previous one (`term = term * (m - i + 1) / i`) in `long`, and stop as soon as the sum reaches `n`. Binary search on `m` gives O(k * log n).
3. **Egg Drop With 2 Eggs and N Floors (LC 1884).** The same problem with `k` fixed at 2. The answer is the smallest `m` with `m * (m + 1) / 2 >= n`.
4. **Recover the plan.** Keep every row of the table. At a state with `m` moves and `k` eggs left, drop from floor `h + f(m - 1, k - 1) + 1`, where `h` is the highest floor already known to survive (`0` at the start); that is `f(m - 1, k - 1)` floors above the lowest floor that is still open.
5. **Fewest eggs for a given number of moves.** Read the same table along the egg axis: the smallest `k` with `f(m, k) >= n`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `k=1, n=2` | `2` | the first example; one egg scans upward |
| 2 | `k=2, n=6` | `3` | the second example; two moves cover only 3 floors |
| 3 | `k=3, n=14` | `4` | the third example; exactly what 4 moves and 3 eggs can cover |
| 4 | `k=1, n=1` | `1` | the smallest valid input |
| 5 | `k=1, n=0` | `0` | outside the limits; no floors, and the loop must not run (fails with `<=`) |
| 6 | `k=1, n=10` | `10` | one egg forces a floor-by-floor scan; the log formula and the two-egg formula both give 4 |
| 7 | `k=2, n=1` | `1` | a spare egg on a one-floor building |
| 8 | `k=2, n=100` | `14` | two eggs; halving then scanning gives 50, and ignoring the eggs gives 7 |
| 9 | `k=3, n=25` | `5` | exactly what 5 moves and 3 eggs cover (`5 + 10 + 10`) |
| 10 | `k=3, n=26` | `6` | one floor past that cover |
| 11 | `k=2, n=10000` | `141` | two eggs at the upper bound for the floors (`141 * 142 / 2 = 10011`) |
| 12 | `k=100, n=10000` | `14` | the upper bounds for both; plain halving, and the two-egg formula gives 141 |
| 13 | `k=4, n=5000` | `19` | four eggs; the log formula gives 13 and the two-egg formula gives 100 |
