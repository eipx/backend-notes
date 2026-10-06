# Dynamic programming

Notes on how to work a dynamic-programming problem from a blank page, followed by the problem families in this folder and a reading order. Every problem named here has a study page, a runnable Java 8 solution and a practice stub in this folder (see the index at the end).

## What the technique is

A dynamic-programming problem asks for the best (or the count, or the possibility) over a set of decisions. Two properties make the technique apply:

1. **Overlapping subproblems.** The plain recursion solves the same smaller problem many times. A memo (top-down) or a table (bottom-up) makes each subproblem cost once.
2. **Optimal substructure.** The best answer to the whole problem is built from the best answers to its parts, and the parts do not interfere with each other. If choosing well in one part can ruin another part, the problem is not a straight DP and needs a different state.

Everything else is bookkeeping. The hard part is writing the recursion.

## The working method

Use the same five questions every time, in this order.

1. **State.** What changes between the original problem and a smaller one? That is the state. It is usually an index, a length, an amount, a pair of indices, or a position plus a count.
2. **Choices.** What can be done at this state that moves it toward a base case? Take or skip an item. Use one coin. Match or skip a character. Burst one balloon last.
3. **Meaning.** Write one sentence: "dp(state) is ...". The answer to the whole problem must be expressible in those words (for example "dp(n) is the fewest coins for amount n; the answer is dp(amount)"). If the sentence is vague, the code will be wrong.
4. **Base cases.** The states where the answer is known without choices: empty input, amount zero, one element, the last cell.
5. **Order.** Which states must be known before this one? Top-down with a memo never needs this question answered; bottom-up does, and the order is the direction of the loops.

Write the brute-force recursion first, as if time were free. Then add the memo. Then, if the order is clear, turn it into a table. Then, if each row needs only the previous row, keep one or two rows. Stop at whichever step passes the time limit; a memoized recursion is a complete answer.

## Two ways to enumerate

The recursion can be written from either end of the choice.

- **From the answer backward.** "To finish at i, what was the last step?" This gives transitions like `dp[i] = best over j < i of dp[j] + cost(j, i)` (longest increasing subsequence, word break).
- **From the choices forward.** "At i, what do I do now?" This gives transitions like `dp[i] = best(take(i) + dp[i + 1], skip(i) + dp[i + 1])` (house robber, knapsack).

Both are correct when they enumerate the same set of decisions. Pick the one whose base case is simpler to state.

## The families in this folder

### One index over a line

State: a position or an amount. Choices: what happens at that position.

- Climbing Stairs (LC 70): the sum of the two previous counts.
- House Robber (LC 198) and House Robber II (LC 213): take this house and skip the next, or skip this house. The circular version runs the line twice, once without the first house and once without the last.
- House Robber III (LC 337): the same choice on a tree; each node reports two values, robbed and not robbed.
- Coin Change (LC 322): fewest coins for an amount; each coin is a choice, amount zero is the base.
- Word Break (LC 139): can the first i characters be split into dictionary words; the last word is the choice.
- Maximum Subarray (LC 53): the best sum of a subarray ending here is either this element alone or this element added to the previous best.

### Subsequences and two strings

State: a prefix length, or a pair of prefix lengths. The table has an extra row and column for the empty prefix.

- Longest Increasing Subsequence (LC 300): `dp[i]` is the longest subsequence ending at i; the patience-sorting version with binary search is the fast one.
- Longest Common Subsequence (LC 1143): equal last characters add one to the diagonal; otherwise take the better of dropping one character.
- Delete Operation for Two Strings (LC 583): the same table; the answer is both lengths minus twice the common subsequence.
- Edit Distance (LC 72): three choices at a mismatch (insert, delete, replace), one free step on a match.
- Regular Expression Matching (LC 10): a star stands for zero copies or one more copy of the element before it; handle the star case before the plain case.
- Longest Palindromic Subsequence (LC 516): an interval problem on one string; equal ends add two to the inside.

### Grids

State: a cell. Choices: which neighbor the path came from or goes to.

