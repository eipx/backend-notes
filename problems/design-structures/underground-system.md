# Underground System
`ref: LC 1396` · Difficulty: Medium · Pattern: two hash maps (in-flight trips + running route totals)

## Problem

Design a system that tracks riders traveling between stations and can report the average travel time recorded for any ordered pair of stations.

The class exposes:
- `void checkIn(int id, String stationName, int t)` — a rider identified by `id` starts a trip at `stationName` at time `t`. `id` is guaranteed to not currently have an in-progress trip when this is called.
- `void checkOut(int id, String stationName, int t)` — the rider `id` (who must have an unfinished trip in progress) ends that trip at `stationName` at time `t`. This completes one trip record of `(start station, end station, t - checkInTime)`.
- `double getAverageTime(String startStation, String endStation)` — return the mean travel time, averaged over every completed trip ever recorded from `startStation` to `endStation` exactly in that direction. This method is guaranteed to be called only after at least one such trip has completed.

## Constraints

- `1 <= id, t <= 10^6`; up to `2 * 10^4` calls to `checkIn`, and the same bound applies across `checkOut` and `getAverageTime` — a linear scan over all past trips on every `getAverageTime` call would be too slow if repeated many times.
- Station names are non-empty strings of letters and spaces.
- `t` is guaranteed to strictly increase across consecutive calls for a given rider's own trip (check-out time is always after that rider's own check-in time) — but there is no guarantee about ordering *between different riders*, so multiple riders can be mid-trip simultaneously.
- `checkOut` is only ever called for an `id` with a genuinely in-progress trip, and `getAverageTime` only for a route with at least one completed trip — the implementation does not need to defensively handle a check-out with no matching check-in, but should be aware of what breaks if that guarantee is violated (see Edge cases).

## Worked examples

**Example 1 — two riders on the same route, averaged together**
```
checkIn(45,"Leyton",3) checkIn(32,"Paradise",8) checkIn(27,"Leyton",10)
checkOut(45,"Waterloo",15)   // Leyton->Waterloo trip time 12
checkOut(27,"Waterloo",20)   // Leyton->Waterloo trip time 10
getAverageTime("Leyton","Waterloo") -> 11.0
checkOut(32,"Cambridge",22)  // Paradise->Cambridge trip time 14
getAverageTime("Paradise","Cambridge") -> 14.0
checkIn(10,"Leyton",24) checkOut(10,"Waterloo",38)  // third Leyton->Waterloo trip, time 14
getAverageTime("Leyton","Waterloo") -> 12.0
```
Rider `45` and rider `27` are both mid-trip on `Leyton->Waterloo` at the same time (rider `45` checked in at `t=3`, rider `27` at `t=10`, and both are still traveling when `checkIn(27,...)` happens) — the system tracks each rider's own trip independently by `id`, so overlapping trips on the same route never get confused with each other. The average `11.0` comes from `(12+10)/2`; after the third `Leyton->Waterloo` trip (time 14) completes, the average becomes `(12+10+14)/3 = 12.0`.

**Example 2 — direction matters**
```
checkIn(1,"A",0) checkOut(1,"B",10)   // A->B = 10
checkIn(2,"B",0) checkOut(2,"A",3)    // B->A = 3
getAverageTime("A","B") -> 10.0
getAverageTime("B","A") -> 3.0
```
`A->B` and `B->A` are tracked as two completely separate routes even though they involve the same two station names — a trip in one direction never contributes to the other direction's average.

**Example 3 — concurrent in-flight riders on the same route**
```
checkIn(1,"S",0) checkIn(2,"S",1)
checkOut(2,"T",4)   // rider 2: S->T = 3
checkOut(1,"T",5)   // rider 1: S->T = 5
getAverageTime("S","T") -> 4.0
```
Rider `2` checks in *after* rider `1` but checks out *before* rider `1` — the `id`-keyed in-progress map means each rider's own start time is recovered correctly regardless of the interleaving order of check-ins and check-outs across different riders.

