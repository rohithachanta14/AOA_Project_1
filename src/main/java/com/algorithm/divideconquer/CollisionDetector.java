package com.algorithm.divideconquer;

import java.util.*;

public class CollisionDetector {
    private static final double COLLISION_THRESHOLD_KM = 5.0;
    private List<Aircraft> activeAircraft;
    
    public CollisionDetector() {
        this.activeAircraft = new ArrayList<>();
    }
    
    public void addAircraft(Aircraft aircraft) {
        activeAircraft.add(aircraft);
    }
    
    public void addMultipleAircraft(List<Aircraft> aircraft) {
        activeAircraft.addAll(aircraft);
    }
    
    public AircraftPair detectCollisionRiskDC() {
        if (activeAircraft.size() < 2) return null;
        return ClosestPairDC.findClosestPair(activeAircraft);
    }
    
    public AircraftPair detectCollisionRiskBruteForce() {
        if (activeAircraft.size() < 2) return null;
        return ClosestPairDC.findClosestPairBruteForce(activeAircraft);
    }
    
    public boolean isCollisionRisk(AircraftPair pair) {
        return pair != null && pair.isCollisionRisk(COLLISION_THRESHOLD_KM);
    }
    
    public int getAircraftCount() { return activeAircraft.size(); }
    public List<Aircraft> getActiveAircraft() { return new ArrayList<>(activeAircraft); }
    
    public static List<Aircraft> generateRandomAircraft(int count, Random random) {
        List<Aircraft> aircraft = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = "FL" + String.format("%04d", i + 1);
            double x = random.nextDouble() * 1000;
            double y = random.nextDouble() * 1000;
            double z = random.nextDouble() * 15;
            aircraft.add(new Aircraft(id, x, y, z));
        }
        return aircraft;
    }
}