- Minimum Path Sum (LC 64): each cell from its top or left neighbor; the first row and column have one neighbor.
- Dungeon Game (LC 174): fill from the bottom-right corner, because the state is the least health needed on entering a cell, which depends on what comes after it.

### Knapsack

State: the items considered so far and the remaining capacity. Choices: take this item or not.

- 0-1 Knapsack (classic): each item at most once. In the one-row version the capacity loop runs from high to low so that an item is not reused inside the same row.
- Partition Equal Subset Sum (LC 416): a 0-1 knapsack where the question is reachability of half the total.
- Target Sum (LC 494): the plus signs form one subset; count the subsets with a fixed sum.
- Coin Change II (LC 518): unlimited copies, counting combinations; coins on the outer loop so that each combination is counted once, capacity loop from low to high.

The loop direction is the whole difference between "each item once" and "unlimited copies". Say which one the problem is before writing the loops.

### Intervals

State: a pair of indices i and j. Choices: what happens at the ends, or which element is handled last inside the interval. Fill by interval length, shortest first.

- Burst Balloons (LC 312): choose the balloon burst last inside an open interval, so that its neighbors are still the interval ends; pad both ends with 1.
- Stone Game (LC 877): the player to move takes an end; `dp[i][j]` is the best score difference from here, and the opponent's best is subtracted.
- Longest Palindromic Subsequence (LC 516) and Longest Palindromic Substring (LC 5): the substring version expands from centers instead, because a substring must be contiguous.

### State machines over time

State: the day plus a small status (holding a share, not holding, cooling down) and sometimes a count of trades. Choices: buy, sell, rest.

- Best Time to Buy and Sell Stock with Cooldown (LC 309): three statuses; a sale forces one idle day.
- Best Time to Buy and Sell Stock with Transaction Fee (LC 714): two statuses; the fee is paid once per trade, on the sale.
- Best Time to Buy and Sell Stock IV (LC 188): add the number of trades left as a dimension; when k is at least half the number of days it is the unlimited case.

### A budget of moves

State: how many moves remain plus what they must cover. Choices: where to spend the next move.

- Super Egg Drop (LC 887): `f(m, k)` is how many floors m moves and k eggs can certainly cover; grow m until it reaches n.
- Freedom Trail (LC 514): position in the key and position on the ring; the cost of a move is the circular distance plus one press.
- Cheapest Flights Within K Stops (LC 787): the number of edges used is the state; one round per edge, each round reading a copy of the previous round so that a path cannot grow by two edges in one round.

## Space

When a transition reads only the previous row (or the previous two cells), keep that much and roll. Two traps: the direction of the inner loop must still match the dependency (high to low for 0-1 knapsack), and a diagonal neighbor (`dp[i - 1][j - 1]`) needs a saved copy before it is overwritten.

## Checks before calling a solution done

- The base case for the empty input is written and tested.
- The index meaning is fixed: `dp[i]` covers the first i characters, so the character is `charAt(i - 1)`, or `dp[i]` covers index i, so it is `charAt(i)`. Mixing the two is the most common bug in this folder.
- The answer cell is the one named in the meaning sentence, not the last cell by habit.
- Sums are in `long` when the inputs can multiply past two billion, or the problem states a modulus and every addition applies it.
- The recursion depth is bounded for the largest input, or the table version is used.
- The smallest interesting example is traced by hand and matches the trace table on the page.

## Reading order for a first pass

1. Climbing Stairs, House Robber, Coin Change: the method on one index.
2. Longest Common Subsequence, Edit Distance, Delete Operation for Two Strings: two strings and the empty-prefix row.
3. 0-1 Knapsack, Partition Equal Subset Sum, Target Sum, Coin Change II: the loop direction.
4. Minimum Path Sum, Dungeon Game: grids, forward and backward.
5. Longest Palindromic Subsequence, Stone Game, Burst Balloons: intervals by length.
6. The three stock problems: a status next to the index.
7. Word Break, Regular Expression Matching, Longest Increasing Subsequence: the last-step view.
8. Super Egg Drop, Freedom Trail, Cheapest Flights Within K Stops, House Robber III: states that are not an index.

