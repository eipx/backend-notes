# Find K Closest Elements
`ref: LC 658` · Difficulty: Medium · Pattern: binary search for the left edge of the k-window (two pointers shrinking from both ends as the simple baseline)

## Problem

Given a sorted integer array `arr`, an integer `k`, and an integer `x`, return the `k` integers in `arr` closest to `x`, in ascending order. An integer `a` is closer to `x` than `b` when `|a - x| < |b - x|`, or when `|a - x| == |b - x|` and `a < b`.

Input: a sorted integer array `arr`, an integer `k`, and an integer `x`.
Output: a list of `k` integers from `arr`, in ascending order, chosen by the closeness rule above.

## Constraints

- `1 <= k <= arr.length <= 10^4`
- `arr` is sorted in ascending order.
- `-10^4 <= arr[i], x <= 10^4`
- `arr` may contain duplicate values.
- The answer set is always a contiguous run of `arr` (see Approach below), so the problem reduces to finding a single boundary index rather than picking `k` elements independently; with `arr.length` up to `10^4` a linear scan for that boundary would already run comfortably, but a binary search over the possible boundary positions is the standard target and is what scales to much larger inputs.

## Worked examples

1. `arr = [1,2,3,4,5]`, `k = 4`, `x = 3` -> `[1,2,3,4]`. The two extreme values, `1` and `5`, are equally far from `x` (distance `2` each). The tie rule keeps the smaller value, so `5` is dropped and `1` is kept, leaving `[1,2,3,4]`.
2. `arr = [1,2,3,4,5]`, `k = 2`, `x = 3` -> `[2,3]`. Distances are `2,1,0,1,2`. `3` (distance `0`) is the closest value. The next closest is a tie between `2` and `4` (distance `1` each); the tie rule keeps `2` over `4`.
3. `arr = [3,4,5,6,7]`, `k = 3`, `x = 0` -> `[3,4,5]`. `x` is below every element, so the three smallest values are the closest ones.
4. `arr = [3,4,5,6,7]`, `k = 3`, `x = 20` -> `[5,6,7]`. `x` is above every element, so the three largest values are the closest ones.

## Edge cases checklist

- `k == arr.length` (the whole array is the answer, no search needed).
- `k == 1` (a single value, decided entirely by the tie rule when two elements are equally close).
- `x` below every element in `arr` (the answer is the first `k` elements).
- `x` above every element in `arr` (the answer is the last `k` elements).
- `x` equal to an element already in `arr`.
- An exact tie in distance between two elements on opposite sides of `x` (the smaller value must be kept).
- Duplicate values in `arr`, including duplicates that sit at distance `0` from `x`.
- Negative values in `arr` together with a negative or zero `x`.

## Approach

### Brute force

Keep two pointers, `left = 0` and `right = arr.length - 1`, spanning the whole array, and shrink the span from whichever end is farther from `x` until exactly `k` elements remain. At each step compare `x - arr[left]` (how far the left end sits below `x`) against `arr[right] - x` (how far the right end sits above `x`). If the left end is strictly farther, drop it by moving `left` forward; otherwise drop the right end by moving `right` backward -- the "otherwise" branch also covers an exact tie, so a tie always drops the larger (right) value, matching the rule that the smaller value wins when two elements are equally close. Stop once `right - left + 1 == k` and return `arr[left..right]`. Each step removes exactly one element, so this runs in `O(n - k)` time and `O(1)` extra space beyond the output.

### Optimal

The answer is always a contiguous run of `arr` of length `k`, so the task is really "find the smallest valid starting index `lo`," searched with binary search over `lo` in `[0, arr.length - k]`. For a midpoint `mid`, compare the element that would leave the window on the left, `arr[mid]`, against the element that would enter it on the right, `arr[mid + k]`: if `x - arr[mid] > arr[mid + k] - x`, the left element is strictly farther from `x` than the one just past the window, so the window must start later (`lo = mid + 1`); otherwise a window starting at `mid` is at least as good, so narrow the search to `hi = mid`. Once `lo == hi`, the window `arr[lo .. lo + k - 1]` is the answer.

