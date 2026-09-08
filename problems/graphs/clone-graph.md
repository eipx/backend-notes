# Clone Graph
`ref: LC 133` · Difficulty: Medium · Pattern: Graph traversal with an old-to-new node map

## Problem
You're given a reference to one node inside a connected, undirected graph. Each node carries an integer label and a list of references to its directly-connected neighbor nodes. Produce a completely independent deep copy of the entire graph reachable from that starting node: every node in the copy must be a brand-new object (never the same object as any node in the original), the copy's labels must match the original's labels node-for-node, and the copy's connections must mirror the original's connections exactly, so that traversing the copy from its starting node reaches an equivalent, isomorphic graph. If the starting reference itself is empty (an empty graph), the copy is also empty.

## Constraints
- The graph is connected and undirected: every node is reachable from the given start node, and if node A lists node B as a neighbor, node B lists node A back.
- Number of nodes is between `0` and `100` (the empty-graph case is explicitly allowed).
- Node labels are unique integers, one per node, and are only used as data -- the algorithm must not rely on labels to find the graph structure (the actual node objects carry no array index, only object references).
- No self-loops and no repeated edges between the same pair of nodes.

## Worked examples
1. A 4-node cycle `1 - 2 - 3 - 4 - 1` (node 1's neighbors are 2 and 4, node 2's neighbors are 1 and 3, etc.) clones into a separate 4-node cycle with the same labels and the same connection pattern, but every node object is new.
2. A single isolated node with label `1` and an empty neighbor list clones into a single new node also labeled `1` with an empty neighbor list -- no traversal needed beyond the one node.
3. An empty graph (the starting reference is empty) clones to empty -- there is nothing to copy.
4. A star shape (center node 1 connected to leaf nodes 2, 3, 4, each leaf connected only back to 1) clones into the same star shape; the clone of node 1 must have exactly three neighbors, matching the original.

## Edge cases checklist
- Empty graph: starting node is empty -- must return empty immediately, no traversal attempted.
- A single node with no neighbors at all.
- A node that neighbors many other nodes (high-degree hub), to confirm every neighbor gets cloned exactly once and re-used correctly on repeat visits.
- A cycle, where naive DFS/BFS without a visited/old-to-new map would recurse or loop forever bouncing between two already-cloned nodes.
- A graph where two different nodes are visited from multiple different starting neighbors before their own neighbor lists are fully built (needs the map lookup, not a fresh node every time a node is encountered).
- Verifying the clone shares zero node objects with the original (a shallow copy that reuses original node references would pass a value-only check but fail this one).

## Approach
### Brute force
One could try to clone recursively with plain, unguarded DFS: for each node, create a new node, then recurse into every neighbor and create new nodes for those too. Without remembering which original nodes have already been cloned, this revisits the same node repeatedly along every cycle in the graph, and on a graph with any cycle it recurses forever (stack overflow) because node 1 clones node 2 which clones node 1 which clones node 2 forever. Even patching this with a "give up after N recursions" hack cannot produce a correct clone -- the recursion needs a real memory of what's already been built.

### Optimal
Traverse the graph exactly once (BFS or DFS, either works) while keeping a map from each original node object to its already-created clone. Before creating a new node for something, check the map: if the original has already been cloned, reuse that clone object instead of creating a duplicate. Process a queue (or stack) of original nodes; when visiting an original node, walk its neighbor list, and for each neighbor either fetch its existing clone from the map or create-and-register a new clone, then attach that clone to the current node's clone's neighbor list.

**Key invariant:** at any point during the traversal, the map contains an entry for original node `X` if and only if a clone of `X` has already been created (though that clone's own neighbor list may still be incomplete if `X` hasn't been dequeued/processed yet). This guarantees every original node gets exactly one clone object over the whole run, and every edge gets translated exactly once into an edge between the two correct clone objects.

