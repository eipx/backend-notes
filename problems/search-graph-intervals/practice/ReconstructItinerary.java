// Reconstruct Itinerary
// ref: LC 332
// Given airline tickets as [from, to] pairs, use every ticket exactly once to
// build the lexicographically smallest itinerary starting from "JFK".
// Required complexity: O(t log t) time, O(t) space (t = number of tickets)
// Study page: ../reconstruct-itinerary.md
// Run: javac --release 8 ReconstructItinerary.java && java ReconstructItinerary

import java.util.*;

class Solution {
    public List<String> findItinerary(List<List<String>> tickets) {
        // TODO: implement
        return new ArrayList<String>();
    }
}

public class ReconstructItinerary {

    private static List<List<String>> toTickets(String[][] raw) {
        List<List<String>> tickets = new ArrayList<List<String>>();
        for (String[] pair : raw) {
            tickets.add(Arrays.asList(pair));
        }
        return tickets;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new String[][]{{"MUC","LHR"},{"JFK","MUC"},{"SFO","SJC"},{"LHR","SFO"}},
              new String[]{"JFK","MUC","LHR","SFO","SJC"} },
            { new String[][]{{"JFK","SFO"},{"JFK","ATL"},{"SFO","ATL"},{"ATL","JFK"},{"ATL","SFO"}},
              new String[]{"JFK","ATL","JFK","SFO","ATL","SFO"} },
            { new String[][]{{"JFK","KUL"},{"JFK","NRT"},{"NRT","JFK"}},
              new String[]{"JFK","NRT","JFK","KUL"} },
            { new String[][]{{"JFK","A"}},
              new String[]{"JFK","A"} },
            { new String[][]{{"JFK","A"},{"A","JFK"},{"JFK","A"}},
              new String[]{"JFK","A","JFK","A"} },
            { new String[][]{{"JFK","A"},{"A","JFK"},{"JFK","B"},{"B","JFK"},{"JFK","C"}},
              new String[]{"JFK","A","JFK","B","JFK","C"} },
            { new String[][]{{"JFK","A"},{"A","B"},{"B","JFK"},{"JFK","C"}},
              new String[]{"JFK","A","B","JFK","C"} },
            { new String[][]{{"JFK","A"},{"A","B"},{"B","C"}},
              new String[]{"JFK","A","B","C"} },
            { new String[][]{{"JFK","C"},{"B","JFK"},{"JFK","B"},{"A","JFK"},{"JFK","A"}},
              new String[]{"JFK","A","JFK","B","JFK","C"} },
            { new String[][]{{"JFK","A"},{"A","C"},{"C","D"},{"D","A"}},
              new String[]{"JFK","A","C","D","A"} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            String[][] rawTickets = (String[][]) cases[i][0];
            List<String> expected = Arrays.asList((String[]) cases[i][1]);
            List<List<String>> tickets = toTickets(rawTickets);
            try {
                List<String> actual = new Solution().findItinerary(tickets);
                if (actual.equals(expected)) {
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
