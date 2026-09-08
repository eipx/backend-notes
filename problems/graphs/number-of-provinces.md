# Number of Provinces
`ref: LC 547` · Difficulty: Medium · Pattern: Union-Find (disjoint set) with path compression and union by size

## Problem
There are a number of cities, and you're given a square matrix describing direct connections between them: entry `(i, j)` is `1` if city `i` and city `j` are directly connected, and `0` otherwise (every city is trivially connected to itself, so the diagonal is always `1`, but that fact carries no extra information). A "province" is a maximal group of cities that are connected to each other, whether directly or through a chain of other cities in the same group. Count how many separate provinces exist among all the cities.

## Constraints
- `1 <= n <= 200` cities, matrix is `n x n`.
- The matrix is symmetric (`isConnected[i][j] == isConnected[j][i]`), since a connection between two cities is mutual.
- Every diagonal entry `isConnected[i][i]` is `1` (a city is always "connected" to itself), but this is a fixed given, not something to derive.
- A city with no direct connections to any other city still forms its own province of size `1`.

## Worked examples
1. `isConnected = [[1,1,0],[1,1,0],[0,0,1]]` -> `2`. Cities 0 and 1 are directly connected to each other, forming one province; city 2 is connected to no one else, forming its own separate province.
2. `isConnected = [[1,0,0],[0,1,0],[0,0,1]]` -> `3`. No city connects to any other (only the required self-connections on the diagonal), so every city is its own province.
3. `isConnected = [[1,1,1],[1,1,1],[1,1,1]]` -> `1`. All three cities are mutually connected, forming a single province.
4. `isConnected = [[1,1,0,0],[1,1,0,0],[0,0,1,1],[0,0,1,1]]` -> `2`. Cities 0-1 form one connected group, and cities 2-3 form a completely separate connected group, with no links between the two groups.

## Edge cases checklist
- A single city (`n == 1`) -- trivially one province, matrix is just `[[1]]`.
- No city connects to any other beyond the mandatory self-loop -- every city is its own province (`n` provinces total).
- All cities mutually connected -- exactly one province regardless of `n`.
- Provinces of very different sizes coexisting (e.g. one giant connected group plus several isolated singleton cities).
- A chain-shaped connection pattern where city 0 connects to 1, 1 connects to 2, but 0 and 2 are not *directly* connected in the matrix -- must still count as a single province via transitive (indirect) connection.
- Symmetric redundancy: since the matrix is symmetric, only the upper (or lower) triangle needs to be examined for union operations without changing the answer -- checking the full matrix would just redo the same unions twice, which is wasteful but not incorrect.

## Approach
### Brute force
Treat the matrix as an adjacency matrix and run a graph traversal (BFS or DFS) from every unvisited city, marking every city reachable from it as visited and counting that as one province, then moving to the next unvisited city. This is entirely correct and actually not asymptotically worse than the union-find approach for this specific problem (`O(n^2)` either way, since building or scanning the matrix itself costs `O(n^2)`), but it doesn't showcase the union-find technique this section is meant to build fluency in, and it requires an explicit `visited` array and traversal logic rather than the flatter union-find bookkeeping.

### Optimal
Union-Find (disjoint set): maintain a `parent` array where `parent[i]` initially points to itself, meaning every city starts in its own singleton set. For every pair `(i, j)` with `i < j` where `isConnected[i][j] == 1`, union their sets together. `find(x)` walks up the `parent` chain to the representative ("root") of `x`'s set, compressing the path along the way (every visited node's parent is updated to point closer to the root, so future lookups are faster). `union(a, b)` finds both roots and, if they differ, attaches the smaller set's root under the larger set's root (union by size), keeping the trees shallow. After processing every connection, the number of distinct roots remaining (cities `i` for which `find(i) == i`) is the number of provinces.

**Key invariant:** at any point during the algorithm, two cities are in the same disjoint-set tree if and only if they are connected by some chain of direct connections processed so far -- path compression and union by size never change *which* cities end up grouped together, only how quickly `find` can compute the answer.

