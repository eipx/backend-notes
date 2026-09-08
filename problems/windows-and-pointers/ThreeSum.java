import java.util.*;

public class ThreeSum {

    public static List<List<Integer>> solve(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (nums == null || nums.length < 3) {
            return result;
        }
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && sorted[i] == sorted[i - 1]) {
                continue; // skip duplicate anchor value to avoid duplicate triplets
            }
            int left = i + 1;
            int right = n - 1;
            while (left < right) {
                int sum = sorted[i] + sorted[left] + sorted[right];
                if (sum == 0) {
                    List<Integer> triplet = new ArrayList<Integer>();
                    triplet.add(sorted[i]);
                    triplet.add(sorted[left]);
                    triplet.add(sorted[right]);
                    result.add(triplet);
                    while (left < right && sorted[left] == sorted[left + 1]) {
                        left++;
                    }
                    while (left < right && sorted[right] == sorted[right - 1]) {
                        right--;
                    }
                    left++;
                    right--;
                } else if (sum < 0) {
                    left++; // sum too small; only a bigger left value can raise it
                } else {
                    right--; // sum too large; only a smaller right value can lower it
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{-1,0,1,2,-1,-4}, tripletList(intList(-1,-1,2), intList(-1,0,1))},
            {new int[]{}, tripletList()},
            {new int[]{0}, tripletList()},
            {new int[]{0,0,0}, tripletList(intList(0,0,0))},
            {new int[]{0,0,0,0}, tripletList(intList(0,0,0))},
            {new int[]{-2,0,0,2,2}, tripletList(intList(-2,0,2))},
            {new int[]{1,2,-2,-1}, tripletList()},
            {new int[]{3,-2,1,0}, tripletList()},
            {new int[]{-1,0,1,0}, tripletList(intList(-1,0,1))},
            {new int[]{-5,-4,-3,-2,-1}, tripletList()}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            @SuppressWarnings("unchecked")
            List<List<Integer>> expected = (List<List<Integer>>) cases[i][1];
            List<List<Integer>> got = solve(nums);
            if (expected.equals(got)) {
                System.out.println("PASS");
                passed++;
            } else {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }

    private static List<Integer> intList(int... vals) {
        List<Integer> l = new ArrayList<Integer>();
        for (int v : vals) {
            l.add(v);
        }
        return l;
    }

    @SafeVarargs
    private static List<List<Integer>> tripletList(List<Integer>... triplets) {
        List<List<Integer>> l = new ArrayList<List<Integer>>();
        for (List<Integer> t : triplets) {
            l.add(t);
        }
        return l;
    }
}
