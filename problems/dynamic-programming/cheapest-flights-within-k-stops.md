# Cheapest Flights Within K Stops
`ref: LC 787` · Difficulty: Medium · Pattern: DP over the number of edges used (Bellman-Ford limited to k+1 rounds) with a copy of the previous round

## Problem

There are `n` cities numbered `0` to `n - 1`. Each flight is a triple `[from, to, price]`: a one-way flight from city `from` to city `to` that costs `price`. You are given a start city `src`, a destination city `dst` and an integer `k`. Return the lowest total price of a trip from `src` to `dst` that makes at most `k` stops, or `-1` if no such trip exists. A stop is a city strictly between the start and the destination, so a trip with `s` stops uses `s + 1` flights.

Input: the integer `n`, the array `flights`, and the integers `src`, `dst` and `k`.
Output: a single integer, the cheapest price of a trip with at most `k` stops, or `-1`.

## Constraints

- `1 <= n <= 100`
- `0 <= flights.length <= n * (n - 1) / 2`
- `flights[i].length == 3`
- `0 <= from, to < n` and `from != to`
- `1 <= price <= 10^4`
- There are no multiple flights between two cities.
- `0 <= src, dst, k < n` and `src != dst`.
- A plain search over every trip with at most `k + 1` flights can try up to `(n - 1)^(k + 1)` routes, which is far too slow. Relaxing every flight once per round, for `k + 1` rounds, is at most `(k + 1) * F` steps, where `F` is the number of flights (at most `4950` here), so about `495000` steps in the worst case.

## Worked examples

1. `n = 4`, `flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]]`, `src = 0`, `dst = 3`, `k = 1` -> `700`. With at most one stop the best trip is `0 -> 1 -> 3` for `100 + 600 = 700`. The route `0 -> 1 -> 2 -> 3` costs only `400`, but it makes two stops.
2. `n = 3`, `flights = [[0,1,100],[1,2,100],[0,2,500]]`, `src = 0`, `dst = 2`, `k = 1` -> `200`. The trip `0 -> 1 -> 2` has one stop and costs `200`, which beats the direct flight at `500`.
3. The same flights and cities with `k = 0` -> `500`. No stops are allowed, so only the direct flight counts, even though it is the expensive one.
4. `n = 3`, `flights = [[0,1,100],[1,2,100]]`, `src = 0`, `dst = 2`, `k = 0` -> `-1`. The only route needs one stop, and none are allowed.

## Edge cases checklist

- No flights at all (`flights = []`): the answer is `-1`.
- `k = 0`: only a direct flight counts. A cheaper trip with a stop must be ignored (example 3).
- A direct flight that exists but is expensive, next to a cheaper trip with stops: the answer depends on `k` (examples 2 and 3).
- A destination that cannot be reached at all, including a flight that points the wrong way (test 6): `-1`.
- A destination that can be reached, but only with more stops than allowed (test 7): `-1`.
- The cheapest route uses too many stops, so a pricier route with fewer stops is the answer (tests 8 and 9). Raising `k` by one can lower the answer (tests 9 and 10).
- Cycles in the flight graph. Prices are positive, so a cycle never makes a trip cheaper, and the round limit ends any looping.
- Several trips with the same number of flights but different prices: keep the smallest.
- Stops versus flights: `k` stops means `k + 1` flights. This off-by-one is the most common mistake.
- The largest `k` (`k = n - 1`) lets every simple route through, so the answer becomes the ordinary cheapest path.
- The upper bound, a chain of 100 cities that needs all 99 flights (tests 12 and 13).

## Approach

### Brute force

Search every trip. Start at `src` with price `0` and `k + 1` flights left. From the current city try every flight that leaves it, add its price, and go on with one flight fewer. Every time the search stands on `dst`, record the price if it is the lowest so far. The number of trips grows like (flights leaving a city) to the power `k + 1`, up to `(n - 1)^(k + 1)`. A shortest-path algorithm that looks only at the price is cheaper, but it answers a different question, because the cheapest trip may use too many flights (see Wrong approaches).

### Optimal

Make the number of flights used part of the state. Let `dp[r][v]` be the cheapest price to reach city `v` using at most `r` flights, for `r` from `0` to `k + 1`:

- `dp[0][src] = 0`, and `dp[0][v] = infinity` for every other city: with no flights you can only be at the start.

For `r >= 1`, start from the previous round and then try every flight `(from, to, price)`:

- `dp[r][v] = dp[r - 1][v]` to begin with: a city reachable with `r - 1` flights is still reachable with `r`.
- Then `dp[r][to] = min(dp[r][to], dp[r - 1][from] + price)` for every flight, skipping flights whose `from` city is still infinity in round `r - 1`.

