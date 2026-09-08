# Koko Eating Bananas
`ref: LC 875` · Difficulty: Medium · Pattern: Binary search on the answer

## Problem
There are several piles of bananas in a row, given as an array of positive integers where each value is the number of bananas in that pile. A fixed number of hours are available before a guard returns. During each hour, exactly one pile is chosen and up to `k` bananas are eaten from it, where `k` is a single fixed eating speed picked once in advance (bananas per hour). If the chosen pile has fewer than `k` bananas left, all of them are eaten that hour and the remaining time in that hour is wasted (it cannot be used to start a second pile). If a pile has more than `k` bananas, only `k` are eaten and the rest stays for a later hour.

The output is the smallest integer eating speed `k` such that it is possible to eat every banana in every pile within the given number of hours. "Valid" means: summing, over every pile, the number of hours needed to finish that pile alone at speed `k` (that is `ceil(pile / k)`), the total does not exceed the hour budget.

## Constraints
- `1 <= piles.length <= 10^4`
- `piles[i]` is a positive integer, `1 <= piles[i] <= 10^9`
- `piles.length <= h <= 10^9` (there are always at least as many hours as piles, so a valid speed always exists)
- With `n` up to `10^4` and pile sizes up to `10^9`, an `O(n log(max(piles)))` binary search (roughly `10^4 * 30`) is required; scanning every possible speed one at a time is off the table.

## Worked examples
1. `piles = [3,6,7,11]`, `h = 8` -> `4`. At speed 4 the hours are `ceil(3/4)+ceil(6/4)+ceil(7/4)+ceil(11/4) = 1+2+2+3 = 8`, exactly the budget; speed 3 needs `1+2+3+4=10` hours, which is too many.
2. `piles = [30,11,23,4,20]`, `h = 5` -> `30`. There are exactly 5 piles and 5 hours, so at most one hour is available per pile; the speed must clear the largest pile, `30`, in a single hour.
3. `piles = [30,11,23,4,20]`, `h = 6` -> `23`. One extra hour lets the 30-pile spill into two hours (`ceil(30/23)=2`), so the bottleneck pile only needs to be finished within 2 hours, dropping the required speed to the second-largest pile's rounded need.

## Edge cases checklist
- A single pile (`piles.length == 1`).
- `h == piles.length`: exactly one hour per pile, so the answer must be `max(piles)`.
- `h` far larger than `piles.length`: the answer can bottom out at the true minimum, `k = 1`.
- All piles the same size.
- A pile so large that `pile / k` computed with truncating division instead of ceiling division under-counts hours (the ceil-division trap).
- Very large pile values (up to `1e9`) summed across up to `1e4` piles -- the total-hours accumulator must be a `long`, not an `int`.

## Approach
### Brute force
Try every possible speed `k = 1, 2, 3, ...` up to `max(piles)`, and for each compute the total hours needed in `O(n)`. This is `O(n * max(piles))`, which with `n = 10^4` and `max(piles) = 10^9` is on the order of `10^13` operations -- many orders of magnitude too slow for any time limit.

### Optimal
Binary search directly on the answer speed `k`, over the range `[1, max(piles)]`.

**Key invariant:** the function `hours(k) = sum(ceil(pile / k))` is monotonically non-increasing as `k` increases, so the set of speeds satisfying `hours(k) <= h` is a contiguous suffix of `[1, max(piles)]`, and binary search finds its left boundary.

Proof sketch: increasing `k` by 1 can never increase `ceil(pile / k)` for any individual pile (a faster eating speed never needs more hours to clear the same pile), so the sum over all piles is also non-increasing. Because the predicate `hours(k) <= h` is `false` for small `k` and `true` for large `k` with no interruption in between, a standard "find first true" binary search on `lo = 1, hi = max(piles)` converges to the minimum valid speed, moving `hi = mid` when the predicate holds and `lo = mid + 1` when it does not.

### Step-by-step trace
Trace on `piles = [3,6,7,11]`, `h = 8` (`lo = 1`, `hi = 11`):

| step | lo | hi | mid | hours(mid) | hours <= 8? | action |
|---|---|---|---|---|---|---|
| 1 | 1 | 11 | 6 | ceil(3/6)+ceil(6/6)+ceil(7/6)+ceil(11/6)=1+1+2+2=6 | yes | hi = 6 |
| 2 | 1 | 6 | 3 | 1+2+3+4=10 | no | lo = 4 |
| 3 | 4 | 6 | 5 | 1+2+2+3=8 | yes | hi = 5 |
| 4 | 4 | 5 | 4 | 1+2+2+3=8 | yes | hi = 4 |
| -- | 4 | 4 | -- | loop ends (lo == hi) | | return 4 |

