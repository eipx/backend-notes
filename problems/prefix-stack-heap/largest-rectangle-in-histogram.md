# Largest Rectangle in Histogram
`ref: LC 84` · Difficulty: Hard · Pattern: Monotonic stack with sentinel

## Problem
You are given a list of non-negative bar heights that together form a histogram: each bar has width 1, sits on a shared baseline, and the bars are placed side by side in the given order with no gaps. Find the area of the single largest axis-aligned rectangle that can be drawn entirely inside the outline formed by these bars. The rectangle's width is however many consecutive bars it spans, and its height is limited by the shortest bar in that span (since the rectangle cannot stick out above any bar it overlaps). Return that maximum area as a single integer.

## Constraints
- The number of bars can range from empty up to on the order of tens of thousands.
- Each bar height is a non-negative integer, bounded well within `int` range, and can be `0` (a bar with no height at all, which splits the histogram).
- Implication: with tens of thousands of bars, checking every possible (start, end) span directly is O(n^2) spans, and re-scanning each span for its minimum height naively makes it O(n^3) — both are too slow. Even the O(n^2) "expand from each bar" idea is borderline at the upper end of the size range, so an O(n) approach is the expected target.

## Worked examples
1. `heights = [4,2,6,7,3,4]` -> `12`. The bars at indices 2 and 3 (heights 6 and 7) both stand at least 6 tall, giving a 2-wide, 6-tall rectangle of area 12. No wider or taller combination beats this (a height-3 rectangle spanning indices 2 through 5 also reaches area 12, but nothing exceeds it).
2. `heights = [5,4,3,2,1]` -> `9`. The best choice uses only the first three bars (heights 5, 4, 3): the shortest of those three is 3, and 3 bars wide times height 3 gives area 9. Using more bars only lowers the limiting height faster than the extra width can compensate.
3. `heights = [1,1,1,1]` -> `4`. Every bar has the same height, so the best rectangle simply spans the entire row: width 4, height 1, area 4.
4. `heights = [5,5,0,5,5]` -> `10`. The middle bar has height 0, which fully splits the row into two independent halves. Each half is 2 bars of height 5, giving area 10 on either side; nothing can span across the zero-height gap.

