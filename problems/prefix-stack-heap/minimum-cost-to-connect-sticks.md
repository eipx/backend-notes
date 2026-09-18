# Minimum Cost to Connect Sticks
`ref: LC 1167` · Difficulty: Medium · Pattern: greedy min-heap (Huffman-style merging)

## Problem

You have a collection of sticks, each with a positive integer length, given as an array. In one move you may pick any two sticks currently available and connect them into a single new stick; connecting sticks of length `x` and `y` costs `x + y`, and the resulting stick of length `x + y` becomes available for further connections. Keep connecting until only one stick remains. Return the minimum total cost across all the moves needed to reduce the whole collection to a single stick.

Equivalent phrasing: given a multiset of numbers, repeatedly replace any two numbers with their sum, paying a cost equal to that sum each time, until only one number remains; return the minimum total cost.

Input: an integer array `sticks` of length `n >= 1`.
Output: a single integer, the minimum total connection cost to merge every stick into one.

## Constraints

- `1 <= sticks.length <= 10^4`
- `1 <= sticks[i] <= 10^4`
- With `n` up to `1e4`, repeatedly scanning the whole collection to find the two smallest values (an `O(n)` scan per move, `O(n)` moves) is `O(n^2)`, correct but not the intended complexity; an `O(n log n)` heap-based approach is expected.
- The running total cost can grow well beyond what any single stick length would suggest. This is worth keeping in mind for the accumulator's type even though this particular bound does not quite overflow a 32-bit `int`.

## Worked examples

1. `sticks = [2,4,3]` -> `14`. Combine the two smallest (`2` and `3`) first for cost `5`, leaving `{5,4}`; combine those for cost `9`. Total `5 + 9 = 14`.
2. `sticks = [1,8,3,5]` -> `30`. Combine `1+3=4` (cost `4`), leaving `{4,5,8}`; combine `4+5=9` (cost `9`), leaving `{9,8}`; combine `9+8=17` (cost `17`). Total `4+9+17=30`.
3. `sticks = [5]` -> `0`. A single stick needs no connections at all.
4. `sticks = [1,1]` -> `2`. One connection: `1+1=2`, cost `2`.

## Edge cases checklist

- A single stick (no connections needed, cost `0`).
- Exactly two sticks (exactly one connection, cost is just their sum).
- All sticks the same length.
- Input order scrambled versus sorted (the answer must not depend on the original order, since a heap is order-agnostic).
- A large number of sticks, to confirm the approach is `O(n log n)` rather than `O(n^2)`.
- The maximum stick length (`10^4`) repeated many times, as a sanity check that the running total does not silently overflow.
- Ties among the smallest values (which specific physical stick is chosen among equal values never changes the total cost).
- An odd number of sticks versus an even number (both must reduce cleanly to a single stick; odd counts leave one stick unpaired at some intermediate step, which is normal, not an error).

## Approach

### Brute force

Repeatedly scan the entire current collection with a linear pass to find the two smallest values, remove them, and insert their sum back in; repeat until one stick remains. This greedy *rule* is already optimal (see below), but implementing "find the two smallest" via a fresh linear scan every time costs `O(n)` per move and there are `O(n)` moves, giving `O(n^2)` overall. That is correct, but too slow at the stated bound of `10^4` sticks, and asymptotically the wrong complexity class regardless of the exact limit.

### Optimal

Use the same greedy rule (always combine the two currently smallest sticks), but back it with a min-heap so that finding and removing the two smallest, and inserting the new sum, are each `O(log n)` instead of `O(n)`.

**Key invariant:** the total cost equals the sum, over every original stick, of `(its length) × (the number of connection moves it is "carried through," i.e. how many times it, or a stick containing it, is combined again before the process ends)`. Always combining the two currently smallest sticks first assigns them to be combined the earliest, meaning their value gets carried through the most subsequent moves; keeping the smallest values at the "deepest," most-repeated position and the largest values combined the fewest times is exactly what minimizes the total weighted sum. This is the same exchange argument that makes Huffman coding optimal: give the smallest weights the most "depth."

Proof sketch: suppose some optimal merge order does *not* combine the two globally smallest sticks first. Then there exist two sticks `a <= b` that are the two smallest overall, but the order first combines some other pair. Standard exchange-argument reasoning (as in Huffman coding) shows swapping the merge order so that `a` and `b` merge at the earliest opportunity never increases the total cost, because doing so can only decrease or preserve the depth (repeat-count) of every other, larger value while `a` and `b` (the smallest values, contributing the least per repeat) absorb any resulting increase in their own depth. Repeating this argument across the whole merge sequence shows a schedule that always combines the two current smallest values is optimal.

### Step-by-step trace

Trace on `sticks = [2,4,3]` (heap contents shown sorted for clarity, min-heap semantics: always poll the smallest):

| step | heap before | poll x | poll y | cost (x+y) | running total | heap after |
|---|---|---|---|---|---|---|
| 1 | `[2,3,4]` | `2` | `3` | `5` | `5` | `[4,5]` |
| 2 | `[4,5]` | `4` | `5` | `9` | `14` | `[9]` |
| end | `[9]` | - | - | - | - | one stick remains, stop |

Final total cost: `14`, matching worked example 1.