**Key invariant:** at every point during the search, the true answer's starting index lies in `[lo, hi]`. Moving `lo` past `mid` is only safe because `arr[mid]` being strictly farther from `x` than `arr[mid + k]` means a window starting at `mid` is strictly worse than one starting at `mid + 1`: it keeps a strictly farther element in exchange for excluding a strictly closer one, so `mid` can never be the answer once that comparison is strict. When the comparison is not strict (equal distances, or the right side is farther), a window starting at `mid` is at least as good as one starting later, so `mid` stays a live possibility and `hi` shrinks down onto it instead of past it. The strict `>` is what makes an exact tie fall into this second case, which is exactly why the smaller value is kept on a tie.

### Step-by-step trace

Trace on `arr = [1,2,3,4,5]`, `k = 2`, `x = 3` (`hi` starts at `arr.length - k = 3`):

| lo | hi | mid | x - arr[mid] | arr[mid+k] - x | strictly greater? | move |
|---|---|---|---|---|---|---|
| 0 | 3 | 1 | 3-2=1 | arr[3]-3=1 | no (tie) | hi = 1 |
| 0 | 1 | 0 | 3-1=2 | arr[2]-3=0 | yes | lo = 1 |
| 1 | 1 | - | - | - | loop ends | - |

Final window: `arr[1 .. 2]` = `[2,3]`, matching worked example 2.

