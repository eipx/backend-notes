import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccountsMerge {

    public static List<List<String>> solve(List<List<String>> accounts) {
        Map<String, String> parent = new HashMap<String, String>();
        Map<String, String> emailToName = new HashMap<String, String>();

        for (List<String> account : accounts) {
            String name = account.get(0);
            String firstEmail = account.get(1);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                if (!parent.containsKey(email)) {
                    parent.put(email, email);
                }
                emailToName.put(email, name);
                union(parent, firstEmail, email);
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
            parent.put(x, parent.get(parent.get(x)));
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
                List<List<String>> actual = solve(input);
                if (toSet(expected).equals(toSet(actual))) {
                    System.out.println("PASS");
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