## Java 8 solution
```java
public static int solve(int[] piles, int h) {
    int lo = 1, hi = 0;
    for (int p : piles) {
        hi = Math.max(hi, p); // upper bound: eating the biggest pile in one hour is always enough
    }
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2; // avoids (lo+hi) overflow, not that it matters at these bounds
        if (hoursNeeded(piles, mid) <= h) {
            hi = mid; // mid works: the answer is mid or something smaller
        } else {
            lo = mid + 1; // mid is too slow: the answer must be larger
        }
    }
    return lo;
}

private static long hoursNeeded(int[] piles, int k) {
    long hours = 0; // must be long: up to 1e4 piles each needing up to 1e9 hours
    for (int p : piles) {
        hours += (p + k - 1) / k; // ceil division without floating point
    }
    return hours;
}
```

## Complexity
- Time: `O(n log(max(piles)))` -- the binary search runs `O(log(max(piles)))` iterations, and each iteration's `hoursNeeded` scan is `O(n)`.
- Space: `O(1)` beyond the input array -- only a few scalar accumulators are kept.

## Java 8 pitfalls for this problem
- `(p + k - 1) / k` is the standard integer ceiling-division idiom; writing `p / k` truncates and silently returns a `k` that is one hour short of enough.
- `hours` must be a `long`. With `piles[i]` up to `1e9` and `k` as low as `1`, a single pile can demand `1e9` hours; summed over `1e4` piles that can reach `1e13`, which overflows `int` (max about `2.1e9`) and wraps to a negative or garbage value, breaking the `<= h` comparison.
- `mid = lo + (hi - lo) / 2` is used instead of `(lo + hi) / 2` out of habit; at these bounds (`hi <= 1e9`) `lo + hi` cannot overflow `int` either, but the habit prevents bugs when bounds are copy-pasted into a problem with larger limits.
- `Math.max` reduction to find `hi` avoids a separate `Arrays.stream(piles).max()` call, which requires boxing/unboxing overhead and is easy to get wrong with `OptionalInt` in Java 8 (no `Optional.isEmpty()` in Java 8, only `isPresent()`).
- Comparing the loop result with the boxed test's expected `int` value should use `Integer.compare` or `.equals`, never `==`, if the test harness stores expectations as `Integer`.

## Wrong approaches and why they fail
- **Linear scan from k = 1 upward:** stop at the first `k` where `hours(k) <= h`. Correct in principle but `O(n * max(piles))` in the worst case; for `piles = [1000000000]`, `h = 2`, this scans roughly 500 million possible speeds before stopping.
- **Binary search on the wrong bound, e.g. `hi = sum(piles)`:** this still works, but wastes iterations since `sum(piles)` can be far larger than `max(piles)` (e.g. many equal small piles), and it obscures the real invariant that the true upper bound is "finish the single biggest pile in one hour."
- **Truncating division on `hours` combined with the same search:** for `piles=[3,6,7,11], h=8`, computing `hours += p / k` instead of ceiling division makes `hours(4) = 0+1+1+2=4 <= 8`, which looks valid, so the search converges to a speed that under-counts hours and would not actually finish every pile in time -- the monotone predicate the whole search depends on silently breaks.

## Variants
- **LC 1011 Capacity To Ship Packages Within D Days** uses the identical binary-search-on-the-answer shape, but the predicate walks the array greedily accumulating a running load instead of summing independent per-item ceilings -- see the companion note in this folder.
- **Minimize the maximum speed subject to a per-pile hour cap** (each pile individually must finish within some `h_i`, not just the sum): the predicate changes to a per-pile check `ceil(pile_i / k) <= h_i` for all `i`, still monotone in `k`, so the same binary search skeleton applies with a different feasibility function.
- **Two guards eating in parallel at possibly different speeds:** this breaks the simple binary search because now there are two unknowns; it becomes an assignment/partition problem rather than a single monotone predicate, and typically needs a different technique (fixing one speed and binary searching the other, or DP over pile subsets for small `n`).

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | piles=[3,6,7,11], h=8 | 4 | standard case from the walkthrough |
| 2 | piles=[30,11,23,4,20], h=5 | 30 | h == n forces k = max(piles) |
| 3 | piles=[30,11,23,4,20], h=6 | 23 | one spare hour lowers the bottleneck |
| 4 | piles=[1,1,1,1], h=4 | 1 | uniform small piles, h == n |
| 5 | piles=[1000000000], h=2 | 500000000 | single huge pile, exercises long-accumulator math |
| 6 | piles=[1], h=1 | 1 | smallest possible input |
| 7 | piles=[312884470], h=968709470 | 1 | h far exceeds need, answer bottoms out at k=1 |
| 8 | piles=[3,6,7,11], h=4 | 11 | h == n again, different pile shape |
| 9 | piles=[5,5,5,5,5], h=5 | 5 | all-equal piles, h == n |
| 10 | piles=[2,10,3], h=3 | 10 | h == n, forces max even though other piles are small |