## Java 8 solution
```java
import java.util.*;

public class FindKClosestElements {

    // Returns the k values in arr closest to x, in ascending order.
    public static List<Integer> solve(int[] arr, int k, int x) {
        int lo = 0;
        int hi = arr.length - k;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (x - arr[mid] > arr[mid + k] - x) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        List<Integer> result = new ArrayList<Integer>();
        for (int i = lo; i < lo + k; i++) {
            result.add(arr[i]);
        }
        return result;
    }

    private static void check(int caseNum, int[] arr, int k, int x, List<Integer> expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        List<Integer> got = solve(arr, k, x);
        if (got.equals(expected)) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1,2,3,4,5}, 4, 3, Arrays.asList(1,2,3,4), fail, total);
        check(2, new int[]{1,2,3,4,5}, 2, 3, Arrays.asList(2,3), fail, total);
        check(3, new int[]{3,4,5,6,7}, 3, 0, Arrays.asList(3,4,5), fail, total);
        check(4, new int[]{3,4,5,6,7}, 3, 20, Arrays.asList(5,6,7), fail, total);
        check(5, new int[]{1,2,3,4,5}, 5, 3, Arrays.asList(1,2,3,4,5), fail, total);
        check(6, new int[]{1,3}, 1, 2, Arrays.asList(1), fail, total);
        check(7, new int[]{1,1,1,10,10,10}, 3, 1, Arrays.asList(1,1,1), fail, total);
        check(8, new int[]{1,1,2,2,3,3}, 2, 2, Arrays.asList(2,2), fail, total);
        check(9, new int[]{1,4,6,8,9}, 3, 5, Arrays.asList(4,6,8), fail, total);
        check(10, new int[]{-4,-3,-2,-1,0,1,2,3,4}, 5, 0, Arrays.asList(-2,-1,0,1,2), fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Optimal (binary search): time `O(log(n - k) + k)` -- the search over the boundary index takes `O(log(n - k))` comparisons, and copying the `k`-element window into the result list takes `O(k)`; space `O(k)` for the output, `O(1)` extra beyond it. Brute force (two pointers): time `O(n - k)`, since each step discards exactly one element and the span shrinks from `n` down to `k`; space `O(1)` extra beyond the output.

## Java 8 pitfalls for this problem

- The binary search bound is `hi = arr.length - k`, not `arr.length - 1`; using the wrong bound either indexes past the end of `arr` when computing `arr[mid + k]`, or excludes the valid rightmost window.
- The loop condition must be `lo < hi`, not `lo <= hi`; this search narrows down to a single boundary index rather than scanning for an exact value match, and reusing the `lo <= hi` template from a value-search binary search either loops forever or checks one comparison too many, depending on how `mid` is computed.
- The window comparison must be strict `>`; using `>=` shifts every tie toward the larger element, which is backward from the required rule that the smaller value wins when two elements are equally close.
- The element compared on the right side of the check is `arr[mid + k]`, the first element just past the current window, not `arr[mid + k - 1]`, the last element still inside it; swapping the two flips which side the comparison favors and shifts the returned window one position too far right.
- `List<Integer>` equality: `List.equals` compares element by element, in order, so the result list must stay in ascending order (it already is, since it is copied directly out of the sorted input) and the expected list in tests must be built with `Arrays.asList(...)` so its element order matches exactly.
- Filling the result with `result.add(arr[i])` boxes each primitive `int` into an `Integer`; harmless at the sizes here, but worth remembering it is not free for a very large `k`.

## Wrong approaches and why they fail

1. **Binary search using `>=` instead of a strict `>` in the window comparison.** This moves `lo` past `mid` even when the two sides are exactly tied, which keeps the larger of two equally close values instead of the smaller one. Counterexample: `arr = [1,2,3,4,5]`, `k = 2`, `x = 3` (test case 2). The correct window is `[2,3]`; with `>=` the search ends at `lo = 2` and returns `[3,4]`, keeping `4` over `2` despite the tie.
2. **Two-pointer shrinking that drops the closer end on a tie instead of the farther one** -- moving `left` forward (instead of `right` backward) whenever the two distances are equal. Counterexample: `arr = [1,3]`, `k = 1`, `x = 2` (test case 6). Both elements sit at distance `1` from `x`; the correct result keeps the smaller value, `[1]`, but this variant drops `1` on the tie and returns `[3]`.
3. **Binary search that compares `arr[mid]` against `arr[mid + k - 1]`** (the last element still inside the window) **instead of `arr[mid + k]`** (the first element just past it). This compares the wrong pair of elements, so the search moves `lo` too aggressively toward the right. Counterexample: `arr = [1,2,3,4,5]`, `k = 4`, `x = 3` (test case 1). The correct window is `[1,2,3,4]`; this variant returns `[2,3,4,5]`, dropping `1` even though `1` and `5` are equally far from `x` and the tie rule should keep the smaller value.

## Variants

1. Return the boundary index `lo` (or the pair `[lo, lo + k - 1]`) instead of the materialized list of values, for callers that only need the window's position.
2. `arr` is not already sorted. Sorting it first costs `O(n log n)`, after which the same `O(log(n - k) + k)` search applies; the overall bound is then dominated by the sort.
3. Instead of a single target `x`, return the `k` elements closest to each of several targets in the same array; sorting the targets and reusing the previous boundary as the new search's lower bound avoids redoing the full search from scratch each time.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `arr=[1,2,3,4,5], k=4, x=3` | `[1,2,3,4]` | general case, tie between the two extreme values |
| 2 | `arr=[1,2,3,4,5], k=2, x=3` | `[2,3]` | exact tie between two elements, smaller value kept |
| 3 | `arr=[3,4,5,6,7], k=3, x=0` | `[3,4,5]` | x smaller than every element |
| 4 | `arr=[3,4,5,6,7], k=3, x=20` | `[5,6,7]` | x larger than every element |
| 5 | `arr=[1,2,3,4,5], k=5, x=3` | `[1,2,3,4,5]` | k equals the array length |
| 6 | `arr=[1,3], k=1, x=2` | `[1]` | k=1 with a tie between the only two elements |
| 7 | `arr=[1,1,1,10,10,10], k=3, x=1` | `[1,1,1]` | duplicates in arr, x equal to an element |
| 8 | `arr=[1,1,2,2,3,3], k=2, x=2` | `[2,2]` | duplicates tied at distance 0, x equal to an element |
| 9 | `arr=[1,4,6,8,9], k=3, x=5` | `[4,6,8]` | sorting by distance gives the same answer, but the window must stay contiguous |
| 10 | `arr=[-4,-3,-2,-1,0,1,2,3,4], k=5, x=0` | `[-2,-1,0,1,2]` | negative values, symmetric window around x |
