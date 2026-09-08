# Trapping Rain Water
`ref: LC 42` · Difficulty: Hard · Pattern: two pointers converging from both ends, tracking running left/right maxima

## Problem

You are given an array `height` describing an elevation map: `height[i]` is the height of a unit-width bar/wall at horizontal position `i`, with no gaps between adjacent positions. If it rains, water collects in the "valleys" between bars, and the water sitting above position `i` is bounded above by the shorter of the tallest wall to its left (anywhere at or before `i`) and the tallest wall to its right (anywhere at or after `i`) — water simply cannot pile higher than the lower of the two surrounding barriers, and any water above `height[i]` itself but below that ceiling is trapped there. Compute the total volume of water trapped across the entire map after it rains.

Input: an integer array `height` of length `n >= 0`.
Output: a single non-negative integer, the total trapped water, computed as `sum over all i of max(0, min(leftMax[i], rightMax[i]) - height[i])`, where `leftMax[i]` is the maximum height in `height[0..i]` and `rightMax[i]` is the maximum height in `height[i..n-1]`.

## Constraints

- `0 <= height.length <= 2 * 10^4`
- `0 <= height[i] <= 10^5`
- With n up to 2*10^4, an O(n) approach comfortably fits typical time limits; even an O(n) with a constant-factor-heavy implementation is fine at this size, but a naive O(n^2) approach (recomputing left/right maxima by scanning outward from every position) is roughly `4 * 10^8` in the worst case — borderline to slow depending on the time budget, and clearly beaten by simpler O(n) or O(n) two-pointer alternatives.

## Worked examples

1. Input: `[0,1,0,2,1,0,1,3,2,1,2,1]` → Output: `6`. Several small pockets trap water: e.g. position 2 (height 0) is between a wall of height 1 (left) and eventually height 2 further right, trapping 1 unit there; position 5 (height 0) sits between walls of height 2 (or higher, e.g. the height-3 wall further right) and height 1, trapping water as well. Summing every position's trapped amount gives 6.
2. Input: `[4,2,0,3,2,5]` → Output: `9`. The tallest wall overall is 5 (rightmost), and there's a wall of height 4 near the left; the low points at heights 2, 0, 3, 2 in between each get capped by `min(leftMax, rightMax)` at their position, and summing those capped amounts gives 9.
3. Input: `[5,5]` → Output: `0`. Two equal-height walls next to each other with nothing between them — there is no basin at all, only two adjacent positions with no gap to hold water.
4. Input: `[1,2,3,4,5]` → Output: `0`. Strictly increasing terrain never has a wall to its right taller than everything before a given point combined with a wall to its left, so nothing is ever trapped — water would simply run off the low (left) end since there's no right-side barrier lower than the terrain height at any interior point (every point's right side is all taller than it, but its LEFT side never has anything taller than itself either except immediately-preceding bars, so the binding constraint, the left max, never exceeds the local height... more directly: monotone terrain has no basin shape at all).

## Edge cases checklist

