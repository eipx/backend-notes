# Subarray Sum Equals K
`ref: LC 560` · Difficulty: Medium · Pattern: Prefix sum + frequency map

## Problem
You are given an array of integers (each entry can be positive, negative, or zero) and a target integer `k`. Count how many contiguous slices of the array — subarrays — add up to exactly `k`. A subarray is a run of consecutive elements described by a start index `i` and an end index `j` with `i <= j`; its sum is just the total of the elements from `i` to `j`. Two subarrays count as different whenever their index ranges differ, even if the numbers inside happen to look identical. The output is a single integer: the total number of index ranges whose sum equals `k`.

## Constraints
- Array length is small enough to fit comfortably in memory but large enough that quadratic scans start to hurt — think on the order of tens of thousands of elements.
- Each element is a bounded integer (roughly a four-digit range in either direction), so running sums stay within `int` range for arrays of this size.
- The target `k` can be any integer in a similarly bounded range, including negative values and zero.
- Implication: with array length in the tens of thousands, an O(n^2) nested-loop scan runs into the hundreds of millions of operations — too slow for a fast solution. An O(n) or O(n log n) approach is expected.

## Worked examples
1. `nums = [1,1,1]`, `k = 2` -> `2`. The subarray at indices `[0,1]` sums to 2, and the subarray at indices `[1,2]` also sums to 2 — two distinct index ranges, even though both contain the values `1,1`.
2. `nums = [1,2,-3,3]`, `k = 3` -> `3`. Three different ranges hit the target: `[0,1]` (`1+2`), the full array `[0,3]` (`1+2-3+3`), and `[3,3]` (just the trailing `3`).
3. `nums = [-1,-1,1]`, `k = 0` -> `1`. Only the range `[1,2]` (`-1+1`) cancels to zero; the full array sums to `-1`, and no other range cancels out.
4. `nums = [0,0,0,0]`, `k = 0` -> `10`. Every one of the 10 possible contiguous ranges of an all-zero array of length 4 sums to zero (4 single elements + 3 pairs + 2 triples + 1 quadruple = 10).

## Edge cases checklist
- Single-element array, both when that element equals `k` and when it does not.
- `k = 0` with literal zeros present (each standalone zero is its own valid range).
- `k = 0` achieved only through cancellation (no literal zero entries, but values that sum back to zero).
- An all-negative array paired with a negative `k`.
- The same running-sum value recurring three or more times — this multiplies the match count combinatorially, not just additively.
- A `k` that is never achievable by any range — the answer must be `0`.
- The very first range in the array (starting at index 0) summing to `k` — this is the case that silently breaks if the algorithm forgets to seed its state correctly.

## Approach

### Brute force
Compute the sum of every possible subarray directly: for each start index `i`, walk forward accumulating a running sum through every end index `j >= i`, and compare it to `k` at each step. This is O(n^2) time and O(1) extra space. At array lengths in the tens of thousands, that is on the order of hundreds of millions of additions — too slow for a solution expected to finish quickly. It is fine only as a correctness check on small inputs.

### Optimal
Define prefix sums `P[0] = 0`, `P[1] = nums[0]`, `P[2] = nums[0] + nums[1]`, and so on, so `P[i]` is the sum of the first `i` elements. The sum of the range from index `i` to `j-1` equals `P[j] - P[i]`. So the question "does some range ending just before position `j` sum to `k`" becomes "does some earlier prefix `P[i]` equal `P[j] - k`". Walk the array once, keeping a running sum (which is exactly `P[j]` at step `j`) and a hash map counting how many times each prefix-sum value has appeared so far. Before any elements are read, `P[0] = 0` has already occurred once — that seed entry `{0: 1}` is not a special-case hack, it is the real, empty-prefix entry. Skipping it silently undercounts every range that starts at index 0 and happens to sum to `k`.

**Key invariant:** immediately after processing index `j`, the map holds the exact count of every prefix sum `P[0..j]`, so looking up `P[j] - k` in that map counts every valid earlier start point exactly once.

Proof sketch (induction on `j`): base case, before any element is read, the map is exactly `{P[0]: 1}`, which is correct since only the empty prefix exists. Inductive step: assume the map correctly reflects `P[0..j-1]` before processing element `j`. Compute `P[j]`, look up `P[j] - k` in the map (which still only reflects `P[0..j-1]`) — by definition this counts every `i < j` with `P[i] = P[j] - k`, meaning every range ending at position `j-1` that sums to `k` — then insert `P[j]` into the map, restoring the invariant for the next step. Summing this per-step count across the whole array counts every valid range exactly once, since every range corresponds to exactly one `(i, j)` pair.

**Why two pointers (sliding window) does not work here:** a sliding window relies on the sum changing in a predictable direction as the window's edges move — shrinking from the left only decreases the sum, and growing from the right only increases it — and that guarantee only holds when every element is non-negative. With negative numbers present, shrinking the window can increase the sum and growing it can decrease the sum, so there is no reliable rule for which pointer to move in response to "sum too big" or "sum too small." The technique has no valid decision rule once negatives are allowed.

### Step-by-step trace
Trace on `nums = [1,1,1]`, `k = 2`:

