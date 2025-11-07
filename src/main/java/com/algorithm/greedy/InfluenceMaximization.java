package com.algorithm.greedy;

/**
 * Influence Maximization - Greedy and CELF Algorithms
 * Graduate Algorithm Project - November 2025
 */
import java.util.*;

public class InfluenceMaximization {
    private Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
    private Map<Integer, List<Integer>> reverseGraph = new HashMap<>();
    private Map<String, Double> edgeWeights = new HashMap<>();
    private Set<Integer> nodes = new HashSet<>();
    private String model;
    private Random random;
    private int influenceEvaluations = 0;
    private int numNodes, numEdges;

    public InfluenceMaximization(String model, int seed) {
        this.model = model;
        this.random = new Random(seed);
    }

    public void addEdge(int u, int v) {
        adjacencyList.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
        reverseGraph.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
        nodes.add(u);
        nodes.add(v);
        numEdges++;
    }

    public void initializePropagationParameters() {
        numNodes = nodes.size();
        if (model.equals("IC")) {
            for (int v : nodes) {
                List<Integer> preds = reverseGraph.getOrDefault(v, new ArrayList<>());
                int inDeg = preds.size();
                if (inDeg > 0) {
                    for (int u : preds) {
                        edgeWeights.put(u + "->" + v, 1.0 / inDeg);
                    }
                }
            }
        } else if (model.equals("LT")) {
            for (int v : nodes) {
                List<Integer> preds = reverseGraph.getOrDefault(v, new ArrayList<>());
                if (preds.size() > 0) {
                    double[] w = new double[preds.size()];
                    double sum = 0;
                    for (int i = 0; i < w.length; i++) {
                        w[i] = random.nextDouble();
                        sum += w[i];
                    }
                    for (int i = 0; i < w.length; i++) {
                        edgeWeights.put(preds.get(i) + "->" + v, w[i] / (sum * 1.1));
                    }
                }
            }
        }
    }

    private Set<Integer> simulateIC(Set<Integer> seeds) {
        Set<Integer> active = new HashSet<>(seeds);
        Set<Integer> newlyActive = new HashSet<>(seeds);
        while (!newlyActive.isEmpty()) {
            Set<Integer> nextActive = new HashSet<>();
            for (int u : newlyActive) {
                for (int v : adjacencyList.getOrDefault(u, new ArrayList<>())) {
                    if (!active.contains(v)) {
                        double prob = edgeWeights.getOrDefault(u + "->" + v, 0.0);
                        if (random.nextDouble() < prob) {
                            nextActive.add(v);
                        }
                    }
                }
            }
            active.addAll(nextActive);
            newlyActive = nextActive;
        }
        return active;
    }

    private Set<Integer> simulateLT(Set<Integer> seeds) {
        Map<Integer, Double> thresholds = new HashMap<>();
        for (int v : nodes) thresholds.put(v, random.nextDouble());

        Set<Integer> active = new HashSet<>(seeds);
        Set<Integer> newlyActive = new HashSet<>(seeds);

        while (!newlyActive.isEmpty()) {
            Set<Integer> nextActive = new HashSet<>();
            for (int v : nodes) {
                if (!active.contains(v)) {
                    double influence = 0.0;
                    for (int u : reverseGraph.getOrDefault(v, new ArrayList<>())) {
                        if (active.contains(u)) {
                            influence += edgeWeights.getOrDefault(u + "->" + v, 0.0);
                        }
                    }
                    if (influence >= thresholds.get(v)) {
                        nextActive.add(v);
                    }
                }
            }
            active.addAll(nextActive);
            newlyActive = nextActive;
        }
        return active;
    }

    public double estimateInfluence(Set<Integer> seeds, int numSim) {
        influenceEvaluations++;
        if (seeds.isEmpty()) return 0.0;
        long total = 0;
        for (int i = 0; i < numSim; i++) {
            Set<Integer> influenced = model.equals("IC") ? simulateIC(seeds) : simulateLT(seeds);
            total += influenced.size();
        }
        return (double) total / numSim;
    }

    public static class Result {
        public Set<Integer> seeds = new HashSet<>();
        public List<IterationHistory> history = new ArrayList<>();
        public double totalTime;
        public int totalEvaluations;
    }

    public static class IterationHistory {
        public int iteration, node, evaluations;
        public double marginalGain, totalSpread, time;

