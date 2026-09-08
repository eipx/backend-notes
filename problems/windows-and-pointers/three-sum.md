# 3Sum
`ref: LC 15` · Difficulty: Medium · Pattern: sort + fixed anchor + two pointers, with explicit duplicate skipping

## Problem

Given an array of integers `nums`, find every distinct triplet of values (by position, but reported by value) `(a, b, c)` such that `a + b + c == 0`. "Distinct" means no two reported triplets should consist of the same three values, even if they came from different index combinations — value-based duplicates must be suppressed. Each triplet must use three different positions in the array (a value cannot be paired with itself using the same index twice), though the same numeric value appearing at different positions can be reused across different triplets or even within one triplet if it appears multiple times in the array.

Input: an integer array `nums` (may contain duplicates, negatives, zero).
Output: a collection of triplets (each a list/array of 3 integers) with no duplicate triplet by value, in any internal or external order (order does not need to match input order, but each individual triplet is conventionally reported ascending).

## Constraints

- `0 <= nums.length <= 3000` (some framings guarantee at least 3, but treat 0, 1, 2 as valid inputs that simply yield no triplets)
- `-10^5 <= nums[i] <= 10^5`
- With n up to 3000, an O(n^3) brute force is about `2.7 * 10^10` operations — far too slow. An O(n^2) approach (sort once, then a linear two-pointer scan per anchor) is the expected complexity.

## Worked examples

1. Input: `[-1,0,1,2,-1,-4]` → Output: `[[-1,-1,2],[-1,0,1]]`. Sorted the array is `[-4,-1,-1,0,1,2]`. The pair `-1` and `2` plus anchor `-1` sums to `0`; separately, `-1 + 0 + 1 = 0`. No other combination sums to zero, and both of these are reported exactly once even though `-1` appears twice in the array.
2. Input: `[0,0,0]` → Output: `[[0,0,0]]`. The only possible triplet uses all three zeros, and `0+0+0=0`, so it is reported exactly once (not three times for three "different" zeros).
3. Input: `[-2,0,0,2,2]` → Output: `[[-2,0,2]]`. Sorted: `[-2,0,0,2,2]`. The triplet `-2, 0, 2` sums to zero; even though there are two `0`s and two `2`s available, every valid combination of them collapses to the same value-triplet `(-2,0,2)`, so it is reported once.
4. Input: `[1,2,-2,-1]` → Output: `[]`. No three values from this array sum to zero (checking all four combinations of three: `1+2-2=1`, `1+2-1=2`, `1-2-1=-2`, `2-2-1=-1` — none is zero).

## Edge cases checklist

- Fewer than 3 elements (`[]`, `[x]`, `[x,y]`): must return an empty result, never throw.
- All elements zero: exactly one triplet, `[0,0,0]`, not a combinatorial explosion of "different" zero-triplets.
- Heavy duplicate values mixed with a valid triplet (must dedupe by value, not silently miss valid combinations or double-report them).
- All-negative or all-positive input: no triplet can sum to zero (with the possible exception if zero itself is "positive" or "negative" by convention — but a strictly negative or strictly positive array, with no zero, can never sum to zero from three same-signed values).
- No valid triplet exists at all.
- Multiple disjoint valid triplets sharing some values, e.g. one triplet uses a `-1` and a different triplet also uses a (different-position) `-1`.
- Large magnitude values near the ±1e5 bound (overflow is not a practical concern in Java `int` arithmetic for sums of three such values, but worth noting: max |sum| here is 3*10^5, well within `int` range).

## Approach

### Brute force

Check every combination of three distinct indices `i < j < l` and test whether `nums[i]+nums[j]+nums[l]==0`, collecting results into a set keyed by the sorted triplet of values to dedupe. This is O(n^3) time (ignoring the dedup overhead) — at `n = 3000` that's roughly `4.5 * 10^9` raw index triples (or ~2.7*10^10 if you don't stop the innermost loop early), which will not finish in reasonable time. It is also awkward to dedupe correctly without extra sorting/set machinery per triplet.

### Optimal

Sort the array once (O(n log n)). Then, for each index `i` from left to right treated as a fixed "anchor" value, use two pointers — `left` starting just after `i`, `right` starting at the end of the array — moving inward across the remaining sorted suffix to find pairs that sum to `-nums[i]`. Because the sub-array from `left` to `right` is sorted, if the current three-sum is too small, only advancing `left` (to a larger value) can help; if it's too large, only retreating `right` (to a smaller value) can help. Skip over duplicate anchor values and duplicate `left`/`right` values immediately after recording a hit, to avoid emitting the same triplet more than once.

**Key invariant:** for a fixed sorted anchor `nums[i]`, every valid pair `(left, right)` with `left < right` in the remaining sorted suffix is reachable by monotonically moving `left` right and/or `right` left from their starting positions, without ever needing to "look backward" — because the array is sorted, moving in only one direction at a time never skips over a valid pair.

