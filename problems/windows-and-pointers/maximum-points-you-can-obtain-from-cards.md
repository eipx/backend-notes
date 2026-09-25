# Maximum Points You Can Obtain from Cards
`ref: LC 1423` · Difficulty: Medium · Pattern: enumerate the k+1 front/back splits with a prefix sum and a running suffix sum (equivalently, the minimum fixed-size window of untaken cards)

## Problem

Cards lie in a row. Card `i` is worth `cardPoints[i]` points. In one step you take one card from either the left end or the right end of the row. You must take exactly `k` cards. Return the largest total you can collect.

Input: an integer array `cardPoints` and an integer `k`.
Output: a single integer, the maximum sum obtainable by taking exactly `k` cards from the two ends.

## Constraints

- `1 <= cardPoints.length <= 10^5`
- `1 <= cardPoints[i] <= 10^4`
- `1 <= k <= cardPoints.length`
- Sums fit in an `int`: at most `10^5 * 10^4 = 10^9`.
- With `n` up to `1e5`, anything quadratic in `n` or in `k` is too slow, and a two-dimensional table of size `k x k` does not fit in memory near the top of the range. Expected: `O(k)` or `O(n)` time, `O(1)` extra space.

## Worked examples

1. `cardPoints = [1,2,3,4,5,6,1]`, `k = 3` -> `12`. The four ways to take three cards from the ends: front three `1+2+3 = 6`; front two and back one `1+2+1 = 4`; front one and back two `1+6+1 = 8`; back three `5+6+1 = 12`.
2. `cardPoints = [2,2,2]`, `k = 2` -> `4`. Every split gives the same total.
3. `cardPoints = [9,7,7,9,7,7,9]`, `k = 7` -> `55`. `k` equals `n`, so every card is taken and the answer is the total.
4. `cardPoints = [1,1000,1]`, `k = 1` -> `1`. The big card in the middle cannot be reached with one take.
5. `cardPoints = [1,79,80,1,1,1,200,1]`, `k = 3` -> `202`. Front one plus back two (`1 + 200 + 1`) and back three (`1 + 200 + 1`) tie at `202`; front three gives only `160`.

## Edge cases checklist

- `k == n`: every card is taken, the block of untaken cards has length zero.
- `k == 1`: the better of the two end cards.
- `n == 1`: then `k == 1` and the answer is the only card.
- All from the front is best; all from the back is best; the best split is strictly in between.
- A large value just inside one end that a one-card take from the other side unlocks (example 5).
- A large value in the middle that no split reaches (example 4).
- Equal values everywhere (every split ties).
- A case where the larger end card is the wrong first take because the cards behind the other end are worth more together (test 12).

## Approach

### What the choices really are

Any sequence of `k` takes from the ends leaves one contiguous block of `n - k` cards in the middle. The cards taken are always the first `j` and the last `k - j` for some `j` in `0..k`. The order of the takes does not matter; only `j` does. So there are exactly `k + 1` distinct outcomes, not `2^k`, and the whole problem is "score `k + 1` splits and keep the best."

### Brute force

Enumerate every take sequence recursively: at each step take the left or the right end card. That is `2^k` leaves, and almost all of them are the same outcome reached in a different order. Too slow beyond tiny `k`.

### Optimal, from the front and the back

Let `front(j)` be the sum of the first `j` cards and `back(m)` the sum of the last `m` cards. The answer is `max over j in 0..k of front(j) + back(k - j)`. Start with `j = k` (all from the front). Then move `j` down one at a time: give back one front card and take one more back card. Each step costs `O(1)`, so the whole scan is `O(k)`.

**Key invariant:** after the step for `j`, `front` holds the sum of the first `j` cards and `back` holds the sum of the last `k - j` cards, so `front + back` is the total of exactly one legal outcome. Every `j` from `k` down to `0` is visited once, so every outcome is scored and the maximum over them is the answer. When `j` front cards are kept, the newest back card is `cardPoints[n - k + j]`: the `k - j` back cards occupy indices `n - (k - j)` to `n - 1`, and the one just added is the leftmost of them.

### Optimal, from the other side

The `n - k` untaken cards form one contiguous window. Maximizing the taken sum is the same as minimizing the sum of that window. Slide a fixed window of length `n - k` across the array, keep the smallest window sum, and return `total - minWindow`. `O(n)` time. When `k == n` the window has length zero and the answer is the total.

