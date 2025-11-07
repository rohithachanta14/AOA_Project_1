package com.algorithm.divideconquer;

public class AircraftPair {
    private Aircraft a1, a2;
    private double distance;
    
    public AircraftPair(Aircraft a1, Aircraft a2) {
        this.a1 = a1;
        this.a2 = a2;
        this.distance = a1.distanceTo(a2);
    }
    
    public Aircraft getA1() { return a1; }
    public Aircraft getA2() { return a2; }
    public double getDistance() { return distance; }
    
    public boolean isCollisionRisk(double threshold) {
        return distance < threshold;
    }
    
    @Override
    public String toString() {
        return String.format("%s ↔ %s: %.2f km", a1.getId(), a2.getId(), distance);
    }
}