Proof sketch: fix `i`, and consider the sorted subarray `nums[i+1..n-1]`. For any `left < right` in this range, the sum `nums[i]+nums[left]+nums[right]` is monotonically non-decreasing in `left` (for fixed `right`) and monotonically non-decreasing in `right` (for fixed `left`), because the array is sorted ascending. If the current sum is too small (`< 0` after adding the fixed anchor), no decrease in `right` could ever help (that would only make the sum smaller still) — the only direction that can increase the sum is advancing `left`. Symmetrically, if the sum is too large, only retreating `right` can decrease it. Because at each step exactly one of "too small" / "too large" / "exact match" holds, and the pointers only move in the single direction that can possibly fix the current situation, no valid pair is ever skipped over: any pair strictly between the pointers' start and current positions that could have summed to zero would have had to be found before the pointers passed it, since the moves are forced and monotonic. Combined with sorting the outer anchor loop and skipping duplicate anchors/pointers immediately after a match, each distinct value-triplet is discovered exactly once.

### Step-by-step trace

Trace on `[-1,0,1,2,-1,-4]` → sorted `[-4,-1,-1,0,1,2]` (indices 0..5), expected `[[-1,-1,2],[-1,0,1]]`.

| i (anchor) | left,right | sum | action | result so far |
|---|---|---|---|---|
| 0 (-4) | 1,5 (-1,2) | -3 | sum<0 → left++ | [] |
| 0 (-4) | 2,5 (-1,2) | -3 | sum<0 → left++ | [] |
| 0 (-4) | 3,5 (0,2) | -2 | sum<0 → left++ | [] |
| 0 (-4) | 4,5 (1,2) | -1 | sum<0 → left++ (now left==right, stop) | [] |
| 1 (-1) | 2,5 (-1,2) | 0 | match! record [-1,-1,2]; skip dup left/right (none); left++, right-- | [[-1,-1,2]] |
| 1 (-1) | 3,4 (0,1) | 0 | match! record [-1,0,1]; left++, right-- (left==right, stop) | [[-1,-1,2],[-1,0,1]] |
| 2 (-1) | — | — | nums[2]==nums[1], skip duplicate anchor | (unchanged) |
| 3 (0) | 4,5 (1,2) | 3 | sum>0 → right-- (left==right, stop) | (unchanged) |

Final result: `[[-1,-1,2],[-1,0,1]]`.

## Java 8 solution

```java
import java.util.*;

public class ThreeSum {

    public static List<List<Integer>> solve(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (nums == null || nums.length < 3) {
            return result;
        }
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && sorted[i] == sorted[i - 1]) {
                continue; // skip duplicate anchor value to avoid duplicate triplets
            }
            int left = i + 1;
            int right = n - 1;
            while (left < right) {
                int sum = sorted[i] + sorted[left] + sorted[right];
                if (sum == 0) {
                    List<Integer> triplet = new ArrayList<Integer>();
                    triplet.add(sorted[i]);
                    triplet.add(sorted[left]);
                    triplet.add(sorted[right]);
                    result.add(triplet);
                    while (left < right && sorted[left] == sorted[left + 1]) {
                        left++;
                    }
                    while (left < right && sorted[right] == sorted[right - 1]) {
                        right--;
                    }
                    left++;
                    right--;
                } else if (sum < 0) {
                    left++; // sum too small; only a bigger left value can raise it
                } else {
                    right--; // sum too large; only a smaller right value can lower it
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{-1,0,1,2,-1,-4}, tripletList(intList(-1,-1,2), intList(-1,0,1))},
            {new int[]{}, tripletList()},
            {new int[]{0}, tripletList()},
            {new int[]{0,0,0}, tripletList(intList(0,0,0))},
            {new int[]{0,0,0,0}, tripletList(intList(0,0,0))},
            {new int[]{-2,0,0,2,2}, tripletList(intList(-2,0,2))},
            {new int[]{1,2,-2,-1}, tripletList()},
            {new int[]{3,-2,1,0}, tripletList()},
            {new int[]{-1,0,1,0}, tripletList(intList(-1,0,1))},
            {new int[]{-5,-4,-3,-2,-1}, tripletList()}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            @SuppressWarnings("unchecked")
            List<List<Integer>> expected = (List<List<Integer>>) cases[i][1];
            List<List<Integer>> got = solve(nums);
            if (expected.equals(got)) {
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

    private static List<Integer> intList(int... vals) {
        List<Integer> l = new ArrayList<Integer>();
        for (int v : vals) {
            l.add(v);
        }
        return l;
    }

    @SafeVarargs
    private static List<List<Integer>> tripletList(List<Integer>... triplets) {
        List<List<Integer>> l = new ArrayList<List<Integer>>();
        for (List<Integer> t : triplets) {
            l.add(t);
        }
        return l;
    }
}
```

## Complexity