### Step-by-step trace

Trace of the front-and-back version on `cardPoints = [1,2,3,4,5,6,1]`, `k = 3` (`n = 7`):

| j | front = first j | back = last k - j | front + back | best |
|---|---|---|---|---|
| 3 | 1+2+3 = 6 | (none) 0 | 6 | 6 |
| 2 | 1+2 = 3 | 1 | 4 | 6 |
| 1 | 1 | 1+6 = 7 | 8 | 8 |
| 0 | 0 | 1+6+5 = 12 | 12 | 12 |

Final answer: `12`, matching worked example 1.

## Java 8 solution
```java
public class MaximumPointsYouCanObtainFromCards {

    // Take exactly k cards from the two ends of the row; return the largest total.
    public static int solve(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int front = 0;
        for (int j = 0; j < k; j++) {
            front += cardPoints[j];              // start with all k cards from the front
        }
        int back = 0;
        int best = front;
        for (int j = k - 1; j >= 0; j--) {       // keep j front cards, take k - j from the back
            front -= cardPoints[j];              // give back front card j
            back += cardPoints[n - k + j];       // take one more card from the back
            best = Math.max(best, front + back);
        }
        return best;
    }

    private static void check(int caseNum, int[] cardPoints, int k, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        int got = solve(cardPoints, k);
        if (got == expected) {
            System.out.println("PASS case " + caseNum);
        } else {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1,2,3,4,5,6,1}, 3, 12, fail, total);
        check(2, new int[]{2,2,2}, 2, 4, fail, total);
        check(3, new int[]{9,7,7,9,7,7,9}, 7, 55, fail, total);
        check(4, new int[]{1,1000,1}, 1, 1, fail, total);
        check(5, new int[]{1,79,80,1,1,1,200,1}, 3, 202, fail, total);
        check(6, new int[]{5}, 1, 5, fail, total);
        check(7, new int[]{10,1,1,1,10}, 2, 20, fail, total);
        check(8, new int[]{3,9,1,1,1,1,9,3}, 2, 12, fail, total);
        check(9, new int[]{100,40,17,9,73,1,1,1,1,1}, 3, 157, fail, total);
        check(10, new int[]{1,2,3,4,5}, 5, 15, fail, total);
        check(11, new int[]{1,2}, 1, 2, fail, total);
        check(12, new int[]{5,1,100,4}, 2, 104, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

## Complexity

Time O(k): one loop of `k` additions builds the all-front sum, then one loop of `k` steps does constant work each. Space O(1): three integers beyond the input. The untaken-window version is O(n) time, O(1) space.

## Java 8 pitfalls for this problem

- The back-card index. With `j` front cards kept, the card just added from the back is `cardPoints[n - k + j]`. Derive it from the state (`k - j` cards from the back, so the leftmost of them sits at `n - (k - j)`) instead of guessing an expression. A wrong offset here is silent on most inputs and wrong on a few, which is the worst kind of bug under a timer.
- `k == n`: `n - k + j` equals `j`, so each step moves one card from the front sum to the back sum and the total never changes. No special case is needed, but say so in a comment and trace it once.
- Initial `best`: use the all-front sum, not `0`. With positive values `0` happens to work; if the values could be zero or negative, `0` would be wrong and the bug would only show on those inputs.
- Sums fit in `int` under these constraints. With a larger value bound, make the three running sums `long`.
- `Arrays.stream(a).sum()` is fine once for the total in the window version, but never inside a loop: it hides an extra `O(n)` per call.

## Wrong approaches and why they fail

1. **Greedy: at each step take the larger of the two end cards.** Counterexample: `[5,1,100,4]`, `k = 2`. Greedy takes `5` (larger than `4`), then the larger of `1` and `4`, total `9`. The answer is `104`, the back two. Greedy compares single cards while the decision is about the sums of the cards behind them.

2. **A two-dimensional take-or-skip table, `dp[i][j]` = `i` cards taken in total, `j` of them from the front.** The state is valid. The attempt usually breaks in three places, and each one is worth knowing:
   - The "one more from the back" transition is not a move from `dp[i][j-1]`. Going from `(i, j-1)` to `(i, j)` keeps the total at `i` and swaps one back card for one front card: add `cardPoints[j-1]`, subtract `cardPoints[n-i+j-1]`. Written as a plain addition of some back card, it adds a card that is not in the set.
   - The answer is read from the wrong place. It is the best cell in row `k`, `max over j of dp[k][j]`. It is not a cell where `i == 0` (after the first row there is no such cell, so a guard like `if (i == 0)` never fires and the method returns `0`), and it is not the last cell of the table.
   - The table cannot exist at the constraints. `(n+1) x (n+1)` ints at `n = 10^5` is tens of gigabytes, and `(k+1) x (k+1)` is no better when `k` is close to `n`.

   Fixed with the fewest edits, the table version is:
   ```java
   int[][] dp = new int[k + 1][k + 1];
   for (int i = 1; i <= k; i++) dp[i][0] = dp[i - 1][0] + cardPoints[n - i];   // all from the back
   for (int i = 1; i <= k; i++)
       for (int j = 1; j <= i; j++)
           dp[i][j] = dp[i - 1][j - 1] + cardPoints[j - 1];                     // one more from the front
   int best = 0;
   for (int j = 0; j <= k; j++) best = Math.max(best, dp[k][j]);
   ```
   Look at what disappeared: the `Math.max` inside the double loop. With this state each cell holds exactly one card set, the first `j` plus the last `i - j`, so both transitions produce the same number and there is nothing to choose inside a cell. The only max is across row `k`. A table whose cells contain no choice is a prefix sum with extra rows; row `k` alone is the `O(k)` solution above.

3. **Sliding a window over the taken cards.** The taken cards are not contiguous: they sit at the two ends of the row. The contiguous block is the untaken one, so the window belongs there, has length `n - k`, and is minimized, not maximized. A window of length `k` maximized over the array answers a different question.

## When a table is not the tool

Two questions before writing `dp[`:

1. How many distinct end states are there, and can each one be written as a formula? Here `k + 1`, each a prefix sum plus a suffix sum. Enumerate them; no table.
2. Is the answer for a large input built from answers for smaller inputs of the same kind, and do those smaller inputs repeat across different paths? In Word Break (LC 139), whether the prefix of length `i` can be split depends only on shorter prefixes, and the shorter prefixes are shared by many split paths. That repetition is the signature of a table. Here nothing repeats: each of the `k + 1` outcomes is scored once.

Heavier machinery than the problem needs is a recognizable failure mode: an exponential jump where a plain binary search would do, a merge sort where a heap would do, a trie where a one-dimensional table would do, a table where a prefix sum would do. The check is to count the outcomes first.

## Variants

1. **Values may be zero or negative, still exactly `k` takes.** Same scan; initialize `best` with the all-front sum instead of `0`.
2. **Return the split, not just the sum.** Record `j` whenever `best` improves; the answer is "first `j` cards and last `k - j` cards".
3. **Minimum total instead of maximum.** Same scan with `Math.min`, or maximize the untaken window.
4. **The untaken-window form is "minimum sum of a fixed-length window."** That sub-problem reappears on its own in many places: keep one running sum, subtract the element leaving, add the element entering.

## Test cases

| # | input | expected | what it tests |
|---|---|---|---|
| 1 | `cardPoints=[1,2,3,4,5,6,1], k=3` | `12` | general case, all from the back is best |
| 2 | `cardPoints=[2,2,2], k=2` | `4` | every split ties |
| 3 | `cardPoints=[9,7,7,9,7,7,9], k=7` | `55` | k == n, take everything |
| 4 | `cardPoints=[1,1000,1], k=1` | `1` | big card in the middle is unreachable |
| 5 | `cardPoints=[1,79,80,1,1,1,200,1], k=3` | `202` | a one-card take from the other side unlocks a big card |
| 6 | `cardPoints=[5], k=1` | `5` | single card |
| 7 | `cardPoints=[10,1,1,1,10], k=2` | `20` | best split strictly in between (one from each end) |
| 8 | `cardPoints=[3,9,1,1,1,1,9,3], k=2` | `12` | symmetric array, front two ties back two |
| 9 | `cardPoints=[100,40,17,9,73,1,1,1,1,1], k=3` | `157` | all from the front is best |
| 10 | `cardPoints=[1,2,3,4,5], k=5` | `15` | k == n with distinct values |
| 11 | `cardPoints=[1,2], k=1` | `2` | k == 1, pick the better end |
| 12 | `cardPoints=[5,1,100,4], k=2` | `104` | breaks the larger-end greedy |
