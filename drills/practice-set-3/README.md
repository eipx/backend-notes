# Practice set 3

Twenty problems in a fixed order. Each section below holds one complete practice
file: the statement in the header comment, an empty method body, and the test
runner. Open a section, copy the block, save it under the file name shown, write
the body, run it.

```
javac --release 8 <ClassName>.java && java <ClassName>
```

Say the time and space complexity before the first line. Trace the smallest
input before calling it done. Stop at 25 minutes.

The same twenty files are in this folder as plain text (`01-...txt` to `20-...txt`).

<details>
<summary>1. LC 121 Best Time to Buy and Sell Stock (Easy). Save as BestTimeToBuyAndSellStock.java</summary>

```java
// Best Time to Buy and Sell Stock
// ref: LC 121
// prices[i] is the price of one share on day i. Pick one day to buy and a
// strictly later day to sell, and return the largest possible profit (sell price
// minus buy price). If no trade can earn anything, return 0 (not trading at all
// is allowed).
// Input: int[] prices. Output: int, the best profit, or 0.
// Examples:
//   [7,1,5,3,6,4] gives 5 (buy at 1 on day 1, sell at 6 on day 4).
//   [7,6,4,3,1] gives 0 (the price only falls, so do not trade).
// Constraints: 1 <= prices.length <= 100000, 0 <= prices[i] <= 10000
// Required complexity: O(n) time, O(1) extra space
// Run: javac --release 8 BestTimeToBuyAndSellStock.java && java BestTimeToBuyAndSellStock

import java.util.*;

class Solution {
    public int maxProfit(int[] prices) {
        // TODO: implement
        return 0;
    }
}

public class BestTimeToBuyAndSellStock {

    private static void check(int caseNum, int[] prices, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxProfit(prices);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    // 100000 days: the price slides from 10000 down to 5001, then climbs back up
    // to 10000. Best trade: buy at 5001, sell at 10000.
    private static int[] valleyThenClimb() {
        int[] a = new int[100000];
        for (int i = 0; i < 50000; i++) {
            a[i] = 10000 - i / 10;
        }
        for (int i = 50000; i < 100000; i++) {
            a[i] = 5001 + (i - 50000) / 10;
        }
        return a;
    }

    // 100000 days of a price that never rises: 10000 down to 1.
    private static int[] longSlide() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            a[i] = 10000 - i / 10;
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{7, 1, 5, 3, 6, 4}, 5, fail, total);
        check(2, new int[]{7, 6, 4, 3, 1}, 0, fail, total);
        check(3, new int[]{5}, 0, fail, total);
        check(4, new int[]{0, 10000}, 10000, fail, total);
        check(5, new int[]{10000, 0}, 0, fail, total);
        check(6, new int[]{2, 4, 1}, 2, fail, total);
        check(7, new int[]{3, 3, 5, 0, 0, 3, 1, 4}, 4, fail, total);
        check(8, new int[]{1, 2}, 1, fail, total);
        check(9, new int[]{2, 1, 2, 0, 1}, 1, fail, total);
        check(10, new int[]{4, 4, 4, 4}, 0, fail, total);
        check(11, new int[]{3, 2, 6, 5, 0, 3}, 4, fail, total);
        check(12, valleyThenClimb(), 4999, fail, total);
        check(13, longSlide(), 0, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>2. LC 11 Container With Most Water (Medium). Save as ContainerWithMostWater.java</summary>

```java
// Container With Most Water
// ref: LC 11
// Given an array `height` where height[i] is the height of a vertical line at
// position i, pick two lines that together with the x-axis form a container.
// Return the maximum amount of water that container can hold.
// Required complexity: O(n) time, O(1) space.
// Study page: ../container-with-most-water.md
// Run: javac --release 8 ContainerWithMostWater.java && java ContainerWithMostWater

import java.util.*;

class Solution {
    public int maxArea(int[] height) {
        // TODO: implement
        return 0;
    }
}

public class ContainerWithMostWater {
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
            try {
                int got = new Solution().maxArea(height);
                if (Integer.compare(expected, got) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
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
```

</details>

<details>
<summary>3. LC 2 Add Two Numbers (Medium). Save as AddTwoNumbers.java</summary>

```java
// Add Two Numbers
// ref: LC 2
// Two non-negative integers are each stored in a singly linked list: one
// decimal digit per node, least significant digit first (so the list 2,4,3
// is the number 342). Add the two numbers and return the sum as a list in the
// same form. The numbers can be far larger than any built-in integer type, so
// work digit by digit instead of converting a list into a number.
// Input:  l1 and l2, the heads of the two lists (neither is null).
// Output: the head of a list holding the digits of the sum, least significant
//         digit first, no leading zeros (the number 0 is a single node 0).
// Constraints: each list has between 1 and 100 nodes; every node value is a
// digit from 0 to 9; a number has no leading zeros except the number 0 itself.
// Example 1: l1 = [2,4,3] (342) and l2 = [5,6,4] (465) give [7,0,8] (807).
// Example 2: l1 = [9,9,9] (999) and l2 = [1] (1) give [0,0,0,1] (1000).
// Required complexity: O(max(m, n)) time, where m and n are the list lengths.
// Run: javac --release 8 AddTwoNumbers.java && java AddTwoNumbers

import java.util.*;

class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // TODO: implement
        return null;
    }
}

public class AddTwoNumbers {

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(2,4,3), intArr(5,6,4), intArr(7,0,8)},
            {intArr(0), intArr(0), intArr(0)},
            {intArr(5), intArr(5), intArr(0,1)},
            {intArr(9,9,9), intArr(1), intArr(0,0,0,1)},
            {intArr(9,9,9,9,9,9,9), intArr(9,9,9,9), intArr(8,9,9,9,0,0,0,1)},
            {intArr(1), intArr(9,9,1), intArr(0,0,2)},
            {intArr(1,8), intArr(0), intArr(1,8)},
            {intArr(0), intArr(7,3,2), intArr(7,3,2)},
            {intArr(0,0,1), intArr(0,0,2), intArr(0,0,3)},
            // the largest long, plus one: the sum no longer fits in a long
            {digits("9223372036854775807"), intArr(1), digits("9223372036854775808")},
            // 27 digits plus 23 digits
            {digits("123456789012345678901234567"), digits("98765432109876543210987"), digits("123555554444455555444445554")},
            // longest allowed lists: 100 nines plus 1, and 100 nines plus 100 nines
            {rep(9,100), intArr(1), concat(rep(0,100), intArr(1))},
            {rep(9,100), rep(9,100), concat(intArr(8), rep(9,99), intArr(1))}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] a = (int[]) cases[i][0];
            int[] b = (int[]) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                Solution.ListNode result = new Solution().addTwoNumbers(buildList(a), buildList(b));
                List<Integer> got = toList(result);
                if (Arrays.equals(expected, toIntArray(got))) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got " + got);
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected) + " got exception " + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }

    private static int[] intArr(int... vals) {
        return vals;
    }

    // the digits of a decimal string, least significant digit first
    private static int[] digits(String s) {
        int[] out = new int[s.length()];
        for (int i = 0; i < out.length; i++) {
            out[i] = s.charAt(s.length() - 1 - i) - '0';
        }
        return out;
    }

    private static int[] rep(int digit, int count) {
        int[] out = new int[count];
        Arrays.fill(out, digit);
        return out;
    }

    private static int[] concat(int[]... parts) {
        int total = 0;
        for (int[] p : parts) {
            total += p.length;
        }
        int[] out = new int[total];
        int pos = 0;
        for (int[] p : parts) {
            System.arraycopy(p, 0, out, pos, p.length);
            pos += p.length;
        }
        return out;
    }

    private static Solution.ListNode buildList(int[] vals) {
        Solution.ListNode dummy = new Solution.ListNode(0);
        Solution.ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new Solution.ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    private static List<Integer> toList(Solution.ListNode head) {
        List<Integer> out = new ArrayList<Integer>();
        Set<Solution.ListNode> seen = Collections.newSetFromMap(new IdentityHashMap<Solution.ListNode, Boolean>());
        Solution.ListNode cur = head;
        while (cur != null) {
            if (!seen.add(cur)) {
                throw new IllegalStateException("the returned list contains a cycle");
            }
            out.add(cur.val);
            cur = cur.next;
        }
        return out;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] out = new int[list.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = list.get(i);
        }
        return out;
    }
}
```

</details>

<details>
<summary>4. LC 207 Course Schedule (Medium). Save as CourseSchedule.java</summary>

```java
// Course Schedule
// ref: LC 207
// Given a number of courses and a list of prerequisite pairs [a, b] (b must be taken
// before a), determine whether it is possible to complete every course at all.
// Required complexity: O(V + E) time, O(V + E) space
// Study page: ../course-schedule.md
// Run: javac --release 8 CourseSchedule.java && java CourseSchedule

