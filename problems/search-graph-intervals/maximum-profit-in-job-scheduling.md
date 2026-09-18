# Maximum Profit in Task Scheduling
`ref: LC 1235` · Difficulty: Hard · Pattern: Sort by end time, DP with binary search for the last compatible item

## Problem
You're given a list of tasks, each described by a start time, an end time, and a profit. A single worker can run tasks one at a time; two tasks are compatible if one ends at or before the other begins (touching at the same instant is fine, overlapping is not). Choose a subset of tasks, no two of which overlap, that maximizes the total profit, and report that maximum total.

## Constraints
- `1 <= startTime.length == endTime.length == profit.length <= 5 * 10^4`
- `1 <= startTime[i] < endTime[i] <= 10^9`
- `1 <= profit[i] <= 10^4`
- With `n` up to `5 * 10^4`, an `O(n log n)` sort-then-DP approach is required; checking every subset of tasks directly is exponential and far too slow.

## Worked examples
1. `startTime=[1,2,3,3]`, `endTime=[3,4,5,6]`, `profit=[50,10,40,70]` -> `120`. Taking the task `(1,3,50)` and the task `(3,6,70)` gives `50 + 70 = 120`; they're compatible since the first ends exactly when the second begins. No other combination beats this.
2. `startTime=[1,2,3,4,6]`, `endTime=[3,5,10,6,9]`, `profit=[20,20,100,70,60]` -> `150`. Taking `(1,3,20)`, `(4,6,70)`, and `(6,9,60)` gives `20 + 70 + 60 = 150`.
3. `startTime=[1,1,1]`, `endTime=[2,3,4]`, `profit=[5,6,4]` -> `6`. All three tasks share the same start time, so no two of them can ever be compatible with each other; the best choice is the single highest-profit task, `(1,3,6)`.

## Edge cases checklist
- Only one task available (the answer is simply its profit).
- Every task overlaps every other task (the answer is the single highest-profit task).
- Every task is compatible with every other task in a chain (the answer is the sum of all profits).
- Two tasks that exactly touch (`end == start` of the next), which must count as compatible, not overlapping.
- Two tasks with identical end times but different start times and profits (tests that sorting by end time alone, with ties broken arbitrarily, still finds the optimal choice).
- A task with a very high profit that overlaps everything else, competing against several smaller compatible tasks whose sum is larger.

## Approach

### Brute force
Try every subset of tasks (there are `2^n` of them), discard any subset containing an overlapping pair, and keep the maximum total profit among the valid ones. This is `O(2^n * n)` in the worst case (checking every pair within every subset) -- for `n` up to `5 * 10^4` this is astronomically slow, though it is a fine way to check correctness on tiny inputs by hand.

### Optimal
Sort the tasks by end time. Then build a DP array `dp[i]`, meaning "the best total profit achievable using only the first `i` tasks in this sorted order." For each task `i` (0-indexed in sorted order), either skip it (`dp[i]` carries over unchanged into `dp[i+1]`) or take it (its own profit plus `dp[j+1]`, where `j` is the last earlier task, in sorted order, whose end time is at most this task's start time -- found by binary search over the already-sorted end times, since only end times before the current task's index need to be considered).

**Key invariant:** once tasks are sorted by end time, `dp[i]` (the best achievable profit using only the first `i` sorted tasks) is monotonically non-decreasing in `i`, and for any task, every earlier task compatible with it (end time `<= this task's start time`) appears as a contiguous prefix of the sorted order up to some index `j` -- so a single binary search over that prefix's end times finds the most profit obtainable from any compatible earlier subset, without needing to check every earlier task individually.

Proof sketch: `dp[i+1] = max(dp[i], profit[i] + dp[j+1])` considers exactly the two choices available for task `i`: skip it, or take it and combine with the best result achievable from only its compatible predecessors. Because tasks are processed in increasing end-time order, `dp[j+1]` at the time task `i` is processed already reflects the optimal profit over all tasks that could possibly be compatible with task `i` (any task with a later end time cannot be a compatible predecessor of task `i`, since it either overlaps task `i` or starts even later). This makes the recurrence correct by induction: `dp[0] = 0` trivially, and each step only ever combines already-correct smaller subproblems.