| Step | value read | running sum | need = sum - k | matches added | total so far | map after this step |
|---|---|---|---|---|---|---|
| start | - | 0 | - | - | 0 | `{0:1}` |
| 1 | 1 | 1 | -1 | 0 (no -1 in map) | 0 | `{0:1, 1:1}` |
| 2 | 1 | 2 | 0 | 1 (map has one 0) | 1 | `{0:1, 1:1, 2:1}` |
| 3 | 1 | 3 | 1 | 1 (map has one 1) | 2 | `{0:1, 1:1, 2:1, 3:1}` |

Final total is `2`, matching worked example 1.

## Java 8 solution
```java
import java.util.HashMap;
import java.util.Map;

public class SubarraySumEqualsK {

    // Counts contiguous subarrays of nums whose elements sum exactly to k.
    public static int solve(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<Integer, Integer>();
        prefixCount.put(0, 1); // P[0] = 0 counts as a real prefix, seen before any element is read
        int runningSum = 0;
        int total = 0;
        for (int num : nums) {
            runningSum += num;
            int need = runningSum - k;
            Integer seen = prefixCount.get(need); // may be null -> use Integer, not int, to avoid NPE on unboxing
            if (seen != null) {
                total += seen;
            }
            Integer existing = prefixCount.get(runningSum);
            prefixCount.put(runningSum, existing == null ? 1 : existing + 1);
        }
        return total;
    }
}
```

## Complexity
Time O(n): one pass over the array, with each hash map `get`/`put` averaging O(1). Space O(n) in the worst case, when every prefix sum is distinct and the map ends up with one entry per index.

## Java 8 pitfalls for this problem
- `Map<Integer,Integer>.get()` returns a boxed `Integer` that can be `null`. Assigning it straight into an `int` triggers an unboxing `NullPointerException` on a miss — keep it as `Integer` and null-check, or use `getOrDefault`.
- Boxed `Integer` caching only covers `-128..127`. Running sums here routinely exceed that range, so comparing two boxed sums with `==` can look correct on tiny test inputs and quietly break on larger ones. Always compare boxed integers with `.equals()` or `Integer.compare()`, never `==`.
- Forgetting to seed the map with `(0, 1)` before the loop is the single most common bug in this pattern — it silently undercounts by exactly the number of valid ranges that start at index 0.
- `map.merge(key, 1, Integer::sum)` is a clean one-line increment-or-insert; writing it out as `map.put(key, map.getOrDefault(key, 0) + 1)` is equally correct and sometimes more readable — pick one style and stay consistent.
- Declaring the map as `HashMap` versus the interface type `Map` does not matter for correctness here, but reaching for `TreeMap` "to be safe" adds an unnecessary O(log n) factor per operation — nothing in this problem needs ordering.

## Wrong approaches and why they fail
1. **Two-pointer / shrinking-growing window**, assuming it generalizes from the non-negative version of this problem. Counterexample: `nums = [1,-1,1]`, `k = 1`. The correct answer is 3 (the two lone 1's plus the full array), but a window that only reacts to "current sum vs k" has no consistent rule once the `-1` can shrink the sum after the window has already grown.
2. **Track prefix sums with a `Set<Integer>`** (seen / not seen) instead of a count map. This undercounts as soon as a prefix sum repeats more than twice. Counterexample: `nums = [0,0,0,0]`, `k = 0` — the correct answer is 10, but a boolean "have I seen sum 0 before" check has no way to represent how many times 0 has occurred, only that it has.
3. **Reset the running sum to 0 whenever it goes negative**, borrowing the trick from maximum-subarray-sum (Kadane's algorithm). That trick solves a different question and throws away valid history here. Counterexample: `nums = [-1,2]`, `k = 1` — resetting after the `-1` makes the algorithm act as if the array restarts at index 1, missing the valid range `[-1,2]` which sums to exactly 1.

## Variants
1. **Return the actual subarrays, not just a count.** Store, for each prefix-sum value, the list of indices where it occurred instead of a plain count. When a match is found at index `j`, pair `j` with every stored index for that value to recover the actual ranges.
2. **Longest subarray summing to k.** Store only the first index at which each prefix sum was seen, since a later duplicate can only produce a shorter match. Whenever a match is found, compare its length against the best seen so far.
3. **Count subarrays whose sum is divisible by k** (a close relative of this problem). Use `((runningSum % k) + k) % k` as the map key instead of the raw running sum, to correctly normalize negative remainders — Java's `%` can return a negative result — then apply the same counting logic unchanged.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `nums=[1,1,1]`, `k=2` | `2` | basic case, multiple overlapping ranges |
| 2 | `nums=[1,2,3]`, `k=3` | `2` | two different ranges reach the target |
| 3 | `nums=[1]`, `k=1` | `1` | single element equals k, exercises the P[0]=0 seed |
| 4 | `nums=[1]`, `k=0` | `0` | single element, target not present |
| 5 | `nums=[-1,-1,1]`, `k=0` | `1` | negative numbers, sum returns to zero once |
| 6 | `nums=[0,0,0,0]`, `k=0` | `10` | all zeros, dense duplicate prefix sums |
| 7 | `nums=[1,-1,0]`, `k=0` | `3` | zeros and cancellation combine |
| 8 | `nums=[1,2,-3,3]`, `k=3` | `3` | negative value in the middle, non-monotonic running sum |
| 9 | `nums=[-1,-2,-3]`, `k=-6` | `1` | all-negative array, negative target |
| 10 | `nums=[5,-5,5,-5]`, `k=0` | `4` | repeated prefix sum value multiplies match count |
| 11 | `nums=[1,2,3]`, `k=100` | `0` | target unreachable, answer is zero |
