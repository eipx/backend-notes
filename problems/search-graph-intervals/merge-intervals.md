# Merge Intervals
`ref: LC 56` · Difficulty: Medium · Pattern: Sort by start, sweep and merge

## Problem
An array of intervals is given, where each interval is a pair `[start, end]` with `start <= end`, representing an inclusive range on the number line. The intervals are not guaranteed to be sorted or non-overlapping.

The output is a new array of intervals that covers exactly the same total range as the input, but with every pair of overlapping (or touching) intervals combined into a single interval, so that no two intervals in the output overlap or touch, and the output is sorted by start. "Overlapping or touching" means two intervals `[s1, e1]` and `[s2, e2]` (with `s1 <= s2`) must be merged whenever `s2 <= e1` -- including the boundary case where `s2 == e1` exactly (they touch at a single point but still count as connected).

## Constraints
- `1 <= intervals.length <= 10^4`
- `intervals[i].length == 2`
- `0 <= start <= end <= 5 * 10^4` (values are bounded and non-negative in the canonical version of this problem)
- With `n` up to `10^4`, an `O(n log n)` sort followed by an `O(n)` linear merge pass is required and sufficient; anything that compares every pair of intervals directly (`O(n^2)`) would be needlessly slow, though still technically within many time limits at this exact bound -- the point of this problem is the sort-then-sweep pattern, not raw performance.

## Worked examples
1. `intervals = [[1,3],[2,6],[8,10],[15,18]]` -> `[[1,6],[8,10],[15,18]]`. After sorting by start (already sorted here), `[1,3]` and `[2,6]` overlap because `2 <= 3`, merging into `[1,6]`; `[8,10]` does not overlap `[1,6]` because `8 > 6`, so it starts a new group, and likewise `[15,18]`.
2. `intervals = [[1,4],[4,5]]` -> `[[1,5]]`. The intervals only touch at the single point `4` (`4 <= 4`), but per the problem's inclusive-touching definition they still merge into one continuous range.
3. `intervals = [[1,4],[0,4]]` -> `[[0,4]]`. The input is not sorted by start; sorting first produces `[0,4],[1,4]`, and since `1 <= 4` they merge, with the merged end taking `max(4,4) = 4`.

## Edge cases checklist
- An empty list of intervals -- output should be an empty list.
- A single interval -- output is that same interval unchanged.
- Touching intervals such as `[1,4],[4,5]` that share exactly one boundary point and must still merge.
- Nested intervals such as `[1,10],[2,3]`, where the second interval's end is smaller than the first's -- the merge must take `max(existingEnd, newEnd)`, not just overwrite with the new end, or the larger interval's true extent gets shrunk.
- Unsorted input -- the algorithm must sort first; testing only with already-sorted input would hide a missing or broken sort.
- Duplicate intervals appearing more than once in the input.
- Intervals with a genuine gap between them (e.g. `[1,4],[5,6]`, gap of exactly one unit) that must NOT be merged, to confirm the boundary condition is `<=` for touching but strictly greater means no merge.
- A long chain where merging interval `i` with the running result then allows it to also reach interval `i+2`, `i+3`, etc. (transitive merging through several steps).

## Approach
### Brute force
For every pair of intervals, check whether they overlap, and if so union them, repeating until no more merges occur (like a fixed-point iteration, or a union-find over intervals). This can take `O(n^2)` or worse per full pass and may need multiple passes to fully settle chains of overlaps, giving `O(n^3)` in the worst case for `n=10^4` -- around `10^12` operations, far too slow, and considerably more complex to implement correctly than the sort-based approach.

### Optimal
Sort the intervals by start value. Then make a single linear pass: keep a running "current merged interval," and for each next interval in sorted order, check whether its start is `<=` the current merged interval's end; if so, extend the current merged interval's end to `max(currentEnd, nextEnd)`, otherwise close off the current merged interval (add it to the result) and start a new running interval from the next one.

**Key invariant:** after sorting by start, any interval that will ever merge with the current running interval must appear immediately in sorted order (there cannot be a "gap" interval in between that is skipped over and merged later), because sorting guarantees every subsequent interval's start is `>=` the current interval's start, so if a subsequent interval's start already exceeds the current running interval's end, every interval after it (with an even larger or equal start) cannot possibly reach back into the current interval either.

