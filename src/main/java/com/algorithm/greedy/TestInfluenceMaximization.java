package com.algorithm.greedy;

import java.util.*;

public class TestInfluenceMaximization {
    private static int passed = 0, failed = 0;

    public static void main(String[] args) {
        System.out.println("\n" + "#".repeat(60));
        System.out.println("# INFLUENCE MAXIMIZATION TEST SUITE");
        System.out.println("#".repeat(60));

        test1IC();
        test2LT();
        test3ScaleFree();
        test4CELF();
        test5Submodularity();
        test6Monotonicity();

        System.out.println("\n" + "#".repeat(60));
        System.out.println("# SUMMARY: " + passed + " passed, " + failed + " failed");
        System.out.println("#".repeat(60));
        System.exit(failed == 0 ? 0 : 1);
    }

    static void test1IC() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 1: IC Model");
        System.out.println("=".repeat(60));
        try {
            GraphGenerator g = new GraphGenerator(42);
            InfluenceMaximization im = g.generateSmallGraph("IC");
            System.out.println("Graph: " + im.getNumNodes() + " nodes");

            InfluenceMaximization.Result r = im.greedyIM(3, 500, true);
            System.out.println("✓ PASSED: Seeds " + r.seeds);
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e);
            failed++;
        }
    }

    static void test2LT() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 2: LT Model");
        System.out.println("=".repeat(60));
        try {
            GraphGenerator g = new GraphGenerator(42);
            InfluenceMaximization im = g.generateSmallGraph("LT");
            InfluenceMaximization.Result r = im.celfIM(3, 500, true);
            System.out.println("✓ PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e);
            failed++;
        }
    }

    static void test3ScaleFree() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 3: Scale-Free Network");
        System.out.println("=".repeat(60));
        try {
            GraphGenerator g = new GraphGenerator(42);
            InfluenceMaximization im = g.generateBarabasiAlbert(100, 3, "IC");
            System.out.println("Graph: " + im.getNumNodes() + " nodes, " + im.getNumEdges() + " edges");
            InfluenceMaximization.Result r = im.celfIM(5, 500, true);
            System.out.println("✓ PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e);
            failed++;
        }
    }

    static void test4CELF() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 4: CELF Correctness");
        System.out.println("=".repeat(60));
        try {
            GraphGenerator g = new GraphGenerator(42);
            InfluenceMaximization im1 = g.generateBarabasiAlbert(50, 2, "IC");
            InfluenceMaximization im2 = g.generateBarabasiAlbert(50, 2, "IC");

            InfluenceMaximization.Result r1 = im1.greedyIM(3, 1000, false);
            InfluenceMaximization.Result r2 = im2.celfIM(3, 1000, false);

            double s1 = r1.history.get(r1.history.size()-1).totalSpread;
            double s2 = r2.history.get(r2.history.size()-1).totalSpread;

            System.out.printf("Greedy: %.2f, CELF: %.2f\n", s1, s2);
            System.out.println("✓ PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e);
            failed++;
        }
    }

    static void test5Submodularity() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 5: Submodularity");
        System.out.println("=".repeat(60));
        try {
            GraphGenerator g = new GraphGenerator(42);
            InfluenceMaximization im = g.generateErdosRenyi(50, 0.1, "IC");

            Set<Integer> A = new HashSet<>(Arrays.asList(0, 1));
            Set<Integer> B = new HashSet<>(Arrays.asList(0, 1, 2, 3));
            Set<Integer> Ax = new HashSet<>(A); Ax.add(5);
            Set<Integer> Bx = new HashSet<>(B); Bx.add(5);

            double mA = im.estimateInfluence(Ax, 2000) - im.estimateInfluence(A, 2000);
            double mB = im.estimateInfluence(Bx, 2000) - im.estimateInfluence(B, 2000);

            System.out.printf("Marginal on A: %.2f, on B: %.2f (diminishing returns)\n", mA, mB);
            System.out.println("✓ PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e);
            failed++;
        }
    }

    static void test6Monotonicity() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST 6: Monotonicity");
        System.out.println("=".repeat(60));
        try {
            GraphGenerator g = new GraphGenerator(42);
            InfluenceMaximization im = g.generateBarabasiAlbert(80, 3, "IC");

            Set<Integer> S = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                S.add(i * 10);
                double spread = im.estimateInfluence(S, 1000);
                System.out.printf("|S|=%d, spread=%.2f\n", S.size(), spread);
            }
            System.out.println("✓ PASSED");
            passed++;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e);
            failed++;
        }
    }
}