- Time: O(n^2). Sorting is O(n log n); the outer anchor loop runs n times, and for each anchor the two-pointer scan across the remaining suffix does at most O(n) work total (pointers only move inward, never backward), giving O(n^2) overall, which dominates the sort.
- Space: O(log n) to O(n) for the sort's internal stack/working space (implementation-dependent), plus O(n) for the cloned sorted array; the output itself is not counted against "extra" space since it's the required result.

## Java 8 pitfalls for this problem

- Comparing sorted array values or `Integer` results with `==` when they might be boxed — here everything is kept as primitive `int` throughout the core algorithm, which sidesteps the boxed-`Integer`-cache trap (values outside -128..127 are not guaranteed `==`-safe as boxed objects).
- Java 8 varargs of a generic type (`List<Integer>...`) triggers an "unchecked generic array creation" warning; annotating the helper with `@SafeVarargs` (allowed on `private static` methods since Java 9, but note: `@SafeVarargs` on **private** instance methods specifically was only allowed starting Java 9 — on a `private static` method it has been allowed since Java 7/8, so this is fine here) suppresses the warning without hiding a real bug, since the method only reads from the varargs array and never writes into it.
- Two overloaded helper methods both accepting varargs (e.g. one for `int...` and one for `List<Integer>...`) would create an ambiguous zero-argument call if both are named identically — hence the two helpers here are named distinctly (`intList` vs `tripletList`) rather than overloaded.
- Forgetting to skip duplicate anchors (`sorted[i] == sorted[i-1]`) is a correctness bug, not just an efficiency one — it produces duplicate triplets in the output, not just slower code.
- No `List.of(...)` in Java 8 — building expected test fixtures requires `Arrays.asList(...)` or manual `ArrayList` construction (used here via the `intList`/`tripletList` helpers) instead of the Java 9+ factory methods.
- Mutating the input array directly with `Arrays.sort(nums)` instead of cloning first can surprise callers who don't expect their input array to be reordered as a side effect; cloning (`nums.clone()`) avoids that.

## Wrong approaches and why they fail

- **Use three nested loops with a `HashSet<String>` (or set of sorted-triplet keys) to dedupe, and consider this "fast enough."** Counterexample: at `n = 3000`, this is still O(n^3) ≈ 2.7*10^10 raw iterations before any hashing overhead — the dedup set fixes correctness, not the fundamental time complexity problem.
- **Fix two anchors with nested loops and hash-lookup the third value in a set, without sorting or dedup logic, and assume distinctness is automatic.** Counterexample: `[0,0,0,0]` — a plain hash-based "does `-a-b` exist in the array" check would find the same valid triplet `(0,0,0)` repeatedly from different index combinations `(0,1,2)`, `(0,1,3)`, `(0,2,3)`, `(1,2,3)`, all mapping to the identical value-triplet, and without explicit value-based dedup this produces 4 duplicate copies of `[0,0,0]` instead of 1.
- **Move both pointers on every non-match regardless of whether the sum was too high or too low.** Counterexample: `[-2,0,0,2,2]` — if `left++` and `right--` both happen unconditionally whenever `sum != 0`, valid pairs can be skipped entirely because you're not responding to the *direction* of the mismatch; sorted two-pointer correctness specifically depends on moving only the pointer that can fix the current sign of the error.

## Variants

- **Find triplets summing to an arbitrary target `t`, not just zero.** Change the anchor's target from `0 - nums[i]` conceptually — really just compare `sum` against `t` instead of `0` in the two-pointer loop; sort/skip/dedup logic is unchanged.
- **4Sum (find quadruplets summing to zero).** Add one more fixed outer loop (two nested anchors instead of one) before falling into the same two-pointer scan on the remaining suffix; complexity becomes O(n^3), and duplicate-skipping is needed at every nesting level, not just the innermost one.
- **Report the count of valid triplets only, not the triplets themselves, allowing duplicate values to count as distinct if they come from different indices.** This changes the problem from a "distinct by value" combinatorial search into a counting problem, better solved by fixing two anchors and hash-counting occurrences of the required third value (with careful handling of same-index and same-value multiplicities) rather than the sorted two-pointer approach shown here.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[-1,0,1,2,-1,-4]` | `[[-1,-1,2],[-1,0,1]]` | classic mixed example with a duplicate value used validly |
| 2 | `[]` | `[]` | empty input |
| 3 | `[0]` | `[]` | single element, too short for any triplet |
| 4 | `[0,0,0]` | `[[0,0,0]]` | all zeros, exactly one triplet |
| 5 | `[0,0,0,0]` | `[[0,0,0]]` | extra duplicate zero must not create extra output rows |
| 6 | `[-2,0,0,2,2]` | `[[-2,0,2]]` | duplicate values on both sides collapse to one triplet |
| 7 | `[1,2,-2,-1]` | `[]` | no combination sums to zero |
| 8 | `[3,-2,1,0]` | `[]` | includes zero but still no valid triplet |
| 9 | `[-1,0,1,0]` | `[[-1,0,1]]` | duplicate zero at a different position, same triplet only once |
| 10 | `[-5,-4,-3,-2,-1]` | `[]` | all-negative input, no triplet can reach zero |