### Step-by-step trace
Trace on `startTime=[1,2,3,3]`, `endTime=[3,4,5,6]`, `profit=[50,10,40,70]` (already sorted by end time):

| i (sorted index) | task (start,end,profit) | j = latest compatible index | dp[j+1] used | take profit = profit[i]+dp[j+1] | dp[i+1] = max(dp[i], take) |
|---|---|---|---|---|---|
| 0 | (1,3,50) | none (-1) | dp[0]=0 | 50+0=50 | dp[1]=max(0,50)=50 |
| 1 | (2,4,10) | none (-1), since no end <= 2 | dp[0]=0 | 10+0=10 | dp[2]=max(50,10)=50 |
| 2 | (3,5,40) | index 0 (end=3<=3) | dp[1]=50 | 40+50=90 | dp[3]=max(50,90)=90 |
| 3 | (3,6,70) | index 0 (end=3<=3; index1's end=4>3) | dp[1]=50 | 70+50=120 | dp[4]=max(90,120)=120 |

Final answer: `dp[4] = 120`, matching worked example 1.

## Java 8 solution
```java
import java.util.Arrays;
import java.util.Comparator;

public static int solve(int[] startTime, int[] endTime, int[] profit) {
    int n = startTime.length;
    Integer[] order = new Integer[n];
    for (int i = 0; i < n; i++) {
        order[i] = i;
    }
    Arrays.sort(order, new Comparator<Integer>() {
        public int compare(Integer a, Integer b) {
            return Integer.compare(endTime[a], endTime[b]); // sort task indices by end time
        }
    });

    int[] start = new int[n];
    int[] end = new int[n];
    int[] prof = new int[n];
    for (int i = 0; i < n; i++) {
        start[i] = startTime[order[i]];
        end[i] = endTime[order[i]];
        prof[i] = profit[order[i]];
    }

    int[] dp = new int[n + 1]; // dp[i] = best profit using only the first i sorted tasks
    for (int i = 0; i < n; i++) {
        int j = latestCompatible(end, i, start[i]); // rightmost index < i with end[index] <= start[i]
        int takeProfit = prof[i] + dp[j + 1];
        dp[i + 1] = Math.max(dp[i], takeProfit);
    }
    return dp[n];
}

// Binary searches end[0..upperExclusive-1] for the rightmost index whose end <= target; -1 if none.
private static int latestCompatible(int[] end, int upperExclusive, int target) {
    int lo = 0, hi = upperExclusive - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (end[mid] <= target) {
            result = mid;
            lo = mid + 1; // there might be an even later compatible index
        } else {
            hi = mid - 1;
        }
    }
    return result;
}
```

## Complexity
- Time: `O(n log n)` -- sorting the tasks by end time costs `O(n log n)`, and the DP loop does `n` binary searches, each `O(log n)`.
- Space: `O(n)` -- the sorted copies of start/end/profit, the index-order array, and the `dp` array.

## Java 8 pitfalls for this problem
- `Arrays.sort` on an `Integer[]` with a `Comparator<Integer>` (sorting task *indices*, not the raw arrays directly) keeps `start[i]`, `end[i]`, and `profit[i]` in sync with each other after reordering; sorting `startTime`, `endTime`, and `profit` separately and independently would scramble which start/end/profit values originally belonged together.
- `new Comparator<Integer>() { ... }` is a plain anonymous class capturing the enclosing `endTime` array; this compiles under `--release 8` because it's an anonymous class with an explicit type argument, not a `new Comparator<>() { ... }` diamond-with-anonymous-class (Java 9+ only).
- The binary search here looks for the rightmost index with `end[index] <= target`, which is deliberately `<=` (not `<`) to correctly treat touching tasks (`end == start` of the next) as compatible, matching the problem's stated boundary rule.
- `dp` is sized `n + 1`, not `n`, so that `dp[j + 1]` is always valid even when `j == -1` (no compatible earlier task at all) -- `dp[0]` represents "zero tasks used so far," which is the correct base case of `0` profit.
- Autoboxing `Integer[] order` (rather than a primitive `int[]`) is required here specifically because `Arrays.sort` only accepts a `Comparator` overload for object arrays, not primitive `int[]` -- this is a real (if usually small) allocation and unboxing cost worth knowing about, not a bug.

## Wrong approaches and why they fail
- **Sort by start time instead of end time, and DP forward the same way.** Counterexample: `startTime=[1,2,3,3]`, `endTime=[3,4,5,6]`, `profit=[50,10,40,70]` -- sorting by start time puts task `(3,5,40)` before task `(3,6,70)` in an order tied on start, but critically, the DP recurrence itself relies on "everything before index `i` in sorted order has already finished or could have finished by some earlier point," which is only guaranteed when sorting by *end* time; sorting by start time breaks the property that `dp[i]` represents a well-defined prefix of tasks that could all be scheduled by time `start[i]`.
- **Greedily take the highest-profit task first, then repeatedly take the next-highest-profit task compatible with everything already chosen, ignoring lower-profit tasks along the way.** Counterexample: `startTime=[1,2]`, `endTime=[2,3]`, `profit=[5,6]` -- picking the higher-profit task `(2,3,6)` first blocks nothing here so it happens to work, but with `startTime=[1,1,2]`, `endTime=[10,2,10]`, `profit=[10,4,4]`, greedily taking the single highest-profit task `(1,10,10)` first blocks both smaller tasks entirely, when in fact no combination beats `10` anyway here -- the real failure shows up once the "blocked" alternative sum would have been larger than the single greedy pick, which a pure greedy-by-profit strategy has no way to detect without the DP's exhaustive-but-efficient comparison.
- **Use interval-scheduling's classic "always pick the task with the earliest end time" greedy (ignoring profit entirely, as if maximizing count of non-overlapping tasks).** Counterexample: `startTime=[1,1]`, `endTime=[2,100]`, `profit=[1,1000]` -- greedily taking the earliest-ending task `(1,2,1)` first (to "leave room" for more tasks) locks in a profit of only `1`, or at best `1` plus whatever fits after time `2`, when simply taking the single task `(1,100,1000)` instead is far more profitable; this greedy rule solves a different problem (maximizing the *count* of tasks) and ignores profit weighting entirely.

## Variants
- **Return the actual set of chosen tasks, not just the total profit.** Track, alongside each `dp[i+1]` update, whether it came from the "skip" or "take" branch, then walk the `dp` array backward from `dp[n]` to reconstruct which tasks were taken.
- **Weighted Interval Scheduling with a small profit range** (profits bounded by a small constant instead of up to `10^4`) can sometimes be solved with a different DP indexed by profit rather than by task, though the sort-and-binary-search approach here works regardless of profit magnitude.
- **LC 1751 Maximum Number of Events That Can Be Attended II** is the same underlying pattern with an added cap `k` on how many tasks may be chosen, extending the DP state to `dp[i][k]`.

## Test cases
| # | startTime | endTime | profit | expected | what it tests |
|---|---|---|---|---|---|
| 1 | `[1,2,3,3]` | `[3,4,5,6]` | `[50,10,40,70]` | 120 | standard case, exact trace |
| 2 | `[1,2,3,4,6]` | `[3,5,10,6,9]` | `[20,20,100,70,60]` | 150 | three compatible tasks chosen out of five |
| 3 | `[1,1,1]` | `[2,3,4]` | `[5,6,4]` | 6 | every task overlaps every other, pick the best single one |
| 4 | `[1]` | `[2]` | `[5]` | 5 | single task |
| 5 | `[1,2]` | `[2,3]` | `[5,6]` | 11 | touching tasks are compatible, both taken |
| 6 | `[1,2]` | `[3,4]` | `[5,6]` | 6 | overlapping tasks, pick the higher-profit one |
| 7 | `[1,2,3]` | `[10,10,10]` | `[5,6,4]` | 6 | all overlapping through a shared late end time |
| 8 | `[1,2,3,4]` | `[2,3,4,5]` | `[10,10,10,10]` | 40 | fully compatible chain, sum every profit |
| 9 | `[1,2]` | `[3,3]` | `[10,20]` | 20 | duplicate end times, overlapping tasks |
| 10 | `[10]` | `[20]` | `[100]` | 100 | single task, larger values |
