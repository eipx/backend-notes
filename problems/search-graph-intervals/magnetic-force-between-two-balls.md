# Magnetic Force Between Two Balls
`ref: LC 1552` · Difficulty: Medium · Pattern: Binary search on the answer

## Problem
A row of baskets sits at given positions along a line (not necessarily sorted or evenly spaced). You must place `m` magnetic balls into `m` different baskets, at most one ball per basket. Two balls in different baskets repel each other with a force that gets weaker the farther apart they are, so the placement you want is the one that makes the two *closest* balls as far apart as possible. Report that maximum possible "minimum distance between any two balls" over every valid way of choosing `m` baskets.

## Constraints
- `2 <= position.length <= 10^5`
- `1 <= position[i] <= 10^9`, all positions distinct
- `2 <= m <= position.length`
- The answer's lower bound is `1` (two baskets can always be at least 1 apart once sorted, since positions are distinct integers) and its upper bound is `max(position) - min(position)` (the full span, achievable only when `m == 2`); with `n` up to `10^5`, an `O(n log(maxPosition))` binary search is required, since trying every possible minimum distance one at a time from `1` up to the full span would be far too slow.

## Worked examples
1. `position = [1,2,3,4,7]`, `m = 3` -> `3`. Sorted positions are already `[1,2,3,4,7]`. Placing balls at `1, 4, 7` gives gaps of `3` and `3`; no choice of 3 baskets can force both gaps to be `4` or larger.
2. `position = [5,4,3,2,1,1000000000]`, `m = 2` -> `999999999`. With only two balls to place, the best choice is always the two most extreme baskets once sorted (`1` and `1000000000`), giving the full span as the answer.
3. `position = [1,2,3,4,5,6,7]`, `m = 4` -> `2`. Placing balls at `1, 3, 5, 7` gives every adjacent gap exactly `2`; trying for a minimum gap of `3` only fits 3 balls (`1, 4, 7`), not 4.
4. `position = [1,3,6,10]`, `m = 3` -> `4`. Placing balls at `1, 6, 10` gives gaps `5` and `4`; no placement of 3 balls among these 4 baskets can make the smaller of the two gaps reach `5`.

## Edge cases checklist
- `m == 2`: the answer is always exactly `max(position) - min(position)`, since only the two extreme baskets matter.
- `m == position.length`: every basket gets a ball, so the answer is the minimum gap between consecutive baskets once sorted.
- The smallest possible input, `position.length == 2`.
- Unsorted input positions (the algorithm must sort first; the input is not guaranteed to arrive in position order).
- Positions clustered tightly together at one end and one isolated far-away basket (uneven spacing), where a naive "average gap" guess is badly wrong.
- Position values near the upper bound `10^9`, to confirm no overflow when computing the span or the binary search midpoint.

## Approach

### Brute force
Try every possible minimum distance `d` from `1` up to `max(position) - min(position)`, one at a time, and for each run the greedy feasibility check described below to see whether `m` balls can be placed with every pair at least `d` apart; take the largest `d` that works. With a span of up to `10^9 - 1`, checking every integer distance one at a time is far too slow.

### Optimal
Binary search directly on the answer distance `d`, over the range `[1, max(position) - min(position)]`.

**Key invariant:** the feasibility predicate `canPlace(d)` ("can `m` balls be placed, sorted left to right, with every adjacent pair of chosen baskets at least `d` apart?") is monotonically non-increasing as `d` increases -- so the set of distances satisfying `canPlace(d)` is a contiguous prefix of `[1, span]`, and binary search finds its right boundary (the largest `d` still in that prefix).

Proof sketch: raising the required minimum distance from `d` to `d+1` only ever makes the greedy left-to-right placement (keep taking the next basket at least `d` past the last placed ball) place the same number of balls or fewer, never more, since every basket that was reachable at gap `d` remains no closer at gap `d+1`. Because `canPlace(1)` is always true (distinct positions can trivially all be `>= 1` apart when `m <= n`) and `canPlace(span + 1)` is always false, the predicate flips from true to false exactly once as `d` increases, and binary search finds that boundary by moving `lo = mid` whenever `canPlace(mid)` holds (trying for something even larger) and `hi = mid - 1` otherwise.

### Step-by-step trace
Trace on `position = [1,3,6,10]`, `m = 3` (sorted already; `lo = 1`, `hi = 10 - 1 = 9`):

| step | lo | hi | mid (biased high) | canPlace(mid) simulation | feasible? | action |
|---|---|---|---|---|---|---|
| 1 | 1 | 9 | 5 | place 1 (count 1); 3-1=2<5 skip; 6-1=5>=5 place (count 2); 10-6=4<5 skip | count 2 < 3, no | hi = 4 |
| 2 | 1 | 4 | 3 | place 1 (count 1); 3-1=2<3 skip; 6-1=5>=3 place (count 2); 10-6=4>=3 place (count 3) | count 3 >= 3, yes | lo = 3 |
| 3 | 3 | 4 | 4 | place 1 (count 1); 3-1=2<4 skip; 6-1=5>=4 place (count 2); 10-6=4>=4 place (count 3) | count 3 >= 3, yes | lo = 4 |
| -- | 4 | 4 | -- | loop ends (lo == hi) | | return 4 |

