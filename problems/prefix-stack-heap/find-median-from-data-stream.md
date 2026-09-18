# Find Median from Data Stream
`ref: LC 295` · Difficulty: Hard · Pattern: two heaps split at the median

## Problem

Design a structure that receives integers one at a time (a stream) and can report the median of every number seen so far at any point. The median of an odd count of numbers is the single middle value once sorted; the median of an even count is the average of the two middle values.

The class exposes:
- `void addNum(int num)`: record a new number from the stream.
- `double findMedian()`: return the median of all numbers recorded so far. Always called after at least one `addNum`.

## Constraints

- `-10^5 <= num <= 10^5`
- Up to roughly `5 * 10^4` total calls across `addNum` and `findMedian` combined.
- `findMedian` is only ever called after at least one `addNum`.
- With up to `5*10^4` insertions, an approach that re-sorts the entire stream on every `findMedian` call is workable in practice at this size but is the wrong complexity class to reach for; each `addNum` should cost `O(log n)` and each `findMedian` should cost `O(1)`.

## Worked examples

1. `addNum(1)`, `addNum(2)` -> `findMedian()` = `1.5`. Then `addNum(3)` -> `findMedian()` = `2`. Two numbers average to `1.5`; adding a third makes `2` the exact middle of `1,2,3`.
2. `addNum(5)` -> `findMedian()` = `5.0`. A single number is its own median.
3. `addNum(2)`, `addNum(1)` -> `findMedian()` = `1.5`. Numbers arriving out of sorted order still produce the correct median, since the two values sorted are `1,2` either way.
4. `addNum(-5)`, `addNum(-1)`, `addNum(-3)` -> `findMedian()` = `-3`. Sorted, the three values are `-5,-3,-1`, and the middle one is `-3`.

## Edge cases checklist

- Exactly one number recorded before the first `findMedian` call (odd count of one).
- Exactly two numbers recorded (even count; the median is an average and may end in `.5`).
- Numbers arriving out of sorted order (the structure must not assume sorted input).
- Duplicate values added more than once.
- All negative values.
- Values that straddle zero (mixed positive and negative).
- The two extreme bounds (`-10^5` and `10^5`) added together, checking that the average computation and any comparator arithmetic stay correct at the edges of the allowed range.
- `findMedian` called more than once in a row with no intervening `addNum` (must return the same value both times, since querying does not mutate state).
- A long run of insertions that keeps alternating which of the two internal halves needs to grow, exercising the rebalancing step repeatedly.

## Approach

### Naive

Keep every number in a plain list. On each `findMedian` call, copy and sort the list (or keep it always sorted by inserting each new number into its correct position via a linear scan and shift), then read off the middle element(s). Sorting from scratch on every query is `O(n log n)` per `findMedian`; keeping it always sorted via insertion is `O(n)` per `addNum` (due to shifting elements to make room). Either way, this does not meet the `O(log n)`-per-`addNum`, `O(1)`-per-`findMedian` target, though it is a useful correctness baseline for testing the faster version on small inputs.

### Optimal

Maintain two heaps that together hold every number seen so far, split down the middle:
- `lowerHalf`, a max-heap holding the smaller half of the numbers (its top is the largest of the small half).
- `upperHalf`, a min-heap holding the larger half of the numbers (its top is the smallest of the large half).

On `addNum(num)`: push `num` into `lowerHalf`, then immediately move `lowerHalf`'s new top into `upperHalf` (this guarantees every value in `lowerHalf` is `<=` every value in `upperHalf`, regardless of where `num` actually belonged). Then, if that left `upperHalf` larger than `lowerHalf`, move `upperHalf`'s top back into `lowerHalf` to restore balance.

On `findMedian()`: if `lowerHalf` has one more element than `upperHalf`, the median is `lowerHalf`'s top. Otherwise the two heaps are the same size, and the median is the average of both tops.

**Key invariant:** every value in `lowerHalf` is less than or equal to every value in `upperHalf`, and the two heaps' sizes never differ by more than one (with `lowerHalf` allowed to hold the single extra element when the total count is odd). Together these two facts mean the boundary between the heaps is always exactly the median boundary, so the median is always readable directly from one or both heap tops without ever scanning either heap's contents.

### Step-by-step trace

Trace on `addNum(1)`, `addNum(2)`, `findMedian()`, `addNum(3)`, `findMedian()`:

| step | operation | lowerHalf (max-heap, top first) | upperHalf (min-heap, top first) | result |
|---|---|---|---|---|
| 1 | `addNum(1)` | `[1]` | `[]` | - |
| 2 | `addNum(2)` | `[1]` | `[2]` | - |
| 3 | `findMedian()` | `[1]` | `[2]` | `(1+2)/2.0 = 1.5` |
| 4 | `addNum(3)` | `[2,1]` | `[3]` | - |
| 5 | `findMedian()` | `[2,1]` | `[3]` | `lowerHalf` bigger, top `= 2` |

