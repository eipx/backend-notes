# Container With Most Water
`ref: LC 11` · Difficulty: Medium · Pattern: two pointers converging from both ends, greedy elimination

## Problem

You are given an array `height` where `height[i]` represents the height of a vertical line drawn at horizontal position `i`. Choosing any two of these lines (by index) and using the x-axis as a base forms a container whose water-holding capacity is `(distance between the two indices) * (the shorter of the two heights)` — the container cannot hold more water than its shorter wall allows, and it spills over if you imagine trying to fill above the shorter line. Find the maximum such capacity over every possible pair of lines.

Input: an integer array `height` of length `n >= 2`.
Output: a single integer, the maximum capacity `(j - i) * min(height[i], height[j])` over all pairs `i < j`.

## Constraints

- `2 <= height.length <= 10^5`
- `0 <= height[i] <= 10^4`
- With n up to 1e5, checking every pair explicitly is O(n^2) ≈ 1e10 operations at the top end — too slow. An O(n) two-pointer scan is the expected complexity.

## Worked examples

1. Input: `[1,8,6,2,5,4,8,3,7]` → Output: `49`. The best pair is index 1 (height 8) and index 8 (height 7): width `8-1=7`, shorter wall `min(8,7)=7`, capacity `7*7=49`. No other pair beats this.
2. Input: `[4,3,2,1,4]` → Output: `16`. The best pair is the two end lines, both height 4: width `4-0=4`, shorter wall `4`, capacity `16`. Even though the middle values are much shorter, using the two tallest-and-widest-apart lines wins here.
3. Input: `[1,1]` → Output: `1`. Only one possible pair: width `1`, shorter wall `1`, capacity `1`.
4. Input: `[0,2]` → Output: `0`. Only one possible pair: width `1`, but one wall has height `0`, so the shorter wall caps capacity at `0` regardless of width.

## Edge cases checklist