## Edge cases checklist

- Two or more riders traveling the same route concurrently (overlapping check-in/check-out windows) — must not mix up their individual start times.
- The same two station names in reversed order (`A->B` vs `B->A`) must be tracked as distinct routes.
- A single completed trip for a route — the "average" of one value is just that value.
- Requesting the average immediately after the route's very first trip completes.
- A rider whose check-in and check-out station names are identical (a trip that starts and ends at the same named station) — this is a legal route key like `"X->X"` and should average normally.
- (Guarantee-violation awareness, not required to handle) `checkOut` called for an `id` with no matching in-progress check-in would throw when the code dereferences the missing entry — worth knowing why, even though the problem guarantees this never happens.
- Many riders checking in and out interleaved across many different routes, to confirm route stats never bleed into each other.

## Approach

### Naive

Store every completed trip as a plain list of `(start, end, duration)` triples. `getAverageTime` scans the entire list on every call, filtering for the matching route and summing — O(total completed trips) per call. With up to `2 * 10^4` calls to `getAverageTime`, each potentially scanning up to `2*10^4` trips, this is O(n^2) in the worst case, which is unnecessary work when a running total is trivial to maintain incrementally.

### Optimal

**Key invariant:** every rider has at most one in-progress trip at a time, so a map keyed by `id` unambiguously recovers that rider's own start station and start time at check-out; and every route accumulates a running `(totalTime, count)` pair that is updated once per completed trip, so the average is always an O(1) division away.

- `checkIn`: record `id -> (stationName, t)` in an in-progress map. O(1).
- `checkOut`: look up and remove `id`'s in-progress entry (O(1)), compute the trip duration, then add it into that route's running total and increment its count (O(1) map access plus O(1) arithmetic).
- `getAverageTime`: look up the route's `(totalTime, count)` and divide (O(1)) — no per-call scanning of individual trips is ever needed because the running sum already reflects every trip recorded so far.

The route is identified by combining `startStation` and `endStation` into one map key. The simplest option is a delimited string like `"A->B"`; the values are then wrapped in a small mutable holder class (`RouteStats { long totalTime; int count; }`) so the running total can be updated in place without re-inserting into the map on every call.

### Step-by-step trace

Trace of Example 1:

| Step | Operation | Return | inProgress | routeStats |
|---|---|---|---|---|
| 1 | `checkIn(45,"Leyton",3)` | — | `{45:(Leyton,3)}` | `{}` |
| 2 | `checkIn(32,"Paradise",8)` | — | `{45:(Leyton,3), 32:(Paradise,8)}` | `{}` |
| 3 | `checkIn(27,"Leyton",10)` | — | `+27:(Leyton,10)` | `{}` |
| 4 | `checkOut(45,"Waterloo",15)` | — | `-45` | `{"Leyton->Waterloo":(12,1)}` |
| 5 | `checkOut(27,"Waterloo",20)` | — | `-27` | `{"Leyton->Waterloo":(22,2)}` |
| 6 | `getAverageTime("Leyton","Waterloo")` | `11.0` | unchanged | `22/2 = 11.0` |
| 7 | `checkOut(32,"Cambridge",22)` | — | `-32` (now empty) | `+{"Paradise->Cambridge":(14,1)}` |
| 8 | `getAverageTime("Paradise","Cambridge")` | `14.0` | unchanged | `14/1 = 14.0` |
| 9 | `checkIn(10,"Leyton",24)` | — | `{10:(Leyton,24)}` | unchanged |
| 10 | `checkOut(10,"Waterloo",38)` | — | `{}` | `{"Leyton->Waterloo":(36,3)}` |
| 11 | `getAverageTime("Leyton","Waterloo")` | `12.0` | unchanged | `36/3 = 12.0` |

## Java 8 solution

