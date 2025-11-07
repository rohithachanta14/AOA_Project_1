package com.algorithm.divideconquer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for Air Traffic Collision Detection (Divide & Conquer)
 */
public class TestCollisionDetection {

    private CollisionDetector detector;
    private static final double EPSILON = 0.001;

    @BeforeEach
    public void setUp() {
        detector = new CollisionDetector();
    }

    /**
     * Test 1: Simple case with 2 aircraft
     */
    @Test
    public void testTwoAircraft() {
        Aircraft a1 = new Aircraft("FL001", 0, 0, 5);
        Aircraft a2 = new Aircraft("FL002", 3, 4, 5);

        detector.addAircraft(a1);
        detector.addAircraft(a2);

        AircraftPair pair = detector.detectCollisionRiskDC();

        assertNotNull(pair);
        assertEquals(5.0, pair.getDistance(), EPSILON);
        assertEquals(2, detector.getAircraftCount());
    }

    /**
     * Test 2: Three aircraft - verify correct closest pair
     */
    @Test
    public void testThreeAircraft() {
        Aircraft a1 = new Aircraft("FL001", 0, 0, 0);
        Aircraft a2 = new Aircraft("FL002", 1, 0, 0);
        Aircraft a3 = new Aircraft("FL003", 10, 0, 0);

        detector.addAircraft(a1);
        detector.addAircraft(a2);
        detector.addAircraft(a3);

        AircraftPair pairDC = detector.detectCollisionRiskDC();
        AircraftPair pairBF = detector.detectCollisionRiskBruteForce();

        assertNotNull(pairDC);
        assertNotNull(pairBF);
        assertEquals(1.0, pairDC.getDistance(), EPSILON);
        assertEquals(1.0, pairBF.getDistance(), EPSILON);

        // Verify both methods find same distance
        assertEquals(pairDC.getDistance(), pairBF.getDistance(), EPSILON);
    }

    /**
     * Test 3: 3D distance calculation
     */
    @Test
    public void test3DDistance() {
        Aircraft a1 = new Aircraft("FL001", 0, 0, 0);
        Aircraft a2 = new Aircraft("FL002", 1, 1, 1);

        double expected = Math.sqrt(3); // sqrt(1^2 + 1^2 + 1^2)
        double actual = a1.distanceTo(a2);

        assertEquals(expected, actual, EPSILON);
    }

    /**
     * Test 4: Collision risk detection
     */
    @Test
    public void testCollisionRisk() {
        Aircraft a1 = new Aircraft("FL001", 0, 0, 5);
        Aircraft a2 = new Aircraft("FL002", 3, 0, 5);  // 3 km away

        detector.addAircraft(a1);
        detector.addAircraft(a2);

        AircraftPair pair = detector.detectCollisionRiskDC();

        assertTrue(detector.isCollisionRisk(pair), "Should detect collision risk at 3 km");
        assertEquals(3.0, pair.getDistance(), EPSILON);
    }

    /**
     * Test 5: No collision risk
     */
    @Test
    public void testNoCollisionRisk() {
        Aircraft a1 = new Aircraft("FL001", 0, 0, 5);
        Aircraft a2 = new Aircraft("FL002", 10, 0, 5);  // 10 km away

        detector.addAircraft(a1);
        detector.addAircraft(a2);

        AircraftPair pair = detector.detectCollisionRiskDC();

        assertFalse(detector.isCollisionRisk(pair), "Should NOT detect collision risk at 10 km");
    }

    /**
     * Test 6: Large dataset - verify D&C matches brute force
     */
    @Test
    public void testLargeDataset() {
        Random random = new Random(12345);
        List<Aircraft> aircraft = CollisionDetector.generateRandomAircraft(100, random);
        detector.addMultipleAircraft(aircraft);

        AircraftPair pairDC = detector.detectCollisionRiskDC();
        AircraftPair pairBF = detector.detectCollisionRiskBruteForce();

        assertNotNull(pairDC);
        assertNotNull(pairBF);

        // Both methods should find same minimum distance
        assertEquals(pairDC.getDistance(), pairBF.getDistance(), EPSILON);
    }

