# Word Ladder
`ref: LC 127` · Difficulty: Hard · Pattern: BFS over an implicit word graph, generic-pattern buckets

## Problem
You're given a starting word, a target word, and a dictionary of words (all the same fixed length, all lowercase letters). A "transformation" changes exactly one letter of the current word to produce a new word that must also appear in the dictionary. Find the fewest number of words needed to go from the start word to the target word, counting both the start and target words themselves, where every intermediate word along the way is drawn from the dictionary. If the target word is not in the dictionary, or no such chain of transformations exists at all, the answer is `0`.

## Constraints
- All words (start, target, and every dictionary word) have the same length, between `1` and `10` letters.
- The dictionary can hold up to `5000` words, all distinct, all lowercase English letters.
- The start word does not need to appear in the dictionary itself, but the target word must appear in the dictionary or the answer is automatically `0`.
- Multiple valid shortest chains may exist; only the chain's *length* is required, not the actual sequence of words.

## Worked examples
1. start=`"hit"`, end=`"cog"`, dict=`{"hot","dot","dog","lot","log","cog"}` -> `5`. One shortest chain is `hit -> hot -> dot -> dog -> cog`, which has 5 words total (start, three intermediates, end).
2. start=`"hit"`, end=`"cog"`, dict=`{"hot","dot","dog","lot","log"}` -> `0`. The target `"cog"` never appears in the dictionary, so no valid chain can end there at all.
3. start=`"a"`, end=`"c"`, dict=`{"a","b","c"}` -> `2`. Single-letter words differ by exactly one letter as soon as they differ at all, so `a` and `c` are directly one transformation apart: chain is `a -> c`.
4. start=`"hot"`, end=`"dog"`, dict=`{"hot","cog"}` -> `0`. `"hot"` and `"dog"` differ in two letter positions (`h`->`d` and `t`->`g`), and the dictionary has no intermediate word bridging that gap (`"cog"` doesn't connect to `"hot"` in one step either), so no chain exists.

## Edge cases checklist
- Target word absent from the dictionary -- return `0` immediately without searching.
- Start word already equal to the target word (not part of the stated constraints here, but worth handling defensively: the chain length would be `1`).
- Start word already present in the dictionary vs. not present at all -- both must work identically, since the start word is only ever used as the BFS seed, never required to be a dictionary member.
- A dictionary containing words that are never reachable from the start word at all (isolated dictionary "islands"), which should simply not affect the answer for words that *are* reachable.
- Words of length `1`, where "one letter different" means "completely different" (no shared letters possible other than being identical).
- A dictionary large enough that comparing every pair of words directly (`O(dict^2)`) would be noticeably more expensive than using the generic-pattern bucketing technique.
- Exactly one transformation needed (start and end differ by exactly one letter and end is in the dictionary) -- shortest possible non-trivial chain, length `2`.

## Approach
### Brute force
Treat every pair of words (from `{start} union dict`) as potentially connected if they differ in exactly one letter position, build that full adjacency relationship by comparing every word against every other word letter-by-letter, and then run BFS over the resulting graph. Comparing all pairs costs `O(dict^2 * wordLength)`, which becomes expensive fast: with up to `5000` words of length up to `10`, that's up to `5000 * 5000 * 10 = 250,000,000` character comparisons just to build the graph, before any traversal even starts.

### Optimal
Avoid ever comparing word-pairs directly. For every word in the dictionary, generate all of its "wildcard patterns" -- one pattern per letter position, with that position replaced by a placeholder character (e.g. `*`), so a 3-letter word like `"hot"` produces the patterns `"*ot"`, `"h*t"`, and `"ho*"`. Group dictionary words into buckets keyed by these patterns (`Map<String, List<String>>`, built with `computeIfAbsent`): any two words sharing a bucket are automatically exactly "one letter apart" (or identical), because they agree on every position outside the wildcard slot. Then run standard level-by-level BFS starting from the start word: at each word, generate its own wildcard patterns, look up the dictionary words sharing each pattern (these are exactly its one-letter-away neighbors), and enqueue any not yet visited.

**Key invariant:** two words belong to the same pattern bucket for pattern `P` if and only if they are identical outside `P`'s wildcard position -- which is exactly the "differs in at most one letter" adjacency relation the problem needs (skipping only the degenerate case where the two words are the literal same string, which BFS's visited-set naturally already excludes from re-processing). Because BFS explores words in order of non-decreasing transformation-distance from the start, the level at which the end word is first dequeued equals the minimum chain length.

