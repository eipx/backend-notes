# Redundant Connection
`ref: LC 684` · Difficulty: Medium · Pattern: Union-Find, first edge that closes a cycle

## Problem
A group of `n` nodes, labeled `1` through `n`, started out connected as a tree (exactly `n-1` edges, no cycles, everything reachable from everything else). One extra edge was then added on top of that tree, creating exactly one cycle somewhere in the graph. You're given the resulting `n` edges in the order they were added. Find the one edge that, when added, was the extra edge that created the cycle -- if more than one edge in the input could be described that way, return whichever one appears last when scanning the edge list in order.

## Constraints
- The edge list has exactly `n` edges for `n` nodes (a tree needs `n-1` edges, so there is exactly one redundant edge among them).
- Node labels are the integers `1` through `n`, with no self-edges (an edge never connects a node to itself).
- The input is guaranteed to describe a connected graph with exactly one cycle -- there is always a well-defined answer.

## Worked examples
1. `edges = [[2,3],[3,1],[1,2]]` -> `[1,2]`. Nodes 2, 3, and 1 already form a connected triangle once the first two edges are added; the third edge `[1,2]` connects two nodes that are already connected, so it's the one that closes the cycle.
2. `edges = [[1,2],[1,3],[3,4],[4,5],[2,5]]` -> `[2,5]`. The first four edges build a tree spanning all five nodes (a simple path `3-1-2` plus a branch `3-4-5`); the fifth edge `[2,5]` connects two nodes already joined by that tree, completing the one cycle.
3. `edges = [[1,3],[1,2],[2,3]]` -> `[2,3]`. Nodes 1, 3, and 2 are already all connected once the first two edges are processed; the third edge `[2,3]` is redundant.
4. `edges = [[1,2],[1,3],[1,4],[1,5],[4,5]]` -> `[4,5]`. The first four edges form a star with node 1 at the center, already connecting every node; the fifth edge `[4,5]` links two nodes that are already both connected through node 1.

## Edge cases checklist
- The redundant edge appears as the very last edge in the list (the simplest case to reason about, and the most common shape of example).
- The redundant edge appears in the middle of the list, with edges added after it that don't touch the cycle at all (they just extend the tree to new, previously-unconnected nodes).
- The smallest possible input: a 3-node triangle (`n=3`), the minimum size where a cycle can exist at all.
- Multiple different pairs of nodes are already connected by the time the redundant edge is read, but only one of them appears explicitly as an edge in the input -- the algorithm must recognize "already connected" via the union-find structure, not by re-scanning prior edges directly.
- Two nodes with a large "path distance" through the tree (e.g. opposite ends of a long chain) being the ones joined by the redundant edge, exercising that `find` correctly walks all the way to a shared root regardless of how many hops apart the nodes are in the original tree.

## Approach
### Brute force
For each edge in the list, temporarily remove it, then check whether the remaining `n-1` edges still connect all `n` nodes into a single tree (no disconnected pieces) via a full graph traversal (BFS/DFS from any node). The one edge whose removal restores full connectivity without a cycle is the answer. This requires up to `n` separate `O(n)` traversals (one per edge removed and re-checked), giving `O(n^2)` overall -- correct, but it re-derives full connectivity from scratch for every edge tried, when a single incremental pass is enough.

### Optimal
Process the edges in the given order with union-find, starting with every node in its own singleton set. For each edge `(u, v)`, check whether `u` and `v` are already in the same set (`find(u) == find(v)`): if they are, this edge connects two nodes that a prior edge (or chain of prior edges) already connected, so it's the answer -- return it immediately. Otherwise, union `u` and `v`'s sets together and continue to the next edge.

