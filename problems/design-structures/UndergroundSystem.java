import java.util.HashMap;

/**
 * LC 1396 - tracks riders checking in and out of stations and reports the
 * average travel time between any two stations.
 * checkIn(id, stationName, t): a rider with this id starts a trip at t.
 * checkOut(id, stationName, t): the same rider ends the trip at t.
 * getAverageTime(startStation, endStation): mean travel time over every
 * completed trip recorded for that exact ordered pair of stations.
 */
public class UndergroundSystem {

    static class CheckInInfo {
        final String station;
        final int time;
        CheckInInfo(String station, int time) { this.station = station; this.time = time; }
    }

    static class RouteStats {
        long totalTime = 0;
        int count = 0;
    }

    static class UndergroundSystemImpl {
        // id -> the in-progress trip's start station and start time
        private final HashMap<Integer, CheckInInfo> inProgress;
        // "startStation->endStation" -> running total time and trip count for that route
        private final HashMap<String, RouteStats> routeStats;

        UndergroundSystemImpl() {
            inProgress = new HashMap<Integer, CheckInInfo>();
            routeStats = new HashMap<String, RouteStats>();
        }

        void checkIn(int id, String stationName, int t) {
            inProgress.put(id, new CheckInInfo(stationName, t));
        }

        void checkOut(int id, String stationName, int t) {
            CheckInInfo start = inProgress.remove(id);
            // A "->" string key is simple and fast, but relies on station names never
            // containing the delimiter; see .md for the Pair/nested-map alternative.
            String routeKey = start.station + "->" + stationName;
            RouteStats stats = routeStats.get(routeKey);
            if (stats == null) {
                stats = new RouteStats();
                routeStats.put(routeKey, stats);
            }
            stats.totalTime += (t - start.time);
            stats.count++;
        }

        double getAverageTime(String startStation, String endStation) {
            RouteStats stats = routeStats.get(startStation + "->" + endStation);
            return (double) stats.totalTime / stats.count;
        }
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

    public static void main(String[] args) {
        // case 1: classic example - two riders on the same route, averaged together
        UndergroundSystemImpl sys1 = new UndergroundSystemImpl();
        sys1.checkIn(45, "Leyton", 3);
        sys1.checkIn(32, "Paradise", 8);
        sys1.checkIn(27, "Leyton", 10);
        sys1.checkOut(45, "Waterloo", 15); // Leyton->Waterloo = 12
        sys1.checkOut(27, "Waterloo", 20); // Leyton->Waterloo = 10, avg with above = 11
        checkDouble(1, "Leyton to Waterloo averaged over two riders", sys1.getAverageTime("Leyton", "Waterloo"), 11.0);
        sys1.checkOut(32, "Cambridge", 22); // Paradise->Cambridge = 14
        checkDouble(2, "Paradise to Cambridge single rider", sys1.getAverageTime("Paradise", "Cambridge"), 14.0);
        sys1.checkIn(10, "Leyton", 24);
        sys1.checkOut(10, "Waterloo", 38); // Leyton->Waterloo third trip = 14, avg = (12+10+14)/3
        checkDouble(3, "Leyton to Waterloo after a third trip", sys1.getAverageTime("Leyton", "Waterloo"), 12.0);

        // case 4: reversed direction is a different route with its own average
        UndergroundSystemImpl sys2 = new UndergroundSystemImpl();
        sys2.checkIn(1, "A", 0);
        sys2.checkOut(1, "B", 10); // A->B = 10
        sys2.checkIn(2, "B", 0);
        sys2.checkOut(2, "A", 3); // B->A = 3, unrelated to A->B
        checkDouble(4, "A to B is separate from B to A (A->B)", sys2.getAverageTime("A", "B"), 10.0);
        checkDouble(5, "A to B is separate from B to A (B->A)", sys2.getAverageTime("B", "A"), 3.0);

        // case 6: single-trip route where check-in and check-out share the same station name pattern
        UndergroundSystemImpl sys3 = new UndergroundSystemImpl();
        sys3.checkIn(7, "X", 100);
        sys3.checkOut(7, "Y", 105);
        checkDouble(6, "single trip average equals that one trip", sys3.getAverageTime("X", "Y"), 5.0);

        // case 7: multiple riders in flight concurrently on different ids do not interfere
        UndergroundSystemImpl sys4 = new UndergroundSystemImpl();
        sys4.checkIn(1, "S", 0);
        sys4.checkIn(2, "S", 1);
        sys4.checkOut(2, "T", 4); // rider 2: S->T = 3
        sys4.checkOut(1, "T", 5); // rider 1: S->T = 5, avg = 4
        checkDouble(7, "concurrent in-flight riders tracked independently", sys4.getAverageTime("S", "T"), 4.0);

        System.out.println(passed + "/" + total + " passed");
        if (passed != total) System.exit(1);
    }
}