Proof sketch: building the pattern map costs `O(dict * wordLength)` total (each word contributes exactly `wordLength` pattern entries), and each BFS step for a given word also costs `O(wordLength)` pattern lookups, each returning a bucket whose combined size across the whole run is bounded by the total number of (word, pattern) pairs. This replaces the brute force's pairwise character comparison with a hash-map lookup, so the overall work becomes `O(dict * wordLength^2)` in the worst case (accounting for substring/pattern-string construction), a large improvement over `O(dict^2 * wordLength)` once the dictionary is large relative to word length. Since the pattern-sharing relation exactly matches "one letter apart," BFS over it computes the true shortest transformation chain.

### Step-by-step trace
Trace on start=`"hit"`, end=`"cog"`, dict=`{"hot","dot","dog","lot","log","cog"}` (word length 3):

Pattern buckets built from the dictionary: `*ot -> [hot, dot, lot]`, `h*t -> [hot]`, `ho* -> [hot]`, `d*t -> [dot]`, `do* -> [dot, dog]`, `*og -> [dog, cog]`, `l*t -> [lot]`, `lo* -> [lot, log]`, `*t -> ...` (only 3-letter patterns shown for brevity), `d*g -> [dog]`, `l*g -> [log]`, `c*g -> [cog]`, `co* -> [cog]`.

| step (BFS level) | queue at start of level | word dequeued | patterns checked | new neighbors found |
|---|---|---|---|---|
| 1 | [hit] | hit | `*it`, `h*t`, `hi*` | hot (via `h*t`) |
| 2 | [hot] | hot | `*ot`, `h*t`, `ho*` | dot, lot (via `*ot`; hot itself skipped as already visited) |
| 3 | [dot, lot] | dot | `*ot`, `d*t`, `do*` | dog (via `do*`) |
| -- | -- | lot | `*ot`, `l*t`, `lo*` | log (via `lo*`) |
| 4 | [dog, log] | dog | `*og`, `d*g`, `do*` | cog (via `*og`) |
| -- | -- | log | `*og`, `l*g`, `lo*` | (cog already discovered this level) |
| 5 | [cog] | cog | -- (dequeued word equals end) | return `5` |

## Java 8 solution
```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public static int solve(String beginWord, String endWord, List<String> wordList) {
    Set<String> dict = new HashSet<String>(wordList);
    if (!dict.contains(endWord)) {
        return 0; // target unreachable by definition if it isn't even in the dictionary
    }
    int len = beginWord.length();
    Map<String, List<String>> patternMap = new HashMap<String, List<String>>();
    for (String word : dict) {
        for (int i = 0; i < len; i++) {
            String pattern = word.substring(0, i) + "*" + word.substring(i + 1);
            patternMap.computeIfAbsent(pattern, k -> new ArrayList<String>()).add(word);
        }
    }

    Set<String> visited = new HashSet<String>();
    Deque<String> queue = new ArrayDeque<String>(); // ArrayDeque rejects null; words are never null here
    queue.add(beginWord);
    visited.add(beginWord);
    int steps = 1; // counts words, so the seeded beginWord itself is step 1
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        for (int i = 0; i < levelSize; i++) {
            String word = queue.poll();
            if (word.equals(endWord)) {
                return steps;
            }
            for (int j = 0; j < len; j++) {
                String pattern = word.substring(0, j) + "*" + word.substring(j + 1);
                List<String> neighbors = patternMap.getOrDefault(pattern, new ArrayList<String>());
                for (String next : neighbors) {
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }
        steps++;
    }
    return 0; // queue drained without ever reaching endWord
}
```

## Complexity
- Time: `O(dict * len^2)` -- building the pattern map does `O(dict * len)` substring constructions, each costing `O(len)`; BFS itself visits each word once and does `O(len)` pattern lookups per word, each lookup costing `O(len)` to build the pattern string.
- Space: `O(dict * len)` -- the pattern map stores `len` entries per dictionary word, and the visited set/queue hold at most `dict` words.