import java.util.*;

class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // TODO: implement
        return false;
    }
}

public class CourseSchedule {
    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { 2, new int[][]{{1,0}}, Boolean.TRUE },
            { 2, new int[][]{{1,0},{0,1}}, Boolean.FALSE },
            { 1, new int[][]{}, Boolean.TRUE },
            { 1, new int[][]{{0,0}}, Boolean.FALSE },
            { 3, new int[][]{{1,0},{2,1}}, Boolean.TRUE },
            { 4, new int[][]{{1,0},{2,0},{3,1},{3,2}}, Boolean.TRUE },
            { 3, new int[][]{{0,1},{1,2},{2,0}}, Boolean.FALSE },
            { 5, new int[][]{}, Boolean.TRUE },
            { 2, new int[][]{{0,1}}, Boolean.TRUE },
            { 6, new int[][]{{1,0},{2,0},{3,1},{3,2},{4,3},{5,4}}, Boolean.TRUE },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int numCourses = (Integer) cases[i][0];
            int[][] prerequisites = (int[][]) cases[i][1];
            boolean expected = (Boolean) cases[i][2];
            try {
                boolean actual = new Solution().canFinish(numCourses, prerequisites);
                if (Boolean.valueOf(actual).equals(Boolean.valueOf(expected))) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (numCourses=" + numCourses + ", prerequisites=" + Arrays.deepToString(prerequisites) + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>5. LC 169 Majority Element (Easy). Save as MajorityElement.java</summary>

```java
// Majority Element
// ref: LC 169
// Given an array of n integers, return the value that occurs strictly more than
// half of the time, that is, in more than n / 2 positions (n / 2 taken as an exact
// fraction, so 3 of 5 qualifies and 2 of 4 does not). Such a value is guaranteed
// to exist.
// Input: int[] nums. Output: int, the majority value.
// Examples:
//   [3,2,3] gives 3 (it fills 2 of the 3 positions).
//   [2,2,1,1,1,2,2] gives 2 (it fills 4 of the 7 positions).
// Constraints: 1 <= nums.length <= 50000, every value fits in a 32-bit int
// (negative values and the extreme values are allowed), a majority value exists.
// Required complexity: O(n) time, O(1) extra space
// Run: javac --release 8 MajorityElement.java && java MajorityElement

import java.util.*;

class Solution {
    public int majorityElement(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class MajorityElement {

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().majorityElement(nums);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    // 50000 entries. Even positions and the last position hold Integer.MIN_VALUE
    // (25001 copies, exactly one more than half); every other odd position holds
    // its own index, so those values are all different.
    private static int[] barelyAMajority() {
        int[] a = new int[50000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (i % 2 == 0) ? Integer.MIN_VALUE : i;
        }
        a[a.length - 1] = Integer.MIN_VALUE;
        return a;
    }

    // 50000 entries: 24999 copies of 1000 first, then 25001 copies of 999.
    private static int[] bigRivalFirst() {
        int[] a = new int[50000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (i < 24999) ? 1000 : 999;
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{3, 2, 3}, 3, fail, total);
        check(2, new int[]{2, 2, 1, 1, 1, 2, 2}, 2, fail, total);
        check(3, new int[]{1}, 1, fail, total);
        check(4, new int[]{Integer.MIN_VALUE}, Integer.MIN_VALUE, fail, total);
        check(5, new int[]{5, 5}, 5, fail, total);
        check(6, new int[]{-1, -1, -1, 5, 6}, -1, fail, total);
        check(7, new int[]{1, 2, 3, 4, 4, 4, 4}, 4, fail, total);
        check(8, new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE, fail, total);
        check(9, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE, fail, total);
        check(10, new int[]{1, 2, 3, 3, 3}, 3, fail, total);
        check(11, new int[]{0, -1, 0, -1, 0}, 0, fail, total);
        check(12, barelyAMajority(), Integer.MIN_VALUE, fail, total);
        check(13, bigRivalFirst(), 999, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>6. LC 15 3Sum (Medium). Save as ThreeSum.java</summary>

```java
// 3Sum
// ref: LC 15
// Given an array of integers nums, find every distinct triplet of values
// (a, b, c) such that a + b + c == 0. No duplicate triplet may be reported
// by value, even if it arises from different index combinations.
// Required complexity: O(n^2) time, O(log n) to O(n) space (sort + output aside).
// Study page: ../three-sum.md
// Run: javac --release 8 ThreeSum.java && java ThreeSum

import java.util.*;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        // TODO: implement
        return new ArrayList<List<Integer>>();
    }
}

public class ThreeSum {
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
            try {
                List<List<Integer>> got = new Solution().threeSum(nums);
                if (expected.equals(got)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
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
```

</details>

<details>
<summary>7. LC 4 Median of Two Sorted Arrays (Hard). Save as MedianOfTwoSortedArrays.java</summary>

```java
// Median of Two Sorted Arrays
// ref: LC 4
// Given two integer arrays nums1 and nums2, each already sorted in ascending order,
// find the median of all of their values taken together, as if the two arrays were
// merged into one sorted sequence. For an odd total count the median is the middle
// value; for an even total count it is the average of the two middle values.
// Input: int[] nums1 (length m), int[] nums2 (length n). Output: a double.
// Constraints: 0 <= m, n <= 1000; m + n >= 1; every value is in -1000000..1000000.
// Example: nums1 = [1,3], nums2 = [2] returns 2.0 (merged: 1,2,3).
// Example: nums1 = [1,2], nums2 = [3,4] returns 2.5 (merged: 1,2,3,4; the middle
// two values are 2 and 3).
// Required complexity: O(log(m + n)) time, O(1) extra space
// Run: javac --release 8 MedianOfTwoSortedArrays.java && java MedianOfTwoSortedArrays

import java.util.*;

class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // TODO: implement
        return 0.0;
    }
}

public class MedianOfTwoSortedArrays {

