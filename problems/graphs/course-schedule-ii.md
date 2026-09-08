# Course Schedule II
`ref: LC 210` · Difficulty: Medium · Pattern: Topological sort (Kahn's algorithm, indegree-driven BFS)

## Problem
There are a fixed number of courses, numbered `0` through `numCourses - 1`, and a list of prerequisite pairs; a pair `[course, prereq]` means `prereq` must be completed at some point before `course`. Produce one valid ordering in which all courses could be completed while respecting every prerequisite pair. If the prerequisites are contradictory -- there is no way to order the courses at all because some subset of courses depends on itself in a cycle -- return an empty array instead.

## Constraints
- `1 <= numCourses <= 2000` courses, numbered contiguously from `0`.
- Up to `5000` prerequisite pairs, each `[a, b]` with `a != b` (no course directly requires itself).
- The same prerequisite pair will not be listed more than once, but a course can have any number of prerequisites, and any number of other courses can depend on it.
- If multiple valid orderings satisfy every prerequisite, any one of them is an acceptable answer -- there is no single "correct" ordering to match exactly.

## Worked examples
1. `numCourses = 2`, `prerequisites = [[1,0]]` -> `[0,1]`. Course `1` needs course `0` first, so `0` must appear before `1`.
2. `numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]` -> `[0,1,2,3]` (one valid order). Course `0` has no prerequisites and can go first; courses `1` and `2` both only need `0`; course `3` needs both `1` and `2` done first.
3. `numCourses = 2`, `prerequisites = [[1,0],[0,1]]` -> `[]`. Course `1` needs `0`, but `0` also needs `1` -- neither can ever go first, so no valid order exists.
4. `numCourses = 3`, `prerequisites = []` -> `[0,1,2]` (one valid order). With no prerequisites at all, any permutation of the three courses is valid; simply listing them in numeric order is one acceptable answer.

## Edge cases checklist
- No prerequisites at all -- every ordering is valid; the courses can be returned in their natural numeric order.
- A single course with no prerequisites (`numCourses == 1`, empty prerequisite list) -- trivially `[0]`.
- A direct two-course cycle (`a` needs `b` and `b` needs `a`) -- smallest possible cycle, must return `[]`.
- A longer cycle spanning three or more courses, where no two individual courses directly reference each other but the chain wraps back around.
- A graph that is a straight-line chain of dependencies (course `i` requires course `i-1` for every `i`) -- exercises a long single path rather than a wide, shallow dependency tree.
- A "diamond" dependency shape, where two courses share a common prerequisite and both are in turn required by a later course -- confirms indegree counting handles a course with more than one incoming edge correctly.
- A prerequisite pair given in the reverse order convention by mistake (`[prereq, course]` instead of `[course, prereq]`) is NOT a case to test for -- the pair format is fixed by the problem; instead, make sure the code reads `pair[0]` as the course and `pair[1]` as its prerequisite consistently, since swapping them silently reverses the entire graph's edge direction.

## Approach
### Brute force
Try every permutation of the `numCourses` courses, and for each one check whether it satisfies every prerequisite pair (i.e., for each pair, the prerequisite's position in the permutation comes before the course's position). This is `O(numCourses!)`, catastrophically slow beyond a tiny handful of courses, and it also doesn't cleanly detect "no valid order exists" except by exhausting every permutation and finding none that work.

### Optimal
Kahn's algorithm: build a directed graph where an edge points from each prerequisite to the course that needs it, and track each course's indegree (how many prerequisites it still has outstanding). Seed a queue with every course that currently has indegree `0` (no prerequisites left, so it can go first). Repeatedly pop a course from the queue, append it to the result order, and "complete" it by decrementing the indegree of every course that lists it as a prerequisite; any course whose indegree drops to `0` as a result becomes newly available and is added to the queue. If the final result order contains every course, it's a valid topological order; if the queue empties early (fewer than `numCourses` courses were ever added to the result), a cycle exists among the remaining courses, and the answer is an empty array.

**Key invariant:** a course is only ever added to the queue once its indegree reaches exactly `0`, which happens if and only if every one of its prerequisites has already been appended to the result order -- so the resulting order always lists every prerequisite of a course strictly before that course.

Proof sketch: indegree counts the number of not-yet-satisfied prerequisites for a course; it strictly decreases only when a prerequisite is completed (popped from the queue and processed), so a course reaches indegree `0` precisely when all its prerequisites are already in the result. This produces a valid topological order by construction whenever one exists. If a true cycle exists among some subset of courses, every course in that subset always has at least one still-outstanding prerequisite from within the same cycle, so none of them can ever reach indegree `0` -- they are permanently stuck out of the queue, and the algorithm correctly terminates with fewer than `numCourses` courses processed, signaling the cycle.

### Step-by-step trace
Trace on `numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]` (edges: 0->1, 0->2, 1->3, 2->3; indegree: 0:0, 1:1, 2:1, 3:2):

| step | queue at start | course popped | order so far | indegree updates |
|---|---|---|---|---|
| 1 | [0] | 0 | [0] | indegree[1]: 1->0 (enqueue 1); indegree[2]: 1->0 (enqueue 2) |
| 2 | [1, 2] | 1 | [0,1] | indegree[3]: 2->1 (not yet 0, stays out) |
| 3 | [2] | 2 | [0,1,2] | indegree[3]: 1->0 (enqueue 3) |
| 4 | [3] | 3 | [0,1,2,3] | no outgoing edges from 3 |
| -- | empty | -- | length 4 == numCourses | return [0,1,2,3] |

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public static int[] solve(int numCourses, int[][] prerequisites) {
    List<List<Integer>> adjacency = new ArrayList<List<Integer>>();
    for (int i = 0; i < numCourses; i++) {
        adjacency.add(new ArrayList<Integer>());
    }
    int[] indegree = new int[numCourses];
    for (int[] pre : prerequisites) {
        int course = pre[0], prereq = pre[1];
        adjacency.get(prereq).add(course); // edge points from prerequisite to the course needing it
        indegree[course]++;
    }

    Deque<Integer> queue = new ArrayDeque<Integer>(); // ArrayDeque rejects null; boxed Integer values are fine
    for (int i = 0; i < numCourses; i++) {
        if (indegree[i] == 0) {
            queue.add(i);
        }
    }

    int[] order = new int[numCourses];
    int idx = 0;
    while (!queue.isEmpty()) {
        int course = queue.poll();
        order[idx++] = course;
        for (int next : adjacency.get(course)) {
            indegree[next]--;
            if (indegree[next] == 0) {
                queue.add(next);
            }
        }
    }
    return idx == numCourses ? order : new int[0]; // fewer than numCourses processed means a cycle exists
}
```

## Complexity
- Time: `O(V + E)` -- building the adjacency list and indegree array is `O(V + E)`, and the main loop visits every course once and every edge once across all its iterations.
- Space: `O(V + E)` -- the adjacency list stores each edge once, plus `O(V)` for the indegree array, queue, and result array.

## Java 8 pitfalls for this problem
- Building the adjacency list with `new ArrayList<Integer>()` per course (not the diamond-with-anonymous-class form, and not skipping initialization) is required before calling `.add(...)` on any `adjacency.get(i)` -- a `List<List<Integer>>` created via `new ArrayList<List<Integer>>()` starts empty, so the `numCourses` inner lists must be added in a loop first, or every `adjacency.get(i)` throws `IndexOutOfBoundsException`.
- Boxed `Integer` values placed into an `ArrayDeque<Integer>` autobox/unbox transparently, but comparing two boxed `Integer`s with `==` instead of `.equals()` (or unboxing to `int` first) is a classic trap -- this solution avoids it entirely by declaring `indegree` and `order` as primitive `int[]`, sidestepping boxed comparison altogether.
- The pair convention matters: `pre[0]` is the course, `pre[1]` is its prerequisite (matching how the problem defines `[a, b]` meaning "to take `a`, first take `b`") -- accidentally swapping which index is read as the "course" vs. the "prerequisite" reverses every edge in the graph and produces a nonsensical (or falsely cyclic) result.
- `idx == numCourses` is the correct cycle-detection check, not `queue.isEmpty()` alone -- the queue always ends up empty (the `while` loop's own exit condition guarantees that), so checking queue emptiness tells you nothing about whether a cycle was skipped; only counting how many courses were actually appended to `order` reveals a cycle.
- Recursion depth: an alternative DFS-based topological sort (see Variants) recurses one call per course along the deepest dependency chain; with `numCourses` up to `2000`, a straight-line dependency chain risks approaching default JVM recursion limits in a way this iterative, indegree-driven BFS never does.
- Declare `adjacency` as `List<List<Integer>>` (interface types both levels), not `ArrayList<ArrayList<Integer>>` -- consistent with the "declared-type List vs ArrayList" convention used throughout this folder.

## Wrong approaches and why they fail
- **DFS without cycle tracking, just marking nodes visited and appending in post-order:** post-order DFS (append a node to the result *after* recursing into all its neighbors, then reverse the result at the end) does compute a valid topological order on an acyclic graph, but without a separate "currently on the recursion stack" marker (distinct from "ever visited"), it cannot detect a cycle -- it would either infinite-loop or silently produce an incomplete/incorrect order on cyclic input. The three-color (white/gray/black) DFS variant described below fixes this.
- **Sorting courses by number of prerequisites, or by course number directly:** neither reflects actual dependency order -- two courses can have the same prerequisite count but need to appear in a specific relative order (or no order at all if unrelated), and course numbers carry no ordering guarantee whatsoever; for `prerequisites=[[1,0],[2,0],[3,1],[3,2]]`, sorting by course number happens to work here by coincidence, but changing the numbering (e.g. relabeling course `0` as `3` and vice versa) breaks it immediately since numeric order carries no dependency information.
- **Only checking indegree without decrementing it as courses are "completed":** this can't distinguish between "this course currently has 2 outstanding prerequisites" and "this course originally had 2 prerequisites, both already satisfied" -- the whole point of the mutable indegree array is to track *remaining* prerequisites as the algorithm progresses, not the original count.

## Variants
- **DFS-color topological sort:** mark every course white (unvisited) initially; when DFS visits a course, mark it gray (in progress, on the current recursion stack) before recursing into its neighbors, then mark it black (fully processed) and prepend it to the result order once all neighbors are done. If DFS ever encounters a gray node (one that's an ancestor of itself in the current recursion), that's a back-edge, proving a cycle exists. This produces the same class of valid orderings as Kahn's algorithm but via recursion instead of an indegree queue, and it naturally detects cycles as part of the traversal rather than as a final count check.
- **Course Schedule I (LC 207, "can all courses be finished?"):** the exact same Kahn's-algorithm skeleton, but only needs a boolean answer (`idx == numCourses`) rather than the actual order array.
- **Minimum semesters to finish all courses:** the same indegree/BFS structure as here, but processed one full "level" at a time (like Rotting Oranges' `levelSize` snapshot) -- each level represents one semester's worth of courses that can be taken simultaneously, and the number of levels processed is the answer.

## Test cases
| # | numCourses | prerequisites | expected | what it tests |
|---|---|---|---|---|
| 1 | 2 | [[1,0]] | [0,1] | minimal single dependency |
| 2 | 4 | [[1,0],[2,0],[3,1],[3,2]] | [0,1,2,3] | diamond dependency shape |
| 3 | 2 | [[1,0],[0,1]] | [] | smallest possible cycle |
| 4 | 1 | [] | [0] | single course, no prerequisites |
| 5 | 3 | [] | [0,1,2] | no prerequisites at all, natural order |
| 6 | 6 | [[1,0],[2,1],[3,2],[4,3],[5,4]] | [0,1,2,3,4,5] | long linear dependency chain |
| 7 | 4 | [[1,0],[2,1],[3,2],[1,3]] | [] | 3-course cycle (1 -> 2 -> 3 -> 1) hidden among indirect edges |
| 8 | 5 | [[1,0],[2,0],[3,1],[4,2],[4,3]] | [0,1,2,3,4] | wider diamond, one course with two prerequisites |
| 9 | 3 | [[0,1],[1,2],[2,0]] | [] | cycle spanning all three courses |
| 10 | 2 | [[0,1]] | [1,0] | single dependency in the reverse numeric direction |
