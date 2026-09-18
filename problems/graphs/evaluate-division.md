# Evaluate Division
`ref: LC 399` · Difficulty: Medium · Pattern: weighted-graph DFS/BFS, with weighted union-find as the many-queries alternative

## Problem
You're given a list of equations of the form `a / b = value`, where `a` and `b` are variable names (strings) and `value` is a positive real number, plus a list of queries of the form `c / d`. Using only the given equations (and the fact that division and multiplication chain together algebraically, so `a/b = 2` and `b/c = 3` together imply `a/c = 6`), answer each query with the resulting ratio, or `-1.0` if the answer cannot be determined -- either because one of the two queried variables never appears in any equation at all, or because the two variables are each individually known but never connected to each other through any chain of equations.

This is exactly the shape of a currency-conversion problem: each equation is like a quoted exchange rate between two currencies (`"1 USD = 1.1 EUR"`), and a query asks for the effective rate between two currencies that might not have a directly quoted rate, requiring a chain through one or more intermediate currencies.

## Constraints
- `1 <= equations.length <= 20`, and `equations[i].length == 2`.
- `equations[i][0] != equations[i][1]` (no equation relates a variable to itself).
- `values.length == equations.length`, `0.0 < values[i] <= 20.0`.
- `1 <= queries.length <= 20`, `queries[i].length == 2`.
- Variable names are lowercase strings; the same variable may appear in several equations.

## Worked examples
1. `equations=[["a","b"],["b","c"]]`, `values=[2.0,3.0]`, `queries=[["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]` -> `[6.0, 0.5, -1.0, 1.0, -1.0]`. `a/c = (a/b)*(b/c) = 2*3 = 6`; `b/a` is the reciprocal, `0.5`; `e` never appears anywhere, so `-1.0`; `a/a` is `1.0` since `a` is known; `x` never appears anywhere either, so `x/x` is also `-1.0` (being unknown disqualifies even a self-query).
2. `equations=[["a","b"],["b","c"],["bc","cd"]]`, `values=[1.5,2.5,5.0]`, `queries=[["a","c"],["c","b"],["bc","cd"],["cd","bc"]]` -> `[3.75, 0.4, 5.0, 0.2]`. `a/c = 1.5*2.5 = 3.75`; `c/b = 1/2.5 = 0.4`; the unrelated `bc`/`cd` pair forms its own separate two-variable equation cluster, answered directly and by reciprocal.
3. `equations=[["x1","x2"]]`, `values=[0.5]`, `queries=[["x1","x2"],["x2","x1"],["x1","x1"],["x1","x3"],["x3","x4"]]` -> `[0.5, 2.0, 1.0, -1.0, -1.0]`. `x3` and `x4` never appear in any equation, so both queries touching them return `-1.0`.

## Edge cases checklist
- A query variable that never appears in any equation at all, including a self-division query on that unknown variable (`x/x` still returns `-1.0` if `x` is unknown).
- A query where both variables are the same and are known -- returns `1.0` even without an explicit self-loop edge ever being stored.
- Two variables that are each individually known (each appears in some equation) but live in two entirely separate, disconnected equation clusters -- returns `-1.0`.
- A query answerable only by chaining three or more equations together.
- Querying both a direct ratio and its reciprocal (`a/b` and `b/a`) to confirm both directions of an equation are stored correctly.
- The exact same equation given twice, or an equation and its algebraic reciprocal both given explicitly, which should not corrupt or contradict the graph.

## Approach

### Brute force
For each query, try chaining every possible sequence of the given equations and their reciprocals together (without tracking which variables have already been used in the current chain), multiplying values along the way, to see whether any chain of substitutions algebraically reduces to the queried ratio. Without a visited-set, this can revisit the same variable arbitrarily many times and loop forever on any pair of equations that are reciprocals of each other (chaining `a/b` then `b/a` then `a/b` again, forever); even with a depth cap added as a band-aid, this explores redundant work that a single graph search avoids entirely.

### Optimal
Build a weighted directed graph: for each equation `a/b = value`, add an edge `a -> b` with weight `value` and an edge `b -> a` with weight `1/value`. For each query `c/d`, first check that both `c` and `d` appear in the graph at all (otherwise answer `-1.0` immediately) and that they aren't the same variable (otherwise answer `1.0` immediately); then run a DFS or BFS from `c`, multiplying edge weights along the way and tracking visited variables to avoid cycles, until `d` is reached (return the accumulated product) or the search is exhausted without reaching it (answer `-1.0`).

**Key invariant:** the accumulated product carried along any explored path from `c` to a given node always equals (by construction) the true ratio of `c` to that node according to the equation system, because each edge traversal multiplies in exactly one equation's value or its reciprocal; the visited set guarantees each variable's accumulated ratio from `c` is only ever computed once, so regardless of which specific path the search happens to explore first, the ratio found upon reaching `d` (if reached at all) is correct, since the problem guarantees the given equations never contradict each other.