The answer is `dp[k + 1][dst]`, or `-1` if it is infinity. Round `r` reads only round `r - 1`, so only two arrays are needed: `prev` for row `r - 1` and `cur` for row `r`. At the end of each round, `cur` becomes the new `prev`.

This is Bellman-Ford with the number of rounds capped at `k + 1`. The copy is what makes the cap mean "at most `k + 1` flights". If a flight updated the same array that later flights read, one round could chain several flights together (the first flight lowers a price, and the second flight in the same round builds on it), and a trip would get more flights than the round count allows. Reading only from `prev` and writing only to `cur` adds at most one flight to a trip per round, whatever the order of the flights in the input.

**Key invariant:** after round `r`, `cur[v]` is the exact cheapest price over all trips from `src` to `v` that use at most `r` flights. A trip with at most `r` flights either has at most `r - 1` flights (its price is already in the copy of `prev`), or its last flight is some `(from, to, price)` and the part before it is a trip to `from` with at most `r - 1` flights. The cheapest such trip to `from` is `prev[from]`, and the price of the last flight does not depend on how the earlier part was flown, so using the cheapest earlier part is never worse. By induction the invariant holds for `r = k + 1`.

### Step-by-step trace

Worked example 2: `n = 3`, `flights = [[0,1,100],[1,2,100],[0,2,500]]`, `src = 0`, `dst = 2`, `k = 1`. There are `k + 1 = 2` rounds. `INF` means unreachable.

| round | city 0 | city 1 | city 2 | what happened |
|---|---|---|---|---|
| 0 | 0 | INF | INF | only the start city is reachable |
| 1 | 0 | 100 | 500 | flight `0->1` gives `0 + 100`, flight `0->2` gives `0 + 500`, and flight `1->2` is skipped because city 1 was `INF` in round 0 |
| 2 | 0 | 100 | 200 | flight `1->2` reads `prev[1] = 100` and gives `100 + 100 = 200`, which beats `500` |

The answer is `dp[2][2] = 200`.

With `k = 0` there is only round 1, and the answer is `dp[1][2] = 500`, which is worked example 3. This shows why the copy matters: in round 1 the flight `1->2` must not see the new price `100` for city 1, which was written in the same round. If it did (updating one array in place, with the flights in the order given), round 1 would give city 2 the price `200`, and the answer for `k = 0` would be a trip that uses two flights.

Walking back from city 2 in round 2: the value `200` came from city 1 in round 1 (value `100`), which came from city 0 in round 0. The trip is `0 -> 1 -> 2`.

