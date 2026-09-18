import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EvaluateDivision {

    public static double[] solve(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = new HashMap<String, Map<String, Double>>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double value = values[i];
            graph.computeIfAbsent(a, k -> new HashMap<String, Double>()).put(b, value);
            graph.computeIfAbsent(b, k -> new HashMap<String, Double>()).put(a, 1.0 / value);
        }

        double[] answers = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String source = queries.get(i).get(0);
            String target = queries.get(i).get(1);
            if (!graph.containsKey(source) || !graph.containsKey(target)) {
                answers[i] = -1.0;
            } else if (source.equals(target)) {
                answers[i] = 1.0;
            } else {
                answers[i] = bfsRatio(graph, source, target);
            }
        }
        return answers;
    }

    private static double bfsRatio(Map<String, Map<String, Double>> graph, String source, String target) {
        Deque<String> queue = new ArrayDeque<String>();
        Deque<Double> accumulated = new ArrayDeque<Double>();
        Set<String> visited = new HashSet<String>();
        queue.add(source);
        accumulated.add(1.0);
        visited.add(source);
        while (!queue.isEmpty()) {
            String node = queue.poll();
            double ratioFromSource = accumulated.poll();
            if (node.equals(target)) {
                return ratioFromSource;
            }
            for (Map.Entry<String, Double> edge : graph.get(node).entrySet()) {
                String next = edge.getKey();
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                    accumulated.add(ratioFromSource * edge.getValue());
                }
            }
        }
        return -1.0;
    }

    private static List<List<String>> toPairs(String[][] raw) {
        List<List<String>> pairs = new ArrayList<List<String>>();
        for (String[] pair : raw) {
            pairs.add(Arrays.asList(pair));
        }
        return pairs;
    }

    private static boolean closeEnough(double[] actual, double[] expected) {
        if (actual.length != expected.length) {
            return false;
        }
        for (int i = 0; i < actual.length; i++) {
            if (Math.abs(actual[i] - expected[i]) > 1e-4) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new String[][]{{"a","b"},{"b","c"}}, new double[]{2.0,3.0},
              new String[][]{{"a","c"},{"b","a"},{"a","e"},{"a","a"},{"x","x"}},
              new double[]{6.0,0.5,-1.0,1.0,-1.0} },
            { new String[][]{{"a","b"},{"b","c"},{"bc","cd"}}, new double[]{1.5,2.5,5.0},
              new String[][]{{"a","c"},{"c","b"},{"bc","cd"},{"cd","bc"}},
              new double[]{3.75,0.4,5.0,0.2} },
            { new String[][]{{"x1","x2"}}, new double[]{0.5},
              new String[][]{{"x1","x2"},{"x2","x1"},{"x1","x1"},{"x1","x3"},{"x3","x4"}},
              new double[]{0.5,2.0,1.0,-1.0,-1.0} },
            { new String[][]{{"a","b"},{"c","d"}}, new double[]{2.0,3.0},
              new String[][]{{"a","d"}},
              new double[]{-1.0} },
            { new String[][]{{"a","b"}}, new double[]{4.0},
              new String[][]{{"b","a"}},
              new double[]{0.25} },
            { new String[][]{{"a","b"},{"b","c"},{"c","d"}}, new double[]{2.0,3.0,4.0},
              new String[][]{{"a","d"},{"d","a"}},
              new double[]{24.0,0.041666667} },
            { new String[][]{{"a","b"},{"b","a"}}, new double[]{2.0,0.5},
              new String[][]{{"a","b"}},
              new double[]{2.0} },
            { new String[][]{{"a","b"}}, new double[]{1.0},
              new String[][]{{"a","b"},{"a","a"}},
              new double[]{1.0,1.0} },
            { new String[][]{}, new double[]{},
              new String[][]{{"a","b"}},
              new double[]{-1.0} },
            { new String[][]{{"a","b"},{"b","c"},{"a","c"}}, new double[]{2.0,3.0,6.0},
              new String[][]{{"a","c"},{"c","a"}},
              new double[]{6.0,0.166666667} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            List<List<String>> equations = toPairs((String[][]) cases[i][0]);
            double[] values = (double[]) cases[i][1];
            List<List<String>> queries = toPairs((String[][]) cases[i][2]);
            double[] expected = (double[]) cases[i][3];
            double[] actual = solve(equations, values, queries);
            if (closeEnough(actual, expected)) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                        + " got " + Arrays.toString(actual));
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