Proof sketch: building the graph costs `O(E)` for `E` equations (two edges each). Each query's search visits every variable reachable from `c` at most once (thanks to the visited set), doing `O(1)` work per edge, so each query costs `O(V + E)` in the worst case. Because every edge weight is exactly the ratio (or its reciprocal) between two connected variables, and the graph is guaranteed consistent, the product accumulated along *any* simple path between two connected variables is the same value -- so a single DFS/BFS search (rather than trying every path) suffices to find the unique correct ratio whenever one exists at all.

### Step-by-step trace
Trace the query `a/c` on `equations=[["a","b"],["b","c"]]`, `values=[2.0,3.0]` (graph: `a->b`=2.0, `b->a`=0.5, `b->c`=3.0, `c->b`=1/3.0):

| step | current node | accumulated ratio from a | visited | action |
|---|---|---|---|---|
| start | a | 1.0 | {a} | enqueue/visit a's neighbors |
| 1 | b | 1.0 * 2.0 = 2.0 | {a,b} | b != target c, expand b's neighbors (a already visited, skip; c not visited) |
| 2 | c | 2.0 * 3.0 = 6.0 | {a,b,c} | c == target, return 6.0 |

Result: `a/c = 6.0`, matching worked example 1.

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public static double[] solve(List<List<String>> equations, double[] values, List<List<String>> queries) {
    Map<String, Map<String, Double>> graph = new HashMap<String, Map<String, Double>>();
    for (int i = 0; i < equations.size(); i++) {
        String a = equations.get(i).get(0);
        String b = equations.get(i).get(1);
        double value = values[i];
        graph.computeIfAbsent(a, k -> new HashMap<String, Double>()).put(b, value);
        graph.computeIfAbsent(b, k -> new HashMap<String, Double>()).put(a, 1.0 / value);
    }

    double[] answers = new double[queries.size()];
    for (int i = 0; i < queries.size(); i++) {
        String source = queries.get(i).get(0);
        String target = queries.get(i).get(1);
        if (!graph.containsKey(source) || !graph.containsKey(target)) {
            answers[i] = -1.0; // one (or both) variables never appear in any equation
        } else if (source.equals(target)) {
            answers[i] = 1.0; // known variable divided by itself
        } else {
            answers[i] = bfsRatio(graph, source, target);
        }
    }
    return answers;
}

