# Course Schedule
`ref: LC 207` · Difficulty: Medium · Pattern: Topological sort (Kahn's algorithm) / DFS 3-color cycle detection

## Problem
There are a fixed number of courses, labeled `0` through `n-1`. A list of prerequisite pairs is given, where a pair `[a, b]` means course `a` cannot be completed until course `b` is completed first (a directed edge from `b` to `a`, "b must come before a"). Multiple prerequisite pairs may share a course, and the same course can appear as both a dependency and a dependent in different pairs.

The output is a single boolean: whether it is possible to complete every course at all, given the prerequisite constraints. "Valid" (i.e. `true`) means the directed graph formed by treating courses as nodes and prerequisites as directed edges contains no cycle -- because a cycle would mean some course indirectly depends on itself, so no valid completion order could ever exist.

## Constraints
- `1 <= numCourses <= 2000`
- `0 <= prerequisites.length <= 5000`
- Each `prerequisites[i] = [a, b]` with `0 <= a, b < numCourses` and `a != b` is not guaranteed by every variant, so a self-loop (`a == b`) should be treated as an immediate cycle rather than assumed away.
- With up to `2000` nodes and `5000` edges, an `O(V + E)` traversal (Kahn's algorithm or DFS) is required; anything that checks all pairs of courses for a dependency path (`O(V^2)` or worse per check) risks `O(V^3)` or more, which is unnecessary and slower than needed at these bounds.

## Worked examples
1. `numCourses = 2`, `prerequisites = [[1,0]]` -> `true`. Course 0 has no prerequisite, so it can be taken first, then course 1; no cycle exists.
2. `numCourses = 2`, `prerequisites = [[1,0],[0,1]]` -> `false`. Course 1 needs course 0 first, but course 0 needs course 1 first -- a direct 2-cycle, so neither can ever be taken first.
3. `numCourses = 3`, `prerequisites = [[0,1],[1,2],[2,0]]` -> `false`. Course 0 depends on 1, which depends on 2, which depends on 0 -- a 3-cycle with no valid starting point.

## Edge cases checklist
- A course with no prerequisites at all (`prerequisites` is empty) -- always `true`, since any order works.
- A single course (`numCourses == 1`) with no prerequisites -- trivially `true`.
- A self-loop, `[0, 0]` -- a course listed as its own prerequisite, which must be detected as a cycle even though it is a "path of length 1."
- Disconnected components: some courses have prerequisite chains, others have none and never appear in `prerequisites` at all.
- A long linear chain of prerequisites (course `i` requires course `i-1`) -- valid, no cycle, but exercises the full traversal depth.
- A "diamond" dependency shape where two courses share a common prerequisite and both feed into a later course -- still valid, tests that in-degree bookkeeping handles multiple incoming edges to the same node correctly.
- Duplicate prerequisite pairs listed more than once -- should not cause a false cycle detection or double-counted in-degree issues beyond simply being redundant edges.

## Approach
### Brute force
For every course, try a DFS from it following prerequisite edges forward and see if that DFS ever revisits the starting course. Doing this independently from every node without memoizing cross-node results is `O(V * (V + E))` in the worst case; at `V=2000, E=5000` that is about `1.4*10^7`, which is actually still fine at these small bounds, but it is wasteful compared to the two standard `O(V+E)` approaches below and does not generalize as cleanly to also producing a valid course order (LC 210's variant).

### Optimal
**Kahn's algorithm (BFS topological sort):** build an adjacency list from prerequisite to dependent (edge `b -> a` for pair `[a, b]`), and compute the in-degree of every node (how many prerequisites it still has outstanding). Seed a queue with every node whose in-degree is `0` (no prerequisites at all -- these can be taken immediately). Repeatedly pop a node, count it as "processed," and decrement the in-degree of each of its dependents; whenever a dependent's in-degree drops to `0`, it becomes newly available and is pushed onto the queue.

**Key invariant:** a node's in-degree reaches `0` and gets pushed onto the queue if and only if every one of its prerequisites has already been processed -- so nodes are always processed in an order consistent with the dependency graph, and the total count of processed nodes at the end equals `numCourses` if and only if the graph is acyclic.

Proof sketch: if the graph is acyclic, every node is part of a valid topological order, and a node's in-degree can only reach `0` after all of its true prerequisites (which, being acyclic, have no dependency back on it) have already been dequeued -- so Kahn's algorithm will eventually process every node. Conversely, if there is a cycle, every node in that cycle always has at least one prerequisite (its predecessor in the cycle) that itself depends -- directly or transitively -- on some other node in the same cycle, so no node in the cycle can ever be the *first* one processed among the cycle; the whole cycle's in-degrees stay stuck above `0` forever, and those nodes are never dequeued, so the processed count falls short of `numCourses`, correctly signaling a cycle.

An equivalent alternative is **DFS with 3-color marking**: mark each node `white` (unvisited), `gray` (currently on the recursion stack, i.e. "being explored right now"), or `black` (fully explored, safe). DFS from any white node; if a gray node is ever encountered again during that DFS, a back-edge exists -- a cycle. If a DFS run completes without hitting gray, mark the whole path black and move to the next unvisited node.

### Step-by-step trace
Trace Kahn's algorithm on `numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]` (edges: `0->1`, `0->2`, `1->3`, `2->3`):

| step | in-degree array [0,1,2,3] | queue | processed count | action |
|---|---|---|---|---|
| init | [0,1,1,2] | [0] | 0 | seed queue with in-degree-0 nodes |
| 1 | [0,0,0,2] | [1,2] | 1 | pop 0, decrement 1 and 2 to 0, push both |
| 2 | [0,0,0,1] | [2,3?no] | 2 | pop 1, decrement 3 to 1 (not yet 0) |
| 3 | [0,0,0,0] | [3] | 3 | pop 2, decrement 3 to 0, push 3 |
| 4 | [0,0,0,0] | [] | 4 | pop 3, nothing to decrement |
| -- | -- | -- | 4 == numCourses | return true |

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public static boolean solve(int numCourses, int[][] prerequisites) {
    List<List<Integer>> adjacency = new ArrayList<List<Integer>>();
    for (int i = 0; i < numCourses; i++) {
        adjacency.add(new ArrayList<Integer>()); // declared type List, concrete type ArrayList (Java 8 style)
    }
    int[] inDegree = new int[numCourses];
    for (int[] pair : prerequisites) {
        int course = pair[0], prereq = pair[1];
        adjacency.get(prereq).add(course); // edge prereq -> course
        inDegree[course]++; // course now needs one more prerequisite satisfied
    }

    Deque<Integer> queue = new ArrayDeque<Integer>(); // ArrayDeque rejects null; boxed Integer is fine here
    for (int i = 0; i < numCourses; i++) {
        if (inDegree[i] == 0) {
            queue.add(i); // no prerequisites at all: can be taken immediately
        }
    }

    int processed = 0;
    while (!queue.isEmpty()) {
        int cur = queue.poll();
        processed++;
        for (int next : adjacency.get(cur)) {
            inDegree[next]--; // one of next's prerequisites is now satisfied
            if (inDegree[next] == 0) {
                queue.add(next); // next just became available
            }
        }
    }

    return processed == numCourses; // fewer means some courses are stuck in a cycle
}
```

## Complexity
- Time: `O(V + E)` -- building the adjacency list and in-degree array is `O(E)`, and Kahn's BFS visits every node once and every edge once.
- Space: `O(V + E)` -- the adjacency list stores every edge once, plus `O(V)` for the in-degree array and queue.

## Java 8 pitfalls for this problem
- Declared type should be `List<List<Integer>>` (interface) with concrete `new ArrayList<Integer>()` instances -- this is the standard "program to the interface" idiom; avoid declaring the field as `ArrayList<List<Integer>>` even though it would compile, since it needlessly locks the variable to a concrete type.
- `ArrayDeque<Integer>` autoboxes `int` course indices into `Integer`. This is fine functionally, but comparing two `Integer` values (e.g. in a test harness comparing expected vs actual course indices) must use `.equals()` or `Integer.compare()`, never `==`, since `Integer` caching only guarantees reference equality for values in `[-128, 127]` and course indices can exceed that in larger inputs (though not at `numCourses <= 2000` for *this specific* range being safe is a coincidence, not something to rely on).
- A self-loop `[0, 0]` must be handled correctly by the in-degree/queue logic without any special-casing: node 0 gets `inDegree[0] = 1` from its own self-edge, so it never starts in the initial zero-in-degree queue, and it can never be dequeued to decrement its own in-degree back to 0 -- the algorithm naturally reports a cycle without needing an explicit `if (a == b) return false` check, but it is worth verifying this rather than assuming it.
- `new int[][]{{1,0}}` and similar 2-D int array literals are the correct Java 8 syntax for constructing `prerequisites` inline in test cases; do not attempt `int[][] x = {{1,0}}` initializer-shorthand outside of a declaration statement (Java only allows the brace-only shorthand at the point of variable declaration, not as a standalone expression or method argument).
- No diamond operator with an anonymous class (`new Comparator<int[]>(){}` requires the explicit type parameter, not `new Comparator<>(){}`, in Java 8) -- not needed for this particular problem's canonical solution, but relevant if a variant sorts courses by some tiebreak.

## Wrong approaches and why they fail
- **Just checking whether `prerequisites.length >= numCourses` as a cycle heuristic:** for `numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]]` there are 4 edges and 4 courses, yet the graph is perfectly acyclic (a diamond shape) -- edge count alone says nothing about cyclicity.
- **DFS without a gray/black distinction, using only a single "visited" set:** for `numCourses=3, prerequisites=[[0,1],[1,2],[2,0]]`, a DFS from node 0 that marks nodes visited permanently the moment they are first reached (rather than distinguishing "currently on this path" from "fully done") can wrongly conclude no cycle exists if, say, node 2 was already fully explored via some other unrelated starting point before the cycle-completing edge back to 0 is checked -- the 3-color scheme (or, equivalently for Kahn's algorithm, in-degree tracking) is required to correctly distinguish a back-edge (cycle) from a cross-edge (no cycle) in a directed graph.
- **Reversing the edge direction by mistake (building `course -> prereq` instead of `prereq -> course`):** this still detects *some* cycles (since a cycle looks the same reversed), but the in-degree seeding for Kahn's algorithm and the resulting queue order come out backwards; for asymmetric graphs like `[[1,0],[2,0],[3,1],[3,2]]` this changes which nodes start with in-degree 0, and can not just misorder but sometimes miscount reachability if combined incorrectly with other logic (e.g. if only DFS reachability from a fixed start node is checked instead of full-graph in-degree accounting).

## Variants
- **LC 210 Course Schedule II -- return one valid completion order, or an empty array if impossible:** identical Kahn's algorithm, but append each node to a result list as it is dequeued instead of just counting it; if the final list's size is less than `numCourses`, return an empty array instead.
- **Return whether a *specific* course is reachable from another (a query, not a full ordering):** this reduces to a single-source reachability check (BFS/DFS from the queried prerequisite) rather than a full topological sort of the whole graph.
- **Minimum number of "semesters" needed if unlimited courses can be taken in parallel per semester, subject to prerequisites:** this is Kahn's algorithm again, but instead of a plain FIFO queue, process the queue in full levels (like the level-snapshot BFS used for Rotting Oranges in this folder) and count the number of levels needed to drain it.

## Test cases
| # | input | expected | what it tests |
|---|---|---|---|
| 1 | numCourses=2, prerequisites=[[1,0]] | true | simple linear dependency |
| 2 | numCourses=2, prerequisites=[[1,0],[0,1]] | false | direct 2-cycle |
| 3 | numCourses=1, prerequisites=[] | true | single course, no prerequisites |
| 4 | numCourses=1, prerequisites=[[0,0]] | false | self-loop |
| 5 | numCourses=3, prerequisites=[[1,0],[2,1]] | true | linear chain of 3 |
| 6 | numCourses=4, prerequisites=[[1,0],[2,0],[3,1],[3,2]] | true | diamond dependency shape |
| 7 | numCourses=3, prerequisites=[[0,1],[1,2],[2,0]] | false | 3-node cycle |
| 8 | numCourses=5, prerequisites=[] | true | multiple disconnected courses, no edges at all |
| 9 | numCourses=2, prerequisites=[[0,1]] | true | single edge, opposite direction from case 1 |
| 10 | numCourses=6, prerequisites=[[1,0],[2,0],[3,1],[3,2],[4,3],[5,4]] | true | longer acyclic chain with a merge point |
