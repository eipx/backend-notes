# Network Delay Time
`ref: LC 743` · Difficulty: Medium · Pattern: Dijkstra with a PriorityQueue of int[] and lazy deletion

## Problem
A directed, weighted graph of `n` nodes (labeled `1` through `n`) represents a signal-relay network: each edge `[u, v, w]` means a signal takes `w` units of time to travel directly from node `u` to node `v` (one direction only). A signal is sent from a given source node `k`. Find the minimum time until every node in the network has received the signal, or `-1` if some node can never receive it at all.

## Constraints
- `1 <= k <= n <= 100`
- `1 <= times.length <= 6000`
- `times[i].length == 3`, `1 <= u_i, v_i <= n`, `u_i != v_i`
- `0 <= w_i <= 100`
- Edges are directed; more than one edge between the same ordered pair of nodes may appear, possibly with different weights.

## Worked examples
1. `times=[[2,1,1],[2,3,1],[3,4,1]]`, `n=4`, `k=2` -> `2`. From node 2: node 1 is reached directly in `1`; node 3 directly in `1`; node 4 is reached via node 3 in `1+1=2`. The last node to receive the signal is node 4, at time `2`.
2. `times=[[1,2,1]]`, `n=2`, `k=1` -> `1`. A single direct edge.
3. `times=[[1,2,1]]`, `n=2`, `k=2` -> `-1`. The only edge points from node 1 to node 2, so starting from node 2 there is no way to ever reach node 1.
4. `times=[[1,2,1],[2,3,2],[1,3,4]]`, `n=3`, `k=1` -> `3`. Node 3 is reached faster via node 2 (`1+2=3`) than by the direct edge (`4`); the answer is the slowest of every node's own fastest arrival time, which here is node 3 at `3`.

## Edge cases checklist
- `n == 1`: the source is the only node, so the answer is always `0`, regardless of the (necessarily empty) edge list.
- At least one node unreachable from `k` -- the answer is `-1` as soon as any node never gets finalized, even if every other node is reachable.
- The source `k` has no outgoing edges at all, while `n > 1` -- immediately `-1` (unless `n == 1`).
- Multiple edges between the same ordered pair of nodes with different weights -- the smaller weight must win, regardless of which one appears first in the input.
- An edge with weight `0` -- it must still relax normally (a zero-cost hop is not "no edge").
- A node reachable faster through two or more intermediate hops than through any direct edge -- the algorithm must not stop at the first (possibly slower) route found.
- The source node `k` is not node `1` -- confirms the algorithm treats `k` as a genuine parameter, not a hardcoded starting point.

## Approach

### Brute force
Enumerate every simple path (no repeated nodes) from `k` to each other node via DFS backtracking, summing edge weights along each path, and keep the minimum total for each destination; the answer is the maximum of those per-node minimums. The number of simple paths in a dense directed graph can grow combinatorially with `n` (up to `O(n!)` in the worst case), which is far too slow once `n` approaches its upper bound of `100` with up to `6000` edges.

### Optimal
Dijkstra's algorithm with a min-heap keyed by accumulated distance, using lazy deletion: push `(distance, node)` pairs onto a `PriorityQueue<int[]>`; when a pair is popped, check whether that node has already been finalized with a smaller (or equal) distance earlier -- if so, this is a stale heap entry left over from a since-improved relaxation, and it is simply skipped rather than proactively removed from the heap at the time it became stale. Otherwise, finalize the node's distance, and relax every outgoing edge, pushing a fresh `(newDistance, neighbor)` pair whenever it improves on the neighbor's best known distance so far. The answer is the maximum finalized distance across all `n` nodes, or `-1` if fewer than `n` nodes ever get finalized.

**Key invariant:** whenever a popped `(distance, node)` pair is *not* stale (its distance matches the best distance ever recorded for that node), that distance is guaranteed to be the true shortest distance from `k` to `node` -- because Dijkstra always finalizes the currently-closest not-yet-finalized node next, and since every edge weight is non-negative, no path routed through any node that is still farther away than `node` could ever produce a shorter route to `node`.