## Java 8 pitfalls for this problem
- `Map.computeIfAbsent(pattern, k -> new ArrayList<String>())` is the idiomatic Java 8 way to build the adjacency (bucket) map without a manual `containsKey`/`put` dance -- but the lambda always allocates a *new* `ArrayList` even on the "already present" path unless the map already contains the key, so this is safe and efficient, not wasteful, since `computeIfAbsent` only invokes the function when the key is truly missing.
- `ArrayDeque` rejects `null` elements; since dictionary words and the begin/end words are always non-null strings here, this never becomes an issue, but it is a reason to avoid using a `null` sentinel to mark "end of BFS level" (use the `queue.size()` snapshot trick instead, exactly as in Rotting Oranges).
- Declared-type discipline: `List<String> neighbors = patternMap.getOrDefault(...)` uses the `List` interface type, not `ArrayList<String>`, even though the map's values are concretely `ArrayList`s -- keeps the code flexible and matches idiomatic Java 8 style.
- Off-by-one on the step counter: this solution counts *words* (steps starts at `1` for the seeded `beginWord`), matching the problem's definition of chain length as "number of words in the chain." A common bug is to instead count *transformations* (edges), which would need `steps` to start at `0`, and mixing the two conventions silently returns an answer one too low or one too high.
- Recursion depth is a non-issue here since the solution is iterative BFS, but a naive recursive-DFS-with-backtracking version of this same problem (trying every possible one-letter-different next word) risks both stack overflow and exponential blowup on a dense dictionary -- this is exactly why BFS, not DFS, is the right traversal for shortest-path questions.
- Rebuilding the `dict` as a `HashSet<String>` up front (rather than repeatedly calling `wordList.contains(...)`, which is `O(n)` per call on a `List`) is what keeps membership checks `O(1)`; forgetting this and using the raw `List<String>` for `contains` checks silently turns the whole algorithm quadratic in the dictionary size.

## Wrong approaches and why they fail
- **Comparing every word to every other word directly to build the adjacency graph (the brute force):** functionally correct, but does `O(dict^2 * len)` work just to build the graph, which becomes the dominant cost for large dictionaries -- the pattern-bucket trick sidesteps this by expressing "one letter apart" as "shares a wildcard pattern," turning pairwise comparison into hash-map grouping.
- **DFS instead of BFS:** DFS can find *some* chain from start to end, but with no guarantee it's the *shortest* one -- it might wander down a long chain before backtracking to find a shorter one, and without extra bookkeeping to track and compare multiple full paths, it cannot answer a shortest-path question correctly in general.
- **Forgetting to check whether `endWord` is even in the dictionary before searching:** without this early check, a BFS that never happens to visit `endWord` (because it was never added to any pattern bucket in the first place, since the pattern map is only built from dictionary words) will still correctly return `0` once the queue drains -- so this particular omission happens to still work here, but only because the pattern map is dictionary-driven; a version of this code that tried to special-case `endWord` outside the dictionary would need much more care.

## Variants
- **Bidirectional BFS:** instead of growing one BFS frontier from `beginWord` all the way to `endWord`, grow two frontiers simultaneously -- one from `beginWord`, one from `endWord` -- and always expand whichever frontier is currently smaller. The two frontiers together cover much less "search volume" than one single-direction BFS reaching the same total depth, because the branching factor is applied over half the distance from each side instead of the full distance from one side; this is a common way to speed up shortest-path search once the graph's branching factor is high, without changing the correctness argument (the answer is still found the moment the two frontiers meet).
- **Word Ladder II (return every shortest chain, not just the length):** requires tracking, for each visited word, the set of predecessor words that reached it at the shortest distance (since multiple different one-letter-away predecessors can tie for shortest), then reconstructing all paths backward from `endWord` once BFS completes -- a strictly harder bookkeeping problem layered on the same BFS skeleton.
- **Weighted transformations (some letter substitutions cost more than others):** plain BFS's level-counting no longer directly gives the answer once edges have different costs; that variant needs Dijkstra's algorithm with a priority queue keyed on accumulated transformation cost.

## Test cases
| # | beginWord | endWord | wordList | expected | what it tests |
|---|---|---|---|---|---|
| 1 | hit | cog | [hot,dot,dog,lot,log,cog] | 5 | standard multi-step chain |
| 2 | hit | cog | [hot,dot,dog,lot,log] | 0 | end word missing from dictionary |
| 3 | a | c | [a,b,c] | 2 | 1-letter words, direct one-step chain |
| 4 | hot | dog | [hot,cog] | 0 | no valid intermediate bridges a 2-letter difference |
| 5 | hot | dog | [hot,dog,dot] | 3 | short chain through one intermediate word |
| 6 | cat | bat | [bat] | 2 | single-letter substitution, minimal non-trivial chain |
| 7 | cat | dog | [cat,cot,cog,dog] | 4 | chain requiring three transformations |
| 8 | qa | rl | [ql,rl] | 3 | 2-letter words, chain through one intermediate |
| 9 | cold | warm | [cold,cord,card,ward,warm] | 5 | longer 4-letter chain, several substitutions |
| 10 | dog | dot | [dot] | 2 | single-step chain, begin word absent from dictionary |
