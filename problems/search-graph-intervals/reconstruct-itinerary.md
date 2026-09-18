# Reconstruct Itinerary
`ref: LC 332` · Difficulty: Hard · Pattern: Hierholzer's algorithm with a min-heap per node

## Problem
You're given a list of airline tickets, each a pair `[from, to]`, and you must use every single ticket exactly once, starting from `"JFK"`, to build one continuous itinerary (an airport is visited each time you land, and you immediately depart again on the next ticket, until every ticket is used). If more than one valid itinerary uses every ticket, return the one that is lexicographically smallest when compared airport by airport. The input is guaranteed to allow at least one itinerary that uses every ticket.

## Constraints
- `1 <= tickets.length <= 300`
- Each `from`/`to` is a three-letter uppercase airport code.
- The same `[from, to]` pair may appear more than once (each occurrence is a separate, distinct ticket that must be used separately).
- The input always admits at least one itinerary using every ticket exactly once, starting at `"JFK"`.

## Worked examples
1. `tickets = [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]` -> `["JFK","MUC","LHR","SFO","SJC"]`. There's exactly one way to chain all four tickets together starting from `JFK`, so no lexicographic tie-breaking is even needed here.
2. `tickets = [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]` -> `["JFK","ATL","JFK","SFO","ATL","SFO"]`. `JFK` and `ATL` are each visited twice; at every branching point, the lexicographically smaller unused destination is chosen first, and this particular graph happens to allow that greedy choice to succeed all the way through.
3. `tickets = [["JFK","KUL"],["JFK","NRT"],["NRT","JFK"]]` -> `["JFK","NRT","JFK","KUL"]`. `KUL` is a dead end (no outgoing ticket from it), so even though `KUL` is lexicographically smaller than `NRT`, flying there first would strand the two remaining tickets (`JFK->NRT` and `NRT->JFK`) forever -- the correct itinerary must save the dead-end destination for the very last step.

## Edge cases checklist
- Only one ticket total (trivial two-airport itinerary).
- The same `[from, to]` pair listed more than once (duplicate tickets must each be used as separate edges, not collapsed into one).
- An airport with three or more outgoing tickets, requiring a real multi-way lexicographic tie-break, not just a two-way choice.
- A "dead end" airport reachable only by using up a specific ticket last, where naive forward-only greedy choice would strand other tickets (worked example 3).
- A destination airport that itself never appears as a departure airport anywhere in the ticket list (a pure sink, looked up as having no outgoing tickets at all).
- Tickets listed out of alphabetical order in the input (the algorithm must not rely on input order, only on sorting destinations itself).

## Approach