Proof sketch: by induction on the order of finalization. The first finalized node is `k` itself at distance `0`, trivially correct. Assume every previously finalized node's recorded distance is exactly its true shortest distance from `k`. The next node popped (ignoring stale entries) has the smallest tentative distance among all not-yet-finalized nodes; any alternative path to it would have to pass through some other not-yet-finalized node first, which (by non-negative weights) cannot possibly shorten the route, since that intermediate node's own distance from `k` is already `>=` the node currently being finalized. Hence the greedy choice is always correct, and lazy deletion only ever discards distances that have already been superseded by a smaller, earlier-finalized value for the same node.

### Step-by-step trace
Trace on `times=[[2,1,1],[2,3,1],[3,4,1]]`, `n=4`, `k=2` (adjacency: `2->1`=1, `2->3`=1, `3->4`=1):

| step | heap before pop | popped (node,dist) | stale? | action | finalized so far |
|---|---|---|---|---|---|
| 1 | [(2,0)] | (2,0) | no | finalize 2 at 0; relax 2->1 (push (1,1)), 2->3 (push (3,1)) | {2:0} |
| 2 | [(1,1),(3,1)] | (1,1) | no | finalize 1 at 1; node 1 has no outgoing edges | {2:0,1:1} |
| 3 | [(3,1)] | (3,1) | no | finalize 3 at 1; relax 3->4 (push (4,2)) | {2:0,1:1,3:1} |
| 4 | [(4,2)] | (4,2) | no | finalize 4 at 2; no outgoing edges | {2:0,1:1,3:1,4:2} |

All 4 nodes finalized; answer is `max(0,1,1,2) = 2`, matching worked example 1.

## Java 8 solution
```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public static int solve(int[][] times, int n, int k) {
    List<List<int[]>> graph = new ArrayList<List<int[]>>();
    for (int i = 0; i <= n; i++) { // 1-indexed nodes; index 0 unused
        graph.add(new ArrayList<int[]>());
    }
    for (int[] edge : times) {
        graph.get(edge[0]).add(new int[]{edge[1], edge[2]}); // {neighbor, weight}
    }

    int[] dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[k] = 0;

    PriorityQueue<int[]> minHeap = new PriorityQueue<int[]>((a, b) -> Integer.compare(a[1], b[1]));
    minHeap.add(new int[]{k, 0}); // {node, accumulated distance}

    boolean[] finalized = new boolean[n + 1];
    int finalizedCount = 0;
    int maxDist = 0;

    while (!minHeap.isEmpty()) {
        int[] current = minHeap.poll();
        int node = current[0];
        int d = current[1];
        if (finalized[node]) {
            continue; // lazy deletion: a shorter entry for this node was already finalized
        }
        finalized[node] = true;
        finalizedCount++;
        maxDist = Math.max(maxDist, d);
        for (int[] edge : graph.get(node)) {
            int next = edge[0];
            int weight = edge[1];
            if (!finalized[next] && d + weight < dist[next]) {
                dist[next] = d + weight;
                minHeap.add(new int[]{next, dist[next]});
            }
        }
    }

    return finalizedCount == n ? maxDist : -1;
}
```

## Complexity
- Time: `O(E log E)` -- each of the up to `E` edges can push at most one heap entry per relaxation, and each heap operation costs `O(log E)`.
- Space: `O(V + E)` -- the adjacency list and the heap.

## Java 8 pitfalls for this problem
- `new PriorityQueue<int[]>((a, b) -> Integer.compare(a[1], b[1]))` uses a lambda for the `Comparator<int[]>`; this compiles cleanly under `--release 8` (lambdas for functional interfaces are a Java 8 feature), unlike `new PriorityQueue<>(...)` combined with an anonymous *class* body, which would need Java 9's diamond-with-anonymous-class support.
- The `finalized[node]` check must happen immediately after popping, before any relaxation work -- this is the entire mechanism of lazy deletion, and skipping it lets stale (larger) distance entries re-process a node's edges redundantly, or, on a change to the algorithm that isn't careful, corrupt an already-correct finalized distance.
- Node labels are `1`-indexed (`1..n`); allocating `dist`, `finalized`, and `graph` with size `n + 1` and leaving index `0` unused avoids constant off-by-one translation throughout the code.
- `Integer.MAX_VALUE` as the "infinity" sentinel is safe here specifically because this solution only ever relaxes edges out of nodes that have already been finalized to a finite distance -- it never computes `Integer.MAX_VALUE + weight`, which would silently overflow into a negative number.
- Allocating a fresh `int[]{node, dist}` for every single relaxation (rather than mutating a value in place) is the idiomatic, safe way to use `PriorityQueue<int[]>` in Java, since the heap needs independent, immutable-in-effect snapshots of "distance at the time this entry was pushed" -- mutating a previously-pushed array in place would corrupt the heap's ordering invariant.

