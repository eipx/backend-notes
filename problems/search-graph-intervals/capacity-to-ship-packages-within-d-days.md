# Capacity To Ship Packages Within D Days
`ref: LC 1011` · Difficulty: Medium · Pattern: Binary search on the answer

## Problem
A conveyor belt loads packages onto a ship in the exact order they are given, one array of positive integer weights. A ship capacity `C` is chosen once in advance; the ship then loads packages for "today," in array order, adding each next package's weight to the running load as long as the load does not exceed `C`. The moment adding the next package would exceed `C`, that day ends, the load resets to zero, and the next day begins with that package. Packages cannot be reordered or split across days.

Given a fixed number of days `D`, the output is the smallest capacity `C` such that every package can be shipped, in its original order, using at most `D` days under the loading rule above. "Valid" means: simulating the greedy day-by-day loading described above with capacity `C` uses `D` days or fewer.

## Constraints
- `1 <= weights.length <= 5 * 10^4`
- `1 <= weights[i] <= 500`
- `1 <= D <= weights.length` (D can never exceed the number of packages, since a day must ship at least one package)
- The answer's lower bound is `max(weights)` (capacity must be able to carry the single heaviest package by itself) and its upper bound is `sum(weights)` (ship everything on day one); with `n` up to `5*10^4`, an `O(n log(sum(weights)))` binary search (about `5*10^4 * 25`) is comfortably fast, while trying every capacity value one at a time from `max` to `sum` is not.

## Worked examples
1. `weights = [1,2,3,4,5,6,7,8,9,10]`, `D = 5` -> `15`. At capacity 15 the days split as `[1,2,3,4,5],[6,7],[8],[9],[10]` (5 days); capacity 14 needs 6 days, so 15 is the minimum.
2. `weights = [3,2,2,4,1,4]`, `D = 3` -> `6`. At capacity 6 the days split as `[3,2],[2,4],[1,4]`; nothing smaller than 6 can even hold the `4` packages alongside a neighbor without overflowing into a 4th day.
3. `weights = [1,2,3,1,1]`, `D = 4` -> `3`. At capacity 3 the days split as `[1,2],[3],[1,1]` (3 days, within budget); capacity 2 forces `[1],[2],[3]... wait 3 alone already needs its own day` and pushes the day count past 4.

## Edge cases checklist
- `D == 1`: the entire array ships in one day, so the answer must be `sum(weights)`.
- `D == weights.length`: each package gets its own day, so the answer must be `max(weights)` -- the lower bound itself is achievable and optimal.
- A single package (`weights.length == 1`).
- All packages the same weight.
- The heaviest package sitting anywhere in the array (start, middle, end) -- it still forces `capacity >= max(weights)` regardless of position.
- Large arrays (`up to 5*10^4` items) with `sum(weights)` up to `2.5*10^7`, which still fits in `int` but should be computed carefully (a running `long` avoids ever worrying about it).

## Approach
### Brute force
Try every possible capacity `C = max(weights), max(weights)+1, ..., sum(weights)`, and for each simulate the greedy day-by-day loading in `O(n)`. This is `O(n * (sum(weights) - max(weights)))`, which with `n = 5*10^4` and a range of up to `~2.5*10^7` is on the order of `10^12` operations -- far too slow.

### Optimal
Binary search directly on the answer capacity `C`, over the range `[max(weights), sum(weights)]`.

**Key invariant:** the function `days(C)` (number of days the greedy loader needs at capacity `C`) is monotonically non-increasing as `C` increases, so the set of capacities satisfying `days(C) <= D` is a contiguous suffix of `[max(weights), sum(weights)]`, and binary search finds its left boundary.

Proof sketch: raising the capacity from `C` to `C+1` only ever lets the greedy loader fit the same or more weight into each day (it never has to stop earlier), so the number of days it needs can only stay the same or decrease. Because `days(C)` is `false` for small `C` (too many days) and `true` for large `C`, the same "find first true" binary search used for Koko Eating Bananas applies, moving `hi = mid` when `days(mid) <= D` and `lo = mid + 1` otherwise.

### Step-by-step trace
Trace on `weights = [3,2,2,4,1,4]`, `D = 3` (`lo = max = 4`, `hi = sum = 16`):

| step | lo | hi | mid | days(mid) simulation | days <= 3? | action |
|---|---|---|---|---|---|---|
| 1 | 4 | 16 | 10 | [3,2,2],[4,1,4] -> wait, 3+2+2=7<=10, +4=11>10 new day; day2: 4+1+4=9<=10 -> 2 days | yes | hi = 10 |
| 2 | 4 | 10 | 7 | 3+2=5,+2=7<=7,+4=11>7 day2; day2:4+1=5,+4=9>7 day3; day3:4 -> 3 days | yes | hi = 7 |
| 3 | 4 | 7 | 5 | 3+2=5,+2=7>5 day2; day2:2+4=6>5... let's use capacity 5: day1:3+2=5(stop,next2 would make7>5) actually 3, then +2=5 fits, +2 next would be 7>5 so day2 starts at that 2; day2: 2+4=6>5 so day2 is just [2], day3 starts at 4: 4+1=5<=5, +4=9>5 day4 starts: [4] -> 4 days total | no | lo = 6 |
| 4 | 6 | 7 | 6 | day1: 3+2=5,+2=7>6 stop -> [3,2]; day2: 2+4=6<=6,+1=7>6 stop -> [2,4]; day3: 1+4=5<=6 -> [1,4]; 3 days | yes | hi = 6 |
| -- | 6 | 6 | -- | loop ends (lo == hi) | | return 6 |