    /**
     * Test 7: Performance comparison - D&C should be faster
     */
    @Test
    public void testPerformanceComparison() {
        Random random = new Random(42);
        List<Aircraft> aircraft = CollisionDetector.generateRandomAircraft(500, random);
        detector.addMultipleAircraft(aircraft);

        // Time D&C approach
        long startDC = System.nanoTime();
        AircraftPair pairDC = detector.detectCollisionRiskDC();
        long timeDC = System.nanoTime() - startDC;

        // Time brute force
        long startBF = System.nanoTime();
        AircraftPair pairBF = detector.detectCollisionRiskBruteForce();
        long timeBF = System.nanoTime() - startBF;

        System.out.println("Performance Test (500 aircraft):");
        System.out.println("  D&C time: " + (timeDC / 1000) + " μs");
        System.out.println("  Brute Force time: " + (timeBF / 1000) + " μs");
        System.out.println("  Speedup: " + String.format("%.2fx", (double)timeBF / timeDC));

        // D&C should be faster for large datasets
        assertTrue(timeDC < timeBF, "D&C should be faster than brute force");

        // Both should find same result
        assertEquals(pairDC.getDistance(), pairBF.getDistance(), EPSILON);
    }

    /**
     * Test 8: Edge case - identical positions
     */
    @Test
    public void testIdenticalPositions() {
        Aircraft a1 = new Aircraft("FL001", 5, 5, 5);
        Aircraft a2 = new Aircraft("FL002", 5, 5, 5);

        detector.addAircraft(a1);
        detector.addAircraft(a2);

        AircraftPair pair = detector.detectCollisionRiskDC();

        assertNotNull(pair);
        assertEquals(0.0, pair.getDistance(), EPSILON);
        assertTrue(detector.isCollisionRisk(pair));
    }

    /**
     * Test 9: Vertical separation only
     */
    @Test
    public void testVerticalSeparation() {
        Aircraft a1 = new Aircraft("FL001", 0, 0, 5);
        Aircraft a2 = new Aircraft("FL002", 0, 0, 12);  // 7 km vertical

        detector.addAircraft(a1);
        detector.addAircraft(a2);

        AircraftPair pair = detector.detectCollisionRiskDC();

        assertNotNull(pair);
        assertEquals(7.0, pair.getDistance(), EPSILON);
        assertFalse(detector.isCollisionRisk(pair));
    }

    /**
     * Test 10: Strip boundary case
     * Tests that algorithm correctly handles points near the dividing line
     */
    @Test
    public void testStripBoundary() {
        // Create aircraft clustered near x=500 (potential dividing line)
        detector.addAircraft(new Aircraft("FL001", 499, 0, 5));
        detector.addAircraft(new Aircraft("FL002", 501, 0, 5));
        detector.addAircraft(new Aircraft("FL003", 0, 0, 5));
        detector.addAircraft(new Aircraft("FL004", 1000, 0, 5));

        AircraftPair pairDC = detector.detectCollisionRiskDC();
        AircraftPair pairBF = detector.detectCollisionRiskBruteForce();

        assertNotNull(pairDC);
        assertNotNull(pairBF);

        // Closest pair should be the two near x=500 (2 km apart)
        assertEquals(2.0, pairDC.getDistance(), EPSILON);
        assertEquals(2.0, pairBF.getDistance(), EPSILON);
    }