Proof sketch: union-find maintains the invariant that "same root" exactly tracks "same connected component," by construction of the `union` operation (merging two sets whenever a connecting edge is processed). Path compression preserves correctness because it only changes intermediate pointers to point more directly at the *same* root the node already belonged to -- it never changes group membership, only lookup speed. Union by size keeps the amortized cost of each `find`/`union` operation nearly constant (technically `O(alpha(n))`, the inverse Ackermann function, which is effectively a small constant for any realistic `n`), so processing all `O(n^2)` matrix entries stays efficient.

### Step-by-step trace
Trace on `isConnected = [[1,1,0,0],[1,1,0,0],[0,0,1,1],[0,0,1,1]]` (n=4), scanning pairs `(i,j)` with `i<j`:

| pair checked | connected? | action | parent array after (index: value) |
|---|---|---|---|
| (0,1) | yes | union(0,1): find(0)=0, find(1)=1, sizes equal, attach 1 under 0 | [0,0,2,3] |
| (0,2) | no | skip | [0,0,2,3] |
| (0,3) | no | skip | [0,0,2,3] |
| (1,2) | no | skip | [0,0,2,3] |
| (1,3) | no | skip | [0,0,2,3] |
| (2,3) | yes | union(2,3): find(2)=2, find(3)=3, sizes equal, attach 3 under 2 | [0,0,2,2] |
| -- | -- | count roots: find(0)=0, find(1)=0, find(2)=2, find(3)=2 -> 2 distinct roots {0,2} | return 2 |

## Java 8 solution
```java
public static int solve(int[][] isConnected) {
    int n = isConnected.length;
    int[] parent = new int[n];
    int[] size = new int[n];
    for (int i = 0; i < n; i++) {
        parent[i] = i;
        size[i] = 1;
    }

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (isConnected[i][j] == 1) {
                union(parent, size, i, j);
            }
        }
    }

    int provinces = 0;
    for (int i = 0; i < n; i++) {
        if (find(parent, i) == i) {
            provinces++;
        }
    }
    return provinces;
}

private static int find(int[] parent, int x) {
    while (parent[x] != x) {
        parent[x] = parent[parent[x]]; // path compression (halving): skip a level toward the root
        x = parent[x];
    }
    return x;
}

private static void union(int[] parent, int[] size, int a, int b) {
    int rootA = find(parent, a);
    int rootB = find(parent, b);
    if (rootA == rootB) {
        return; // already in the same set, nothing to do
    }
    if (size[rootA] < size[rootB]) {
        int tmp = rootA;
        rootA = rootB;
        rootB = tmp;
    }
    parent[rootB] = rootA; // attach the smaller tree under the larger tree's root
    size[rootA] += size[rootB];
}
```

## Complexity
- Time: `O(n^2 * alpha(n))` -- scanning the matrix is `O(n^2)`, and every `find`/`union` call costs amortized `O(alpha(n))` (inverse Ackermann, effectively constant) thanks to path compression plus union by size.
- Space: `O(n)` -- the `parent` and `size` arrays are each sized `n`, independent of how many connections exist.

