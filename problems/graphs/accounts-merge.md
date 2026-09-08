# Accounts Merge
`ref: LC 721` · Difficulty: Medium · Pattern: Union-Find over string identifiers (emails), keyed by value not index

## Problem
You're given a list of accounts, where each account is a name followed by one or more email addresses belonging to that account. Two accounts actually belong to the same underlying person if they share at least one email address in common -- and this sharing is transitive: if account A shares an email with account B, and account B shares a different email with account C, then A, B, and C all belong to the same person even if A and C share no email directly. Merge every group of accounts that belong to the same person into one combined account: the merged account's name is the (common) name of that person, and its emails are every distinct email from every account in the group, sorted alphabetically. Note that two different accounts can have the exact same name without being the same person, as long as they share no email.

## Constraints
- Up to `1000` accounts, each with up to `10` emails, each email up to `30` characters.
- An account's first field is always the name; the remaining fields are that account's emails, with no duplicate emails *within* a single account (though the same email can legitimately appear in more than one account, which is exactly the signal that those accounts should merge).
- The order of the returned merged accounts, and the order of accounts in the input, do not matter -- only the final grouping and each group's sorted emails matter.

## Worked examples
1. accounts = `[["John","johnsmith@mail.com","john_newyork@mail.com"], ["John","johnsmith@mail.com","john00@mail.com"], ["Mary","mary@mail.com"], ["John","johnnybravo@mail.com"]]` -> three merged accounts: `["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"]` (the first two John accounts share `johnsmith@mail.com`, so they merge), `["Mary","mary@mail.com"]` (unaffected), and `["John","johnnybravo@mail.com"]` (same name as the merged John, but shares no email, so it stays a separate account for a different person).
2. accounts = `[["Alice","alice1@mail.com"], ["Bob","bob1@mail.com"]]` -> unchanged: `["Alice","alice1@mail.com"]` and `["Bob","bob1@mail.com"]`, since there is no shared email anywhere.
3. accounts = `[["Xen","x1@mail.com","x2@mail.com"], ["Xen","x2@mail.com","x3@mail.com"], ["Xen","x3@mail.com","x4@mail.com"]]` -> one merged account `["Xen","x1@mail.com","x2@mail.com","x3@mail.com","x4@mail.com"]`, because the shared emails chain all three accounts together transitively (account 1 and 2 share `x2`, account 2 and 3 share `x3`), even though account 1 and account 3 share nothing directly.
4. accounts = `[["Sam","sam1@mail.com"], ["Sam","sam2@mail.com"], ["Sam","sam3@mail.com"]]` -> three separate unchanged accounts, since despite the identical name, none of them share any email with each other.

## Edge cases checklist
- Two accounts with the exact same name but zero shared emails -- must stay separate, not merge just because names match.
- A chain of three or more accounts that only merge transitively (no single pair shares an email with every other account, but the chain connects them all).
- The exact same account listed twice (identical name and identical single email) -- must merge into one account with no duplicate email in the output.
- A shared email appearing in a position other than "first" within an account's email list -- confirms the union step doesn't accidentally rely on email position within the account.
- A single account with a single email and nothing to merge with -- must appear unchanged in the output (still passed through the union-find and grouping logic, just alone in its own group).
- Multiple independent merge clusters plus one or more untouched singleton accounts, all in the same input, to confirm the grouping and singleton logic coexist correctly.