    // Builds the values start, start + step, start + 2 * step, ... (count values).
    private static int[] range(int start, int count, int step) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) {
            a[i] = start + i * step;
        }
        return a;
    }

    private static boolean closeEnough(double actual, double expected) {
        return Math.abs(actual - expected) <= 1e-9;
    }

    private static String brief(int[] a) {
        if (a.length <= 12) {
            return Arrays.toString(a);
        }
        return Arrays.toString(Arrays.copyOf(a, 12)) + "...(length " + a.length + ")";
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{1,3}, new int[]{2}, 2.0 },
            { new int[]{1,2}, new int[]{3,4}, 2.5 },
            { new int[]{}, new int[]{1}, 1.0 },
            { new int[]{2}, new int[]{}, 2.0 },
            { new int[]{1,2,3}, new int[]{10,20,30}, 6.5 },
            { new int[]{10,20,30}, new int[]{1,2}, 10.0 },
            { new int[]{1,2,2}, new int[]{2,2,3}, 2.0 },
            { new int[]{-3,-1}, new int[]{-2,0}, -1.5 },
            { new int[]{999999}, new int[]{1000000}, 999999.5 },
            { new int[]{-1000000,-1000000}, new int[]{-999999}, -1000000.0 },
            { new int[]{1,2,3}, new int[]{4,5,6,7,8,9,10}, 5.5 },
            { new int[]{1,3,8,9,15}, new int[]{7,11,18,19,21,25}, 11.0 },
            { range(0, 1000, 2), range(1, 1000, 2), 999.5 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums1 = (int[]) cases[i][0];
            int[] nums2 = (int[]) cases[i][1];
            double expected = (Double) cases[i][2];
            try {
                double actual = new Solution().findMedianSortedArrays(nums1, nums2);
                if (closeEnough(actual, expected)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (nums1=" + brief(nums1) + ", nums2=" + brief(nums2) + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>8. LC 9 Palindrome Number (Easy). Save as PalindromeNumber.java</summary>

```java
// Palindrome Number
// ref: LC 9
// Decide whether an integer x reads the same from left to right and from right
// to left when written in decimal. A negative number is never a palindrome (its
// minus sign appears only at the front).
// Input: int x. Output: boolean, true if x is a palindrome.
// Examples:
//   121 gives true. -121 gives false (it reads 121- backwards).
//   10 gives false (it reads 01 backwards).
// Constraints: x is any 32-bit int, from -2147483648 to 2147483647
// Required: do not convert the number to a string. This is the target, and the
// tests cannot enforce it. Aim for O(log10 |x|) time (one step per digit) and
// O(1) extra space.
// Run: javac --release 8 PalindromeNumber.java && java PalindromeNumber

import java.util.*;

class Solution {
    public boolean isPalindrome(int x) {
        // TODO: implement
        return false;
    }
}

public class PalindromeNumber {

    private static void check(int caseNum, int x, boolean expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            boolean got = new Solution().isPalindrome(x);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, 121, true, fail, total);
        check(2, -121, false, fail, total);
        check(3, 10, false, fail, total);
        check(4, 0, true, fail, total);
        check(5, 7, true, fail, total);
        check(6, 1221, true, fail, total);
        check(7, 1210, false, fail, total);
        check(8, Integer.MAX_VALUE, false, fail, total);
        check(9, Integer.MIN_VALUE, false, fail, total);
        check(10, 2147447412, true, fail, total);
        check(11, 1000001, true, fail, total);
        check(12, 1000021, false, fail, total);
        check(13, 12321, true, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>9. LC 5 Longest Palindromic Substring (Medium). Save as LongestPalindromicSubstring.java</summary>

```java
// Longest Palindromic Substring
// ref: LC 5
// Given a string s, return a longest contiguous piece of s that reads the same
// forwards and backwards. Letters are case-sensitive, so 'A' and 'a' are different
// characters. When several pieces tie for the longest length, any one of them is
// accepted.
// s has length from 1 to 1000 and holds only digits and English letters.
// Examples: "babad" returns "bab" ("aba" is also accepted); "cbbd" returns "bb".
// Required: O(n^2) time or better.
// Run: javac --release 8 LongestPalindromicSubstring.java && java LongestPalindromicSubstring

import java.util.*;

class Solution {
    public String longestPalindrome(String s) {
        // TODO: implement
        return "";
    }
}

public class LongestPalindromicSubstring {

    private static boolean isPalindrome(String t) {
        for (int i = 0, j = t.length() - 1; i < j; i++, j--) {
            if (t.charAt(i) != t.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    private static String show(String t) {
        if (t.length() > 40) {
            return "\"" + t.substring(0, 37) + "...\"";
        }
        return "\"" + t + "\"";
    }

    private static String repeat(String unit, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(unit);
        }
        return sb.toString();
    }

    // Any longest palindromic piece is accepted: the answer must be a substring of s,
    // must read the same in both directions, and must have the expected length.
    private static void check(int caseNum, String s, int expectedLen, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        String expected = "a palindromic substring of length " + expectedLen;
        try {
            String got = new Solution().longestPalindrome(s);
            String problem = null;
            if (got == null) {
                problem = "null";
            } else if (!s.contains(got)) {
                problem = show(got) + " (not a substring of s)";
            } else if (!isPalindrome(got)) {
                problem = show(got) + " (not a palindrome)";
            } else if (got.length() != expectedLen) {
                problem = show(got) + " (length " + got.length() + ")";
            }
            if (problem == null) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + problem);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, "babad", 3, fail, total);
        check(2, "cbbd", 2, fail, total);
        check(3, "a", 1, fail, total);
        check(4, "ac", 1, fail, total);
        check(5, "zzzzzz", 6, fail, total);
        check(6, "xyzabccba", 6, fail, total);
        check(7, "qwertyracecar", 7, fail, total);
        check(8, "abcdeXYYZedcba", 2, fail, total);
        check(9, "x12321y", 5, fail, total);
        check(10, "AbBa", 1, fail, total);
        check(11, repeat("k", 1000), 1000, fail, total);
        check(12, repeat("ab", 500), 999, fail, total);
        check(13, "abaxyzzyxf", 6, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>10. LC 26 Remove Duplicates from Sorted Array (Easy). Save as RemoveDuplicatesFromSortedArray.java</summary>

```java
// Remove Duplicates from Sorted Array
// ref: LC 26
// nums is sorted in non-decreasing order. Rewrite it in place so that each distinct
// value appears exactly once, in the original order, at the front of the array, and
// return the count k of distinct values. Only the first k positions are examined;
// whatever is stored after position k - 1 is ignored. Do not build a second array
// to hold the answer.
// Array length is from 1 to 30000; each value is from -100 to 100.
// Example: [0,0,1,1,1,2] returns 3 and its first 3 elements must be [0,1,2].
// Required: O(n) time, O(1) extra space.
// Run: javac --release 8 RemoveDuplicatesFromSortedArray.java && java RemoveDuplicatesFromSortedArray

import java.util.*;

class Solution {
    public int removeDuplicates(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class RemoveDuplicatesFromSortedArray {

    private static String preview(int[] a, int count) {
        int shown = Math.max(0, Math.min(count, a.length));
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < shown && i < 12; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(a[i]);
        }
        if (shown > 12) {
            sb.append(", ...");
        }
        sb.append("]");
        return sb.toString();
    }

    // The solution gets a copy of the input. The check passes when the returned count
    // equals the number of distinct values and the first k elements of the copy are
    // exactly those distinct values in order.
    private static void check(int caseNum, int[] nums, int[] unique, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        String expected = "k=" + unique.length + " " + preview(unique, unique.length);
        int[] work = Arrays.copyOf(nums, nums.length);
        try {
            int k = new Solution().removeDuplicates(work);
            boolean ok = (k == unique.length);
            for (int i = 0; ok && i < k; i++) {
                if (work[i] != unique[i]) {
                    ok = false;
                }
            }
            if (ok) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected
                        + " got k=" + k + " " + preview(work, k));
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    private static int[] filled(int value, int count) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    private static int[] range(int lo, int hi) {
        int[] a = new int[hi - lo + 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = lo + i;
        }
        return a;
    }

    // 30000 values that climb from -100 to 100, every value present, many repeats.
    private static int[] stepped() {
        int[] a = new int[30000];
        for (int i = 0; i < a.length; i++) {
            a[i] = -100 + (int) ((long) i * 201 / a.length);
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{1,1,2}, new int[]{1,2}, fail, total);
        check(2, new int[]{0,0,1,1,1,2,2,3,3,4}, new int[]{0,1,2,3,4}, fail, total);
        check(3, new int[]{5}, new int[]{5}, fail, total);
        check(4, new int[]{2,2,2,2,2,2}, new int[]{2}, fail, total);
        check(5, new int[]{1,2,3,4,5}, new int[]{1,2,3,4,5}, fail, total);
        check(6, new int[]{-3,-3,-2,-1,-1,0}, new int[]{-3,-2,-1,0}, fail, total);
        check(7, new int[]{-100,-100,100,100}, new int[]{-100,100}, fail, total);
        check(8, new int[]{-100,100}, new int[]{-100,100}, fail, total);
        check(9, new int[]{1,2,3,3,3}, new int[]{1,2,3}, fail, total);
        check(10, new int[]{1,1,1,2,3}, new int[]{1,2,3}, fail, total);
        check(11, new int[]{-5,-5,0,0,0,5,5,5,5}, new int[]{-5,0,5}, fail, total);
        check(12, stepped(), range(-100, 100), fail, total);
        check(13, filled(-100, 30000), new int[]{-100}, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>11. LC 53 Maximum Subarray (Medium). Save as MaximumSubarray.java</summary>

```java
// Maximum Subarray
// ref: LC 53
// Given an integer array nums, consider every non-empty run of adjacent elements
// (a subarray) and return the largest sum among them. The run must contain at
// least one element, so if every value is negative the answer is the largest
// single value.
// Array length is from 1 to 100000; each value is from -10000 to 10000.
// Examples: [-2, 1, -3, 4, -1, 2, 1, -5, 4] returns 6 (the run 4, -1, 2, 1);
// [-3, -1, -2] returns -1.
// Required: O(n) time, O(1) space.
// Run: javac --release 8 MaximumSubarray.java && java MaximumSubarray

import java.util.*;

class Solution {
    public int maxSubArray(int[] nums) {
        // TODO: implement
        return 0;
    }
}

public class MaximumSubarray {

    private static void check(int caseNum, int[] nums, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxSubArray(nums);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    private static int[] filled(int value, int count) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    // 100000 values: a rich run, a dip worth crossing, another rich run, a poor tail.
    private static int[] generated() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            if (i < 40000) {
                a[i] = 10000;
            } else if (i < 50000) {
                a[i] = -10000;
            } else if (i < 90000) {
                a[i] = 10000;
            } else {
                a[i] = -10000;
            }
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, 6, fail, total);
        check(2, new int[]{1}, 1, fail, total);
        check(3, new int[]{5, 4, -1, 7, 8}, 23, fail, total);
        check(4, new int[]{-1}, -1, fail, total);
        check(5, new int[]{-10000}, -10000, fail, total);
        check(6, new int[]{10000}, 10000, fail, total);
        check(7, new int[]{-3, -2, -5, -1, -4}, -1, fail, total);
        check(8, new int[]{-2, -1}, -1, fail, total);
        check(9, new int[]{8, -19, 5, -4, 20}, 21, fail, total);
        check(10, new int[]{4, -10, 3, 3}, 6, fail, total);
        check(11, new int[]{2, -1, 2, -1, 2}, 4, fail, total);
        check(12, filled(10000, 100000), 1000000000, fail, total);
        check(13, generated(), 700000000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>12. LC 410 Split Array Largest Sum (Hard). Save as SplitArrayLargestSum.java</summary>

```java
// Split Array Largest Sum
// ref: LC 410
// Given an array nums of non-negative integers and an integer k, cut nums into
// exactly k non-empty contiguous parts (each part is a run of adjacent elements and
// the order of the elements is unchanged). The cost of a cut is the largest sum
// among its k parts. Return the smallest cost over all ways to cut.
// Input: int[] nums, int k. Output: an int.
// Constraints: 1 <= nums.length <= 1000; 0 <= nums[i] <= 1000000;
// 1 <= k <= min(50, nums.length).
// Example: nums = [7,2,5,10,8], k = 2 returns 18 (parts [7,2,5] and [10,8] have
// sums 14 and 18, and no other cut has a smaller largest sum).
// Example: nums = [1,2,3], k = 3 returns 3 (every element is its own part).
// Required complexity: O(n log(sum(nums))) time, O(1) extra space
// Run: javac --release 8 SplitArrayLargestSum.java && java SplitArrayLargestSum

import java.util.*;

class Solution {
    public int splitArray(int[] nums, int k) {
        // TODO: implement
        return 0;
    }
}

public class SplitArrayLargestSum {

    // Builds an array of count copies of value.
    private static int[] filled(int count, int value) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    // Builds a repeatable pseudo-random array with values in 0..maxValue.
    private static int[] pseudoRandom(int count, int maxValue, long seed) {
        int[] a = new int[count];
        long x = seed;
        for (int i = 0; i < count; i++) {
            x = (x * 1103515245L + 12345L) & 0x7fffffffL;
            a[i] = (int) ((x >> 8) % (maxValue + 1L));
        }
        return a;
    }

    private static String brief(int[] a) {
        if (a.length <= 12) {
            return Arrays.toString(a);
        }
        return Arrays.toString(Arrays.copyOf(a, 12)) + "...(length " + a.length + ")";
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{7,2,5,10,8}, 2, 18 },
            { new int[]{1,2,3,4,5}, 2, 9 },
            { new int[]{5}, 1, 5 },
            { new int[]{3,1,4,1,5}, 5, 5 },
            { new int[]{2,3,1,2,4,3}, 1, 15 },
            { new int[]{0,0,0,0}, 2, 0 },
            { new int[]{0,5,0,0,7,0}, 3, 7 },
            { new int[]{1,1,1000000,1,1}, 3, 1000000 },
            { new int[]{1,4,1,1,1}, 2, 5 },
            { new int[]{1,2,3,4,5,6,7,8,9}, 3, 17 },
            { filled(1000, 1000000), 1, 1000000000 },
            { filled(1000, 1000000), 50, 20000000 },
            { pseudoRandom(1000, 1000000, 12345L), 50, 9790029 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int k = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            try {
                int actual = new Solution().splitArray(nums, k);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (nums=" + brief(nums) + ", k=" + k + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>13. LC 70 Climbing Stairs (Easy). Save as ClimbingStairs.java</summary>

```java
// Climbing Stairs
// ref: LC 70
// A staircase has n steps. Each move climbs either 1 step or 2 steps. Return the
// number of different move sequences that go from the ground to exactly the top
// step. Order matters: 1 then 2 and 2 then 1 are different sequences.
// n is from 1 to 45; the answer for every legal n fits in an int.
// Examples: n = 3 returns 3 (1+1+1, 1+2, 2+1); n = 4 returns 5.
// Required: O(n) time, O(1) space.
// Run: javac --release 8 ClimbingStairs.java && java ClimbingStairs

import java.util.*;

class Solution {
    public int climbStairs(int n) {
        // TODO: implement
        return 0;
    }
}

public class ClimbingStairs {

    private static void check(int caseNum, int n, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().climbStairs(n);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, 1, 1, fail, total);
        check(2, 2, 2, fail, total);
        check(3, 3, 3, fail, total);
        check(4, 4, 5, fail, total);
        check(5, 5, 8, fail, total);
        check(6, 6, 13, fail, total);
        check(7, 10, 89, fail, total);
        check(8, 20, 10946, fail, total);
        check(9, 30, 1346269, fail, total);
        check(10, 40, 165580141, fail, total);
        check(11, 43, 701408733, fail, total);
        check(12, 44, 1134903170, fail, total);
        check(13, 45, 1836311903, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>14. LC 3914 Minimum Operations to Make Array Non-Decreasing (Medium). Save as MinimumOperationsToMakeArrayNonDecreasing.java</summary>

```java
// Minimum Operations to Make Array Non-Decreasing
// ref: LC 3914
// You are given an array nums of positive integers. One operation picks a
// contiguous range of positions l..r (both ends included) and a positive integer
// x, and adds x to every element in that range. The cost of the operation is x
// itself (the amount added), no matter how long the range is. Return the smallest
// total cost of any sequence of operations that leaves the array non-decreasing,
// meaning nums[i] <= nums[i + 1] for every i.
// Input: int[] nums (positions are 0-based). Output: long, the smallest total
// cost (0 if the array is already non-decreasing). The answer can be larger than
// the int range.
// Examples:
//   [3,3,2,1] gives 2 (add 1 to positions 2..3 to get [3,3,3,2], then add 1 to
//   position 3 to get [3,3,3,3]).
//   [5,1,2,3] gives 4 (add 4 to positions 1..3 to get [5,5,6,7]).
// Constraints: 1 <= nums.length <= 100000, 1 <= nums[i] <= 1000000000
// Required complexity: O(n) time
// Run: javac --release 8 MinimumOperationsToMakeArrayNonDecreasing.java && java MinimumOperationsToMakeArrayNonDecreasing

import java.util.*;

class Solution {
    public long minOperations(int[] nums) {
        // TODO: implement
        return 0L;
    }
}

public class MinimumOperationsToMakeArrayNonDecreasing {

    private static void check(int caseNum, int[] nums, long expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            long got = new Solution().minOperations(nums);
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    // 6 elements alternating 1000000000 and 1: three drops of 999999999.
    private static int[] sixSwings() {
        return new int[]{1000000000, 1, 1000000000, 1, 1000000000, 1};
    }

    // 100000 elements alternating 1000000000 and 1: 50000 drops of 999999999.
    private static int[] longSwings() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (i % 2 == 0) ? 1000000000 : 1;
        }
        return a;
    }

    // 100000 elements in 30 flat steps; each step is 30000000 lower than the
    // one before it (29 drops in total).
    private static int[] longStaircase() {
        int[] a = new int[100000];
        for (int i = 0; i < a.length; i++) {
            a[i] = 1000000000 - 30000000 * (i / 3334);
        }
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new int[]{3, 3, 2, 1}, 2L, fail, total);
        check(2, new int[]{5, 1, 2, 3}, 4L, fail, total);
        check(3, new int[]{1}, 0L, fail, total);
        check(4, new int[]{1, 2, 3, 4, 5}, 0L, fail, total);
        check(5, new int[]{2, 2, 2}, 0L, fail, total);
        check(6, new int[]{1000000000, 1}, 999999999L, fail, total);
        check(7, new int[]{5, 4, 3, 2, 1}, 4L, fail, total);
        check(8, new int[]{1, 5, 2, 6, 3, 7}, 6L, fail, total);
        check(9, new int[]{10, 3, 7, 1, 9, 2}, 20L, fail, total);
        check(10, sixSwings(), 2999999997L, fail, total);
        check(11, new int[]{4, 1, 4, 1, 4, 1}, 9L, fail, total);
        check(12, longSwings(), 49999999950000L, fail, total);
        check(13, longStaircase(), 870000000L, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>15. LC 124 Binary Tree Maximum Path Sum (Hard). Save as BinaryTreeMaximumPathSum.java</summary>

```java
// Binary Tree Maximum Path Sum
// ref: LC 124
// A path in a binary tree is a chain of nodes where each adjacent pair is joined
// by a parent-child edge and no node is used twice. The chain may start and end
// at any nodes and does not have to pass through the root. The sum of a path is
// the total of its node values. Given the root of a tree, return the largest sum
// over all non-empty paths.
// Input: the root of a tree with 1 to 30000 nodes, each value in -1000..1000.
// Output: the maximum path sum, as an int.
// Example 1: tree [1,2,3] gives 6 (the path 2, 1, 3).
// Example 2: tree [-10,9,20,null,null,15,7] gives 42 (the path 15, 20, 7; the
//            root is not used).
// Example 3: tree [-3] gives -3 (a path needs at least one node, so the answer
//            can be negative).
// Trees are written in level order, with null for a missing child.
// Required complexity: O(n) time, O(h) space (h is the height of the tree)
// Run: javac --release 8 BinaryTreeMaximumPathSum.java && java BinaryTreeMaximumPathSum

import java.util.*;

class Solution {
    public int maxPathSum(BinaryTreeMaximumPathSum.TreeNode root) {
        // TODO: implement
        return 0;
    }
}

public class BinaryTreeMaximumPathSum {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    // Standard level-order tree builder: null entries mark a missing child, and
    // that position's subtree is simply not expanded further.
    private static TreeNode build(Integer[] values) {
        if (values.length == 0 || values[0] == null) {
            return null;
        }
        TreeNode root = new TreeNode(values[0]);
        Deque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode current = queue.poll();
            if (i < values.length) {
                Integer leftVal = values[i++];
                if (leftVal != null) {
                    current.left = new TreeNode(leftVal);
                    queue.add(current.left);
                }
            }
            if (i < values.length) {
                Integer rightVal = values[i++];
                if (rightVal != null) {
                    current.right = new TreeNode(rightVal);
                    queue.add(current.right);
                }
            }
        }
        return root;
    }

    // Level-order array of a complete tree with n nodes, every value equal to val.
    private static Integer[] complete(int n, int val) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = val;
        }
        return values;
    }

    // Level-order array of a tree that is a single chain of n nodes, each node the
    // right child of the one before it. The node at depth d (root is depth 0)
    // holds evenVal when d is even and oddVal when d is odd.
    private static Integer[] rightChain(int n, int evenVal, int oddVal) {
        Integer[] values = new Integer[2 * n - 1];
        values[0] = evenVal;
        for (int d = 1; d < n; d++) {
            values[2 * d - 1] = null;
            values[2 * d] = (d % 2 == 0) ? evenVal : oddVal;
        }
        return values;
    }

    private static void check(int caseNum, Integer[] values, int expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            int got = new Solution().maxPathSum(build(values));
            if (got == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got);
            }
        } catch (Exception | StackOverflowError e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new Integer[]{1, 2, 3}, 6, fail, total);
        check(2, new Integer[]{-10, 9, 20, null, null, 15, 7}, 42, fail, total);
        check(3, new Integer[]{-3}, -3, fail, total);
        check(4, new Integer[]{-1000}, -1000, fail, total);
        check(5, new Integer[]{-5, -2, -9, -3, -7}, -2, fail, total);
        check(6, new Integer[]{-1, -2, -3}, -1, fail, total);
        check(7, new Integer[]{1, -2, -3, 1, 3, -2, null, -1}, 3, fail, total);
        check(8, new Integer[]{100, 1, null, 10, 10}, 111, fail, total);
        check(9, new Integer[]{1, null, 2, null, 3, null, 4}, 10, fail, total);
        check(10, new Integer[]{1, -5, null, 10}, 10, fail, total);
        check(11, new Integer[]{-1000, 1000, 1000}, 1000, fail, total);
        check(12, rightChain(1000, 5, -1), 2001, fail, total);
        check(13, complete(30000, 1000), 29000, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>16. LC 236 Lowest Common Ancestor of a Binary Tree (Medium). Save as LowestCommonAncestorOfABinaryTree.java</summary>

```java
// Lowest Common Ancestor of a Binary Tree
// ref: LC 236
// Given a binary tree and two different nodes p and q that both belong to it,
// return the node that is the lowest (deepest) one having both p and q somewhere
// in its subtree. A node counts as being in its own subtree, so if p sits above
// q, the answer is p itself.
// Input: the root of a tree with 2 to 100000 nodes whose values are all distinct,
// plus the nodes p and q. The test runner is given the tree as a level-order
// array and two values, and looks up the two nodes by value.
// Output: the lowest common node itself (the runner checks its value).
// Example 1: tree [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1 gives the node
//            with value 3.
// Example 2: same tree, p = 5, q = 4 gives the node with value 5, because 4 is
//            inside the subtree of 5.
// Trees are written in level order, with null for a missing child. The values
// are not arranged as a search tree, so do not rely on their order.
// Required complexity: O(n) time, O(h) space (h is the height of the tree)
// Run: javac --release 8 LowestCommonAncestorOfABinaryTree.java && java LowestCommonAncestorOfABinaryTree

import java.util.*;

class Solution {
    public LowestCommonAncestorOfABinaryTree.TreeNode lowestCommonAncestor(
            LowestCommonAncestorOfABinaryTree.TreeNode root,
            LowestCommonAncestorOfABinaryTree.TreeNode p,
            LowestCommonAncestorOfABinaryTree.TreeNode q) {
        // TODO: implement
        return null;
    }
}

public class LowestCommonAncestorOfABinaryTree {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    // Standard level-order tree builder: null entries mark a missing child, and
    // that position's subtree is simply not expanded further.
    private static TreeNode build(Integer[] values) {
        if (values.length == 0 || values[0] == null) {
            return null;
        }
        TreeNode root = new TreeNode(values[0]);
        Deque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode current = queue.poll();
            if (i < values.length) {
                Integer leftVal = values[i++];
                if (leftVal != null) {
                    current.left = new TreeNode(leftVal);
                    queue.add(current.left);
                }
            }
            if (i < values.length) {
                Integer rightVal = values[i++];
                if (rightVal != null) {
                    current.right = new TreeNode(rightVal);
                    queue.add(current.right);
                }
            }
        }
        return root;
    }

    // Breadth-first search for the node holding the given value.
    private static TreeNode find(TreeNode root, int val) {
        Deque<TreeNode> queue = new ArrayDeque<TreeNode>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            if (current.val == val) {
                return current;
            }
            if (current.left != null) {
                queue.add(current.left);
            }
            if (current.right != null) {
                queue.add(current.right);
            }
        }
        return null;
    }

    // Level-order array of a complete tree with n nodes; the node in position i
    // (1-based) holds the value i, so the parent of value v is v / 2.
    private static Integer[] completeCounting(int n) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = i + 1;
        }
        return values;
    }

    // Level-order array of a chain of n nodes, each the right child of the one
    // before it. The node at depth d (root is depth 1) holds (37 * d) % 1009,
    // so the values are distinct and not in any sorted order.
    private static Integer[] scrambledRightChain(int n) {
        Integer[] values = new Integer[2 * n - 1];
        values[0] = 37 % 1009;
        for (int d = 2; d <= n; d++) {
            values[2 * d - 3] = null;
            values[2 * d - 2] = (37 * d) % 1009;
        }
        return values;
    }

    private static void check(int caseNum, Integer[] values, int pVal, int qVal, int expected,
                              int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            TreeNode root = build(values);
            TreeNode p = find(root, pVal);
            TreeNode q = find(root, qVal);
            TreeNode got = new Solution().lowestCommonAncestor(root, p, q);
            if (got == null) {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got null");
            } else if (got.val == expected) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected " + expected + " got " + got.val);
            }
        } catch (Exception | StackOverflowError e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected " + expected + " got exception " + e);
        }
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        Integer[] sample = new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
        Integer[] perfect15 = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        Integer[] signed = new Integer[]{0, -1, 5, -3, null, 4, 9};

        check(1, sample, 5, 1, 3, fail, total);
        check(2, sample, 5, 4, 5, fail, total);
        check(3, new Integer[]{1, 2}, 1, 2, 1, fail, total);
        check(4, new Integer[]{1, null, 2}, 2, 1, 1, fail, total);
        check(5, perfect15, 8, 9, 4, fail, total);
        check(6, perfect15, 8, 11, 2, fail, total);
        check(7, perfect15, 8, 15, 1, fail, total);
        check(8, new Integer[]{1, 2, null, 3, null, 4, null, 5}, 5, 3, 3, fail, total);
        check(9, new Integer[]{5, null, 3, null, 8, null, 1, null, 9}, 1, 9, 1, fail, total);
        check(10, signed, -3, 4, 0, fail, total);
        check(11, signed, 4, 9, 5, fail, total);
        check(12, completeCounting(100000), 99999, 100000, 1562, fail, total);
        check(13, scrambledRightChain(1000), 676, 2, 2, fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>17. LC 14 Longest Common Prefix (Easy). Save as LongestCommonPrefix.java</summary>

```java
// Longest Common Prefix
// ref: LC 14
// Given an array of strings, return the longest string that is a prefix of every
// string in the array. If the strings share no first character (or one of them is
// empty), the answer is the empty string.
// Input: String[] strs. Output: String, the longest common prefix, or "".
// Examples:
//   ["flower","flow","flight"] gives "fl".
//   ["dog","racecar","car"] gives "" (nothing is shared).
// Constraints: 1 <= strs.length <= 200, 0 <= strs[i].length() <= 200, every
// character is a lowercase English letter
// Required complexity: O(S) time, where S is the total number of characters in
// the input, and O(1) extra space (not counting the returned string)
// Run: javac --release 8 LongestCommonPrefix.java && java LongestCommonPrefix

import java.util.*;

class Solution {
    public String longestCommonPrefix(String[] strs) {
        // TODO: implement
        return "";
    }
}

public class LongestCommonPrefix {

    private static void check(int caseNum, String[] strs, String expected, int[] failCount, int[] totalCount) {
        totalCount[0]++;
        try {
            String got = new Solution().longestCommonPrefix(strs);
            if (expected.equals(got)) {
                System.out.println("PASS case " + caseNum);
            } else {
                failCount[0]++;
                System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got " + (got == null ? "null" : "\"" + got + "\""));
            }
        } catch (Exception e) {
            failCount[0]++;
            System.out.println("FAIL case " + caseNum + ": expected \"" + expected + "\" got exception " + e);
        }
    }

    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // 200 copies of a string made of 200 letters 'a'.
    private static String[] allMaxLength() {
        String[] a = new String[200];
        for (int i = 0; i < a.length; i++) {
            a[i] = repeat('a', 200);
        }
        return a;
    }

    // 199 strings of 200 letters 'a', then one string with 199 'a' and a final 'b'.
    private static String[] lastOneBreaksTheEnd() {
        String[] a = new String[200];
        for (int i = 0; i < a.length - 1; i++) {
            a[i] = repeat('a', 200);
        }
        a[a.length - 1] = repeat('a', 199) + "b";
        return a;
    }

    public static void main(String[] args) {
        int[] fail = new int[]{0};
        int[] total = new int[]{0};

        check(1, new String[]{"flower", "flow", "flight"}, "fl", fail, total);
        check(2, new String[]{"dog", "racecar", "car"}, "", fail, total);
        check(3, new String[]{"alone"}, "alone", fail, total);
        check(4, new String[]{""}, "", fail, total);
        check(5, new String[]{"abc", "", "abcd"}, "", fail, total);
        check(6, new String[]{"", "abc"}, "", fail, total);
        check(7, new String[]{"ab", "abc", "abcd"}, "ab", fail, total);
        check(8, new String[]{"same", "same"}, "same", fail, total);
        check(9, new String[]{"cir", "car"}, "c", fail, total);
        check(10, new String[]{"abcd", "ab", "abc"}, "ab", fail, total);
        check(11, new String[]{"abc", "abd", "ax"}, "a", fail, total);
        check(12, allMaxLength(), repeat('a', 200), fail, total);
        check(13, lastOneBreaksTheEnd(), repeat('a', 199), fail, total);

        int passed = total[0] - fail[0];
        System.out.println(passed + "/" + total[0] + " passed");
        if (fail[0] > 0) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>18. LC 141 Linked List Cycle (Easy). Save as LinkedListCycle.java</summary>

```java
// Linked List Cycle
// ref: LC 141
// Given the head of a singly linked list, decide whether the list contains a
// cycle: whether following next pointers from the head ever reaches a node
// that was already visited. Return true if it does, false if the chain ends at
// null. The function receives only the head; nothing in the list records where
// a cycle starts. In the tests below, each row gives the node values and the
// index that the last node's next pointer is linked back to (-1 means the last
// node's next stays null).
// Input:  head, the first node of the list (null for an empty list).
// Output: true if some node can be reached twice by following next, else false.
// Constraints: 0 to 10000 nodes; node values are between -100000 and 100000
// and may repeat, so equal values say nothing about two nodes being the same.
// Example 1: values [3,2,0,-4] with the last node linked back to index 1
//            (the node holding 2) give true.
// Example 2: values [1,2] with the last node's next left as null give false.
// Required complexity: O(n) time, O(1) extra space, where n is the node count.
// Run: javac --release 8 LinkedListCycle.java && java LinkedListCycle

import java.util.*;

class Solution {
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    public boolean hasCycle(ListNode head) {
        // TODO: implement
        return false;
    }
}

public class LinkedListCycle {

    public static void main(String[] args) {
        Object[][] cases = {
            {intArr(3,2,0,-4), 1, true},
            {intArr(1,2), 0, true},
            {intArr(1), -1, false},
            {intArr(), -1, false},
            {intArr(1), 0, true},
            {intArr(1,2,3,4,5), 0, true},
            {intArr(1,2,3,4,5,6), 2, true},
            {intArr(1,2,3,4), 3, true},
            {intArr(7,7,7,7,7), -1, false},
            {intArr(1,2), 1, true},
            {intArr(4,4,4,4), -1, false},
            // largest allowed size: all values equal and no cycle
            {rep(7,10000), -1, false},
            // largest allowed size: distinct values, tail linked back to the middle
            {ramp(10000), 5000, true}
        };
        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] vals = (int[]) cases[i][0];
            int pos = (Integer) cases[i][1];
            boolean expected = (Boolean) cases[i][2];
            try {
                Solution.ListNode[] nodes = buildNodes(vals);
                if (pos >= 0) {
                    nodes[nodes.length - 1].next = nodes[pos];
                }
                Solution.ListNode head = nodes.length > 0 ? nodes[0] : null;
                boolean got = new Solution().hasCycle(head);
                if (got == expected) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + got);
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

    private static int[] intArr(int... vals) {
        return vals;
    }

    private static int[] rep(int value, int count) {
        int[] out = new int[count];
        Arrays.fill(out, value);
        return out;
    }

    // count distinct values, centred on zero
    private static int[] ramp(int count) {
        int[] out = new int[count];
        for (int i = 0; i < count; i++) {
            out[i] = i - count / 2;
        }
        return out;
    }

    private static Solution.ListNode[] buildNodes(int[] vals) {
        Solution.ListNode[] nodes = new Solution.ListNode[vals.length];
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new Solution.ListNode(vals[i]);
        }
        for (int i = 0; i < vals.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        return nodes;
    }
}
```

</details>

<details>
<summary>19. LC 35 Search Insert Position (Easy). Save as SearchInsertPosition.java</summary>

```java
// Search Insert Position
// ref: LC 35
// Given an array nums sorted in ascending order with all values distinct, and an
// integer target, return the index of target if it is present. If it is not
// present, return the index at which it would have to be inserted to keep the
// array sorted.
// Input: int[] nums, int target. Output: an int in the range 0..nums.length.
// Constraints: 1 <= nums.length <= 10000; -10000 <= nums[i], target <= 10000;
// nums is strictly increasing.
// Example: nums = [1,3,5,6], target = 5 returns 2.
// Example: nums = [1,3,5,6], target = 2 returns 1 (it would go between 1 and 3).
// Required complexity: O(log n) time, O(1) extra space
// Run: javac --release 8 SearchInsertPosition.java && java SearchInsertPosition

import java.util.*;

class Solution {
    public int searchInsert(int[] nums, int target) {
        // TODO: implement
        return 0;
    }
}

public class SearchInsertPosition {

    // Builds the values start, start + step, start + 2 * step, ... (count values).
    private static int[] range(int start, int count, int step) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) {
            a[i] = start + i * step;
        }
        return a;
    }

    private static String brief(int[] a) {
        if (a.length <= 12) {
            return Arrays.toString(a);
        }
        return Arrays.toString(Arrays.copyOf(a, 12)) + "...(length " + a.length + ")";
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{1,3,5,6}, 5, 2 },
            { new int[]{1,3,5,6}, 2, 1 },
            { new int[]{1,3,5,6}, 7, 4 },
            { new int[]{1,3,5,6}, 0, 0 },
            { new int[]{1}, 1, 0 },
            { new int[]{1}, 0, 0 },
            { new int[]{1}, 2, 1 },
            { new int[]{1,3}, 2, 1 },
            { new int[]{-10,-5,0,5,10}, -7, 1 },
            { range(-10000, 10000, 2), 9999, 10000 },
            { range(-10000, 10000, 2), 1, 5001 },
            { range(-10000, 10000, 2), 4000, 7000 },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int target = (Integer) cases[i][1];
            int expected = (Integer) cases[i][2];
            try {
                int actual = new Solution().searchInsert(nums, target);
                if (Integer.compare(actual, expected) == 0) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got " + actual
                            + " (nums=" + brief(nums) + ", target=" + target + ")");
                }
            } catch (Exception e) {
                System.out.println("FAIL case " + (i + 1) + ": expected " + expected + " got exception "
                        + e);
            }
        }
        System.out.println(passed + "/" + cases.length + " passed");
        if (passed != cases.length) {
            System.exit(1);
        }
    }
}
```

</details>

<details>
<summary>20. LC 34 Find First and Last Position of Element in Sorted Array (Medium). Save as FindFirstAndLastPositionOfElementInSortedArray.java</summary>

```java
// Find First and Last Position of Element in Sorted Array
// ref: LC 34
// Given an integer array nums sorted in non-decreasing order (duplicates allowed)
// and an integer target, return the index of the first occurrence and the index of
// the last occurrence of target as {first, last}. If target does not occur in nums,
// return {-1, -1}.
// Input: int[] nums, int target. Output: an int[] of length 2.
// Constraints: 0 <= nums.length <= 100000;
// -1000000000 <= nums[i], target <= 1000000000.
// Example: nums = [5,7,7,8,8,10], target = 8 returns {3,4}.
// Example: nums = [5,7,7,8,8,10], target = 6 returns {-1,-1}.
// Required complexity: O(log n) time, O(1) extra space
// Run: javac --release 8 FindFirstAndLastPositionOfElementInSortedArray.java && java FindFirstAndLastPositionOfElementInSortedArray

import java.util.*;

class Solution {
    public int[] searchRange(int[] nums, int target) {
        // TODO: implement
        return new int[0];
    }
}

public class FindFirstAndLastPositionOfElementInSortedArray {

    // Builds an array of count copies of value.
    private static int[] filled(int count, int value) {
        int[] a = new int[count];
        Arrays.fill(a, value);
        return a;
    }

    // Builds 0,0,...,1,1,...,2,2,... where every value appears times times.
    private static int[] repeatEach(int count, int times) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) {
            a[i] = i / times;
        }
        return a;
    }

    private static String brief(int[] a) {
        if (a.length <= 12) {
            return Arrays.toString(a);
        }
        return Arrays.toString(Arrays.copyOf(a, 12)) + "...(length " + a.length + ")";
    }

    public static void main(String[] args) {
        Object[][] cases = new Object[][] {
            { new int[]{5,7,7,8,8,10}, 8, new int[]{3,4} },
            { new int[]{5,7,7,8,8,10}, 6, new int[]{-1,-1} },
            { new int[]{}, 0, new int[]{-1,-1} },
            { new int[]{1}, 1, new int[]{0,0} },
            { new int[]{1}, 2, new int[]{-1,-1} },
            { new int[]{7,7,7,7,7}, 7, new int[]{0,4} },
            { new int[]{3,3,5,7,9}, 3, new int[]{0,1} },
            { new int[]{2,4,6,6,6}, 6, new int[]{2,4} },
            { new int[]{-9,-5,-5,-5,0,4}, -5, new int[]{1,3} },
            { new int[]{1,1,3,3}, 2, new int[]{-1,-1} },
            { new int[]{4,5,6}, 3, new int[]{-1,-1} },
            { filled(100000, 1000000000), 1000000000, new int[]{0,99999} },
            { repeatEach(100000, 4), 12345, new int[]{49380,49383} },
        };

        int passed = 0;
        for (int i = 0; i < cases.length; i++) {
            int[] nums = (int[]) cases[i][0];
            int target = (Integer) cases[i][1];
            int[] expected = (int[]) cases[i][2];
            try {
                int[] actual = new Solution().searchRange(nums, target);
                if (Arrays.equals(actual, expected)) {
                    System.out.println("PASS case " + (i + 1));
                    passed++;
                } else {
                    System.out.println("FAIL case " + (i + 1) + ": expected " + Arrays.toString(expected)
                            + " got " + Arrays.toString(actual)
                            + " (nums=" + brief(nums) + ", target=" + target + ")");
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
```

</details>