        public IterationHistory(int iter, int n, double mg, double ts, double t, int e) {
            iteration = iter; node = n; marginalGain = mg;
            totalSpread = ts; time = t; evaluations = e;
        }

        public String toString() {
            return String.format("Seed %d: Node %d | Spread: %.2f | Time: %.2fs",
                iteration, node, totalSpread, time);
        }
    }

    public Result greedyIM(int k, int numSim, boolean verbose) {
        if (verbose) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Naive Greedy (k=" + k + ", model=" + model + ")");
            System.out.println("=".repeat(60));
        }

        Result result = new Result();
        Set<Integer> S = new HashSet<>();
        influenceEvaluations = 0;
        long start = System.currentTimeMillis();

        for (int i = 0; i < k; i++) {
            long iterStart = System.currentTimeMillis();
            int bestNode = -1;
            double bestGain = -1;

            for (int u : nodes) {
                if (!S.contains(u)) {
                    Set<Integer> Su = new HashSet<>(S);
                    Su.add(u);
                    double gain = estimateInfluence(Su, numSim) - 
                                 (S.isEmpty() ? 0 : estimateInfluence(S, numSim));
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestNode = u;
                    }
                }
            }

            S.add(bestNode);
            double spread = estimateInfluence(S, numSim);
            double iterTime = (System.currentTimeMillis() - iterStart) / 1000.0;

            IterationHistory h = new IterationHistory(i+1, bestNode, bestGain, 
                                                     spread, iterTime, influenceEvaluations);
            result.history.add(h);
            if (verbose) System.out.println(h);
        }

        result.totalTime = (System.currentTimeMillis() - start) / 1000.0;
        result.totalEvaluations = influenceEvaluations;
        result.seeds = S;

        if (verbose) {
            System.out.printf("\nTotal: %.2fs, %d evaluations\n", 
                            result.totalTime, result.totalEvaluations);
        }
        return result;
    }

    private static class CELFNode implements Comparable<CELFNode> {
        int nodeId, iteration;
        double marginalGain;

        CELFNode(int id, double gain, int iter) {
            nodeId = id; marginalGain = gain; iteration = iter;
        }

        public int compareTo(CELFNode o) {
            return Double.compare(o.marginalGain, marginalGain);
        }
    }

    public Result celfIM(int k, int numSim, boolean verbose) {
        if (verbose) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("CELF Algorithm (k=" + k + ", model=" + model + ")");
            System.out.println("=".repeat(60));
        }

        long start = System.currentTimeMillis();
        influenceEvaluations = 0;

        PriorityQueue<CELFNode> Q = new PriorityQueue<>();
        for (int u : nodes) {
            Set<Integer> single = new HashSet<>();
            single.add(u);
            double gain = estimateInfluence(single, numSim);
            Q.add(new CELFNode(u, gain, 0));
        }

        Result result = new Result();
        Set<Integer> S = new HashSet<>();
        double spread = 0;
        int iteration = 0;

        while (S.size() < k) {
            long iterStart = System.currentTimeMillis();
            CELFNode top = Q.poll();

            if (top.iteration == iteration) {
                S.add(top.nodeId);
                spread = estimateInfluence(S, numSim);
                double iterTime = (System.currentTimeMillis() - iterStart) / 1000.0;

                IterationHistory h = new IterationHistory(iteration+1, top.nodeId,
                    top.marginalGain, spread, iterTime, influenceEvaluations);
                result.history.add(h);

                if (verbose) {
                    System.out.printf("Seed %d/%d: Node %d | Spread: %.2f | Evals: %d\n",
                        S.size(), k, top.nodeId, spread, influenceEvaluations);
                }
                iteration++;
            } else {
                Set<Integer> Su = new HashSet<>(S);
                Su.add(top.nodeId);
                double newGain = estimateInfluence(Su, numSim) - spread;
                Q.add(new CELFNode(top.nodeId, newGain, iteration));
            }
        }

        result.totalTime = (System.currentTimeMillis() - start) / 1000.0;
        result.totalEvaluations = influenceEvaluations;
        result.seeds = S;

        if (verbose) {
            System.out.printf("\nTotal: %.2fs, %d evals (%.2fx speedup)\n",
                result.totalTime, result.totalEvaluations,
                (double)(k * numNodes) / result.totalEvaluations);
        }
        return result;
    }

    public int getNumNodes() { return numNodes; }
    public int getNumEdges() { return numEdges; }
    public Set<Integer> getNodes() { return new HashSet<>(nodes); }
}