Proof sketch: the map enforces a bijection between original nodes and clone nodes -- "clone exists" is recorded the instant a node is first discovered (not when it's fully processed), so no node is ever cloned twice even if it is reached from multiple neighbors before its own turn comes up. Since the graph is connected, a full BFS/DFS from the start node visits every node exactly once (guarded by the same map, doubling as the visited set), so every node and every edge is copied exactly once, producing a graph isomorphic to the original with entirely new objects.

### Step-by-step trace
Trace cloning the 4-cycle `1-2-3-4-1` (node 1 neighbors [2,4], node 2 neighbors [1,3], node 3 neighbors [2,4], node 4 neighbors [1,3]) with BFS starting at node 1:

| step | queue | oldToNew map (keys by original label) | action |
|---|---|---|---|
| 0 | [1] | {1: clone1} | seed: create clone1, enqueue original node 1 |
| 1 | pop 1 -> [] | {1: clone1, 2: clone2, 4: clone4} | process original 1: for neighbor 2, create clone2, link clone1<->clone2, enqueue 2; for neighbor 4, create clone4, link clone1<->clone4, enqueue 4 |
| 2 | pop 2 -> [4] | {..., 3: clone3} | process original 2: neighbor 1 already in map (reuse clone1, link clone2<->clone1); neighbor 3 not in map, create clone3, link clone2<->clone3, enqueue 3 |
| 3 | pop 4 -> [3] | (no new entries) | process original 4: neighbor 1 already in map (reuse, link clone4<->clone1); neighbor 3 already in map (reuse, link clone4<->clone3) |
| 4 | pop 3 -> [] | (no new entries) | process original 3: neighbor 2 already in map (reuse, link clone3<->clone2); neighbor 4 already in map (reuse, link clone3<->clone4) |
| 5 | empty | done | return clone1 -- a fully-linked 4-cycle of brand-new objects |

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public static Node solve(Node node) {
    if (node == null) {
        return null; // empty graph clones to nothing
    }
    Map<Node, Node> oldToNew = new HashMap<Node, Node>(); // identity-keyed by default Node equals/hashCode
    Deque<Node> queue = new ArrayDeque<Node>(); // ArrayDeque rejects null, fine since we never queue null
    queue.add(node);
    oldToNew.put(node, new Node(node.val));
    while (!queue.isEmpty()) {
        Node current = queue.poll();
        for (Node neighbor : current.neighbors) {
            if (!oldToNew.containsKey(neighbor)) {
                oldToNew.put(neighbor, new Node(neighbor.val)); // first time seeing this original node
                queue.add(neighbor);
            }
            oldToNew.get(current).neighbors.add(oldToNew.get(neighbor)); // wire the clone edge
        }
    }
    return oldToNew.get(node);
}

public static class Node {
    public int val;
    public List<Node> neighbors;

    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }

    public Node(int val) {
        this.val = val;
        neighbors = new ArrayList<Node>();
    }
}
```

## Complexity
- Time: `O(V + E)` -- every node is enqueued and dequeued once (`V`), and every directed adjacency entry is examined once (`2E` for an undirected graph stored as two directed entries per edge).
- Space: `O(V)` -- the `oldToNew` map and the BFS queue each hold at most one entry per node.

## Java 8 pitfalls for this problem
- `ArrayDeque` rejects `null`, which is why the queue only ever holds real `Node` references here; if a caller could pass a graph containing a literal null neighbor entry, `queue.add(null)` would throw `NullPointerException` immediately rather than silently misbehaving.
- Boxed reference types in a `HashMap<Node, Node>` rely on `Node`'s default (identity-based) `equals`/`hashCode` inherited from `Object`, which is exactly what's wanted here (two different original nodes with the same `val` must never be treated as the same key) -- do not add a custom `equals(Object)` override based on `val` to this class, or the map will incorrectly merge distinct nodes that happen to share a label.
- Declare the neighbor list field as `List<Node>` (the interface), not `ArrayList<Node>`, even though it's constructed with `new ArrayList<Node>()` -- declared-type `List` keeps the class usable if the concrete implementation ever needs to change, and matches idiomatic Java 8 field style.
- `new ArrayList<Node>()` needs the explicit type argument on the right-hand side in Java 8; only the diamond `new ArrayList<>()` is allowed as shorthand for a concrete class -- diamond-with-anonymous-subclass is a different, unsupported thing and should never appear here.
- Recursion depth: a recursive DFS clone on a graph with up to 100 nodes is safe, but the same pattern generalized to a `10^4`+ node graph risks `StackOverflowError` on the default JVM thread stack -- prefer the iterative BFS/DFS-with-explicit-stack shown here so the pattern scales without a `-Xss` tweak.
- Forgetting to seed the map entry for the starting node before entering the loop (`oldToNew.put(node, new Node(node.val))`) causes the starting node to be cloned again as soon as some other node lists it as a neighbor, silently producing two different clone objects for the same original start node.

## Wrong approaches and why they fail
- **Recursive DFS with no visited/map guard:** as described in Brute force, any cycle in the graph causes infinite mutual recursion between already-visited nodes, ending in `StackOverflowError`. Even a graph as small as the 3-node triangle example below triggers this.
- **Cloning by label only (a map keyed on the int label) instead of by node object:** this works only while labels are guaranteed unique, which the constraints do promise here, but it breaks the general shape of the pattern -- if this same technique were reused on a graph where node identity is the only reliable key (labels could collide or be missing), keying by label would incorrectly merge distinct nodes. Keying the map by the node object itself is the version of the pattern that always generalizes.
- **Returning references into the original graph instead of newly created nodes:** a "clone" that returns the original node itself (or reuses original neighbor objects while only copying the top-level node) passes a superficial value check but fails the "no shared objects" requirement -- mutating the "clone" would then also mutate the original.

## Variants
- **Directed graph clone:** the same old-to-new map technique works unchanged; the only difference is that neighbor lists represent one-directional edges, so there is no guarantee of a back-edge to reuse, which actually makes the map even more essential (nothing else would catch cross-references between branches).
- **Clone with additional per-node data (e.g. a color or weight field) instead of just an int label:** copy the extra field(s) at the same point the new node is constructed; the traversal and map logic are identical.
- **Serialize/deserialize a graph instead of cloning in-memory:** conceptually the same map-based idea, but the map goes from original node to an integer id (or string token) written to a stream, then rebuilt into new node objects on read-back using the same id-to-node map pattern in reverse.

## Test cases
| # | input (adjacency list, 1-indexed) | expected | what it tests |
|---|---|---|---|
| 1 | [] | empty clone | empty graph |
| 2 | [[]] | single node, no neighbors | smallest non-empty graph |
| 3 | [[2],[1]] | 2-node clone, symmetric edge | minimal cycle-free pair |
| 4 | [[2,3],[1,3],[1,2]] | 3-node triangle clone | smallest cycle (a brute-force DFS would loop forever here) |
| 5 | [[2,4],[1,3],[2,4],[1,3]] | 4-node cycle clone | classic 4-cycle shape |
| 6 | [[2,3,4],[1],[1],[1]] | star clone, center degree 3 | hub node reached from three different neighbors |
| 7 | [[2],[1,3],[2,4],[3,5],[4]] | 5-node path clone | linear chain, two leaf (degree-1) ends |
| 8 | [[2,3],[1,4],[1],[2]] | 4-node tree clone | branching tree shape, no cycle |
| 9 | [[2,6],[1,3],[2,4],[3,5],[4,6],[5,1]] | 6-node cycle clone | larger cycle, more BFS levels |
| 10 | [[2,3,4],[1,3,4],[1,2,4],[1,2,3]] | complete graph K4 clone | dense graph, every pair of nodes connected |

Every case is checked with structural verification: same node count, same per-node label, same neighbor sets by label, and zero shared node objects between original and clone.