### Brute force
Treat every ticket as a distinct, individually-consumable edge, and try every possible ordering of using them (backtracking: at each airport, try each still-unused ticket departing from it, recursing, and undoing the choice if it doesn't lead to a full itinerary that uses every ticket), stopping at the first complete itinerary found in lexicographic-first order. Without a principled way to know which branch to try first, this can backtrack over up to `O(t!)` orderings of `t` tickets in the worst case before finding (or confirming) a valid full itinerary.

### Optimal
Build an adjacency map from each departure airport to a min-heap of its remaining destinations (so the lexicographically smallest unused destination is always available in `O(log k)`), then run Hierholzer's algorithm: recursively visit the smallest remaining destination from the current airport, and only append the *current* airport to the route once none of its outgoing tickets remain (i.e., in DFS post-order, after every ticket reachable from it has already been folded into the route being built beneath it). Reversing (or, equivalently, always inserting at the front of) that post-order sequence produces the final itinerary.

**Key invariant:** an airport is appended to the route only once every ticket departing from it has already been consumed by the recursion -- so if that airport is a dead end (no outgoing tickets left after some point), it gets appended almost immediately, correctly placing it at the far end of whatever branch led into it, rather than truncating the itinerary as soon as a dead end is reached.

Proof sketch: because every destination is drawn from a min-heap, `visit(x)` always fully drains `x`'s heap (trying its smallest remaining ticket, recursing all the way down through that choice, and only then moving on to the next remaining ticket from `x`) before appending `x` to the route. Since the graph is guaranteed to admit a complete Eulerian-style traversal (every ticket used exactly once, starting from `JFK`), draining every node's heap this way visits every ticket exactly once; the post-order append then guarantees each node lands in exactly the position needed so that reversing the whole sequence reconstructs a route where consecutive entries are always connected by an actually-used ticket. Lexicographic minimality follows because at every branch point, the smallest available destination is always explored (and therefore appended) first among the branch's own alternatives.

### Step-by-step trace
Trace on `tickets = [["JFK","KUL"],["JFK","NRT"],["NRT","JFK"]]` (heaps: `JFK -> [KUL, NRT]`, `NRT -> [JFK]`, `KUL -> []`):

| call | heap[JFK] before | action | stack after push (bottom to top) |
|---|---|---|---|
| visit(JFK) | [KUL, NRT] | poll smallest, KUL; heap[JFK] becomes [NRT] | -- |
| &nbsp;&nbsp;visit(KUL) | -- | heap[KUL] empty, nothing to poll | push KUL -> `[KUL]` |
| visit(JFK) resumes | [NRT] | poll NRT; heap[JFK] becomes [] | -- |
| &nbsp;&nbsp;visit(NRT) | heap[NRT]=[JFK] | poll JFK; heap[NRT] becomes [] | -- |
| &nbsp;&nbsp;&nbsp;&nbsp;visit(JFK) (nested) | [] (already drained) | nothing to poll | push JFK -> `[KUL, JFK]` |
| &nbsp;&nbsp;visit(NRT) resumes | [] | heap[NRT] empty, done | push NRT -> `[KUL, JFK, NRT]` |
| visit(JFK) resumes | [] | heap[JFK] empty, done | push JFK -> `[KUL, JFK, NRT, JFK]` |

Reversing the stack gives the route `[JFK, NRT, JFK, KUL]`, matching worked example 3. Note that `KUL` (lexicographically smaller than `NRT`) is still explored *first* out of `JFK`, but because it's a dead end it gets pushed to the bottom of the stack immediately -- and the reversal is exactly what moves it to the *end* of the final route instead of the beginning.

## Java 8 solution
```java
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public static List<String> solve(List<List<String>> tickets) {
    Map<String, PriorityQueue<String>> graph = new HashMap<String, PriorityQueue<String>>();
    for (List<String> ticket : tickets) {
        String from = ticket.get(0);
        String to = ticket.get(1);
        graph.computeIfAbsent(from, k -> new PriorityQueue<String>()).add(to);
    }

    LinkedList<String> route = new LinkedList<String>(); // used as a stack via addFirst
    visit("JFK", graph, route);
    return route;
}

private static void visit(String airport, Map<String, PriorityQueue<String>> graph, LinkedList<String> route) {
    PriorityQueue<String> destinations = graph.get(airport); // null for a pure sink airport
    while (destinations != null && !destinations.isEmpty()) {
        String next = destinations.poll(); // always the lexicographically smallest unused ticket
        visit(next, graph, route);
    }
    route.addFirst(airport); // post-order: only append once every outgoing ticket is exhausted
}
```

## Complexity
- Time: `O(t log t)` where `t` is the number of tickets -- each ticket is inserted into a heap once (`O(log t)`) and polled once (`O(log t)`).
- Space: `O(t)` -- the adjacency map holds one heap entry per ticket, and the recursion depth can reach `O(t)` in the worst case (a single long chain).

## Java 8 pitfalls for this problem
- `PriorityQueue<String>`'s natural ordering is already lexicographic (`String` implements `Comparable<String>`), so no custom comparator is needed here.
- `graph.computeIfAbsent(from, k -> new PriorityQueue<String>())` only allocates a new heap the first time a given `from` airport is seen; this is the idiomatic Java 8 way to build an adjacency map without a manual `containsKey`/`put` dance.
- `graph.get(airport)` can legitimately return `null` for an airport that only ever appears as a destination, never as a departure point (a pure sink) -- the `destinations != null` check in the `while` loop's condition must come first, or a `NullPointerException` follows immediately on `.isEmpty()`.
- Using `LinkedList<String>` (a genuine doubly linked list, implementing `Deque`) and its `addFirst` method keeps each prepend `O(1)`; using an `ArrayList<String>` and calling `add(0, airport)` instead would make every prepend `O(n)`, turning the whole algorithm quadratic.
- A `PriorityQueue` correctly stores duplicate entries (two identical ticket destinations both get added and both get polled out separately), which is exactly what's needed to model duplicate tickets between the same two airports as two distinct usable edges -- a `Set`-based adjacency structure would incorrectly collapse them into one.
- Recursion depth in `visit` scales with the number of tickets (`<= 300` here), which is small enough that stack depth is not a practical concern for this problem's constraints, but the same idea ported to a much larger ticket count would need an iterative rewrite.

## Wrong approaches and why they fail
- **Greedily walk forward from `JFK`, always taking the lexicographically smallest unused ticket, with no backtracking or post-order deferral at all.** Counterexample: `tickets = [["JFK","KUL"],["JFK","NRT"],["NRT","JFK"]]` -- pure forward greedy departs `JFK` for `KUL` immediately (since `KUL < NRT`), then gets stuck at `KUL` (a dead end) with two tickets (`JFK->NRT` and `NRT->JFK`) never used, instead of the correct full itinerary `[JFK,NRT,JFK,KUL]` that visits `KUL` last.
- **Sort all tickets globally by `(from, to)` and simply concatenate them in that order wherever endpoints line up.** This ignores the fact that a valid itinerary may need to revisit the same airport multiple times in a very specific order dictated by which branch is a dead end; a single global sort has no mechanism for backtracking or for deciding which of several valid connections to take at a shared airport, and can easily produce several disconnected fragments that cannot be concatenated into one path at all.
- **Run a standard DFS marking tickets as visited, but append each airport to the route on the way *in* (pre-order) rather than on the way out (post-order).** This has the same failure mode as pure forward greedy: an airport gets written into the route the moment it's first reached, even if it is a dead end reached too early, stranding other tickets that needed to be used from that same airport (or from an ancestor of it) at a later point.

## Variants
- **Find an Eulerian circuit (start and end at the same airport) instead of an Eulerian path.** The identical Hierholzer's-algorithm skeleton applies unchanged, as long as the ticket graph's in-degree/out-degree balance actually admits a circuit rather than a path.
- **Determine whether a valid full itinerary exists at all, without assuming the input guarantees one.** Requires first checking that the ticket graph's degree balance permits an Eulerian path (at most one airport with one more outgoing than incoming ticket, at most one with one more incoming than outgoing, everything else balanced, and the ticket graph forming a single connected component) before running Hierholzer's algorithm.
- **Minimize total flight distance or cost while still using every ticket exactly once**, rather than minimizing lexicographic order. This is a fundamentally different (and much harder) objective, closer to route optimization than to a fixed Eulerian-path tie-break.

## Test cases
| # | tickets | expected | what it tests |
|---|---|---|---|
| 1 | `[[MUC,LHR],[JFK,MUC],[SFO,SJC],[LHR,SFO]]` | `[JFK,MUC,LHR,SFO,SJC]` | one unique chain, no branching |
| 2 | `[[JFK,SFO],[JFK,ATL],[SFO,ATL],[ATL,JFK],[ATL,SFO]]` | `[JFK,ATL,JFK,SFO,ATL,SFO]` | branching graph, greedy choices all succeed |
| 3 | `[[JFK,KUL],[JFK,NRT],[NRT,JFK]]` | `[JFK,NRT,JFK,KUL]` | dead-end trap, must defer the lexicographically smaller destination |
| 4 | `[[JFK,A]]` | `[JFK,A]` | single ticket |
| 5 | `[[JFK,A],[A,JFK],[JFK,A]]` | `[JFK,A,JFK,A]` | duplicate ticket between the same two airports |
| 6 | `[[JFK,A],[A,JFK],[JFK,B],[B,JFK],[JFK,C]]` | `[JFK,A,JFK,B,JFK,C]` | three-way tie-break at JFK, dead end last |
| 7 | `[[JFK,A],[A,B],[B,JFK],[JFK,C]]` | `[JFK,A,B,JFK,C]` | cycle back through JFK before a final dead end |
| 8 | `[[JFK,A],[A,B],[B,C]]` | `[JFK,A,B,C]` | simple chain, no branching |
| 9 | `[[JFK,C],[B,JFK],[JFK,B],[A,JFK],[JFK,A]]` | `[JFK,A,JFK,B,JFK,C]` | tickets listed out of alphabetical order in the input |
| 10 | `[[JFK,A],[A,C],[C,D],[D,A]]` | `[JFK,A,C,D,A]` | route ends at a non-JFK airport after a cycle |