For each one: read the Problem and Edge cases sections of the page, write the body in the practice stub inside 25 minutes, run it, then read the Approach section and compare.

## Index of this folder

| Problem | Ref | Difficulty | Page | Solution | Practice |
|---|---|---|---|---|---|
| Climbing Stairs | LC 70 | Easy | | | [ClimbingStairs.java](practice/ClimbingStairs.java) |
| House Robber | LC 198 | Medium | [house-robber.md](house-robber.md) | [HouseRobber.java](HouseRobber.java) | [HouseRobber.java](practice/HouseRobber.java) |
| House Robber II | LC 213 | Medium | [house-robber-ii.md](house-robber-ii.md) | [HouseRobberII.java](HouseRobberII.java) | [HouseRobberII.java](practice/HouseRobberII.java) |
| House Robber III | LC 337 | Medium | [house-robber-iii.md](house-robber-iii.md) | [HouseRobberIII.java](HouseRobberIII.java) | [HouseRobberIII.java](practice/HouseRobberIII.java) |
| Coin Change | LC 322 | Medium | [coin-change.md](coin-change.md) | [CoinChange.java](CoinChange.java) | [CoinChange.java](practice/CoinChange.java) |
| Coin Change II | LC 518 | Medium | [coin-change-ii.md](coin-change-ii.md) | [CoinChangeII.java](CoinChangeII.java) | [CoinChangeII.java](practice/CoinChangeII.java) |
| Word Break | LC 139 | Medium | [word-break.md](word-break.md) | [WordBreak.java](WordBreak.java) | [WordBreak.java](practice/WordBreak.java) |
| Maximum Subarray | LC 53 | Medium | | | [MaximumSubarray.java](practice/MaximumSubarray.java) |
| Longest Increasing Subsequence | LC 300 | Medium | [longest-increasing-subsequence.md](longest-increasing-subsequence.md) | [LongestIncreasingSubsequence.java](LongestIncreasingSubsequence.java) | [LongestIncreasingSubsequence.java](practice/LongestIncreasingSubsequence.java) |
| Longest Common Subsequence | LC 1143 | Medium | [longest-common-subsequence.md](longest-common-subsequence.md) | [LongestCommonSubsequence.java](LongestCommonSubsequence.java) | [LongestCommonSubsequence.java](practice/LongestCommonSubsequence.java) |
| Delete Operation for Two Strings | LC 583 | Medium | [delete-operation-for-two-strings.md](delete-operation-for-two-strings.md) | [DeleteOperationForTwoStrings.java](DeleteOperationForTwoStrings.java) | [DeleteOperationForTwoStrings.java](practice/DeleteOperationForTwoStrings.java) |
| Edit Distance | LC 72 | Medium | [edit-distance.md](edit-distance.md) | [EditDistance.java](EditDistance.java) | [EditDistance.java](practice/EditDistance.java) |
| Regular Expression Matching | LC 10 | Hard | [regular-expression-matching.md](regular-expression-matching.md) | [RegularExpressionMatching.java](RegularExpressionMatching.java) | [RegularExpressionMatching.java](practice/RegularExpressionMatching.java) |
| Longest Palindromic Subsequence | LC 516 | Medium | [longest-palindromic-subsequence.md](longest-palindromic-subsequence.md) | [LongestPalindromicSubsequence.java](LongestPalindromicSubsequence.java) | [LongestPalindromicSubsequence.java](practice/LongestPalindromicSubsequence.java) |
| Longest Palindromic Substring | LC 5 | Medium | | | [LongestPalindromicSubstring.java](practice/LongestPalindromicSubstring.java) |
| Minimum Path Sum | LC 64 | Medium | [minimum-path-sum.md](minimum-path-sum.md) | [MinimumPathSum.java](MinimumPathSum.java) | [MinimumPathSum.java](practice/MinimumPathSum.java) |
| Dungeon Game | LC 174 | Hard | [dungeon-game.md](dungeon-game.md) | [DungeonGame.java](DungeonGame.java) | [DungeonGame.java](practice/DungeonGame.java) |
| 0-1 Knapsack | classic | Medium | [zero-one-knapsack.md](zero-one-knapsack.md) | [ZeroOneKnapsack.java](ZeroOneKnapsack.java) | [ZeroOneKnapsack.java](practice/ZeroOneKnapsack.java) |
| Partition Equal Subset Sum | LC 416 | Medium | [partition-equal-subset-sum.md](partition-equal-subset-sum.md) | [PartitionEqualSubsetSum.java](PartitionEqualSubsetSum.java) | [PartitionEqualSubsetSum.java](practice/PartitionEqualSubsetSum.java) |
| Target Sum | LC 494 | Medium | [target-sum.md](target-sum.md) | [TargetSum.java](TargetSum.java) | [TargetSum.java](practice/TargetSum.java) |
| Burst Balloons | LC 312 | Hard | [burst-balloons.md](burst-balloons.md) | [BurstBalloons.java](BurstBalloons.java) | [BurstBalloons.java](practice/BurstBalloons.java) |
| Stone Game | LC 877 | Medium | [stone-game.md](stone-game.md) | [StoneGame.java](StoneGame.java) | [StoneGame.java](practice/StoneGame.java) |
| Best Time to Buy and Sell Stock with Cooldown | LC 309 | Medium | [best-time-to-buy-and-sell-stock-with-cooldown.md](best-time-to-buy-and-sell-stock-with-cooldown.md) | [BestTimeToBuyAndSellStockWithCooldown.java](BestTimeToBuyAndSellStockWithCooldown.java) | [BestTimeToBuyAndSellStockWithCooldown.java](practice/BestTimeToBuyAndSellStockWithCooldown.java) |
| Best Time to Buy and Sell Stock with Transaction Fee | LC 714 | Medium | [best-time-to-buy-and-sell-stock-with-transaction-fee.md](best-time-to-buy-and-sell-stock-with-transaction-fee.md) | [BestTimeToBuyAndSellStockWithTransactionFee.java](BestTimeToBuyAndSellStockWithTransactionFee.java) | [BestTimeToBuyAndSellStockWithTransactionFee.java](practice/BestTimeToBuyAndSellStockWithTransactionFee.java) |
| Best Time to Buy and Sell Stock IV | LC 188 | Hard | [best-time-to-buy-and-sell-stock-iv.md](best-time-to-buy-and-sell-stock-iv.md) | [BestTimeToBuyAndSellStockIV.java](BestTimeToBuyAndSellStockIV.java) | [BestTimeToBuyAndSellStockIV.java](practice/BestTimeToBuyAndSellStockIV.java) |
| Super Egg Drop | LC 887 | Hard | [super-egg-drop.md](super-egg-drop.md) | [SuperEggDrop.java](SuperEggDrop.java) | [SuperEggDrop.java](practice/SuperEggDrop.java) |
| Freedom Trail | LC 514 | Hard | [freedom-trail.md](freedom-trail.md) | [FreedomTrail.java](FreedomTrail.java) | [FreedomTrail.java](practice/FreedomTrail.java) |
| Cheapest Flights Within K Stops | LC 787 | Medium | [cheapest-flights-within-k-stops.md](cheapest-flights-within-k-stops.md) | [CheapestFlightsWithinKStops.java](CheapestFlightsWithinKStops.java) | [CheapestFlightsWithinKStops.java](practice/CheapestFlightsWithinKStops.java) |
| Minimum Inversion Merge | custom | Hard | [minimum-inversion-merge.md](minimum-inversion-merge.md) | [MinimumInversionMerge.java](MinimumInversionMerge.java) | [MinimumInversionMerge.java](practice/MinimumInversionMerge.java) |

Three problems (Climbing Stairs, Maximum Subarray, Longest Palindromic Substring) have practice stubs only, from an earlier practice set; their pages may follow.
