// Accounts Merge
// ref: LC 721
// Given accounts as [name, email1, email2, ...], merge every group of accounts
// that share at least one email (transitively) into one account per person: the
// shared name plus every distinct email in that group, sorted alphabetically.
// Required complexity: O(N * K * alpha(N*K)) time, O(N * K) space
// Study page: ../accounts-merge.md
// Run: javac --release 8 AccountsMerge.java && java AccountsMerge

import java.util.*;

class Solution {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        // TODO: implement
        return new ArrayList<List<String>>();
    }
}

public class AccountsMerge {
    private static List<List<String>> toAccounts(String[][] raw) {
        List<List<String>> accounts = new ArrayList<List<String>>();
        for (String[] row : raw) {
            List<String> account = new ArrayList<String>();
            for (String s : row) {
                account.add(s);
            }
            accounts.add(account);
        }
        return accounts;
    }

    private static Set<List<String>> toSet(List<List<String>> accounts) {
        return new HashSet<List<String>>(accounts);
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            {
                new String[][]{
                    {"John","johnsmith@mail.com","john_newyork@mail.com"},
                    {"John","johnsmith@mail.com","john00@mail.com"},
                    {"Mary","mary@mail.com"},
                    {"John","johnnybravo@mail.com"}
                },
                new String[][]{
                    {"John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"},
                    {"Mary","mary@mail.com"},
                    {"John","johnnybravo@mail.com"}
                }
            },
            {
                new String[][]{
                    {"Alice","alice1@mail.com"},
                    {"Bob","bob1@mail.com"}
                },
                new String[][]{
                    {"Alice","alice1@mail.com"},
                    {"Bob","bob1@mail.com"}
                }
            },
            {
                new String[][]{
                    {"Xen","x1@mail.com","x2@mail.com"},
                    {"Xen","x2@mail.com","x3@mail.com"},
                    {"Xen","x3@mail.com","x4@mail.com"}
                },
                new String[][]{
                    {"Xen","x1@mail.com","x2@mail.com","x3@mail.com","x4@mail.com"}
                }
            },
            {
                new String[][]{
                    {"Solo","solo@mail.com"}
                },
                new String[][]{
                    {"Solo","solo@mail.com"}
                }
            },
            {
                new String[][]{
                    {"Dup","dup@mail.com"},
                    {"Dup","dup@mail.com"}
                },
                new String[][]{
                    {"Dup","dup@mail.com"}
                }
            },
            {
                new String[][]{
                    {"Mid","a@mail.com","shared@mail.com"},
                    {"Mid","shared@mail.com","b@mail.com"}
                },
                new String[][]{
                    {"Mid","a@mail.com","b@mail.com","shared@mail.com"}
                }
            },
            {
                new String[][]{
                    {"Group","g1@mail.com","g2@mail.com"},
                    {"Group","g3@mail.com"},
                    {"Group","g2@mail.com","g4@mail.com"}
                },
                new String[][]{
                    {"Group","g1@mail.com","g2@mail.com","g4@mail.com"},
                    {"Group","g3@mail.com"}
                }
            },
            {
                new String[][]{
                    {"Sam","sam1@mail.com"},
                    {"Sam","sam2@mail.com"},
                    {"Sam","sam3@mail.com"}
                },
                new String[][]{
                    {"Sam","sam1@mail.com"},
                    {"Sam","sam2@mail.com"},
                    {"Sam","sam3@mail.com"}
                }
            },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            String[][] rawAccounts = (String[][]) cases[i][0];
            String[][] rawExpected = (String[][]) cases[i][1];
            List<List<String>> input = toAccounts(rawAccounts);
            List<List<String>> expected = toAccounts(rawExpected);
            try {
                List<List<String>> actual = new Solution().accountsMerge(input);
                if (toSet(expected).equals(toSet(actual))) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
