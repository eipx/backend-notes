// Evaluate Division
// ref: LC 399
// Given equations of the form a/b = value and a list of queries c/d, build a
// weighted graph of the ratios and answer each query by chaining edges, or -1.0
// if either variable is unknown or the two variables are disconnected.
// Required complexity: O(Q * (V + E)) time, O(V + E) space
// Study page: ../evaluate-division.md
// Run: javac --release 8 EvaluateDivision.java && java EvaluateDivision

import java.util.*;

class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        // TODO: implement
        return new double[queries.size()];
    }
}

public class EvaluateDivision {

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
            try {
                double[] actual = new Solution().calcEquation(equations, values, queries);
                if (closeEnough(actual, expected)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                            + " got " + Arrays.toString(actual));
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                        + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