- Empty array or array with fewer than 3 elements: no basin is geometrically possible, answer is always `0`.
- Strictly increasing or strictly decreasing terrain: answer is always `0` (no basin shape).
- All heights equal (a flat plateau): answer is `0` (nothing to hold water above a flat surface).
- A single very tall spike surrounded by zeros on both sides with equal-height shoulders (symmetric basin).
- Multiple separate basins in one array, separated by peaks (must sum contributions from all of them, not just the largest).
- A basin whose two bounding walls are of different heights (the answer is governed by the *shorter* of the two, not their average or the taller one).
- Zero-height bars mixed among taller ones.
- Maximum height bound (1e5) to sanity-check no overflow when summing across up to 2*10^4 positions (`2*10^4 * 1e5 = 2*10^9`, which is right at the edge of a 32-bit `int`'s positive range of ~2.1*10^9 — worth flagging as a case where a real system might prefer summing into a `long` defensively, even though it technically still fits for a plain Java `int` at these exact bounds).

## Approach

### Brute force

For every position `i`, scan leftward to find the tallest wall at or before `i` (`leftMax`), scan rightward to find the tallest wall at or after `i` (`rightMax`), and add `max(0, min(leftMax, rightMax) - height[i])` to the running total. This is O(n) work per position, O(n^2) overall. At `n = 2*10^4`, that's about `4*10^8` operations, likely too slow for a tight time budget even though it's not astronomically large.

A better-but-still-not-optimal approach precomputes `leftMax[]` and `rightMax[]` arrays in two O(n) passes, then does a single O(n) pass to sum contributions — this is O(n) time but O(n) *extra* space for the two auxiliary arrays.

### Optimal

Use two pointers, `left` starting at index 0 and `right` starting at index `n-1`, along with two running values `leftMax` and `rightMax` (both initialized to 0). At each step, compare `height[left]` and `height[right]`. Whichever side currently has the smaller height is the side that is safe to resolve: if it's `left`, either update `leftMax` (if `height[left]` is a new high on that side) or add `leftMax - height[left]` to the trapped total, then advance `left`; symmetric logic applies to `right`. Continue until the pointers meet.

**Key invariant:** whenever we process the side with the smaller current height (say `height[left] < height[right]`), the true `rightMax` for position `left` (the tallest wall anywhere from `left` to the end of the array) is guaranteed to be at least `height[right] > height[left] >= leftMax` at that moment — so the binding constraint at position `left` is `leftMax`, not the true (possibly still-unknown) `rightMax`, and it is safe to resolve `left`'s contribution using only `leftMax`.

Proof sketch: at the moment we compare `height[left]` and `height[right]` and find `height[left] < height[right]`, we know two things: first, `rightMax` (the running max of everything scanned so far from the right side) is a lower bound on the *true* maximum height anywhere in `[left, n-1]`, since it was built by scanning inward from the right and every position it has visited is `>= right`, all of which lie within `[left, n-1]`; and `rightMax >= height[right] > height[left]`. Second, we don't yet know the true maximum of the *entire* right side beyond what's been scanned, but we don't need to: since `height[right] > height[left]`, and `height[right]` itself lies within `[left, n-1]`, the true right-side maximum for position `left` is *at least* `height[right]`, which already exceeds `height[left]`. Combined with `leftMax` being the exact true maximum of everything in `[0, left]` (since that side has been fully scanned up to `left`), the binding minimum in `min(trueLeftMax[left], trueRightMax[left])` must be `trueLeftMax[left] = leftMax`, because the right side is already known to be taller than `height[left]` regardless of what lies further right. So `min(leftMax, rightMax_true) - height[left]` simplifies to exactly `leftMax - height[left]` (or `0` if `height[left] >= leftMax`, handled by updating `leftMax` instead of adding to the total), which is precisely what the algorithm computes — no information about the unscanned portion of the right side is actually needed for this position.

### Step-by-step trace

Trace on `[0,1,0,2,1,0,1,3,2,1,2,1]` (expected `6`). Indices 0..11.

| left | right | height[left] | height[right] | leftMax | rightMax | action | water added | running total |
|---|---|---|---|---|---|---|---|---|
| 0 | 11 | 0 | 1 | 0 | 0 | left<right → left side: 0>=leftMax(0) → leftMax=0; left++ | 0 | 0 |
| 1 | 11 | 1 | 1 | 0 | 0 | tie → treat as left side: 1>=leftMax(0) → leftMax=1; left++ | 0 | 0 |
| 2 | 11 | 0 | 1 | 1 | 0 | left side (0<1... wait compare heights: 0 vs 1, left smaller) → 0<leftMax(1) → add 1-0=1; left++ | 1 | 1 |
| 3 | 11 | 2 | 1 | 1 | 0 | right side (1<2) → 1>=rightMax(0) → rightMax=1; right-- | 0 | 1 |
| 3 | 10 | 2 | 2 | 1 | 1 | tie → left side: 2>=leftMax(1) → leftMax=2; left++ | 0 | 1 |
| 4 | 10 | 1 | 2 | 2 | 1 | left side (1<2) → 1<leftMax(2) → add 2-1=1; left++ | 1 | 2 |
| 5 | 10 | 0 | 2 | 2 | 1 | left side (0<2) → 0<leftMax(2) → add 2-0=2; left++ | 2 | 4 |
| 6 | 10 | 1 | 2 | 2 | 1 | left side (1<2) → 1<leftMax(2) → add 2-1=1; left++ | 1 | 5 |
| 7 | 10 | 3 | 2 | 2 | 1 | right side (2<3) → 2>=rightMax(1) → rightMax=2; right-- | 0 | 5 |
| 7 | 9 | 3 | 1 | 2 | 2 | right side (1<3) → 1<rightMax(2) → add 2-1=1; right-- | 1 | 6 |
| 7 | 8 | 3 | 2 | 2 | 2 | tie → left side: 3>=leftMax(2) → leftMax=3; left++ | 0 | 6 |
| 8 | 8 | — | — | — | — | left==right, loop ends | — | 6 |

Final total: `6`.

## Java 8 solution

```java
public class TrappingRainWater {

    public static int solve(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                // the taller wall guaranteeing the trap is on the right side somewhere at or
                // beyond `right`, so the water level at `left` is capped only by leftMax
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }
        return water;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{0,1,0,2,1,0,1,3,2,1,2,1}, 6},
            {new int[]{4,2,0,3,2,5}, 9},
            {new int[]{}, 0},
            {new int[]{5}, 0},
            {new int[]{5,5}, 0},
            {new int[]{1,2,3,4,5}, 0},
            {new int[]{5,4,3,2,1}, 0},
            {new int[]{2,0,2}, 2},
            {new int[]{3,0,3,0,3}, 6},
            {new int[]{4,4,4,4}, 0}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] height = (int[]) cases[i][0];
            int expected = (Integer) cases[i][1];
            int got = solve(height);
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

- Time: O(n). `left` and `right` move toward each other and the loop stops the moment they meet, so together they take at most `n` total steps.
- Space: O(1). Only a constant number of scalar variables are used, avoiding the O(n) auxiliary `leftMax[]`/`rightMax[]` arrays that the precompute-then-sum approach needs.

## Java 8 pitfalls for this problem

- Off-by-one and boundary handling: `height.length == 0` (and by extension `1` or `2`) must return `0` cleanly rather than crash or misbehave — the loop condition `left < right` already handles lengths of 0 or 1 correctly (loop body never executes), but it's worth confirming explicitly rather than assuming.
- Confusing "the wall directly at this position" with "the running max seen so far" — `leftMax`/`rightMax` are *maxima up to and including the current pointer position*, not just the single current bar's height; conflating the two is a common source of off-by-one-style logic errors here.
- Overflow: summing up to `2*10^4` positions each contributing up to `1e5` gives a theoretical max around `2*10^9`, right at the edge of `int`'s positive range (~2.147*10^9); this specific problem's actual achievable maximum trapped water is well under that theoretical worst case in practice, but a defensive real-world implementation might still choose to accumulate into a `long` rather than rely on the exact bound holding.
- Ties (`height[left] == height[right]`): the `if (height[left] < height[right])` branch is false on a tie, so ties fall into the `else` (right-side) branch here — this is a safe, arbitrary choice (either side is provably fine to process first on a tie, following the same proof sketch as above), but it's worth being able to justify rather than treating it as coincidental.
- Do not confuse this problem with Container With Most Water — that problem only cares about the best *single pair* of walls and ignores everything between them; this problem sums contributions from *every* interior position simultaneously. Reusing one problem's two-pointer rule for the other without adjusting the accumulation logic is a common mix-up given how similar the setups look.

## Wrong approaches and why they fail

- **Reuse the Container With Most Water two-pointer rule unchanged (just track a single best area) instead of accumulating per-position trapped water.** Counterexample: `[0,1,0,2,1,0,1,3,2,1,2,1]` — Container-style logic would only ever report information about the single best pair of walls, never the sum of many small pockets of trapped water between every pair of local peaks; the two problems share a two-pointer skeleton but compute fundamentally different quantities.
- **Compute `min(leftMax, rightMax) - height[i]` for each position using only *locally adjacent* neighbors instead of the true running max from each side.** Counterexample: `[3,0,3,0,3]` — at position 1 (height 0), the immediate left neighbor is height 3 and immediate right neighbor is height 0, which would incorrectly suggest a low ceiling; the correct `rightMax` for position 1 must look all the way to position 4 (height 3), not just position 2, because water is bounded by the tallest wall anywhere to that side, not merely the adjacent bar.
- **Assume the two-pointer optimization is unnecessary and always precompute full `leftMax[]`/`rightMax[]` arrays, treating the two-pointer version as a pure style preference.** This isn't a correctness bug, but treating the O(n) extra space version as strictly equivalent misses the space-complexity distinction — the two-pointer approach achieves the same O(n) time in O(1) space, which is the actual point of the optimal approach, not just "yet another O(n) way."

## Variants

- **Return the water level (or a 2D grid) rather than the total volume, for a 2D "trapping rain water II" style extension.** This is a materially different problem: extending to two dimensions requires a priority-queue-based approach that processes cells from the boundary inward in order of height (a min-heap "flood fill" from the perimeter), since the simple two-pointer left/right argument does not generalize past one dimension.
- **Report the trapped water at each individual position as an array, not just the sum.** Track the same `leftMax`/`rightMax` two-pointer scan but write `leftMax - height[left]` (or the right-side equivalent, each clamped to `>= 0`) into an output array at the corresponding index instead of accumulating a single running total.
- **The elevation map can change over time (bars added/removed) and you need the trapped-water total after each update.** The simple linear two-pointer scan does not support efficient incremental updates; this variant typically needs a different data structure such as a monotonic-stack-based approach combined with segment-tree-style range-maximum queries to avoid recomputing everything from scratch after every change.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[0,1,0,2,1,0,1,3,2,1,2,1]` | 6 | classic multi-basin example |
| 2 | `[4,2,0,3,2,5]` | 9 | asymmetric bounding walls, deep basin |
| 3 | `[]` | 0 | empty input |
| 4 | `[5]` | 0 | single element, no basin possible |
| 5 | `[5,5]` | 0 | two equal elements, no gap to trap water |
| 6 | `[1,2,3,4,5]` | 0 | strictly increasing, no basin shape |
| 7 | `[5,4,3,2,1]` | 0 | strictly decreasing, no basin shape |
| 8 | `[2,0,2]` | 2 | simplest possible single basin |
| 9 | `[3,0,3,0,3]` | 6 | two separate basins with equal bounding walls, sums both |
| 10 | `[4,4,4,4]` | 0 | flat plateau, nothing to trap |