Walking through step 4 in detail: `lowerHalf.add(3)` makes `lowerHalf = [3,1]` (top `3`); moving its top into `upperHalf` gives `upperHalf = [2,3]` (top `2`) and `lowerHalf = [1]`; since `upperHalf` (size 2) is now bigger than `lowerHalf` (size 1), move `upperHalf`'s top back: `lowerHalf = [2,1]` (top `2`), `upperHalf = [3]`. Final median at step 5 is `2`, matching worked example 1.

## Java 8 solution
```java
import java.util.PriorityQueue;

class MedianFinder {
    private final PriorityQueue<Integer> lowerHalf; // max-heap: largest of the lower half on top
    private final PriorityQueue<Integer> upperHalf; // min-heap: smallest of the upper half on top

    public MedianFinder() {
        lowerHalf = new PriorityQueue<Integer>((a, b) -> Integer.compare(b, a));
        upperHalf = new PriorityQueue<Integer>();
    }

    public void addNum(int num) {
        lowerHalf.add(num);
        upperHalf.add(lowerHalf.poll()); // keeps every lowerHalf value <= every upperHalf value
        if (upperHalf.size() > lowerHalf.size()) {
            lowerHalf.add(upperHalf.poll()); // rebalance: lowerHalf may hold at most one extra
        }
    }

    public double findMedian() {
        if (lowerHalf.size() > upperHalf.size()) {
            return lowerHalf.peek();
        }
        return (lowerHalf.peek() + upperHalf.peek()) / 2.0;
    }
}

public class FindMedianFromDataStream {

    private static void check(int caseNum, String label, String[] ops, int[] vals, Double[] expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        MedianFinder mf = new MedianFinder();
        for (int i = 0; i < ops.length; i++) {
            if (ops[i].equals("add")) {
                mf.addNum(vals[i]);
            } else {
                double got = mf.findMedian();
                double exp = expected[i];
                if (Math.abs(got - exp) > 1e-9) {
                    failCount[0]++;
                    System.out.println("FAIL case " + caseNum + " (" + label + ") step " + i + ": expected " + exp + " got " + got);
                    return;
                }
            }
        }
        System.out.println("PASS case " + caseNum + " (" + label + ")");
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "increasing then odd count", new String[]{"add", "add", "find", "add", "find"},
                new int[]{1, 2, 0, 3, 0}, new Double[]{null, null, 1.5, null, 2.0}, fail, total);

        check(2, "single element", new String[]{"add", "find"},
                new int[]{5, 0}, new Double[]{null, 5.0}, fail, total);

        check(3, "out of order insertion", new String[]{"add", "add", "find"},
                new int[]{2, 1, 0}, new Double[]{null, null, 1.5}, fail, total);

        check(4, "all negative values", new String[]{"add", "add", "add", "find"},
                new int[]{-5, -1, -3, 0}, new Double[]{null, null, null, -3.0}, fail, total);

        check(5, "duplicate values", new String[]{"add", "add", "add", "find"},
                new int[]{1, 1, 1, 0}, new Double[]{null, null, null, 1.0}, fail, total);

        check(6, "even count average with .5", new String[]{"add", "add", "add", "add", "find"},
                new int[]{1, 2, 3, 4, 0}, new Double[]{null, null, null, null, 2.5}, fail, total);

        check(7, "repeated zero values", new String[]{"add", "find", "add", "find"},
                new int[]{0, 0, 0, 0}, new Double[]{null, 0.0, null, 0.0}, fail, total);

        check(8, "mixed sign values, odd count", new String[]{"add", "add", "add", "add", "add", "find"},
                new int[]{-1, 2, -3, 4, -5, 0}, new Double[]{null, null, null, null, null, -1.0}, fail, total);

        check(9, "extreme bound values", new String[]{"add", "add", "find"},
                new int[]{100000, -100000, 0}, new Double[]{null, null, 0.0}, fail, total);

        check(10, "median re-queried after every insertion", new String[]{"add", "find", "add", "find", "add", "find"},
                new int[]{3, 0, 1, 0, 2, 0}, new Double[]{null, 3.0, null, 2.0, null, 2.0}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

`addNum`: O(log n). Each call does a constant number of heap pushes/pops, each O(log n) where n is the count of numbers seen so far. `findMedian`: O(1), a peek (or two) and, when needed, one addition and division. Space: O(n) total across both heaps, to hold every number ever added.

## Java 8 pitfalls for this problem

- `PriorityQueue`'s no-argument constructor gives a min-heap; `lowerHalf` needs a max-heap, so it must be built with an explicit reversing comparator, e.g. `new PriorityQueue<Integer>((a, b) -> Integer.compare(b, a))`. Using the default constructor here silently produces the wrong ordering rather than throwing an error, and the bug only shows up as a wrong median, not a crash.
- Prefer `Integer.compare(b, a)` over the shorter `b - a` for the comparator body: for values already bounded to `-10^5..10^5` the subtraction happens to be safe here, but `Integer.compare` is the overflow-safe habit to default to regardless of the specific bound.
- `peek()` on an empty `PriorityQueue` returns `null`, not an exception; unboxing that `null` into a `double` throws `NullPointerException`. This problem guarantees `findMedian` is never called before at least one `addNum`, so no defensive check is required here, but it's worth being able to say precisely why it's safe to omit one.
- `(lowerHalf.peek() + upperHalf.peek()) / 2.0` must divide by the `double` literal `2.0`, not the `int` literal `2`. Dividing two `int`s (or an `int` sum by `int 2`) truncates any `.5` fractional part silently.
- The rebalancing check (`if (upperHalf.size() > lowerHalf.size())`) must run on every single `addNum` call, not conditionally skipped in some code path. An easy way to introduce a subtle bug is restructuring the method so the rebalance step is only reached along one branch, letting heap sizes drift apart silently over many calls.

## Wrong approaches and why they fail

1. **Track a running sum and count, and report `sum / count` (the mean) as the median.** Counterexample: `addNum(1)`, `addNum(2)`, `addNum(100)`. The mean is `103/3 ≈ 34.33`, but the true median (sorted: `1,2,100`) is `2`. Mean and median are different statistics and only coincide in special, symmetric cases.
2. **Keep numbers in an ordinary list in insertion order, and on `findMedian`, read off the value at the physical middle index of that list (never sorting it).** Counterexample: `addNum(5)`, `addNum(1)`, `addNum(3)`. The list in insertion order is `[5,1,3]`, and its middle index (`1`) holds `1`, but the true sorted order is `[1,3,5]` with true median `3`. The physical middle of an unsorted list has no relationship to the sorted middle.
3. **Push values into the two heaps by call count (alternate: 1st insert to `lowerHalf`, 2nd to `upperHalf`, 3rd to `lowerHalf`, ...) instead of by value, skipping the "always insert into `lowerHalf` first, then shuffle across" dance.** Counterexample: `addNum(1)` -> `lowerHalf`, `addNum(2)` -> `upperHalf`, `addNum(3)` -> `lowerHalf`: this leaves `lowerHalf = {1,3}` and `upperHalf = {2}`, and now `lowerHalf`'s max (`3`) is *not* `<=` `upperHalf`'s min (`2`), breaking the ordering invariant entirely; `findMedian()` would read `(3+2)/2 = 2.5` where the true median of `1,2,3` is `2`.

## Variants

1. **Median of a fixed-size sliding window over the stream (old values expire).** A plain heap cannot efficiently remove an arbitrary non-top element, so this needs lazy deletion (mark-and-skip) or a different structure such as a balanced order-statistics tree.
2. **Support removing a previously added number, not just adding.** Same complication as the sliding-window variant: heaps are not designed for efficient arbitrary removal.
3. **Track the k-th percentile instead of exactly the median.** The same two-heap idea generalizes by keeping the size ratio between the two heaps at roughly `k : (100-k)` instead of `1 : 1`.

## Test cases

| # | operation sequence | expected outputs (at find steps) | what it tests |
|---|---|---|---|
| 1 | `add(1) add(2) find() add(3) find()` | `1.5, 2.0` | classic increasing sequence, odd/even transition |
| 2 | `add(5) find()` | `5.0` | single element |
| 3 | `add(2) add(1) find()` | `1.5` | out-of-order insertion |
| 4 | `add(-5) add(-1) add(-3) find()` | `-3.0` | all negative values |
| 5 | `add(1) add(1) add(1) find()` | `1.0` | duplicate values |
| 6 | `add(1) add(2) add(3) add(4) find()` | `2.5` | even count, average has a .5 |
| 7 | `add(0) find() add(0) find()` | `0.0, 0.0` | repeated zero, degenerate median |
| 8 | `add(-1) add(2) add(-3) add(4) add(-5) find()` | `-1.0` | mixed sign values, odd count |
| 9 | `add(100000) add(-100000) find()` | `0.0` | extreme bound values |
| 10 | `add(3) find() add(1) find() add(2) find()` | `3.0, 2.0, 2.0` | median re-queried after every insertion |