## Approach
### Brute force
Repeatedly scan all pairs of accounts, and whenever two accounts share any email, merge their email sets into one and remove the duplicate account, then restart the pairwise scan from the top (since merging two accounts can now create a new shared email with a third account that didn't share anything before). This can take multiple full passes over the account list before it stabilizes, costing up to `O(accounts^2 * emailsPerAccount)` per pass and up to `O(accounts)` passes in the worst chained-merge case -- correct, but needlessly expensive and awkward to implement cleanly compared to union-find.

### Optimal
Union-Find over individual email strings (not account indices): every distinct email starts as its own singleton set. For each account, union its first email with every other email in that same account -- this correctly links all of an account's own emails together, and because emails repeated across different accounts are literally the same string key in the union-find structure, unioning within one account automatically chains into any other account that happens to share one of those emails. Alongside the union-find, keep a map from each email to the name of the account that listed it (any account works, since all accounts sharing an email must belong to the same person and therefore share the same name). After processing every account, group all emails by their union-find root, and for each group, build one merged account: the name from any email in the group, plus all of that group's emails sorted alphabetically.

**Key invariant:** two emails end up in the same union-find set if and only if there exists a chain of accounts, each pair of which is linked by residing in the same account or having been previously unioned, connecting them -- which is exactly the "same underlying person" relation defined by transitive email sharing.

Proof sketch: unioning every email in an account with that account's first email guarantees all of a single account's own emails end up in the same set. When two different accounts share an email, that shared email string is one and the same union-find element regardless of which account's processing added it to a set first, so both accounts' entire email groups end up merged into a single set the moment the second account processes that shared email. By induction over the order accounts are processed, transitively-linked accounts (through any length chain of shared emails) always end up in the same final set, since union-find composes pairwise unions into full transitive closure automatically.

### Step-by-step trace
Trace on accounts = `[["Xen","x1@mail.com","x2@mail.com"], ["Xen","x2@mail.com","x3@mail.com"], ["Xen","x3@mail.com","x4@mail.com"]]`:

| account processed | first email (union anchor) | unions performed | parent map state (informal, by root) |
|---|---|---|---|
| ["Xen", x1, x2] | x1 | union(x1,x1) no-op; union(x1,x2) | {x1,x2} share a root |
| ["Xen", x2, x3] | x2 | union(x2,x2) no-op; union(x2,x3) | x2's root now also covers x3 -> {x1,x2,x3} one set |
| ["Xen", x3, x4] | x3 | union(x3,x3) no-op; union(x3,x4) | x3's root now also covers x4 -> {x1,x2,x3,x4} one set |
| -- | -- | grouping pass | one group of 4 emails, all mapping to name "Xen" |
| -- | -- | sort + build result | `["Xen","x1@mail.com","x2@mail.com","x3@mail.com","x4@mail.com"]` |

## Java 8 solution
```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public static List<List<String>> solve(List<List<String>> accounts) {
    Map<String, String> parent = new HashMap<String, String>(); // email -> parent email
    Map<String, String> emailToName = new HashMap<String, String>();

    for (List<String> account : accounts) {
        String name = account.get(0);
        String firstEmail = account.get(1);
        for (int i = 1; i < account.size(); i++) {
            String email = account.get(i);
            if (!parent.containsKey(email)) {
                parent.put(email, email); // new email starts as its own singleton set
            }
            emailToName.put(email, name);
            union(parent, firstEmail, email); // anchor every email in this account to the first one
        }
    }

    Map<String, List<String>> groups = new HashMap<String, List<String>>();
    for (String email : parent.keySet()) {
        String root = find(parent, email);
        if (!groups.containsKey(root)) {
            groups.put(root, new ArrayList<String>());
        }
        groups.get(root).add(email);
    }

    List<List<String>> result = new ArrayList<List<String>>();
    for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
        List<String> emails = entry.getValue();
        Collections.sort(emails);
        List<String> mergedAccount = new ArrayList<String>();
        mergedAccount.add(emailToName.get(emails.get(0)));
        mergedAccount.addAll(emails);
        result.add(mergedAccount);
    }
    return result;
}

private static String find(Map<String, String> parent, String x) {
    while (!parent.get(x).equals(x)) {
        parent.put(x, parent.get(parent.get(x))); // path halving
        x = parent.get(x);
    }
    return x;
}

private static void union(Map<String, String> parent, String a, String b) {
    String rootA = find(parent, a);
    String rootB = find(parent, b);
    if (!rootA.equals(rootB)) {
        parent.put(rootA, rootB);
    }
}
```

## Complexity
- Time: `O(N * K * alpha(N*K))` where `N` is the number of accounts and `K` is the max emails per account -- every email is processed a constant number of times for union/find, each costing amortized near-constant time; the final sort of each group costs `O(m log m)` for a group of `m` emails, summing to at most `O(total emails * log(total emails))` across all groups.
- Space: `O(N * K)` -- the `parent` and `emailToName` maps each hold one entry per distinct email.

## Java 8 pitfalls for this problem
- Union-Find keyed by `String` (the email itself) rather than by an integer index requires using `.equals()` for comparisons (`!parent.get(x).equals(x)`), never `==` -- two separate `String` objects holding the same characters are `.equals()` but not necessarily `==`, and this trips up anyone used to writing integer-indexed union-find where `!=` is safe.
- `HashMap<String, String>` lookups for `find`/`union` are `O(1)` amortized, same as an integer-indexed array would be `O(1)` directly -- but every `parent.get(x)` call does a hash computation and equality check on the string, which is real (if usually small) overhead compared to array indexing; for a truly large-scale version of this problem, mapping each distinct email to a dense integer id first (via a `Map<String,Integer>`) and running union-find on `int[]` arrays would be faster, though unnecessary at this problem's stated scale.
- Using `account.get(1)` as the "anchor" email for every union in that account (rather than re-anchoring to `account.get(i-1)`, the previous email) is a deliberate, simpler choice: unioning every email directly with the *first* email in the account produces the same final grouping as unioning consecutive pairs, since union-find transitively merges either way -- but it does mean the union calls form a star shape around the first email rather than a chain, which is slightly more efficient to build.
- The comparator/equality contract used by the test runner here ("compare as a set of sorted lists") relies on `List<String>.equals()` being element-order-sensitive -- each merged account's email list absolutely must be sorted (via `Collections.sort`) before being wrapped in the outer set comparison, or two logically-identical merged accounts built in different internal orders would compare as unequal.
- Declaring `groups` as `Map<String, List<String>>` (not `HashMap` on the left) and `entry.getValue()` as `List<String>` keeps to the declared-type convention used throughout this folder, even though the concrete runtime types are `HashMap` and `ArrayList`.

## Wrong approaches and why they fail
- **Merging accounts by matching names instead of emails:** two different people can share the exact same name (see the `johnnybravo` example), so grouping by name alone would incorrectly merge unrelated accounts into one; the problem's entire premise is that email overlap, not name equality, defines "same person."
- **Only checking for shared emails between pairs of accounts directly adjacent in the input list:** a transitively-connected chain (`account1` shares with `account2`, `account2` shares with `account3`, but `account1` and `account3` share nothing directly) is missed entirely if only immediate neighbors in the input order are compared -- union-find naturally handles arbitrary-length transitive chains regardless of input order, which is exactly why it's the right tool here.
- **Using a plain `Set<String>` per account and repeatedly checking for set intersection across all pairs to decide when to merge:** technically arrives at a correct grouping if implemented as a full fixed-point iteration (keep merging until no more merges happen), but is far more expensive than union-find (`O(accounts^2)` intersection checks per pass, potentially many passes) and much more error-prone to get right, since it requires explicitly re-checking previously-processed pairs after every merge.

## Variants
- **Return accounts merged by phone number instead of email, or by multiple types of shared identifiers at once:** the union-find skeleton is unchanged; simply union across whichever identifier fields indicate "same person," possibly unioning across two different identifier maps into the same underlying disjoint-set structure.
- **LC 547 Number of Provinces** and **LC 684 Redundant Connection** (both in this same folder) use the identical `find`/`union` logic, just keyed by integer indices into a fixed-size array instead of by string email addresses -- comparing the three side by side is a good way to see that union-find's "key type" (int index vs. string) is a superficial detail, not a structural difference.
- **Report which accounts merged and why (i.e. which specific shared email triggered each merge), not just the final grouping:** track, alongside each `union` call, which two original account indices were responsible, to build an audit trail on top of the plain union-find result.

## Test cases
| # | input accounts | expected merged accounts | what it tests |
|---|---|---|---|
| 1 | [["John","johnsmith@mail.com","john_newyork@mail.com"],["John","johnsmith@mail.com","john00@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]] | [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]] | classic merge plus same-name-different-person |
| 2 | [["Alice","alice1@mail.com"],["Bob","bob1@mail.com"]] | [["Alice","alice1@mail.com"],["Bob","bob1@mail.com"]] | no shared emails, nothing merges |
| 3 | [["Xen","x1@mail.com","x2@mail.com"],["Xen","x2@mail.com","x3@mail.com"],["Xen","x3@mail.com","x4@mail.com"]] | [["Xen","x1@mail.com","x2@mail.com","x3@mail.com","x4@mail.com"]] | transitive chain merge across three accounts |
| 4 | [["Solo","solo@mail.com"]] | [["Solo","solo@mail.com"]] | single account, single email |
| 5 | [["Dup","dup@mail.com"],["Dup","dup@mail.com"]] | [["Dup","dup@mail.com"]] | duplicate account, output has no duplicate email |
| 6 | [["Mid","a@mail.com","shared@mail.com"],["Mid","shared@mail.com","b@mail.com"]] | [["Mid","a@mail.com","b@mail.com","shared@mail.com"]] | shared email in a non-first position within an account |
| 7 | [["Group","g1@mail.com","g2@mail.com"],["Group","g3@mail.com"],["Group","g2@mail.com","g4@mail.com"]] | [["Group","g1@mail.com","g2@mail.com","g4@mail.com"],["Group","g3@mail.com"]] | one merge cluster plus one untouched singleton, same name throughout |
| 8 | [["Sam","sam1@mail.com"],["Sam","sam2@mail.com"],["Sam","sam3@mail.com"]] | [["Sam","sam1@mail.com"],["Sam","sam2@mail.com"],["Sam","sam3@mail.com"]] | identical names, zero shared emails, all stay separate |