- Minimum-size input (`n = 2`): exactly one pair, trivially the answer.
- A height of `0` present (that line contributes zero capacity for any pair that uses it as the limiting wall).
- All heights equal: the best pair is always the two endpoints (maximum possible width).
- Strictly increasing or strictly decreasing heights.
- The tallest lines both sitting at the two extreme ends already (best case is trivially width = n-1).
- The tallest lines both sitting adjacent to each other in the middle (best case requires favoring width over height at some point).
- Maximum height value (1e4) combined with maximum width (~1e5) as an overflow sanity check (`1e4 * 1e5 = 1e9`, still comfortably inside a 32-bit `int`, which maxes out around 2.1e9 — worth explicitly confirming no overflow, since it's close enough to be worth checking rather than assuming).
- Duplicate heights scattered throughout, to make sure the "move the shorter pointer" rule doesn't get confused by ties (when equal, moving either pointer is fine and the reference solution consistently moves `right` on ties since the condition is a strict `<`).

## Approach

### Brute force

Check every pair `(i, j)` with `i < j`, compute `(j - i) * min(height[i], height[j])` for each, and keep the max. This is O(n^2) time and O(1) extra space. At `n = 10^5`, that is on the order of `5 * 10^9` pair evaluations — too slow for a solution expected to run in about a second.

### Optimal

Start two pointers at the two ends of the array, `left = 0` and `right = n - 1`. Compute the capacity for the current pair, update the running best, and then move whichever pointer points at the *shorter* of the two current walls one step inward (on a tie, move either one — this reference implementation moves `right`). Repeat until the pointers meet.

**Key invariant:** at every step, the pointer sitting on the taller (or equal) wall never needs to move before the pointer on the shorter wall does, because moving the taller-wall pointer inward can never produce a larger capacity than what has already been captured for the current width.

Proof sketch: suppose at some step `height[left] <= height[right]` (the left wall is the limiting/shorter one, or they're tied). The current capacity is `(right - left) * height[left]`. Consider any pair `(left, r')` with `left < r' < right` — its width `r' - left` is strictly smaller than `right - left`, and its capacity is capped by `min(height[left], height[r']) <= height[left]` (since the left wall's height doesn't change and is already the smaller or equal one). So `(r' - left) * min(height[left], height[r']) <= (right - left) * height[left]`, meaning no pair using the current `left` and any index strictly between `left` and `right` can beat the capacity already recorded for `(left, right)`. This proves it is safe to permanently discard `right` as a future partner for `left` — but crucially, the algorithm instead advances `left` (the shorter-wall pointer), which is the pointer that has a chance of finding a *taller* wall and thus possibly a better capacity despite the reduced width; advancing `right` (the already-checked-as-suboptimal side) would provably waste a step. Repeating this argument at every step means every pair that gets skipped is provably no better than one already recorded, so the true maximum is never skipped, and the scan safely covers the full search space in O(n) instead of O(n^2).

### Step-by-step trace

Trace on `[1,8,6,2,5,4,8,3,7]` (expected `49`). Indices 0..8.

| left | right | height[left] | height[right] | width | shorter | area | best | move |
|---|---|---|---|---|---|---|---|---|
| 0 | 8 | 1 | 7 | 8 | 1 | 8 | 8 | left++ (1<7) |
| 1 | 8 | 8 | 7 | 7 | 7 | 49 | 49 | right-- (7<8 is false, so move right) |
| 1 | 7 | 8 | 3 | 6 | 3 | 18 | 49 | right-- |
| 1 | 6 | 8 | 8 | 5 | 8 | 40 | 49 | right-- (tie, moves right) |
| 1 | 5 | 8 | 4 | 4 | 4 | 16 | 49 | right-- |
| 1 | 4 | 8 | 5 | 3 | 5 | 15 | 49 | right-- |
| 1 | 3 | 8 | 2 | 2 | 2 | 4 | 49 | right-- |
| 1 | 2 | 8 | 6 | 1 | 6 | 6 | 49 | right-- |
| 1 | 1 | — | — | — | — | — | 49 | loop ends (left==right) |

Final best: `49`.

## Java 8 solution

```java
public class ContainerWithMostWater {

    public static int solve(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int best = 0;
        while (left < right) {
            int width = right - left;
            int shorter = Math.min(height[left], height[right]);
            int area = width * shorter;
            best = Math.max(best, area);
            // moving the taller pointer only shrinks the width while the cap stays the same
            // wall or gets worse, so it can never beat the current area; only moving the
            // shorter pointer has any chance of finding a taller wall
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{1,8,6,2,5,4,8,3,7}, 49},
            {new int[]{1,1}, 1},
            {new int[]{4,3,2,1,4}, 16},
            {new int[]{1,2,1}, 2},
            {new int[]{2,2,2,2}, 6},
            {new int[]{1,2,4,3}, 4},
            {new int[]{0,2}, 0},
            {new int[]{5,4,3,2,1}, 6},
            {new int[]{1,2,3,4,5}, 6},
            {new int[]{10000,10000}, 10000}
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

- Time: O(n). `left` and `right` move toward each other and the loop stops the moment they meet, so together they take at most `n` total steps across the whole run.
- Space: O(1). Only a constant number of scalar variables are used regardless of input size.

## Java 8 pitfalls for this problem

- `Integer.compare` vs raw subtraction: not directly relevant here since we never compute a difference for comparison purposes that could overflow, but it's worth noting `width * shorter` (up to `1e5 * 1e4 = 1e9`) safely fits in a 32-bit `int` (max ~2.1e9) — if the bounds were even slightly larger this would need a `long`, so it's worth double-checking rather than assuming.
- Off-by-one on the loop condition: it must be `left < right` (strict), never `left <= right`, since a pointer cannot form a container with itself (zero width, and more importantly it would double-process the final position or under/over-count).
- Ties: `height[left] < height[right]` is a strict comparison, so when the two walls are exactly equal, `right` moves. This is a deliberate, safe choice — moving either pointer on a tie is provably fine (both walls are equally limiting, so neither side is "more wasted" than the other) — but it is worth being able to state out loud, unprompted, that the tie-breaking direction doesn't affect correctness, since it is a detail reviewers of this kind of solution commonly probe.
- Boxed `Integer` comparisons: not an issue here since everything stays as primitive `int`, but if this were rewritten to store heights as `List<Integer>`, comparing with `<` still auto-unboxes safely — the real danger would only appear with `==` comparisons on boxed values, which this solution never does.

## Wrong approaches and why they fail

- **Always move the pointer with the taller wall, on the theory that "the short wall is already the bottleneck so keep it."** Counterexample: `[1,8,6,2,5,4,8,3,7]` — starting at `left=0` (height 1) and `right=8` (height 7), the *shorter* wall is on the left (height 1), not the tall one; moving the taller pointer (`right`, height 7) first would immediately throw away the pairing with index 1 (height 8) at full width, which is exactly where the true answer of 49 comes from. The correct rule is to move the pointer at the shorter wall, not the taller one.
- **Greedily pick the two tallest lines in the array regardless of their positions.** Counterexample: `[1,2,4,3]` — the two tallest lines are height 4 (index 2) and height 3 (index 3), adjacent to each other, giving width 1 and capacity `1*3=3`; but the true best pair is index 1 (height 2) and index 3 (height 3), width 2, capacity `4`, which beats the "tallest two" approach because width matters just as much as height.
- **Assume the answer must use at least one of the two global endpoints.** Counterexample: `[1,8,6,2,5,4,8,3,7]` — the optimal pair here is indices 1 and 8, and index 8 IS the last element, so this particular example doesn't disprove it, but consider `[5,1,1,1,1,1,1,1,5,100]` conceptually: with a very tall spike not at the very end, or more simply `[6,1,5]` — pairs are `(6,1)` width1 cap1, `(6,5)` width2 cap min(6,5)*2=10, `(1,5)` width1 cap1 — best is the endpoints here too, but in general a tall middle spike paired with a moderately tall nearby wall can beat an endpoint pairing when the endpoints themselves are short; the two-pointer method handles this correctly without any endpoint assumption, which is precisely why the assumption is unsafe to hardcode.

## Variants

- **Return the pair of indices achieving the maximum, not just the capacity.** Track `bestLeft`/`bestRight` alongside `best`, updating them together whenever a new maximum capacity is found.
- **Container must be built from at least 3 lines (a more complex shape, not just two walls).** This is a fundamentally different problem (related to computing an area under a more complex boundary) and does not reduce to the simple two-pointer approach; it typically needs a different technique such as computing running prefix maxima (see the related Trapping Rain Water problem for a structurally similar but distinct calculation).
- **Heights can be floating-point instead of integer.** The two-pointer algorithm and its correctness proof are unaffected by the numeric type; only the area computation and comparisons need to use `double` instead of `int`, with the usual floating-point equality caution if ties are ever compared directly.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[1,8,6,2,5,4,8,3,7]` | 49 | classic example, best pair not at the array ends |
| 2 | `[1,1]` | 1 | minimum size input, single pair |
| 3 | `[4,3,2,1,4]` | 16 | best pair is the two endpoints |
| 4 | `[1,2,1]` | 2 | odd-length small array |
| 5 | `[2,2,2,2]` | 6 | all heights equal, width maximized at the endpoints |
| 6 | `[1,2,4,3]` | 4 | tallest two lines are adjacent but not optimal |
| 7 | `[0,2]` | 0 | zero height caps capacity at zero |
| 8 | `[5,4,3,2,1]` | 6 | strictly decreasing heights |
| 9 | `[1,2,3,4,5]` | 6 | strictly increasing heights |
| 10 | `[10000,10000]` | 10000 | maximum height bound, overflow sanity check |
