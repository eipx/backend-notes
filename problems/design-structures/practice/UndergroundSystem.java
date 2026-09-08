/**
 * Underground System
 * ref: LC 1396
 *
 * Implement class UndergroundSystem: checkIn(id, stationName, t) records
 * that rider id starts a trip at station stationName at time t; checkOut
 * (id, stationName, t) records that the same rider ends their in-progress
 * trip at station stationName at time t; getAverageTime(startStation,
 * endStation) returns the mean travel time over every completed trip
 * recorded for that exact ordered pair of stations. Each method must run in
 * O(1) average time.
 *
 * Note: the required class name (UndergroundSystem) coincides with this
 * file's public class name, so the fill-in methods below and the test
 * runner live in one class (Java forbids two top-level types sharing a name
 * in one file) - checkIn/checkOut/getAverageTime below are exactly the
 * methods to implement.
 *
 * study page: ../underground-system.md
 * run: javac --release 8 UndergroundSystem.java && java UndergroundSystem
 */
import java.util.*;

public class UndergroundSystem {

    public UndergroundSystem() {
        // TODO: implement
    }

    public void checkIn(int id, String stationName, int t) {
        // TODO: implement
    }

    public void checkOut(int id, String stationName, int t) {
        // TODO: implement
    }

    public double getAverageTime(String startStation, String endStation) {
        // TODO: implement
        return 0.0;
    }

    // ---------------------------------------------------------------
    // test harness
    // ---------------------------------------------------------------
    static int passed = 0;
    static int total = 0;

    static void checkDouble(int caseNum, String label, double got, double expected) {
        total++;
        if (Math.abs(got - expected) < 1e-5) {
            passed++;
            System.out.println("PASS case " + caseNum + " (" + label + ")");
        } else {
            System.out.println("FAIL case " + caseNum + " (" + label + "): expected " + expected + " got " + got);
        }
    }

    static void fail(int caseNum, String label, Exception e) {
        total++;
        System.out.println("FAIL case " + caseNum + " (" + label + "): threw " + e);
    }

    public static void main(String[] args) {
        // case 1-3: classic example - two riders on the same route, averaged together
        UndergroundSystem sys1 = new UndergroundSystem();
        try {
            sys1.checkIn(45, "Leyton", 3);
            sys1.checkIn(32, "Paradise", 8);
            sys1.checkIn(27, "Leyton", 10);
            sys1.checkOut(45, "Waterloo", 15); // Leyton->Waterloo = 12
            sys1.checkOut(27, "Waterloo", 20); // Leyton->Waterloo = 10, avg with above = 11
            checkDouble(1, "Leyton to Waterloo averaged over two riders", sys1.getAverageTime("Leyton", "Waterloo"), 11.0);
        } catch (Exception e) { fail(1, "Leyton to Waterloo averaged over two riders", e); }

        try {
            sys1.checkOut(32, "Cambridge", 22); // Paradise->Cambridge = 14
            checkDouble(2, "Paradise to Cambridge single rider", sys1.getAverageTime("Paradise", "Cambridge"), 14.0);
        } catch (Exception e) { fail(2, "Paradise to Cambridge single rider", e); }

        try {
            sys1.checkIn(10, "Leyton", 24);
            sys1.checkOut(10, "Waterloo", 38); // Leyton->Waterloo third trip = 14, avg = (12+10+14)/3
            checkDouble(3, "Leyton to Waterloo after a third trip", sys1.getAverageTime("Leyton", "Waterloo"), 12.0);
        } catch (Exception e) { fail(3, "Leyton to Waterloo after a third trip", e); }

        // case 4-5: reversed direction is a different route with its own average
        UndergroundSystem sys2 = new UndergroundSystem();
        try {
            sys2.checkIn(1, "A", 0);
            sys2.checkOut(1, "B", 10); // A->B = 10
            sys2.checkIn(2, "B", 0);
            sys2.checkOut(2, "A", 3); // B->A = 3, unrelated to A->B
            checkDouble(4, "A to B is separate from B to A (A->B)", sys2.getAverageTime("A", "B"), 10.0);
        } catch (Exception e) { fail(4, "A to B is separate from B to A (A->B)", e); }

        try {
            checkDouble(5, "A to B is separate from B to A (B->A)", sys2.getAverageTime("B", "A"), 3.0);
        } catch (Exception e) { fail(5, "A to B is separate from B to A (B->A)", e); }

        // case 6: single-trip route where check-in and check-out share the same station name pattern
        UndergroundSystem sys3 = new UndergroundSystem();
        try {
            sys3.checkIn(7, "X", 100);
            sys3.checkOut(7, "Y", 105);
            checkDouble(6, "single trip average equals that one trip", sys3.getAverageTime("X", "Y"), 5.0);
        } catch (Exception e) { fail(6, "single trip average equals that one trip", e); }

        // case 7: multiple riders in flight concurrently on different ids do not interfere
        UndergroundSystem sys4 = new UndergroundSystem();
        try {
            sys4.checkIn(1, "S", 0);
            sys4.checkIn(2, "S", 1);
            sys4.checkOut(2, "T", 4); // rider 2: S->T = 3
            sys4.checkOut(1, "T", 5); // rider 1: S->T = 5, avg = 4
            checkDouble(7, "concurrent in-flight riders tracked independently", sys4.getAverageTime("S", "T"), 4.0);
        } catch (Exception e) { fail(7, "concurrent in-flight riders tracked independently", e); }

        System.out.println(passed + "/" + total + " passed");
        if (passed != total) System.exit(1);
    }
}
