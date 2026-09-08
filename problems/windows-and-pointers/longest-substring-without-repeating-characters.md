# Longest Substring Without Repeating Characters
`ref: LC 3` · Difficulty: Medium · Pattern: variable-size sliding window with a last-seen index map

## Problem

Given a string `s`, find the length of the longest contiguous run of characters in which no character appears twice. "Contiguous" means an unbroken slice of the original string (not a subsequence with gaps). "Valid" means every character inside the slice is distinct from every other character inside that same slice. Return a single integer: the length of the longest such slice. You do not need to return the slice itself, only its length.

Input: a string `s` (may be empty, may contain letters, digits, spaces, or punctuation).
Output: a non-negative integer, the length of the longest repeat-free contiguous slice.

## Constraints

- `0 <= s.length <= 5 * 10^4`
- `s` consists of printable ASCII characters (letters, digits, symbols, space).
- The bound of 5*10^4 rules out anything worse than roughly O(n log n); an O(n^2) brute force (about 2.5 billion character comparisons at the top end) will time out, so the expected solution is O(n) or O(n log n).

## Worked examples

1. Input: `"abcabcbb"` → Output: `3`. The longest repeat-free slice is `"abc"` (or any of its shifted repeats); as soon as a second `"a"` appears the window must drop the first `"a"`, and no window longer than 3 avoids a repeat here.
2. Input: `"bbbbb"` → Output: `1`. Every character is `"b"`, so the moment the window holds two characters it already has a repeat; the best possible window is a single character.
3. Input: `"pwwkew"` → Output: `3`. The best slice is `"wke"` (length 3). Note that `"pwke"` is NOT contiguous in the original string (there's a second `"w"` sitting between them), so it does not qualify at all even though its characters are all distinct.
4. Input: `"dvdf"` → Output: `3`. The best slice is `"vdf"`. This is the classic trap case: the first `"d"` (index 0) is already outside the window by the time the second `"d"` (index 2) shows up, so the window start must NOT jump back to right after index 0 — it should stay put, then the second `"d"` forces the start forward to index 1.

## Edge cases checklist

- Empty string (`""`) → answer is `0`.
- Single character string → answer is `1`.
- All characters identical (`"aaaa..."`) → answer is `1`.
- All characters distinct already → answer is `s.length()`.
- A repeated character whose earlier occurrence is now stale (already outside the current window) — must not incorrectly shrink the window using that stale position.
- Repeats that are adjacent (`"aa"`) vs. far apart (`"a...a"`).
- Case sensitivity: `'A'` and `'a'` are different characters.
- Non-letter characters (spaces, digits, punctuation) must be treated like any other character.
- Maximum length input (5*10^4 chars) for a performance sanity check.

## Approach

### Brute force

Check every possible contiguous slice `s[i..j]` and, for each, scan it to see whether all characters are distinct (e.g. by dropping them into a set one at a time and bailing out on the first collision). There are O(n^2) slices and checking each one costs up to O(n), giving O(n^3) in the worst case, or O(n^2) if you incrementally extend a set as `j` grows for a fixed `i`. At n = 5*10^4, even the O(n^2) version is on the order of 2.5 billion primitive operations — far too slow for a solution that needs to run in around a second.

### Optimal

Maintain a window `[windowStart, windowEnd]` that always represents a valid (repeat-free) slice, and a map from character to the last index at which it was seen. Slide `windowEnd` forward one step at a time; whenever the character at `windowEnd` was last seen at an index that is still inside the current window, jump `windowStart` to one past that last-seen index. Track the best window length seen so far.

**Key invariant:** at the end of every iteration of the loop, `s[windowStart..windowEnd]` contains no repeated character.

Proof sketch: the invariant holds trivially for the empty window before the loop starts. On each step, before including `s[windowEnd]`, the algorithm checks whether that character's last-seen index is `>= windowStart` (i.e., inside the current window). If so, it must move `windowStart` past that stale duplicate before extending, because otherwise two copies of the same character would sit in `[windowStart, windowEnd]`. If the last-seen index is `< windowStart`, the earlier copy is already outside the window and can be safely ignored — extending is immediately safe. Either way, after the fix-up, adding `s[windowEnd]` cannot create a duplicate within the window, so the invariant is preserved by induction. Since the invariant always holds, every length computed (`windowEnd - windowStart + 1`) corresponds to an actually-valid slice, and since `windowStart` only ever increases and `windowEnd` visits every index, every valid maximal slice is considered at the moment its right edge is processed.

### Step-by-step trace

Trace on `"abcabcbb"` (expected answer 3). State shown as `(windowStart, windowEnd, best, lastSeen map)`:

| windowEnd | char | prior lastSeen | windowStart after fix-up | window | best |
|---|---|---|---|---|---|
| 0 | a | none | 0 | "a" | 1 |
| 1 | b | none | 0 | "ab" | 2 |
| 2 | c | none | 0 | "abc" | 3 |
| 3 | a | 0 (inside window) | 1 | "bca" | 3 |
| 4 | b | 1 (inside window) | 2 | "cab" | 3 |
| 5 | c | 2 (inside window) | 3 | "abc" | 3 |
| 6 | b | 4 (inside window) | 5 | "cb" | 3 |
| 7 | b | 6 (inside window) | 7 | "b" | 3 |

Final answer: `3`.

## Java 8 solution

```java
import java.util.*;

public class LongestSubstringWithoutRepeatingCharacters {

    public static int solve(String s) {
        int[] lastSeen = new int[128]; // ASCII index -> last index seen, -1 means not seen
        Arrays.fill(lastSeen, -1);
        int windowStart = 0;
        int best = 0;
        for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
            char c = s.charAt(windowEnd);
            int prevIndex = lastSeen[c];
            // Only jump windowStart forward if the earlier occurrence is INSIDE the current
            // window; a stale index from before windowStart must never pull windowStart backward.
            if (prevIndex >= windowStart) {
                windowStart = prevIndex + 1;
            }
            lastSeen[c] = windowEnd;
            best = Math.max(best, windowEnd - windowStart + 1);
        }
        return best;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {"", 0},
            {"a", 1},
            {"abcabcbb", 3},
            {"bbbbb", 1},
            {"pwwkew", 3},
            {"abba", 2},
            {"dvdf", 3},
            {" ", 1},
            {"au", 2},
            {"abcdefg", 7}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            String input = (String) cases[i][0];
            int expected = (Integer) cases[i][1];
            int got = solve(input);
            if (Integer.compare(expected, got) == 0) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
```

## Complexity

- Time: O(n). `windowEnd` advances n times; `windowStart` only ever increases and can advance at most n times total across the whole run, so the total work is O(n) amortized, not O(n) per step times n steps.
- Space: O(1) extra when using a fixed 128-entry array keyed by ASCII code (or O(min(n, alphabet size)) if using a `HashMap<Character, Integer>` instead), since the map/array size is bounded by the character set, not by `n`.

## Java 8 pitfalls for this problem

- Using a `HashMap<Character, Integer>` and comparing values with `==` instead of `.equals()` or auto-unboxing into an `int` — boxed `Integer` caching only covers -128..127, so this can silently break for indices above 127.
- Forgetting to guard "is this last-seen index still inside the window" — without the `prevIndex >= windowStart` check, a stale index (from a character seen long before the window even started) can incorrectly yank `windowStart` backward, shrinking a window that should be growing (this is exactly the failure mode `"dvdf"` and `"abba"` are designed to catch).
- Declaring the map as `Map<Character, Integer>` and then trying to call array-only conveniences, or vice versa declaring `int[]` and trying to use `.getOrDefault` — pick one representation and stick to it.
- Off-by-one when computing window length: it is `windowEnd - windowStart + 1`, not `windowEnd - windowStart`, because both ends are inclusive.
- Char arithmetic: `s.charAt(i) - 'A'` style tricks are for the character-replacement style problems with a restricted alphabet; here the alphabet is the full ASCII (or Unicode) range, so a 128 (or larger) array or a hash map is the right container, not a 26-slot array.

## Wrong approaches and why they fail

- **Reset the window to start right after the very first occurrence of any duplicate, unconditionally.** Counterexample: `"abba"`. When the second `"a"` (index 3) is reached, its previous occurrence was at index 0 — but index 0 is already outside the current window (which starts at index 2, after processing the two `b`s). Blindly jumping `windowStart` to `1` (one past the stale index 0) would incorrectly shrink the window from `"ba"` back down, producing a wrong shorter answer instead of correctly extending to `"ba"` → length 2.
- **Use a `Set<Character>` and remove characters one at a time from the front when a duplicate is found, without tracking indices.** This works but degenerates to O(n) removals per duplicate in the worst case (e.g., `"aaaaaaaa...b"`), making the overall approach O(n^2) instead of O(n), because you don't know how far to jump — you can only remove one character at a time from the left.
- **Track only whether a character has been "seen before" (boolean) instead of its last index.** Counterexample: `"abcabcbb"` — without an index you cannot know exactly how far to advance `windowStart`; you would have to fall back to advancing it one step at a time and rechecking, which reintroduces O(n) work per collision.

## Variants

- **Return the substring itself, not just its length.** Track `bestStart` and `bestLength` alongside `best`, updating them together whenever a new maximum is found, then slice `s.substring(bestStart, bestStart + bestLength)` at the end.
- **At most `k` repeated characters allowed instead of zero.** This generalizes to a frequency-count sliding window: keep a count per character, and shrink the window only when more than `k` characters have a count greater than 1 (or some similar rule depending on the exact definition of "repeated" allowed) — same window mechanics, different shrink condition.
- **Process the string as a stream (characters arrive one at a time, no full string in memory, no going back).** The same last-seen-index technique still works, since it only ever looks forward; just process arriving characters one at a time and maintain `windowStart`/`best` incrementally without needing random access into a stored string, though you do need to be able to still recover characters between `windowStart` and the newest index if you also want the substring itself (e.g., with a circular buffer sized to the maximum window seen so far).

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `""` | 0 | empty input |
| 2 | `"a"` | 1 | single character |
| 3 | `"abcabcbb"` | 3 | classic repeating pattern |
| 4 | `"bbbbb"` | 1 | all characters identical |
| 5 | `"pwwkew"` | 3 | best window is not a prefix/suffix; non-contiguous lookalike must be rejected |
| 6 | `"abba"` | 2 | stale last-seen index must not pull window start backward |
| 7 | `"dvdf"` | 3 | same stale-index trap in a different shape |
| 8 | `" "` | 1 | whitespace treated as an ordinary character |
| 9 | `"au"` | 2 | short all-distinct string |
| 10 | `"abcdefg"` | 7 | fully distinct string, answer equals full length |
