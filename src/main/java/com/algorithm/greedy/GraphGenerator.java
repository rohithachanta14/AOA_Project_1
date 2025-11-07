package com.algorithm.greedy;
import java.util.*;

public class GraphGenerator {
    private Random random;

    public GraphGenerator(int seed) {
        this.random = new Random(seed);
    }

    public InfluenceMaximization generateBarabasiAlbert(int n, int m, String model) {
        InfluenceMaximization im = new InfluenceMaximization(model, random.nextInt());
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                im.addEdge(i, j);
                im.addEdge(j, i);
            }
        }
        List<Integer> targets = new ArrayList<>();
        for (int i = 0; i < m; i++) targets.add(i);

        for (int i = m; i < n; i++) {
            Set<Integer> selected = new HashSet<>();
            while (selected.size() < m) {
                selected.add(targets.get(random.nextInt(targets.size())));
            }
            for (int t : selected) {
                im.addEdge(i, t);
                im.addEdge(t, i);
                targets.add(t);
                targets.add(i);
            }
        }
        im.initializePropagationParameters();
        return im;
    }

    public InfluenceMaximization generateWattsStrogatz(int n, int k, double p, String model) {
        InfluenceMaximization im = new InfluenceMaximization(model, random.nextInt());
        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= k / 2; j++) {
                int target = (i + j) % n;
                im.addEdge(i, target);
                im.addEdge(target, i);
            }
        }
        im.initializePropagationParameters();
        return im;
    }

    public InfluenceMaximization generateErdosRenyi(int n, double p, String model) {
        InfluenceMaximization im = new InfluenceMaximization(model, random.nextInt());
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && random.nextDouble() < p) {
                    im.addEdge(i, j);
                }
            }
        }
        im.initializePropagationParameters();
        return im;
    }

    public InfluenceMaximization generateSmallGraph(String model) {
        InfluenceMaximization im = new InfluenceMaximization(model, 42);
        int[][] edges = {{0,1}, {0,2}, {1,3}, {2,3}, {3,4}, {3,5}, 
                        {4,6}, {5,6}, {1,7}, {2,7}, {7,8}, {8,9}};
        for (int[] e : edges) im.addEdge(e[0], e[1]);
        im.initializePropagationParameters();
        return im;
    }
}
