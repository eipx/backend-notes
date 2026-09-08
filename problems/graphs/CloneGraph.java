import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CloneGraph {

    public static class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }

        public Node(int val) {
            this.val = val;
            neighbors = new ArrayList<Node>();
        }
    }

    public static Node solve(Node node) {
        if (node == null) {
            return null;
        }
        Map<Node, Node> oldToNew = new HashMap<Node, Node>();
        Deque<Node> queue = new ArrayDeque<Node>();
        queue.add(node);
        oldToNew.put(node, new Node(node.val));
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Node neighbor : current.neighbors) {
                if (!oldToNew.containsKey(neighbor)) {
                    oldToNew.put(neighbor, new Node(neighbor.val));
                    queue.add(neighbor);
                }
                oldToNew.get(current).neighbors.add(oldToNew.get(neighbor));
            }
        }
        return oldToNew.get(node);
    }

    private static Node buildGraph(int[][] adjList) {
        if (adjList.length == 0) {
            return null;
        }
        Node[] nodes = new Node[adjList.length];
        for (int i = 0; i < adjList.length; i++) {
            nodes[i] = new Node(i + 1);
        }
        for (int i = 0; i < adjList.length; i++) {
            for (int nb : adjList[i]) {
                nodes[i].neighbors.add(nodes[nb - 1]);
            }
        }
        return nodes[0];
    }

    private static boolean verifyClone(Node original, Node cloned) {
        if (original == null) {
            return cloned == null;
        }
        if (cloned == null) {
            return false;
        }
        Set<Node> visitedOriginal = new HashSet<Node>();
        Map<Integer, Node> origByVal = new HashMap<Integer, Node>();
        Deque<Node> stack = new ArrayDeque<Node>();
        stack.push(original);
        visitedOriginal.add(original);
        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            origByVal.put(cur.val, cur);
            for (Node nb : cur.neighbors) {
                if (!visitedOriginal.contains(nb)) {
                    visitedOriginal.add(nb);
                    stack.push(nb);
                }
            }
        }

        Map<Integer, Node> cloneByVal = new HashMap<Integer, Node>();
        Set<Node> visitedClone = new HashSet<Node>();
        stack.push(cloned);
        visitedClone.add(cloned);
        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            if (visitedOriginal.contains(cur)) {
                return false; // clone shares an object with the original graph
            }
            cloneByVal.put(cur.val, cur);
            for (Node nb : cur.neighbors) {
                if (!visitedClone.contains(nb)) {
                    visitedClone.add(nb);
                    stack.push(nb);
                }
            }
        }

        if (!origByVal.keySet().equals(cloneByVal.keySet())) {
            return false;
        }
        for (Integer val : origByVal.keySet()) {
            List<Integer> oNeighborVals = new ArrayList<Integer>();
            for (Node nb : origByVal.get(val).neighbors) {
                oNeighborVals.add(nb.val);
            }
            List<Integer> cNeighborVals = new ArrayList<Integer>();
            for (Node nb : cloneByVal.get(val).neighbors) {
                cNeighborVals.add(nb.val);
            }
            Collections.sort(oNeighborVals);
            Collections.sort(cNeighborVals);
            if (!oNeighborVals.equals(cNeighborVals)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int[][][] cases = new int[][][] {
            {},
            { {} },
            { {2}, {1} },
            { {2,3}, {1,3}, {1,2} },
            { {2,4}, {1,3}, {2,4}, {1,3} },
            { {2,3,4}, {1}, {1}, {1} },
            { {2}, {1,3}, {2,4}, {3,5}, {4} },
            { {2,3}, {1,4}, {1}, {2} },
            { {2,6}, {1,3}, {2,4}, {3,5}, {4,6}, {5,1} },
            { {2,3,4}, {1,3,4}, {1,2,4}, {1,2,3} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            try {
                Node original = buildGraph(cases[i]);
                Node actual = solve(original);
                boolean ok = verifyClone(original, actual);
                if (ok) {
                    System.out.println("PASS");
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": clone did not match original structure");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected valid clone got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
