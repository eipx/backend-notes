import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ReconstructItinerary {

    public static List<String> solve(List<List<String>> tickets) {
        Map<String, PriorityQueue<String>> graph = new HashMap<String, PriorityQueue<String>>();
        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            graph.computeIfAbsent(from, k -> new PriorityQueue<String>()).add(to);
        }

        LinkedList<String> route = new LinkedList<String>();
        visit("JFK", graph, route);
        return route;
    }

    private static void visit(String airport, Map<String, PriorityQueue<String>> graph, LinkedList<String> route) {
        PriorityQueue<String> destinations = graph.get(airport);
        while (destinations != null && !destinations.isEmpty()) {
            String next = destinations.poll();
            visit(next, graph, route);
        }
        route.addFirst(airport);
    }

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
            List<String> actual = solve(tickets);
            if (actual.equals(expected)) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