## Java 8 solution
```java
import java.util.PriorityQueue;

public class MinimumCostToConnectSticks {

    // Minimum total cost to combine every stick into one, always combining
    // the two currently shortest sticks first.
    public static int solve(int[] sticks) {
        PriorityQueue<Integer> heap = new PriorityQueue<Integer>(); // natural ordering is already a min-heap
        for (int stick : sticks) {
            heap.add(stick);
        }

        long totalCost = 0; // long as a defensive margin against the worst-case accumulated sum
        while (heap.size() > 1) {
            int first = heap.poll();
            int second = heap.poll();
            int combined = first + second;
            totalCost += combined;
            heap.add(combined);
        }
        return (int) totalCost;
    }

    private static void check(int caseNum, int[] sticks, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(sticks);
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

        check(1, new int[]{2, 4, 3}, 14, fail, total);
        check(2, new int[]{1, 8, 3, 5}, 30, fail, total);
        check(3, new int[]{5}, 0, fail, total);
        check(4, new int[]{1, 1}, 2, fail, total);
        check(5, new int[]{1, 2, 3, 4}, 19, fail, total);
        check(6, new int[]{4, 3, 2, 1}, 19, fail, total);
        check(7, new int[]{10000, 10000, 10000}, 50000, fail, total);
        check(8, new int[]{1, 1, 1, 1}, 8, fail, total);
        check(9, new int[]{1, 2}, 3, fail, total);
        check(10, new int[]{3, 3, 3, 3, 3}, 36, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(n log n): building the initial heap is `O(n)` (or `O(n log n)` with repeated `add` calls), and there are `n - 1` combine steps, each doing a constant number of `O(log n)` heap operations. Space O(n) for the heap.

## Java 8 pitfalls for this problem

- `PriorityQueue<Integer>`'s default (no-argument) constructor already gives a min-heap by natural ordering, which is exactly what this problem needs. No custom comparator is required here, unlike Reorganize String or the lower-half heap in Find Median, which both need an explicit max-heap comparator; mixing up which problems need which heap direction is an easy slip when working through several heap problems back to back.
- The loop guard must be `heap.size() > 1`, strictly greater than one. This guarantees both `poll()` calls inside the loop are always safe (never called on an empty or single-element heap), so no extra null checks are needed on the polled values.
- Accumulate the running total in a `long`, not an `int`. The true worst case here (`10^4` sticks of length `10^4` each) does not quite overflow a 32-bit `int`, but it is close enough that defaulting to `long` for the accumulator, and only narrowing to `int` once at the very end, is the safer habit to default to for this class of "repeatedly re-sum everything" problem.
- `heap.poll()` returns a boxed `Integer`; adding two of them (`first + second`) auto-unboxes safely to compute a primitive `int` sum, which is fine at this problem's bounds, but remember auto-unboxing a `null` (from polling an empty heap) throws `NullPointerException` rather than returning a sentinel value. This is again why the `size() > 1` guard matters.
- This problem does not need `Comparator.reverseOrder()` or a lambda at all, since natural `Integer` ordering already sorts ascending. Reaching for a reversed comparator out of habit (carried over from a max-heap problem solved earlier in the same sitting) is a common mix-up.

## Wrong approaches and why they fail

1. **Always combine the two largest available sticks first.** Counterexample: `sticks = [2,4,3]`. Combining the largest two (`4` and `3`) first costs `7`, leaving `{7,2}`, then combining those costs `9`, for a total of `16`, worse than the optimal `14` obtained by combining the smallest two first. Large values should be combined as few times as possible, not repeatedly re-summed early.
2. **Combine sticks strictly in their original input order (always combine the current first two elements, replace with their sum, repeat), ignoring value entirely.** Counterexample: `sticks = [1,8,3,5]` combined left-to-right in place: `1+8=9` (cost `9`), leaving `[9,3,5]`; `9+3=12` (cost `12`, running `21`), leaving `[12,5]`; `12+5=17` (cost `17`, running `38`), far worse than the optimal `30` found by always combining the two smallest by value.
3. **Sort once at the start, then statically pair up adjacent sticks in that fixed order (e.g. combine `sticks[0]+sticks[1]`, `sticks[2]+sticks[3]`, ...) and only afterward combine those intermediate sums, without ever re-inserting a freshly created sum back into full competition with the original values.** Counterexample: `sticks = [1,2,3,4]`: static pairing gives `(1+2)=3` (cost `3`) and `(3+4)=7` (cost `7`), then combining those two results costs `10`; total `3+7+10=20`. The true optimum (always take the two smallest currently available, including newly created sums) is `19`: combine `1+2=3` (cost `3`), which creates a *second* `3` that should immediately compete with the original `3` already in the collection. Combine `3+3=6` (cost `6`, running `9`), then `6+4=10` (cost `10`, running `19`). The static approach fails because it never lets the newly created `3` re-enter competition with the pre-existing `3` before moving on to a fixed later round.

## Variants

1. **Return the actual sequence of merges (which two values were combined at each step), not just the total cost.** Record each polled pair alongside the running total as the loop executes.
2. **"Minimum Cost to Connect Ropes"** is the identical problem under a different name (ropes instead of sticks); same algorithm, no changes needed.
3. **Huffman coding.** The identical always-combine-the-two-smallest-weights algorithm, applied to symbol frequencies instead of stick lengths, builds an optimal prefix-free binary code; the total accumulated cost computed here is exactly the weighted external path length that Huffman coding minimizes.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `[2,4,3]` | `14` | general case, three sticks |
| 2 | `[1,8,3,5]` | `30` | four sticks, multiple merge levels |
| 3 | `[5]` | `0` | single stick, no connections |
| 4 | `[1,1]` | `2` | minimal two-stick case |
| 5 | `[1,2,3,4]` | `19` | tests the static-pairing wrong approach |
| 6 | `[4,3,2,1]` | `19` | same multiset as case 5, reversed order, confirms order independence |
| 7 | `[10000,10000,10000]` | `50000` | maximum stick length, boundary value |
| 8 | `[1,1,1,1]` | `8` | four equal sticks |
| 9 | `[1,2]` | `3` | minimal two-stick case, distinct values |
| 10 | `[3,3,3,3,3]` | `36` | odd count, all equal sticks |
