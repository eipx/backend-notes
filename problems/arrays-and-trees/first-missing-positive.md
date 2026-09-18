# First Missing Positive
`ref: LC 41` · Difficulty: Hard · Pattern: cyclic index placement (array as its own hash table)

## Problem

You are given an unsorted integer array that may contain negatives, zero, duplicates, and positive numbers in any order. Find the smallest positive integer that does not appear anywhere in the array. The solution must run in `O(n)` time using only `O(1)` extra space (beyond the input array itself, which may be freely rearranged in place).

Input: an integer array `nums` of length `n >= 1`.
Output: a single integer, the smallest positive integer (`1, 2, 3, ...`) that is not present in `nums`.

## Constraints

- `1 <= nums.length <= 10^5` (roughly)
- `-2^31 <= nums[i] <= 2^31 - 1`. Values can be any 32-bit int, including negative numbers, zero, and positive numbers far larger than `nums.length`; all of those are irrelevant to the final answer except insofar as they must be safely ignored.
- Required: `O(n)` time, `O(1)` extra space. The answer is always in the range `[1, n+1]`: with only `n` numbers in the array, if every one of `1..n` were present, the answer would have to be `n+1`; there is no way for the answer to exceed that.

## Worked examples

1. `nums = [1,2,0]` -> `3`. Both `1` and `2` are present; `3` is the smallest positive integer missing.
2. `nums = [3,4,-1,1]` -> `2`. `1` is present, but `2` is missing (the negative `-1` and the `4` beyond the array's length are both irrelevant).
3. `nums = [7,8,9,11,12]` -> `1`. None of the values fall in the range `[1, 5]` (the array has 5 elements), so `1` itself is missing.
4. `nums = [1,2,3]` -> `4`. Every value in `[1,3]` is present, so the answer is `n+1 = 4`.

## Edge cases checklist

- Array is exactly a permutation of `1..n` (answer is `n+1`).
- No positive numbers at all (all zero or negative); answer is `1`.
- Duplicate copies of the same positive value.
- Values far outside `[1, n]` (very large positives, very negative numbers, or zero) that must be skipped without breaking the in-place swapping logic.
- Single-element array: `[1]` -> `2`; `[2]` -> `1`; `[-1]` -> `1`.
- Array already in "canonical" placed form (`nums[i] == i+1` for every `i`), which needs zero swaps.
- A value that, if the swap loop is written carelessly, could swap with itself forever (a duplicate whose target slot already holds an identical copy). The loop must recognize "already correct" and stop, not spin.
- The value `0` present in the array (not positive, must be skipped exactly like any other out-of-range value).

## Approach

### Brute force

Put every element into a hash set, then test `k = 1, 2, 3, ...` in increasing order until a `k` not in the set is found. This is `O(n)` time but `O(n)` extra space, which violates the required `O(1)` space bound. It is a useful correctness oracle for testing the in-place version on small inputs, not the accepted solution here. A second natural but too-slow idea: sort the array first (`O(n log n)`, and typically not `O(1)` extra space either, depending on the sort), then scan for the first gap starting from `1`. That is correct, but the wrong time complexity.

### Optimal

The answer must lie in `[1, n]` or be exactly `n+1`. Use this to place values where they "belong": for every index `i`, while `nums[i]` is a value in the valid range `[1, n]` and it is not already sitting at its own home position (index `nums[i]-1`) with the correct value there, swap `nums[i]` with `nums[nums[i]-1]`. Skip (do nothing further for that index) once the current value is out of range or already correctly placed. After this placement pass, scan the array from the front: the first index `i` where `nums[i] != i+1` reveals that `i+1` is the missing value. If every index matches, the answer is `n+1`.

**Key invariant:** after the placement pass finishes, for every value `v` in `[1, n]` that appears anywhere in the array, at least one copy of `v` sits at index `v-1`. The scan step exploits this directly: the first index whose value doesn't match its expected "home" value is exactly the smallest missing positive.

### Step-by-step trace

Trace on `nums = [3,4,-1,1]` (n = 4):

| i | nums[i] at start of this index | action | array after |
|---|---|---|---|
| 0 | `3` | in range, target index 2 holds `-1 != 3`, swap indices 0 and 2 | `[-1,4,3,1]` |
| 0 (re-check) | `-1` | out of range `[1,4]`, stop | `[-1,4,3,1]` |
| 1 | `4` | in range, target index 3 holds `1 != 4`, swap indices 1 and 3 | `[-1,1,3,4]` |
| 1 (re-check) | `1` | in range, target index 0 holds `-1 != 1`, swap indices 1 and 0 | `[1,-1,3,4]` |
| 1 (re-check) | `-1` | out of range, stop | `[1,-1,3,4]` |
| 2 | `3` | in range, target index 2 is itself (already home), stop | `[1,-1,3,4]` |
| 3 | `4` | in range, target index 3 is itself (already home), stop | `[1,-1,3,4]` |
| scan | - | index 0: `1==1` OK; index 1: `-1 != 2`, mismatch found | answer = `1+1 = 2` |

Final answer: `2`, matching worked example 2.

## Java 8 solution
```java
public class FirstMissingPositive {

    // Smallest positive integer not present in nums. O(n) time, O(1) extra space.
    public static int solve(int[] nums) {
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            // Keep swapping nums[i] into its home slot (index value-1) as long
            // as it belongs in range and isn't already correctly placed there.
            while (nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
                int targetIndex = nums[i] - 1;
                int temp = nums[targetIndex];
                nums[targetIndex] = nums[i];
                nums[i] = temp;
            }
        }

        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        return n + 1; // every value 1..n was present
    }

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(nums);
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

        check(1, new int[]{1, 2, 0}, 3, fail, total);
        check(2, new int[]{3, 4, -1, 1}, 2, fail, total);
        check(3, new int[]{7, 8, 9, 11, 12}, 1, fail, total);
        check(4, new int[]{1, 2, 3}, 4, fail, total);
        check(5, new int[]{1}, 2, fail, total);
        check(6, new int[]{2}, 1, fail, total);
        check(7, new int[]{-1}, 1, fail, total);
        check(8, new int[]{1, 1}, 2, fail, total);
        check(9, new int[]{0, 0, 0}, 1, fail, total);
        check(10, new int[]{2, 3, 4, 5, 6}, 1, fail, total);
        check(11, new int[]{1, 2, 2, 3}, 4, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n): every swap moves at least one value into its correct final home position, and a value is only ever moved into "home" once, so the total number of swaps performed across the entire outer loop is bounded by `n` (the `while` loop can iterate many times for one particular `i`, but each iteration is "charged" to a value becoming newly homed, and there are only `n` values). Space O(1) beyond the input array: a fixed number of scalar index/temp variables.

## Java 8 pitfalls for this problem

- The `while` condition must check **both** that the value is in range **and** that the target slot doesn't already hold that exact value (`nums[nums[i]-1] != nums[i]`). Checking only "not yet in place" (e.g. `nums[i] != i+1`) is not sufficient and can spin forever on duplicates: with `nums = [1,1]`, once one `1` is home at index 0, the second index also wants to send its `1` there, and swapping two equal values changes nothing, so a loop that doesn't recognize "already equal, stop" never terminates.
- Off-by-one between a *value* and an *index*: value `v` belongs at index `v - 1`, never index `v`. This mix-up is the single most common bug in this problem.
- Reaching for a `HashSet`/`HashMap` (as in the brute-force baseline) is easy and correct, but uses `O(n)` extra space, which fails the stated requirement even though it would pass a correctness-only check. This is the same category of near-miss as `TreeMap`-based `getMin` in Min Stack.
- The final answer when every index matches its expected value is `n + 1`, not `n`. It is easy to forget the "fully packed" case and return the wrong sentinel.
- Since `nums` is a raw `int[]`, there is no boxing/unboxing concern here; if this were adapted to work on an `Integer[]` or `List<Integer>`, comparisons like `nums[i] != i + 1` still auto-unbox safely, but swapping elements would need to go through the collection's `set` method rather than direct array assignment.

## Wrong approaches and why they fail

1. **Sort the array first, then scan for the first gap.** This is correct in value but violates the required time complexity: sorting is `O(n log n)`, not `O(n)`. It is worth stating plainly that "this passes on correctness but fails the stated complexity requirement," the same category of near-miss called out in Min Stack's `TreeMap`-based `getMin`.
2. **For each value `k` starting from 1, scan the original unsorted array from scratch to check whether `k` is present, stopping at the first `k` not found.** This is correct but can be `O(n^2)`: consider an array that is exactly `1..n`. Checking each of the `n` values being tested against the full array via a linear "contains" scan costs `O(n)` per value, `O(n^2)` total, and it does not use any in-place marking at all.
3. **Track only the count of positive numbers seen, and return `count + 1`.** Counterexample: `nums = [1,1,5]` has 3 positive numbers, so this approach would report `4`, but the true smallest missing positive is `2`. Duplicates of `1` do not help fill the gap at `2`, and simply counting positives ignores which specific values are present versus missing.

## Variants

1. **Return every missing positive integer up to some bound, not just the smallest.** After the same placement pass, scan the whole array and collect every index whose value doesn't match, rather than stopping at the first one.
2. **The array is read-only and cannot be mutated in place.** This forces a different `O(1)`-extra-space trick (or acceptance of `O(n)` space with a presence array as the practical fallback), since the core technique here fundamentally relies on rearranging the array.
3. **"Smallest missing non-negative integer"** (0-indexed instead of 1-indexed) shifts the target range down by one and changes the home-slot formula from `nums[i]-1` to `nums[i]`, but is otherwise the identical technique.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[1,2,0]` | `3` | classic small case |
| 2 | `[3,4,-1,1]` | `2` | negative and out-of-range values mixed in |
| 3 | `[7,8,9,11,12]` | `1` | no value falls within [1,n] at all |
| 4 | `[1,2,3]` | `4` | fully packed, answer is n+1 |
| 5 | `[1]` | `2` | single element, present |
| 6 | `[2]` | `1` | single element, missing 1 |
| 7 | `[-1]` | `1` | single negative element |
| 8 | `[1,1]` | `2` | duplicate value, tests the infinite-swap-loop guard |
| 9 | `[0,0,0]` | `1` | no positives at all |
| 10 | `[2,3,4,5,6]` | `1` | never contains 1 |
| 11 | `[1,2,2,3]` | `4` | duplicate present, but 1..3 all covered |