```java
import java.util.HashMap;

/**
 * LC 1396 - tracks riders checking in and out of stations and reports the
 * average travel time between any two stations.
 * checkIn(id, stationName, t): a rider with this id starts a trip at t.
 * checkOut(id, stationName, t): the same rider ends the trip at t.
 * getAverageTime(startStation, endStation): mean travel time over every
 * completed trip recorded for that exact ordered pair of stations.
 */
public class UndergroundSystem {

    static class CheckInInfo {
        final String station;
        final int time;
        CheckInInfo(String station, int time) { this.station = station; this.time = time; }
    }

    static class RouteStats {
        long totalTime = 0;
        int count = 0;
    }

    static class UndergroundSystemImpl {
        // id -> the in-progress trip's start station and start time
        private final HashMap<Integer, CheckInInfo> inProgress;
        // "startStation->endStation" -> running total time and trip count for that route
        private final HashMap<String, RouteStats> routeStats;

        UndergroundSystemImpl() {
            inProgress = new HashMap<Integer, CheckInInfo>();
            routeStats = new HashMap<String, RouteStats>();
        }

        void checkIn(int id, String stationName, int t) {
            inProgress.put(id, new CheckInInfo(stationName, t));
        }

        void checkOut(int id, String stationName, int t) {
            CheckInInfo start = inProgress.remove(id);
            // A "->" string key is simple and fast, but relies on station names never
            // containing the delimiter; see .md for the Pair/nested-map alternative.
            String routeKey = start.station + "->" + stationName;
            RouteStats stats = routeStats.get(routeKey);
            if (stats == null) {
                stats = new RouteStats();
                routeStats.put(routeKey, stats);
            }
            stats.totalTime += (t - start.time);
            stats.count++;
        }

        double getAverageTime(String startStation, String endStation) {
            RouteStats stats = routeStats.get(startStation + "->" + endStation);
            return (double) stats.totalTime / stats.count;
        }
    }
}
```

## Complexity

- `checkIn`: O(1) — one map insert.
- `checkOut`: O(1) — one map removal, one map lookup/insert on the route, constant arithmetic.
- `getAverageTime`: O(1) — one map lookup, one division.
- Space: O(riders currently in-flight + number of distinct routes ever seen) — `inProgress` only ever holds trips that have not yet checked out, and `routeStats` holds one small fixed-size record per distinct route, not per trip.

## Java 8 pitfalls for this problem