## Java 8 solution
```java
public static int solve(int[] weights, int d) {
    int lo = 0;
    long sum = 0;
    for (int w : weights) {
        lo = Math.max(lo, w); // capacity must be able to carry the single heaviest package
        sum += w;
    }
    int hi = (int) sum; // sum(weights) <= 5e4 * 500 = 2.5e7, safely fits in int, but summed as long first
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (daysNeeded(weights, mid) <= d) {
            hi = mid; // mid works: the answer is mid or something smaller
        } else {
            lo = mid + 1; // mid is too small a capacity: too many days needed
        }
    }
    return lo;
}

private static int daysNeeded(int[] weights, int capacity) {
    int days = 1;
    long current = 0; // long defensively, though weights[i] <= 500 keeps this small in practice
    for (int w : weights) {
        if (current + w > capacity) { // this package would overflow today's load
            days++;
            current = 0;
        }
        current += w;
    }
    return days;
}
```

## Complexity
- Time: `O(n log(sum(weights)))` -- the binary search runs `O(log(sum(weights)))` iterations, and each iteration's `daysNeeded` scan is `O(n)`.
- Space: `O(1)` beyond the input array -- only a running load and a day counter are kept.

## Java 8 pitfalls for this problem
- `lo` must start at `max(weights)`, not `0` or `1` -- a capacity smaller than the heaviest single package can never ship it, and a naive lower bound of `1` wastes many binary search iterations probing infeasible capacities (it still terminates correctly, but it is a sign the invariant was not fully understood).
- `current + w > capacity` must be checked before adding `w`, not after; adding first and checking afterward could let a single day's load exceed capacity by the time it is detected.
- Even though `weights[i] <= 500` and `n <= 5*10^4` keep `sum(weights) <= 2.5*10^7` well within `int` range, accumulating into a `long` first and casting once is a defensive habit that costs nothing and prevents surprises if the bounds are copy-pasted into a harder variant with larger weights.
- `new int[][]{{...}}` style two-dimensional literals are unnecessary here since weights is a flat `int[]`, but this problem is frequently paired with 2-D variants (e.g. shipments as `[weight, destination]` pairs) where that literal syntax and `Arrays.sort` with an explicit `Comparator<int[]>` become relevant -- see Merge Intervals in this same folder for that pattern.
- Prefer `ArrayDeque`-free code here; this problem does not need a queue, and reaching for one out of habit (because it "feels like" a scheduling problem) adds needless complexity.

## Wrong approaches and why they fail
- **Greedily pack as many packages as possible per day without checking against a target day count `D`:** this describes the *simulation* correctly but does not, by itself, find the minimum `C`; without binary search over `C`, there is no way to know which capacity to simulate, so this is a predicate, not a full algorithm.
- **Binary search with `lo = 0`:** technically still converges to the right answer since `daysNeeded` at `capacity < max(weights)` never lets that heaviest package load at all (an infinite-day scenario in a correct simulation, or a crash in a careless one that forgets to check `current == 0` before forcing a package through) -- for `weights=[7,2,5,10,8]` with `capacity=5`, the package of weight `7` alone already exceeds capacity, and a buggy simulation that does not special-case "current day is already empty but this package alone exceeds capacity" will either loop forever or under-count days.
- **Sorting the weights before simulating to "balance" the days:** the problem is explicit that packages load in the given array order; sorting changes which packages end up together on a day and produces a capacity for a different (invalid) problem.

## Variants
- **LC 875 Koko Eating Bananas** is the sibling binary-search-on-the-answer problem in this folder; the predicate there sums independent per-item ceiling divisions instead of a greedy running-load simulation.
- **Return the actual day-by-day split, not just the minimum capacity:** after finding `C` with binary search, re-run the greedy `daysNeeded` simulation once more at that `C`, this time recording the package indices where each new day starts.
- **Minimize the number of days given a fixed capacity (the inverse question):** this is no longer a search problem at all -- just run the greedy `daysNeeded` simulation once with the given capacity and return the day count directly.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | weights=[1,2,3,4,5,6,7,8,9,10], D=5 | 15 | standard case from the walkthrough |
| 2 | weights=[3,2,2,4,1,4], D=3 | 6 | mixed weights, exact trace case |
| 3 | weights=[1,2,3,1,1], D=4 | 3 | small array, tight day budget |
| 4 | weights=[1,2,3,1,1], D=1 | 8 | D == 1 forces capacity = sum(weights) |
| 5 | weights=[7,2,5,10,8], D=5 | 10 | D == n forces capacity = max(weights) |
| 6 | weights=[1,1,1,1,1], D=5 | 1 | D == n, uniform tiny weights |
| 7 | weights=[10], D=1 | 10 | single package |
| 8 | weights=[5,5,5,5], D=2 | 10 | uniform weights split across exactly 2 days |
| 9 | weights=[1,2,3,4,5,6,7,8,9,10], D=10 | 10 | D == n forces capacity = max(weights) |
| 10 | weights=[100,200,300], D=2 | 300 | heaviest package dominates the answer even with only 3 items |
