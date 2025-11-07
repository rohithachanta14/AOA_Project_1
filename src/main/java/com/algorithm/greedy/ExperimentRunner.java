package com.algorithm.greedy;

import java.util.*;
import java.io.*;

public class ExperimentRunner {
    private String outDir;
    private GraphGenerator gen;

    public ExperimentRunner(String dir) {
        this.outDir = dir;
        this.gen = new GraphGenerator(42);
        new File(dir).mkdirs();
    }

    public void exp1Scaling() throws Exception {
        System.out.println("\nEXP 1: Scaling Analysis");
        PrintWriter w = new PrintWriter(outDir + "/exp1_scaling.csv");
        w.println("graph,n,m,k,algorithm,runtime,spread,evaluations");

        for (int n : new int[]{50, 100, 200, 500}) {
            System.out.println("  Testing n=" + n);
            InfluenceMaximization im = gen.generateBarabasiAlbert(n, 3, "IC");
            long start = System.currentTimeMillis();
            InfluenceMaximization.Result r = im.celfIM(10, 500, false);
            double time = (System.currentTimeMillis() - start) / 1000.0;
            double spread = r.history.get(r.history.size()-1).totalSpread;
            w.printf("BA_%d,%d,%d,10,CELF,%.2f,%.2f,%d\n",
                     n, n, im.getNumEdges(), time, spread, r.totalEvaluations);
        }
        w.close();
        System.out.println("  ✓ Saved exp1_scaling.csv");
    }

    public void exp2Comparison() throws Exception {
        System.out.println("\nEXP 2: Greedy vs CELF");
        PrintWriter w = new PrintWriter(outDir + "/exp2_comparison.csv");
        w.println("k,algorithm,runtime,evaluations,spread");

        for (int k : new int[]{5, 10, 15, 20}) {
            System.out.println("  k=" + k);
            InfluenceMaximization im1 = gen.generateBarabasiAlbert(200, 3, "IC");
            long s1 = System.currentTimeMillis();
            InfluenceMaximization.Result r1 = im1.greedyIM(k, 300, false);
            double t1 = (System.currentTimeMillis() - s1) / 1000.0;

            InfluenceMaximization im2 = gen.generateBarabasiAlbert(200, 3, "IC");
            long s2 = System.currentTimeMillis();
            InfluenceMaximization.Result r2 = im2.celfIM(k, 300, false);
            double t2 = (System.currentTimeMillis() - s2) / 1000.0;

            w.printf("%d,Greedy,%.2f,%d,%.2f\n", k, t1, r1.totalEvaluations,
                     r1.history.get(r1.history.size()-1).totalSpread);
            w.printf("%d,CELF,%.2f,%d,%.2f\n", k, t2, r2.totalEvaluations,
                     r2.history.get(r2.history.size()-1).totalSpread);
        }
        w.close();
        System.out.println("  ✓ Saved exp2_comparison.csv");
    }

    public void exp3Spread() throws Exception {
        System.out.println("\nEXP 3: Influence Spread");
        for (String model : new String[]{"IC", "LT"}) {
            InfluenceMaximization im = gen.generateBarabasiAlbert(200, 3, model);
            InfluenceMaximization.Result r = im.celfIM(30, 500, false);

            PrintWriter w = new PrintWriter(outDir + "/exp3_spread_" + model + ".csv");
            w.println("k,spread,marginal_gain");
            for (InfluenceMaximization.IterationHistory h : r.history) {
                w.printf("%d,%.2f,%.2f\n", h.iteration, h.totalSpread, h.marginalGain);
            }
            w.close();
            System.out.println("  ✓ Saved exp3_spread_" + model + ".csv");
        }
    }

    public void exp4Networks() throws Exception {
        System.out.println("\nEXP 4: Network Types");
        PrintWriter w = new PrintWriter(outDir + "/exp4_network_types.csv");
        w.println("network,n,m,avg_degree,spread,spread_percent");

        InfluenceMaximization[] ims = {
            gen.generateBarabasiAlbert(100, 3, "IC"),
            gen.generateWattsStrogatz(100, 6, 0.3, "IC"),
            gen.generateErdosRenyi(100, 0.05, "IC")
        };
        String[] names = {"BA_100", "WS_100", "ER_100"};

        for (int i = 0; i < 3; i++) {
            InfluenceMaximization.Result r = ims[i].celfIM(15, 500, false);
            double spread = r.history.get(r.history.size()-1).totalSpread;
            w.printf("%s,100,%d,%.2f,%.2f,%.2f\n", names[i], ims[i].getNumEdges(),
                     2.0*ims[i].getNumEdges()/100, spread, 100*spread/100);
        }
        w.close();
        System.out.println("  ✓ Saved exp4_network_types.csv");
    }

    public void runAll() throws Exception {
        System.out.println("\n" + "#".repeat(60));
        System.out.println("# RUNNING ALL EXPERIMENTS");
        System.out.println("#".repeat(60));

        exp1Scaling();
        exp2Comparison();
        exp3Spread();
        exp4Networks();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✓ ALL EXPERIMENTS COMPLETE!");
        System.out.println("  Results saved to: " + outDir + "/");
        System.out.println("=".repeat(60));
    }

    public static void main(String[] args) {
        try {
            new ExperimentRunner("results").runAll();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