**Key invariant:** by the time edge `i` is examined, the union-find structure exactly reflects "are these two nodes connected using only edges `0..i-1`" -- so the very first edge for which both endpoints are already in the same set is, by definition, the edge that closes the first (and, per the problem's guarantee, only) cycle.

Proof sketch: adding edges one at a time and unioning endpoints builds up connectivity incrementally and exactly; `find(u) == find(v)` is true precisely when some path of already-processed edges connects `u` and `v`. Since the input is guaranteed to contain exactly `n` edges for `n` nodes (one more than a spanning tree needs), there is exactly one point in this left-to-right scan where an edge's endpoints are already connected -- and because the problem asks for "the edge that occurs last" among any ties, simply returning the *first* edge encountered during this forward left-to-right scan that satisfies the same-set condition automatically satisfies that requirement (there's only one such edge under the problem's guarantee, so "first found" and "last in input" coincide here).

### Step-by-step trace
Trace on `edges = [[1,2],[1,3],[3,4],[4,5],[2,5]]` (parent array indices 1..5, all initialized to themselves):

| edge | find(u), find(v) before | same set? | action | parent array after (index 1..5) |
|---|---|---|---|---|
| [1,2] | find(1)=1, find(2)=2 | no | union: parent[1]=2 | [_,2,2,3,4,5] |
| [1,3] | find(1)=2, find(3)=3 | no | union: parent[2]=3 | [_,2,3,3,4,5] |
| [3,4] | find(3)=3, find(4)=4 | no | union: parent[3]=4 | [_,2,3,4,4,5] |
| [4,5] | find(4)=4, find(5)=5 | no | union: parent[4]=5 | [_,2,3,4,5,5] |
| [2,5] | find(2)=5 (via 2->3->4->5), find(5)=5 | yes | return this edge | -- |

(Array is 1-indexed here; index 0 is unused since node labels start at 1.) Final answer: `[2,5]`.

## Java 8 solution
```java
public static int[] solve(int[][] edges) {
    int n = edges.length; // n edges for n nodes, per the problem's guarantee
    int[] parent = new int[n + 1]; // 1-indexed node labels
    for (int i = 1; i <= n; i++) {
        parent[i] = i;
    }
    for (int[] edge : edges) {
        int u = edge[0], v = edge[1];
        int rootU = find(parent, u);
        int rootV = find(parent, v);
        if (rootU == rootV) {
            return edge; // first edge whose endpoints are already connected
        }
        parent[rootU] = rootV;
    }
    return new int[0]; // unreachable under the problem's guarantee, but kept for a total function
}

private static int find(int[] parent, int x) {
    while (parent[x] != x) {
        parent[x] = parent[parent[x]]; // path compression (halving)
        x = parent[x];
    }
    return x;
}
```

## Complexity
- Time: `O(n * alpha(n))` -- a single left-to-right pass over the `n` edges, each doing amortized near-constant-time `find`/union work thanks to path compression.
- Space: `O(n)` -- the `parent` array holds one entry per node.

## Java 8 pitfalls for this problem
- The `parent` array must be sized `n + 1` (not `n`), since node labels run `1` to `n` inclusive and index `0` is simply unused -- allocating only `new int[n]` and then indexing `parent[n]` throws `ArrayIndexOutOfBoundsException` on the highest-labeled node.
- Returning the raw `edge` reference (an `int[]` of length 2 straight from the input array) rather than constructing a new array is fine and idiomatic here, since the caller only needs the values, not a defensive copy -- but be aware this means mutating the returned array would mutate the original input edge too.
- This solution's simple `union` (`parent[rootU] = rootV`, no union-by-size/rank) is a deliberate simplification: for this specific problem the total work is still bounded by `O(n * alpha(n))` in practice for the input sizes involved, but a production-grade union-find (as used in Number of Provinces and Accounts Merge, both in this folder) would add union by size for a tighter worst-case guarantee on adversarial input ordering.
- Comparing `rootU == rootV` works because `parent` is a primitive `int[]`, not `Integer[]` -- boxed `Integer` comparison with `==` is unreliable outside the small-integer cache range, a trap avoided here entirely by staying with primitives.
- The answer must be the **first** edge encountered (scanning left to right) whose endpoints are already connected, not the *last* edge in the entire list unconditionally -- returning `edges[edges.length - 1]` without actually running union-find would happen to work only by coincidence on inputs where the redundant edge is literally the last one listed (like most of the worked examples above), and fails outright on inputs where it appears earlier (see the Wrong approaches section).
- Recursion depth is a non-issue here since `find` is written iteratively; a recursive `find` on an extremely unbalanced, uncompressed tree (which can't happen after the first `find` on a given path due to path halving, but theoretically could on a first-ever traversal of a very long unioned chain) would recurse once per node along that chain.

## Wrong approaches and why they fail
- **Always returning the literal last edge in the input array:** this coincidentally matches the answer in inputs specifically constructed so the redundant edge happens to be listed last, but fails whenever the input lists the redundant edge earlier and then continues adding valid tree-extending edges afterward -- for example, an edge list where edge index 2 (0-indexed) is the one that closes a cycle among already-connected nodes, but a later edge in the list simply connects a brand-new, previously-unconnected node, would be answered wrong by this shortcut.
- **BFS/DFS cycle detection re-run from scratch after adding each edge:** correct, but redundant and much slower (`O(n)` per check, `O(n^2)` total) compared to incrementally maintaining connectivity with union-find, which reuses all prior work via the `parent` array instead of re-deriving reachability every time.
- **Checking `rootU == rootV` using boxed `Integer` objects with `==` instead of primitive `int`:** for small node labels this can accidentally "work" due to Java's `Integer` cache for values `-128` to `127`, masking the bug for small test inputs while silently breaking on larger node counts -- exactly the kind of trap that primitive `int[]` arrays avoid by construction.

## Variants
- **Redundant Connection II (the underlying structure is a rooted tree/directed graph, and the extra edge can create either a cycle or a node with two parents):** significantly more involved -- it requires separately detecting "a node with indegree 2" and "a cycle" as two different failure modes and reasoning about which single edge removal fixes both, rather than a single straightforward union-find scan.
- **Return every edge that would need to be removed to make the graph acyclic, for a graph with more than one extra edge (not guaranteed exactly one):** the same union-find scan generalizes directly -- collect every edge along the way whose endpoints are already connected, rather than returning immediately at the first one.
- **LC 547 Number of Provinces** and **LC 721 Accounts Merge** (both in this folder) reuse the same `find`/`union` core, applied to a dense adjacency matrix and to string email identifiers respectively, instead of a raw edge list.

## Test cases
| # | input edges | expected | what it tests |
|---|---|---|---|
| 1 | [[2,3],[3,1],[1,2]] | [1,2] | smallest possible cycle, 3-node triangle |
| 2 | [[1,2],[1,3],[3,4],[4,5],[2,5]] | [2,5] | tree spanning 5 nodes, cycle-closing edge listed last |
| 3 | [[1,2],[2,3],[2,4],[3,4]] | [3,4] | one node (2) with three connections, cycle among leaves |
| 4 | [[1,3],[1,2],[2,3]] | [2,3] | 3-node triangle, edges listed in a different order |
| 5 | [[1,2],[2,3],[3,4],[4,5],[5,6],[6,2]] | [6,2] | larger 6-node cycle, redundant edge wraps back to an early node |
| 6 | [[1,2],[1,3],[1,4],[1,5],[4,5]] | [4,5] | star-shaped tree, redundant edge joins two leaves |
| 7 | [[2,1],[1,3],[3,2]] | [3,2] | 3-node triangle, non-sequential label order |
| 8 | [[1,2],[2,3],[1,4],[3,4]] | [3,4] | 4-node graph, redundant edge appears last after two separate tree branches merge |
