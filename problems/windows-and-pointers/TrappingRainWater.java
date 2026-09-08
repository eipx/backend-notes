public class TrappingRainWater {

    public static int solve(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                // the taller wall guaranteeing the trap is on the right side somewhere at or
                // beyond `right`, so the water level at `left` is capped only by leftMax
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }
        return water;
    }

    public static void main(String[] args) {
        Object[][] cases = {
            {new int[]{0,1,0,2,1,0,1,3,2,1,2,1}, 6},
            {new int[]{4,2,0,3,2,5}, 9},
            {new int[]{}, 0},
            {new int[]{5}, 0},
            {new int[]{5,5}, 0},
            {new int[]{1,2,3,4,5}, 0},
            {new int[]{5,4,3,2,1}, 0},
            {new int[]{2,0,2}, 2},
            {new int[]{3,0,3,0,3}, 6},
            {new int[]{4,4,4,4}, 0}
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