## Java 8 pitfalls for this problem
- `parent[x] = parent[parent[x]]` inside `find` is *path halving* (a lightweight, iterative form of path compression) -- it doesn't fully flatten the tree in one pass the way a recursive "compress everything to the root" implementation would, but combined with union by size it still gives the same amortized near-constant bound, and it avoids any recursion-depth concern entirely (a fully recursive path-compression `find` could recurse once per node on a long un-compressed chain, which is a real risk before any compression has happened).
- Comparing `find(parent, i) == i` at the end works because these are primitive `int`s, not boxed `Integer`s -- if `parent` were declared as `Integer[]` instead of `int[]`, the same `==` comparison would compare object references for values outside the `-128..127` `Integer` cache range, silently breaking the "is this a root" check for larger `n`. Keeping `parent`/`size` as primitive arrays sidesteps that boxed-`==` trap entirely.
- Union by size requires comparing `size[rootA]` and `size[rootB]` *before* mutating either -- swapping `rootA`/`rootB` first and only then reading sizes would compare a value against itself after already having reassigned one of the local variables, silently breaking the "attach smaller under larger" guarantee (though on a symmetric matrix like this problem's input, an unbalanced union merely costs a bit more time, not correctness).
- Only scanning `j = i + 1` to `n` (not `j = 0` to `n`) exploits the matrix's symmetry to avoid redundant union calls -- scanning the full matrix wouldn't break anything (union on already-connected cities is a harmless no-op), but it doubles the number of `union` calls examined for no benefit.
- Declaring `parent` and `size` as fields on the class (rather than passing them as parameters, as this solution does) would leak state between separate calls to `solve` unless carefully reset each time -- this solution avoids that entirely by allocating fresh arrays local to each call.

## Wrong approaches and why they fail
- **Union-Find without path compression or union by size ("naive" union-find):** still produces a correct final count, but in the worst case (e.g. always attaching the new root under the same growing chain) each `find` call degrades to `O(n)`, making the whole algorithm `O(n^3)` in the worst case rather than near-`O(n^2)` -- correctness isn't at stake here, only performance, but on `n` near its upper bound of `200` this is the difference between a fast solution and a needlessly slow one.
- **Counting `1`s in the matrix directly (e.g. summing rows) instead of tracking connectivity:** this conflates "how many direct connections exist" with "how many provinces exist" -- a fully connected province of size 3 has many `1` entries in its rows/columns, but is still just *one* province, not three.
- **BFS/DFS with a `visited` boolean array but forgetting to check every unvisited city as a potential new starting point:** if the traversal only starts from city `0` and never restarts from later unvisited cities, disconnected provinces beyond the first one are silently missed, undercounting the answer.

## Variants
- **DFS-based province counting:** for each unvisited city, DFS (or BFS) into every city it's directly connected to (reading a row of the matrix as that city's neighbor list), marking each visited city along the way, and increment a province counter once per DFS call that starts from a fresh unvisited city -- functionally equivalent to the union-find approach for this specific problem, and arguably simpler to write, though it doesn't build the union-find muscle this pattern group is meant to reinforce.
- **LC 684 Redundant Connection** and **LC 721 Accounts Merge** (both in this same folder) reuse the identical union-find skeleton (`find` with path compression, `union` merging two sets) but apply it to a list of edges or shared identifiers instead of a dense adjacency matrix.
- **Given the matrix as a sparse edge list instead of a dense `n x n` matrix:** the union-find logic is unchanged; only the input-scanning loop changes from a nested `i,j` matrix scan to a single pass over the edge list.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | isConnected=[[1,1,0],[1,1,0],[0,0,1]] | 2 | one pair connected, one isolated city |
| 2 | isConnected=[[1,0,0],[0,1,0],[0,0,1]] | 3 | no connections beyond the diagonal, every city isolated |
| 3 | isConnected=[[1,1,1],[1,1,1],[1,1,1]] | 1 | fully connected, single province |
| 4 | isConnected=[[1]] | 1 | single city |
| 5 | isConnected=[[1,0],[0,1]] | 2 | two cities, no connection |
| 6 | isConnected=[[1,1,0,0],[1,1,0,0],[0,0,1,1],[0,0,1,1]] | 2 | two equal-sized separate groups |
| 7 | isConnected=[[1,0,0,1],[0,1,1,0],[0,1,1,0],[1,0,0,1]] | 2 | non-adjacent-index groups (0-3 and 1-2) |
| 8 | isConnected=[[1,1,0,0,0],[1,1,1,0,0],[0,1,1,0,0],[0,0,0,1,1],[0,0,0,1,1]] | 2 | one 3-city chain group plus one 2-city group |
| 9 | isConnected=[[1,0,0,0,0],[0,1,0,0,0],[0,0,1,0,0],[0,0,0,1,0],[0,0,0,0,1]] | 5 | 5x5 identity matrix, all isolated |
| 10 | isConnected=[[1,1,1,1,1,1],[1,1,1,1,1,1],[1,1,1,1,1,1],[1,1,1,1,1,1],[1,1,1,1,1,1],[1,1,1,1,1,1]] | 1 | 6x6 fully connected matrix |