## Wrong approaches and why they fail
- **Run plain BFS counting edges (hops) instead of summing edge weights.** Counterexample: `times=[[1,2,1],[2,3,2],[1,3,4]]`, `k=1` -- BFS would report node 3 reached in "1 hop" via the direct edge, entirely ignoring that this specific edge's actual weight (`4`) is worse than the two-hop route's total weight (`1+2=3`); BFS answers a different problem (fewest edges), not "least total delay."
- **Skip the `finalized[node]` staleness check on pop, assuming the heap alone is enough.** For a graph with non-negative weights this doesn't produce a wrong final answer, but it does needless repeated relaxation work from nodes whose true shortest distance was already found earlier via a smaller heap entry -- the deeper danger is that this check is exactly what makes lazy deletion *correct and efficient* rather than a source of quietly-wasted cycles that grows with how many times a given node gets re-pushed before its true minimum is found.
- **Always trust the direct edge from `k` to a node as that node's shortest distance, without a full relaxation pass.** Counterexample: `times=[[1,2,1],[2,3,2],[1,3,4]]`, `k=1` -- the direct edge `1->3` has weight `4`, but the true shortest distance to node 3 is `3` (via node 2); skipping full Dijkstra relaxation in favor of "just check direct edges" misses every improvement routed through an intermediate node.

## Variants
- **Bellman-Ford when the number of hops (edges used) is bounded** (e.g., "cheapest route using at most `K` intermediate stops"), as in LC 787 Cheapest Flights Within K Stops -- Dijkstra's greedy finalize-once-and-never-revisit approach has no natural way to track how many edges a given shortest path used, while Bellman-Ford's edge-relaxation-by-rounds structure directly bounds the number of hops by capping the number of relaxation passes at `K + 1`.
- **Negative edge weights.** Dijkstra's correctness proof depends entirely on non-negative weights (see the proof sketch above); if any edge weight could be negative, Dijkstra can finalize a node too early and never revisit it even though a later-discovered path would have been shorter, so Bellman-Ford (or SPFA) becomes required regardless of any hop limit.
- **Report the actual shortest path to the last-finalized node, not just its distance.** Track a `predecessor` array alongside `dist`, updating it every time a shorter distance is found during relaxation, then walk it backward from the final answer's node once the algorithm completes.

## Test cases
| # | times | n | k | expected | what it tests |
|---|---|---|---|---|---|
| 1 | `[[2,1,1],[2,3,1],[3,4,1]]` | 4 | 2 | 2 | standard multi-hop case from the walkthrough |
| 2 | `[[1,2,1]]` | 2 | 1 | 1 | minimal reachable case |
| 3 | `[[1,2,1]]` | 2 | 2 | -1 | wrong direction, source cannot reach the other node |
| 4 | `[[1,2,1],[2,3,2],[1,3,4]]` | 3 | 1 | 3 | two-hop route beats a slower direct edge |
| 5 | `[]` | 1 | 1 | 0 | single node, no edges needed |
| 6 | `[[1,2,1],[2,1,3]]` | 2 | 1 | 1 | directed edges, correct direction from the source |
| 7 | `[[1,2,1],[2,1,3]]` | 2 | 2 | 3 | directed edges, reverse direction dominates |
| 8 | `[[1,2,10],[1,3,5],[3,2,1],[2,4,1],[3,4,9],[4,1,7]]` | 4 | 1 | 7 | several relaxations needed across multiple paths |
| 9 | `[[1,2,1],[1,3,1],[2,3,1]]` | 3 | 1 | 1 | direct edge beats a longer alternate path |
| 10 | `[[1,2,5],[2,3,5],[1,3,3]]` | 3 | 1 | 5 | direct edge is not globally shortest but still the max of the per-node bests |
| 11 | `[[1,2,1]]` | 3 | 1 | -1 | a node with no incoming path at all is unreachable |