## Java 8 solution
```java
import java.util.*;

public class CheapestFlightsWithinKStops {

    // Cheapest total price to fly from src to dst using at most k stops, or -1
    // when no such trip exists. A stop is a city strictly between src and dst,
    // so a trip with at most k stops uses at most k + 1 flights.
    //
    // Let dp[r][v] be the cheapest price to reach city v using at most r
    // flights. Round r is built from round r - 1 only, so after k + 1 rounds
    // the table holds the best price for every city under the flight limit.
    // Only the previous round is ever read, so two arrays are enough: prev is
    // row r - 1 and cur is row r.
    public static int solve(int n, int[][] flights, int src, int dst, int k) {
        final int INF = Integer.MAX_VALUE;

        // Row 0: with zero flights only the start city is reachable, for free.
        int[] prev = new int[n];
        Arrays.fill(prev, INF);
        prev[src] = 0;

        for (int round = 1; round <= k + 1; round++) {
            // Start from a copy of the previous round: a city that was
            // reachable with r - 1 flights is still reachable with r.
            int[] cur = Arrays.copyOf(prev, n);
            for (int[] flight : flights) {
                int from = flight[0];
                int to = flight[1];
                int price = flight[2];
                // Read only prev, never cur, so each flight is used at most
                // once per round. Skip unreachable cities so INF + price can
                // never overflow.
                if (prev[from] == INF) {
                    continue;
                }
                cur[to] = Math.min(cur[to], prev[from] + price);
            }
            prev = cur;
        }
        return prev[dst] == INF ? -1 : prev[dst];
    }

    // A straight line of cities 0 -> 1 -> ... -> n - 1, every flight costs 1.
    private static int[][] chain(int n) {
        int[][] flights = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            flights[i] = new int[]{i, i + 1, 1};
        }
        return flights;
    }

    private static void check(int caseNum, int n, int[][] flights, int src, int dst, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(n, flights, src, dst, k);
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

        check(1, 4, new int[][]{{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}}, 0, 3, 1, 700, fail, total);
        check(2, 3, new int[][]{{0, 1, 100}, {1, 2, 100}, {0, 2, 500}}, 0, 2, 1, 200, fail, total);
        check(3, 3, new int[][]{{0, 1, 100}, {1, 2, 100}, {0, 2, 500}}, 0, 2, 0, 500, fail, total);
        check(4, 2, new int[][]{{0, 1, 5}}, 0, 1, 0, 5, fail, total);
        check(5, 2, new int[][]{}, 0, 1, 0, -1, fail, total);
        check(6, 3, new int[][]{{1, 0, 5}}, 0, 1, 1, -1, fail, total);
        check(7, 3, new int[][]{{0, 1, 100}, {1, 2, 100}}, 0, 2, 0, -1, fail, total);
        check(8, 4, new int[][]{{0, 1, 1}, {1, 2, 1}, {0, 2, 5}, {2, 3, 1}}, 0, 3, 1, 6, fail, total);
        check(9, 5, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 4, 1}, {0, 4, 10}}, 0, 4, 2, 10, fail, total);
        check(10, 5, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 4, 1}, {0, 4, 10}}, 0, 4, 3, 4, fail, total);
        check(11, 4, new int[][]{{0, 1, 1}, {1, 2, 1}, {2, 0, 1}, {2, 3, 1}}, 0, 3, 3, 3, fail, total);
        check(12, 100, chain(100), 0, 99, 98, 99, fail, total);
        check(13, 100, chain(100), 0, 99, 97, -1, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O((k + 1) * (F + n)), where `F = flights.length`: each of the `k + 1` rounds copies an array of `n` entries and looks at every flight once. With `F` at most `4950` and `k + 1` at most `100`, that is about `500000` steps. Space O(n) for the two arrays `prev` and `cur`. Keeping every round, to rebuild a route, would cost O(k * n) (see Variants).

## Java 8 pitfalls for this problem

- Stops versus flights. `k` stops are `k + 1` flights, so the loop runs `k + 1` rounds. With only `k` rounds, worked example 3 (`k = 0`) runs no rounds and returns `-1` instead of `500`, and worked example 2 (`k = 1`) returns `500` instead of `200`.
- Updating one array in place. Without the copy, a round can use several flights in a row. Worked example 3 returns `200` instead of `500`. Copy the previous round with `Arrays.copyOf(prev, n)` (or `clone()`), read from `prev`, and write to `cur`.
- Integer overflow on the "unreachable" value. If it is `Integer.MAX_VALUE` and you add a price to it, the sum wraps around to a large negative number that wins every `Math.min`. For example `2147483647 + 100` is `-2147483549`. Skip flights whose source is unreachable, as the solution does, or use `Integer.MAX_VALUE / 2`. Real prices are at most `99 * 10000 = 990000`, so they never overflow.
- Mutable default values. A new `int[n]` is all zeros, and a zero reads as "reachable for free". Fill the array with the unreachable value first and set only `prev[src] = 0`. Writing `cur = prev` makes both names point to one array, which is the in-place bug again, so make a real copy.
- Array sizing. The arrays have `n` entries, one per city, and the cities are numbered `0` to `n - 1`, so there is no `+ 1`. An empty `flights` array is legal and the round loop simply changes nothing, so do not read `flights[0]` to size anything.
- Do not change the input. The solution only reads `flights`, so the caller's array stays intact (sorting it in place, for example, would change the caller's data).
- Recursion depth. A recursive search over trips goes at most `k + 1 <= 100` deep, which is safe in Java. The round loop has no recursion at all.
- A priority-queue comparator written as `a[0] - b[0]` can overflow for large prices. If you use the Dijkstra variant, write `Integer.compare(a[0], b[0])`.

## Wrong approaches and why they fail

1. **Dijkstra on the price alone, ignoring `k`.** It finds the cheapest trip with any number of flights. Counterexample: `n = 3`, `flights = [[0,1,100],[1,2,100],[0,2,500]]`, `src = 0`, `dst = 2`, `k = 0`. It answers `200` (two flights), and the correct answer is `500`. On the chain `[[0,1,1],[1,2,1],[2,3,1],[3,4,1],[0,4,10]]` with `src = 0`, `dst = 4` and `k = 2`, it answers `4`, and the correct answer is `10`, because the cheap chain needs four flights and only three are allowed.
2. **Bellman-Ford with `k + 1` rounds, updating a single array in place.** A round can chain flights, so the round count no longer limits the number of flights. Counterexample: the same three flights with `k = 0` and the flights in the order given. The first flight sets city 1 to `100`, the second flight builds on it and sets city 2 to `200`, and the method answers `200`. The correct answer is `500`. On the five-flight chain above with `k = 2` it answers `4`, and the correct answer is `10`.
3. **Run `k` rounds instead of `k + 1`.** A trip with `k` stops has `k + 1` flights. Counterexample: the three flights above with `k = 0` run no rounds and answer `-1`, and the correct answer is `500`. With `k = 1` the method answers `500` (one round), and the correct answer is `200`.
4. **Dijkstra that finishes each city the first time it comes off the queue, and stops expanding at `k + 1` flights.** The first time a city comes off the queue it has the lowest price, but not necessarily the fewest flights, and a later arrival with more money but fewer flights is thrown away. Counterexample: `n = 4`, `flights = [[0,1,1],[1,2,1],[0,2,5],[2,3,1]]`, `src = 0`, `dst = 3`, `k = 1`. City 2 comes off the queue first with price `2` after two flights. It cannot be expanded (a third flight is not allowed), and it is marked finished, so the arrival `0 -> 2` for `5` is discarded. The method answers `-1`. The correct answer is `6` (`0 -> 2 -> 3`).

## Variants

1. **Dijkstra over the state `(city, flights used)`.** A priority queue orders states by price. A state is finished once, and a city can be finished again with a different number of flights. Drop any state that has used more than `k + 1` flights. This is the same answer as the rounds, and it can be faster when the graph is sparse.
2. **Level-by-level search.** Run a queue for `k + 1` levels and keep a best price per city. Drop a candidate that does not beat the best price already recorded for its city, because an earlier level reached that city with fewer flights at a price that is no higher.
3. **Exactly `k` stops instead of at most.** Do not start a round from a copy of the previous one. Start `cur` as all unreachable, so `dp[r][v]` means "using exactly `r` flights".
4. **Recover the route.** Keep a `parent` array for every round, `parent[r][v] = from`, and walk back from round `k + 1`. This needs O(k * n) space.
5. **Stop early.** If a round changes nothing, all later rounds change nothing too, so the loop can stop.
6. **Negative prices.** The round-limited version still works with negative prices, because it always stops after `k + 1` rounds, while Dijkstra's finish-once rule breaks on them. Only a cycle of negative total price is a problem when the number of flights is not limited.
7. **No limit on the number of flights.** The same graph with a single source and no stop limit is Network Delay Time (LC 743). See `../graphs/network-delay-time.md`.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `n=4, flights=[[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]], src=0, dst=3, k=1` | `700` | the cheaper route `0,1,2,3` (400) has too many stops, and the graph has a cycle |
| 2 | `n=3, flights=[[0,1,100],[1,2,100],[0,2,500]], src=0, dst=2, k=1` | `200` | one stop is allowed, so the trip with a stop beats the direct flight |
| 3 | `n=3, flights=[[0,1,100],[1,2,100],[0,2,500]], src=0, dst=2, k=0` | `500` | no stops, direct flight only; an in-place update gives 200 and a `k`-round loop gives -1 |
| 4 | `n=2, flights=[[0,1,5]], src=0, dst=1, k=0` | `5` | smallest case, one direct flight |
| 5 | `n=2, flights=[], src=0, dst=1, k=0` | `-1` | no flights at all |
| 6 | `n=3, flights=[[1,0,5]], src=0, dst=1, k=1` | `-1` | flights are one-way, the only flight points the wrong way |
| 7 | `n=3, flights=[[0,1,100],[1,2,100]], src=0, dst=2, k=0` | `-1` | the only route needs one stop and none are allowed |
| 8 | `n=4, flights=[[0,1,1],[1,2,1],[0,2,5],[2,3,1]], src=0, dst=3, k=1` | `6` | cheapest way to city 2 uses too many flights; a finish-each-city-once Dijkstra gives -1 |
| 9 | `n=5, flights=[[0,1,1],[1,2,1],[2,3,1],[3,4,1],[0,4,10]], src=0, dst=4, k=2` | `10` | the cheap chain needs 4 flights, only 3 are allowed; an in-place update or a price-only Dijkstra gives 4 |
| 10 | `n=5, flights=[[0,1,1],[1,2,1],[2,3,1],[3,4,1],[0,4,10]], src=0, dst=4, k=3` | `4` | the same graph with one more stop allowed; the chain now fits exactly (3 stops, 4 flights) |
| 11 | `n=4, flights=[[0,1,1],[1,2,1],[2,0,1],[2,3,1]], src=0, dst=3, k=3` | `3` | a cycle `0,1,2` before the exit; looping never helps and the rounds end |
| 12 | `n=100, flights=` a chain `i -> i+1` with price 1 for `i` from 0 to 98, `src=0, dst=99, k=98` | `99` | upper size, all 99 flights needed, which is exactly 98 stops |
| 13 | `n=100, flights=` the same chain, `src=0, dst=99, k=97` | `-1` | upper size, one stop too few |