## Edge cases checklist
- Empty input (no bars at all; answer is `0`).
- A single bar (answer is just that bar's own height).
- All bars the same height (best answer uses the full width).
- Strictly increasing heights (the best rectangle only ever uses a suffix of the bars).
- Strictly decreasing heights (the best rectangle only ever uses a prefix of the bars).
- One or more bars with height `0`, which act as hard breaks that no rectangle can cross.
- A histogram that is entirely zero-height bars (answer is `0`).
- A tie between two different spans producing the same maximum area (the algorithm only needs to report the value, not which span produced it).

## Approach

### Brute force
For every possible pair of start and end bar indices, find the minimum height in that range and multiply by the range's width, keeping the best result. Done naively (re-scanning the range for its minimum every time) this is O(n^3). A slightly smarter version expands outward from each bar as the limiting height, stopping as soon as a shorter bar is hit, which brings it down to O(n^2) — for every bar treated as the shortest one in its own rectangle, walk left and right while neighbors are at least as tall. At tens of thousands of bars, O(n^2) is on the order of hundreds of millions of operations, which is too slow for a fast solution, though it is a fine way to check correctness on small inputs.

### Optimal
Scan the bars left to right while maintaining a stack of bar indices with strictly increasing heights from bottom to top. When the next bar is shorter than the height at the top of the stack, that top bar can no longer grow any further to the right — pop it and compute the largest rectangle that uses it as the *limiting* (shortest) height: its right edge stops just before the current bar, and its left edge is bounded by whatever is now exposed at the new top of the stack (or the very start of the histogram, if the stack becomes empty). To avoid writing a separate cleanup loop after the main scan to flush whatever is still on the stack, append a single sentinel bar of height `0` at the very end of the (conceptual) array — since `0` is less than or equal to every real height, it forces every remaining bar to be popped and evaluated during the normal loop, with no special-cased final pass.

**Key invariant:** the stack always holds bar indices with strictly increasing heights, and every bar still on the stack represents a rectangle whose height could still grow taller. rightward, because no shorter bar has appeared yet.

Proof sketch: when a bar shorter than the current stack top appears, the top bar's rectangle cannot extend past the position just before the new, shorter bar — that shorter bar caps its height going forward. Its left boundary is exactly the position just after whatever bar is now exposed below it on the stack, because that exposed bar is the nearest bar to the left that is shorter than the one being popped (anything taller in between would already have been popped earlier, by the same rule, before this bar was ever pushed). So the width computed at pop time (`current index - new top index - 1`, or `current index` if the stack empties) is exactly the maximal span where the popped bar's height is the limiting one, which is exactly the definition of the best rectangle anchored at that height.

### Step-by-step trace
Trace on `heights = [4,2,6,7,3,4]` with the sentinel `0` appended, so the scanned array is `[4,2,6,7,3,4,0]`:

| i | height | pops (index:height -> width, area) | stack after (bottom to top, index:height) | max area so far |
|---|---|---|---|---|
| 0 | 4 | none, stack empty | `0:4` | 0 |
| 1 | 2 | pop 0:4 -> width=1 (stack empties), area=4 | `1:2` | 4 |
| 2 | 6 | none, 6 > 2 | `1:2, 2:6` | 4 |
| 3 | 7 | none, 7 > 6 | `1:2, 2:6, 3:7` | 4 |
| 4 | 3 | pop 3:7 -> width=4-2-1=1, area=7; pop 2:6 -> width=4-1-1=2, area=12 | `1:2, 4:3` | 12 |
| 5 | 4 | none, 4 > 3 | `1:2, 4:3, 5:4` | 12 |
| 6 (sentinel) | 0 | pop 5:4 -> width=6-4-1=1, area=4; pop 4:3 -> width=6-1-1=4, area=12; pop 1:2 -> width=6 (stack empties), area=12 | `6:0` | 12 |

Final maximum area is `12`, matching worked example 1.

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;

public class LargestRectangleInHistogram {

    // Returns the area of the largest rectangle that fits under the histogram outline.
    public static int solve(int[] heights) {
        int n = heights.length;
        // Build an extended array with a 0-height sentinel bar at the end.
        // The sentinel is <= every real height, so it forces the stack to fully drain
        // at the end without a separate cleanup loop after the main scan.
        int[] extended = new int[n + 1];
        System.arraycopy(heights, 0, extended, 0, n);
        extended[n] = 0;

        Deque<Integer> indexStack = new ArrayDeque<Integer>(); // ArrayDeque as stack; holds indices with increasing heights
        int maxArea = 0;
        for (int i = 0; i < extended.length; i++) {
            while (!indexStack.isEmpty() && extended[indexStack.peek()] > extended[i]) {
                int height = extended[indexStack.pop()];
                // width spans from just after the new stack top to i - 1;
                // if the stack is now empty, the rectangle reaches back to index 0
                int width = indexStack.isEmpty() ? i : i - indexStack.peek() - 1;
                int area = height * width;
                if (area > maxArea) {
                    maxArea = area;
                }
            }
            indexStack.push(i);
        }
        return maxArea;
    }
}
```

## Complexity
Time O(n): the sentinel-extended array has `n + 1` entries, and just as with the daily-temperatures pattern, every index is pushed exactly once and popped at most once, so the total work across all the `while` loops is bounded by `2(n+1)` — amortized O(n) overall, not O(n^2), even though a single iteration of the outer loop can trigger many pops. Space O(n) for the stack and the sentinel-extended copy of the array.

## Java 8 pitfalls for this problem
- Forgetting the sentinel (or an equivalent manual drain loop after the main scan) means bars that are never shorter than anything after them — for example a strictly increasing histogram — never get popped and never get evaluated, silently producing a wrong (too small) answer.
- `indexStack.peek()` on an empty deque returns `null` for `ArrayDeque`, not an exception — checking `!indexStack.isEmpty()` before dereferencing it (as the `while` condition does here) is required, and Java's left-to-right short-circuit evaluation of `&&` is what makes that check safe.
- Computing width as `i - indexStack.peek() - 1` assumes the stack is non-empty at that point; the ternary in the code above must check `isEmpty()` first, otherwise unboxing a `null` `Integer` throws `NullPointerException`.
- Using `java.util.Stack` here works but is a legacy synchronized class; `ArrayDeque` is the idiomatic choice for stack behavior in modern Java code, including Java 8.
- Height and width are both plain `int` here and their product (`height * width`) can be large for bigger inputs — for very large histograms this could approach `int` overflow, so if the constraint bounds were larger, computing the area as a `long` would be the safer choice; worth a comment even when `int` is currently sufficient.

## Wrong approaches and why they fail
1. **For each bar, only look at its immediate left and right neighbor to decide the rectangle's extent.** Counterexample: `heights = [2,1,2]` — bar 0 (height 2) has a shorter neighbor (height 1) immediately to its right, but the correct best rectangle uses all three bars at height 1 (since 1 is the minimum across the whole row), giving area 3; a "just check neighbors" rule never discovers that the height-1 rectangle can extend across the whole row.
2. **Sort the heights before processing them.** Sorting destroys the left-to-right adjacency that defines which bars are actually next to each other in the histogram — a rectangle's width depends entirely on original position, and sorting silently produces answers for a histogram that no longer exists. Counterexample: `heights = [6,2,5]` sorted becomes `[2,5,6]`, which would suggest bars of height 5 and 6 are adjacent and could combine into a width-2 rectangle of height 5 (area 10), but in the real, unsorted layout the height-2 bar sits between them and caps any rectangle spanning all three at height 2.
3. **Use a monotonic stack that pops on `>=` instead of `>`** (i.e., also pop equal-height bars immediately). This still produces a correct area value in most cases but can compute the wrong width for a run of equal heights, since popping an equal-height bar too early discards the information needed to later treat the whole equal-height run as one wide rectangle. Counterexample: `heights = [2,2,2]` — popping eagerly on equal heights can end up evaluating each `2` alone (area 2 each) instead of recognizing the full-width rectangle of area 6, depending on exactly how the width is computed after the premature pop.

## Variants
1. **Maximal rectangle of 1s in a binary matrix** (a well-known follow-up): treat each row of the matrix as the base of a histogram, where each cell's "height" is how many consecutive 1s are stacked above it (0 if the current cell is 0). Run this exact histogram algorithm once per row, updating the heights array as you go down, and keep the running maximum across all rows.
2. **Return the actual span (left and right bar indices), not just the area.** Track, alongside the max area, the popped height's index and the current left/right boundary whenever a new maximum is found.
3. **Support bars with negative height as "trenches" that a rectangle must avoid.** This changes the problem meaningfully (a plain histogram never has negative bars), and would require deciding whether a rectangle can span a trench at height 0 or must stop at it — worth treating as effectively the same as this problem's height-0 sentinel bars, which already act as hard splits.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[4,2,6,7,3,4]` | `12` | mixed heights, full multi-pop chain |
| 2 | `[]` | `0` | empty histogram |
| 3 | `[5]` | `5` | single bar |
| 4 | `[1,1,1,1]` | `4` | uniform low bars, full-width rectangle |
| 5 | `[5,4,3,2,1]` | `9` | strictly decreasing, best rectangle uses only a prefix |
| 6 | `[1,2,3,4,5]` | `9` | strictly increasing, best rectangle uses only a suffix |
| 7 | `[0,0,0]` | `0` | all-zero heights |
| 8 | `[2,2,2]` | `6` | uniform equal heights, full width used |
| 9 | `[3,6,5,7,4,8,1,0]` | `20` | complex pop chain with an explicit zero bar |
| 10 | `[5,5,0,5,5]` | `10` | zero bar splits the row into two equal blocks |