    /**
     * Test 11: Scaling test - verify O(n log n) complexity
     */
    @Test
    public void testScalingComplexity() {
        Random random = new Random(42);

        int[] sizes = {50, 100, 200, 400};
        long[] times = new long[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            CollisionDetector testDetector = new CollisionDetector();
            List<Aircraft> aircraft = CollisionDetector.generateRandomAircraft(sizes[i], random);
            testDetector.addMultipleAircraft(aircraft);

            long start = System.nanoTime();
            for (int j = 0; j < 10; j++) {  // Average over 10 runs
                testDetector.detectCollisionRiskDC();
            }
            times[i] = (System.nanoTime() - start) / 10;
        }

        System.out.println("\nScaling Analysis (D&C):");
        for (int i = 0; i < sizes.length; i++) {
            System.out.println("  n=" + sizes[i] + ": " + (times[i]/1000) + " μs");
        }

        // Verify roughly O(n log n) scaling
        // Time ratio should be approximately (n2/n1) * log(n2)/log(n1)
        double timeRatio = (double)times[3] / times[0];  // 400 vs 50
        double expectedRatio = (400.0 / 50.0) * (Math.log(400) / Math.log(50));

        System.out.println("  Time ratio (400/50): " + String.format("%.2f", timeRatio));
        System.out.println("  Expected O(n log n): " + String.format("%.2f", expectedRatio));

        // Allow 50% tolerance due to constant factors
        assertTrue(timeRatio < expectedRatio * 1.5, 
                   "Time scaling should be close to O(n log n)");
    }

    /**
     * Test 12: Empty and single aircraft edge cases
     */
    @Test
    public void testEdgeCases() {
        // Empty detector
        AircraftPair emptyResult = detector.detectCollisionRiskDC();
        assertNull(emptyResult);

        // Single aircraft
        detector.addAircraft(new Aircraft("FL001", 0, 0, 5));
        AircraftPair singleResult = detector.detectCollisionRiskDC();
        assertNull(singleResult);
    }

    /**
     * Test 13: Deterministic results
     */
    @Test
    public void testDeterministicResults() {
        Random random1 = new Random(999);
        Random random2 = new Random(999);

        List<Aircraft> aircraft1 = CollisionDetector.generateRandomAircraft(100, random1);
        List<Aircraft> aircraft2 = CollisionDetector.generateRandomAircraft(100, random2);

        CollisionDetector detector1 = new CollisionDetector();
        CollisionDetector detector2 = new CollisionDetector();

        detector1.addMultipleAircraft(aircraft1);
        detector2.addMultipleAircraft(aircraft2);

        AircraftPair pair1 = detector1.detectCollisionRiskDC();
        AircraftPair pair2 = detector2.detectCollisionRiskDC();

        // Same seed should produce same results
        assertEquals(pair1.getDistance(), pair2.getDistance(), EPSILON);
    }

    /**
     * Test 14: All aircraft at same altitude (2D closest pair)
     */
    @Test
    public void test2DClosestPair() {
        detector.addAircraft(new Aircraft("FL001", 0, 0, 10));
        detector.addAircraft(new Aircraft("FL002", 3, 4, 10));
        detector.addAircraft(new Aircraft("FL003", 100, 100, 10));

        AircraftPair pair = detector.detectCollisionRiskDC();

        // Distance should be sqrt(3^2 + 4^2) = 5.0
        assertEquals(5.0, pair.getDistance(), EPSILON);
    }

    /**
     * Test 15: Stress test with 1000 aircraft
     */
    @Test
    public void testStressTest() {
        Random random = new Random(777);
        List<Aircraft> aircraft = CollisionDetector.generateRandomAircraft(1000, random);
        detector.addMultipleAircraft(aircraft);

        long start = System.nanoTime();
        AircraftPair pair = detector.detectCollisionRiskDC();
        long duration = System.nanoTime() - start;

        assertNotNull(pair);
        assertTrue(pair.getDistance() >= 0);

        System.out.println("\nStress Test (1000 aircraft): " + (duration/1000) + " μs");

        // Should complete in reasonable time (< 10ms)
        assertTrue(duration < 10_000_000, "Should complete within 10ms");
    }
}