Proof sketch: once intervals are sorted by start, consider the first interval, `I_1`. Any interval that overlaps `I_1` must have `start <= I_1.end`; because the array is sorted, all such overlapping intervals appear contiguously right after `I_1` (their starts are all `<= I_1.end`, and once an interval's start exceeds the current running merged end, no later interval -- having an even larger start -- can have a smaller start that sneaks back under that threshold). This means a single left-to-right sweep, extending the running end via `max` whenever the next start falls within the current range, correctly captures every interval that belongs in the same merged group, and correctly closes the group the first time a later start exceeds the current running end.

### Step-by-step trace
Trace on `intervals = [[1,3],[2,6],[8,10],[15,18]]` (already sorted by start):

| step | current running interval | next interval | next.start <= current.end? | action | merged list so far |
|---|---|---|---|---|---|
| init | none | [1,3] | -- | start running interval at [1,3] | [] |
| 1 | [1,3] | [2,6] | 2 <= 3, yes | extend end to max(3,6)=6 -> [1,6] | [] |
| 2 | [1,6] | [8,10] | 8 <= 6, no | close [1,6], start new running [8,10] | [[1,6]] |
| 3 | [8,10] | [15,18] | 15 <= 10, no | close [8,10], start new running [15,18] | [[1,6],[8,10]] |
| end | [15,18] | -- | -- | close final running interval | [[1,6],[8,10],[15,18]] |

## Java 8 solution
```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public static int[][] solve(int[][] intervals) {
    if (intervals == null || intervals.length == 0) {
        return new int[0][];
    }
    int[][] sorted = intervals.clone(); // shallow clone: reorders the outer array without touching inner arrays yet
    Arrays.sort(sorted, new Comparator<int[]>() {
        public int compare(int[] a, int[] b) {
            return Integer.compare(a[0], b[0]); // compare by start; avoids overflow risk of a[0] - b[0]
        }
    });

    List<int[]> merged = new ArrayList<int[]>(); // declared type List, concrete type ArrayList
    for (int[] interval : sorted) {
        if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
            // no overlap with the current running interval: start a fresh one.
            // a NEW array is created here (not a reference into `sorted`) so later
            // mutation of the merged end never corrupts the caller's original input.
            merged.add(new int[]{interval[0], interval[1]});
        } else {
            int[] last = merged.get(merged.size() - 1);
            last[1] = Math.max(last[1], interval[1]); // nested interval trap: must take max, not overwrite
        }
    }
    return merged.toArray(new int[merged.size()][]);
}
```

## Complexity
- Time: `O(n log n)` -- dominated by the sort; the merge sweep afterward is a single `O(n)` linear pass.
- Space: `O(n)` -- for the sorted copy and the result list (not counting the output itself, which is required).

## Java 8 pitfalls for this problem
- `Arrays.sort` on an `int[][]` needs an explicit `Comparator<int[]>`; there is no natural ordering for arrays, so `Arrays.sort(intervals)` alone (without a comparator) throws `ClassCastException` at runtime.
- Never write the comparator as `a[0] - b[0]`. Although this problem's bounds (`0 <= start,end <= 5*10^4`) make overflow impossible here, `a[0] - b[0]` is a habit that silently breaks (wraps to the wrong sign) the moment values approach `Integer.MIN_VALUE`/`MAX_VALUE` in a different problem -- `Integer.compare(a[0], b[0])` is always safe and costs nothing extra.
- `new Comparator<int[]>() { ... }` is a plain anonymous class with an explicit type parameter; `new Comparator<>() { ... }` (diamond operator with an anonymous class) is Java 9+ syntax and will not compile under `--release 8`.
- `intervals.clone()` only performs a **shallow** copy: it copies the outer `int[][]` array's references, not the inner `int[]` arrays themselves. Mutating `sorted[i][1]` in place would also mutate the caller's original `intervals[i]` since they are the same inner array object. The solution above avoids this trap entirely by constructing a brand-new `int[]{interval[0], interval[1]}` for every entry added to `merged`, so the original input is never touched.
- `2-D` array literal syntax for tests: `new int[][]{{1,3},{2,6}}` is correct; a common mistake is writing `new int[2][]{...}` with a mismatched first-dimension size, or forgetting the outer `new int[][]` entirely when passing a literal as a method argument.
- `List<int[]>` iteration order preserves insertion order, which matters here since the output must remain sorted by start; using a `HashSet<int[]>` or similar unordered collection anywhere in this pipeline would silently break the sorted-output guarantee.

## Wrong approaches and why they fail
- **Sorting by end instead of by start:** for `intervals=[[1,4],[0,4]]`, sorting by end first (both end at 4) leaves the relative order of starts ambiguous depending on sort stability, and more importantly, for `intervals=[[1,10],[2,3],[11,12]]`, sorting by end gives order `[2,3],[1,10],[11,12]`, which breaks the left-to-right sweep invariant entirely (a later-starting-but-earlier-ending interval would be evaluated before the interval that actually needs to absorb it), producing an incorrect merge.
- **Checking overlap with `next.start < current.end` (strict less-than) instead of `<=`:** for `intervals=[[1,4],[4,5]]`, this incorrectly treats the touching pair as non-overlapping and returns `[[1,4],[4,5]]` unmerged, when the correct answer per this problem's inclusive-touching definition is `[[1,5]]`.
- **Overwriting the running end instead of taking the max:** for `intervals=[[1,10],[2,3]]` (a nested interval), naively setting `current.end = interval.end` after detecting overlap would shrink the running interval to `[1,3]`, silently dropping the portion of `[1,10]` from `3` to `10` that no other interval covers.

## Variants
- **LC 57 Insert Interval:** given an already-sorted, already-merged list of intervals plus one new interval to insert, merge the new interval into the correct position in a single linear pass, without needing to re-sort the whole array.
- **Interval intersection (LC 986):** given two already-sorted lists of disjoint intervals, find every pairwise overlap between the two lists using a two-pointer sweep instead of a single sort-then-merge pass.
- **Minimum number of meeting rooms (LC 253):** instead of merging overlapping intervals into fewer ranges, count the maximum number of intervals active at the same time -- typically solved by sorting start and end times separately and sweeping both simultaneously, or by a min-heap tracking currently-occupied room end times.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | [[1,3],[2,6],[8,10],[15,18]] | [[1,6],[8,10],[15,18]] | standard case with one real merge |
| 2 | [[1,4],[4,5]] | [[1,5]] | touching intervals must merge |
| 3 | [] | [] | empty input |
| 4 | [[1,4]] | [[1,4]] | single interval, unchanged |
| 5 | [[1,4],[0,4]] | [[0,4]] | unsorted input requires sorting first |
| 6 | [[1,4],[2,3]] | [[1,4]] | nested interval, must take max end, not overwrite |
| 7 | [[1,4],[5,6]] | [[1,4],[5,6]] | genuine gap, must NOT merge |
| 8 | [[1,10],[2,3],[4,5],[6,7]] | [[1,10]] | several intervals all nested within the first |
| 9 | [[1,4],[1,4]] | [[1,4]] | duplicate intervals collapse to one |
| 10 | [[1,4],[0,2],[3,5]] | [[0,5]] | transitive chain merge across three intervals |