// Breadth-first search over the weighted graph, carrying the accumulated ratio
// from `source` alongside each queued node; -1.0 if `target` is unreachable.
private static double bfsRatio(Map<String, Map<String, Double>> graph, String source, String target) {
    Deque<String> queue = new ArrayDeque<String>();
    Deque<Double> accumulated = new ArrayDeque<Double>();
    Set<String> visited = new HashSet<String>();
    queue.add(source);
    accumulated.add(1.0);
    visited.add(source);
    while (!queue.isEmpty()) {
        String node = queue.poll();
        double ratioFromSource = accumulated.poll();
        if (node.equals(target)) {
            return ratioFromSource;
        }
        for (Map.Entry<String, Double> edge : graph.get(node).entrySet()) {
            String next = edge.getKey();
            if (!visited.contains(next)) {
                visited.add(next);
                queue.add(next);
                accumulated.add(ratioFromSource * edge.getValue());
            }
        }
    }
    return -1.0; // target unreachable from source
}
```

## Complexity
- Time: `O(Q * (V + E))` where `Q` is the number of queries, `V` the number of distinct variables, and `E` the number of equations -- each query runs its own independent BFS/DFS over the graph.
- Space: `O(V + E)` for the graph, plus `O(V)` per query for the visited set and queues.

## Java 8 pitfalls for this problem
- Comparing the resulting `double` ratios with `==` is unsafe, since floating-point multiplication accumulates rounding error over a chain of divisions; always compare with a small tolerance (e.g. `Math.abs(actual - expected) < 1e-4`), exactly as this page's own test runner does.
- `graph.computeIfAbsent(a, k -> new HashMap<String, Double>())` only allocates a new inner map the first time a variable is seen as a graph node; subsequent equations touching the same variable reuse the existing map.
- The `!graph.containsKey(source) || !graph.containsKey(target)` guard must run before any attempt to read `graph.get(node).entrySet()` inside the search -- skipping it risks a `NullPointerException` the moment an unknown variable's (missing) adjacency map is dereferenced.
- `Deque<Double>` boxes every accumulated ratio; at this problem's tiny scale (`<= 20` equations, `<= 20` queries) that's a non-issue, but it would be worth avoiding at a much larger scale by carrying the ratio in a small parallel array indexed alongside a primitive `int`-based queue instead.
- Division by zero is not actually possible here since `values[i]` is constrained to be strictly positive, but a more defensive version of this code (e.g. one relaxing that constraint) would need to guard `1.0 / value` against `value == 0.0`.

## Wrong approaches and why they fail
- **Store only one direction of each equation (`a -> b`, forgetting the reciprocal edge `b -> a`).** Counterexample: `equations=[["a","b"]]`, `values=[2.0]`, query `["b","a"]` -- without the reciprocal edge, `b` never even appears as a graph node with any outgoing edge to `a`, so the query wrongly returns `-1.0` instead of the correct `0.5`.
- **Assume any two variables that each individually appear in some equation must be answerable, and return some fallback (like the product of unrelated ratios) instead of correctly checking reachability.** Counterexample: `equations=[["a","b"],["c","d"]]`, `values=[2.0,3.0]`, query `["a","d"]` -- `a` and `d` are each known, but belong to two completely disconnected equation clusters; a reachability-based search correctly returns `-1.0` here, while any shortcut that skips the actual graph search (e.g., naively multiplying `2.0 * 3.0 = 6.0` because both values happen to be "in scope") returns a fabricated, wrong answer.
- **Run DFS without any visited set on a graph that contains a cycle formed by an equation and its reciprocal.** Since every equation contributes both `a -> b` and `b -> a`, the graph always contains at least a 2-cycle between any two directly related variables; a search that doesn't track visited nodes can bounce back and forth across that cycle forever, or (if a hop-count cap is bolted on as a fix) return whatever partial, incorrect ratio happens to be accumulated when the cap is hit, instead of correctly recognizing termination via the visited set.

## Variants
- **Weighted union-find for a very large number of queries against a fixed set of equations.** Instead of re-running a full graph search per query, maintain a union-find where each node stores its ratio to its own parent; `find(x)` both locates `x`'s root and (via path compression) accumulates and caches `x`'s ratio to that root, and `union(a, b, value)` merges `a`'s and `b`'s groups while keeping every ratio inside the merged group consistent. Once every equation has been folded in this way, each query is answered in near-`O(alpha(n))` time by comparing `a` and `b`'s roots and combining their cached ratios-to-root -- this amortizes much better than DFS/BFS per query once the number of queries is large relative to the number of equations, which is exactly the "many currency conversions against a fixed table of quoted rates" scenario in practice.
- **Detect an inconsistent or contradictory set of equations** (e.g. `a/b = 2` and, separately, `a/b = 3` given explicitly) -- requires comparing a newly-added equation's implied ratio against any ratio already reachable between the same two variables before accepting it, which plain DFS/BFS answering doesn't need to do at all.
- **Support incremental equations added one at a time, interleaved with queries**, rather than receiving the full equation list upfront -- the weighted union-find variant above naturally supports this, while the DFS/BFS approach here already supports it too, since the graph is just a mutable adjacency map that queries read from directly.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | equations=[[a,b],[b,c]], values=[2.0,3.0], queries=[[a,c],[b,a],[a,e],[a,a],[x,x]] | [6.0,0.5,-1.0,1.0,-1.0] | chained ratio, reciprocal, unknown variables |
| 2 | equations=[[a,b],[b,c],[bc,cd]], values=[1.5,2.5,5.0], queries=[[a,c],[c,b],[bc,cd],[cd,bc]] | [3.75,0.4,5.0,0.2] | separate disconnected equation cluster answered directly |
| 3 | equations=[[x1,x2]], values=[0.5], queries=[[x1,x2],[x2,x1],[x1,x1],[x1,x3],[x3,x4]] | [0.5,2.0,1.0,-1.0,-1.0] | single equation, self-query, unknown variables |
| 4 | equations=[[a,b],[c,d]], values=[2.0,3.0], queries=[[a,d]] | [-1.0] | both variables known, but disconnected clusters |
| 5 | equations=[[a,b]], values=[4.0], queries=[[b,a]] | [0.25] | reciprocal of a single equation |
| 6 | equations=[[a,b],[b,c],[c,d]], values=[2.0,3.0,4.0], queries=[[a,d],[d,a]] | [24.0,0.041666667] | three-hop chain and its reciprocal |
| 7 | equations=[[a,b],[b,a]], values=[2.0,0.5], queries=[[a,b]] | [2.0] | both directions of an equation given explicitly |
| 8 | equations=[[a,b]], values=[1.0], queries=[[a,b],[a,a]] | [1.0,1.0] | identity ratio, self-query |
| 9 | equations=[], values=[], queries=[[a,b]] | [-1.0] | no equations at all, everything unknown |
| 10 | equations=[[a,b],[b,c],[a,c]], values=[2.0,3.0,6.0], queries=[[a,c],[c,a]] | [6.0,0.166666667] | redundant equation consistent with the derived chain |