- The route key as a string `"A->B"` vs a pair object: concatenating with a delimiter is simple and gives a correct `hashCode`/`equals` for free (via `String`'s own implementation), but it is only safe if station names are guaranteed to never contain the delimiter substring itself (e.g. a station literally named `"A->B"` could collide with the pair `("A","B")` if some other station were named to produce the same concatenation) — for this problem's constraints (letters and spaces only) `"->"` is a safe delimiter. The alternative is a small immutable `RoutePair` class with `equals`/`hashCode` overridden (or `Arrays.asList(start, end)`, which has a correct value-based `equals`/`hashCode` out of the box), or a nested `HashMap<String, HashMap<String, RouteStats>>` — either avoids delimiter collision risk entirely at the cost of a slightly heavier key.
- `stats.totalTime` is declared `long`, not `int`: with up to `2*10^4` trips each up to roughly `10^6` in duration, a running sum could approach `2*10^10`, which overflows a 32-bit `int`.
- `inProgress.remove(id)` both fetches and deletes the entry in one call — using `.get(id)` followed by a separate `.remove(id)` is not wrong but is two hash lookups instead of one; more importantly, forgetting the `.remove(...)` step entirely would leak completed trips in the in-progress map forever.
- `RouteStats` is a small mutable holder class updated in place; this only works correctly because it is stored as an object reference in the map (mutating its fields is visible without re-inserting) — if `RouteStats` were an immutable value type, every `checkOut` would need `routeStats.put(key, new RouteStats(oldTotal + delta, oldCount + 1))` instead.
- No `var`, no records for `CheckInInfo`/`RouteStats` — plain classes with explicit fields and constructors are required in Java 8.
- `(double) stats.totalTime / stats.count` — the cast must be applied before the division (or to the numerator) to force floating-point division; `stats.totalTime / stats.count` alone would perform integer division and truncate the result.

## Wrong approaches and why they fail

- **Storing every individual completed trip in a list and scanning it inside `getAverageTime`.** Sequence: thousands of `checkIn`/`checkOut` pairs across few distinct routes, then repeated `getAverageTime` calls on a hot route — each call re-scans and re-sums every historical trip for that route, doing redundant work that a running `(total, count)` pair avoids entirely.
- **Keying the in-progress map by station name instead of rider `id`.** Sequence: `checkIn(45,"Leyton",3)`, `checkIn(27,"Leyton",10)` (Example 1) — two different riders check in at the *same* station while both are still traveling; keying by station name would let the second check-in overwrite the first rider's start time, and the eventual `checkOut(45, ...)` would incorrectly use rider 27's start time instead of rider 45's.
- **Treating `"A->B"` and `"B->A"` as the same route (e.g. by sorting the two station names before building the key).** Sequence: Example 2, `checkIn(1,"A",0) checkOut(1,"B",10)` then `checkIn(2,"B",0) checkOut(2,"A",3)` — sorting the names into one canonical key would merge these into a single route averaging `(10+3)/2 = 6.5`, when the problem requires `getAverageTime("A","B") = 10.0` and `getAverageTime("B","A") = 3.0` to be reported separately.

## Variants

- **Cancel an in-progress check-in (rider never completes the trip).** Add a `cancelCheckIn(int id)` that simply removes `id` from the in-progress map without touching `routeStats` — no averages are affected since the trip never completed.
- **Median instead of average travel time.** A running `(total, count)` pair cannot answer "median" incrementally; this would require storing the actual per-trip durations per route (e.g. in a sorted structure such as two heaps forming a running-median structure, or a `TreeMap<Integer,Integer>` counting durations) so the median can be recomputed as trips are added.
- **Time-windowed average ("average over the last hour").** Store each trip's completion time alongside its duration in a per-route queue; maintain a running sum but evict (subtract and pop) trips whose completion time falls outside the window whenever a new trip completes or an average is requested, keeping the running sum representative of only the current window.

## Test cases

| # | Operation sequence | Expected outputs | What it tests |
|---|---|---|---|
| 1 | `checkIn(45,Leyton,3) checkIn(32,Paradise,8) checkIn(27,Leyton,10) checkOut(45,Waterloo,15) checkOut(27,Waterloo,20) getAverageTime(Leyton,Waterloo)` | `11.0` | Two overlapping riders averaged on the same route |
| 2 | ...continuing... `checkOut(32,Cambridge,22) getAverageTime(Paradise,Cambridge)` | `14.0` | Single-trip route average |
| 3 | ...continuing... `checkIn(10,Leyton,24) checkOut(10,Waterloo,38) getAverageTime(Leyton,Waterloo)` | `12.0` | Running average updates correctly after a third trip |
| 4 | `checkIn(1,A,0) checkOut(1,B,10) getAverageTime(A,B)` | `10.0` | Basic single trip |
| 5 | ...continuing... `checkIn(2,B,0) checkOut(2,A,3) getAverageTime(B,A)` | `3.0` | Reversed direction is a distinct route from A->B |
| 6 | `checkIn(7,X,100) checkOut(7,Y,105) getAverageTime(X,Y)` | `5.0` | Single trip average equals that one trip exactly |
| 7 | `checkIn(1,S,0) checkIn(2,S,1) checkOut(2,T,4) checkOut(1,T,5) getAverageTime(S,T)` | `4.0` | Concurrent in-flight riders resolved independently by id |

Randomization note: this problem has no randomized methods; all expected values are exact (compared with a small floating-point tolerance for the division).
