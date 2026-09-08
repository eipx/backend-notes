public class ContainerWithMostWater {

    public static int solve(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int best = 0;
        while (left < right) {
            int width = right - left;
            int shorter = Math.min(height[left], height[right]);
            int area = width * shorter;
            best = Math.max(best, area);
            // moving the taller pointer only shrinks the width while the cap stays the same
            // wall or gets worse, so it can never beat the current area; only moving the
            // shorter pointer has any chance of finding a taller wall
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{1,8,6,2,5,4,8,3,7}, 49},
            {new int[]{1,1}, 1},
            {new int[]{4,3,2,1,4}, 16},
            {new int[]{1,2,1}, 2},
            {new int[]{2,2,2,2}, 6},
            {new int[]{1,2,4,3}, 4},
            {new int[]{0,2}, 0},
            {new int[]{5,4,3,2,1}, 6},
            {new int[]{1,2,3,4,5}, 6},
            {new int[]{10000,10000}, 10000}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] height = (int[]) cases[i][0];
            int expected = (Integer) cases[i][1];
            int got = solve(height);
            if (Integer.compare(expected, got) == 0) {
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
}