## Java 8 solution
```java
import java.util.Arrays;

public static int solve(int[] position, int m) {
    int[] sorted = position.clone(); // never mutate the caller's array
    Arrays.sort(sorted);
    int lo = 1;
    int hi = sorted[sorted.length - 1] - sorted[0];
    while (lo < hi) {
        int mid = lo + (hi - lo + 1) / 2; // bias toward the upper half: we move lo=mid on success
        if (countPlaceable(sorted, mid) >= m) {
            lo = mid; // mid is achievable; try for something even larger
        } else {
            hi = mid - 1; // mid is too large a minimum gap to fit m balls
        }
    }
    return lo;
}

// Greedily places balls left to right, always taking the earliest basket that keeps
// the running gap at least minGap, and returns how many balls fit under that rule.
private static int countPlaceable(int[] sortedPositions, int minGap) {
    int count = 1;
    int last = sortedPositions[0]; // the first ball always goes in the leftmost basket
    for (int i = 1; i < sortedPositions.length; i++) {
        if (sortedPositions[i] - last >= minGap) {
            count++;
            last = sortedPositions[i];
        }
    }
    return count;
}
```

## Complexity
- Time: `O(n log n)` for the sort, plus `O(n log(span))` for the binary search (each of the `O(log(span))` iterations runs an `O(n)` feasibility scan) -- the sort is dominated by the binary search term for large spans, so this is usually quoted as `O(n log(maxPosition))`.
- Space: `O(n)` for the sorted copy; `O(1)` beyond that.

## Java 8 pitfalls for this problem
- The binary search here searches for the *largest* value satisfying a predicate ("find last true"), which needs `mid = lo + (hi - lo + 1) / 2` (biased toward the upper half) paired with `lo = mid` on success -- using the "find first true" bias (`mid = lo + (hi - lo) / 2` paired with `lo = mid` on success) can get stuck in an infinite loop, since `mid` can equal `lo` forever once `hi == lo + 1`.
- `position.clone()` before sorting avoids silently reordering the caller's original array; this problem doesn't need the original order preserved for its own logic, but mutating input arrays in place is a habit worth avoiding generally.
- The lower bound for binary search must start at `1`, not `0` -- a minimum gap of `0` is a trivially true predicate for any `m <= n` (every basket already satisfies "at least 0 apart") but is meaningless as a possible answer here since positions are guaranteed distinct.
- Computing `hi = sorted[n - 1] - sorted[0]` is always safe from overflow given `position[i] <= 10^9` fits comfortably in `int`, but the habit of double-checking subtraction bounds before relying on them is worth carrying into similar problems with larger value ranges.
- `countPlaceable` must scan the *sorted* array; running the same greedy scan over the original unsorted array silently produces a smaller, wrong answer instead of a compile error or exception, making this an easy mistake to miss without a dedicated test case.

## Wrong approaches and why they fail
1. **Reuse the "find first true" binary search template unmodified (as in Koko Eating Bananas or Capacity To Ship Packages Within D Days), moving `hi = mid` on success instead of `lo = mid`.** This problem's predicate is true for *small* distances and false for *large* ones (the opposite direction from those two problems), so blindly copying `hi = mid` on success converges to the *smallest* feasible distance instead of the largest, silently returning `1` (or some other far-too-small value) for almost every input.
2. **Estimate the answer as `(max(position) - min(position)) / (m - 1)` and return it directly, without verifying feasibility.** Counterexample: `position = [1,2,3,1000]`, `m = 3` -- the span is `999`, giving a naive average guess of about `499`, but placing 3 balls with every gap `>= 499` is impossible here (only 2 balls fit at that gap: `1` and `1000`); the true answer is `2`, achieved by `1, 3, 1000`. Averaging ignores how unevenly the baskets are actually distributed.
3. **Sort the positions, then always place balls at fixed evenly-spaced *index* intervals (e.g. every `n / m`-th basket) instead of running the greedy feasibility scan.** This can both overshoot (skip a basket that a tighter greedy placement could still have used) and undershoot (fail to place all `m` balls at all) whenever the basket spacing is uneven, since index-based spacing has no relationship to the actual position values.

## Variants
1. **Minimize the maximum gap instead of maximizing the minimum gap** (a different, unrelated objective sometimes confused with this one) -- that variant is closer to Capacity To Ship Packages Within D Days's family of "minimize the answer" binary searches, not this one.
2. **Balls of different "reach" (some balls can be placed regardless of gap, others require a larger minimum distance)** -- the feasibility check would need to consider per-ball constraints, no longer reducible to a single greedy left-to-right scan.
3. **Report one valid optimal placement, not just the achieved distance** -- after binary search finds the best `d`, re-run `countPlaceable`'s greedy scan once more at that `d`, this time recording which basket indices were chosen.

## Test cases
| # | position | m | expected | what it tests |
|---|---|---|---|---|
| 1 | `[1,2,3,4,7]` | 3 | 3 | standard case from the walkthrough |
| 2 | `[5,4,3,2,1,1000000000]` | 2 | 999999999 | unsorted input, m=2 forces the full span |
| 3 | `[1,2,3,4,5,6,7]` | 4 | 2 | evenly spaced baskets, m less than n |
| 4 | `[1,2]` | 2 | 1 | minimal input size |
| 5 | `[1,10]` | 2 | 9 | minimal input, larger span |
| 6 | `[1,3,6,10]` | 2 | 9 | m=2 forces the two extreme baskets |
| 7 | `[1,3,6,10]` | 3 | 4 | uneven spacing, exact trace case |
| 8 | `[79,74,57,22]` | 4 | 5 | m == n, answer is the minimum consecutive gap |
| 9 | `[3,6,9,12,15]` | 3 | 6 | evenly spaced baskets, exact average happens to work |
| 10 | `[1,1000000000]` | 2 | 999999999 | minimal input at the upper bound of position values |
