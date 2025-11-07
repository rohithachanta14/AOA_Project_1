package com.algorithm.divideconquer;

public class Aircraft {
    private String id;
    private double x, y, z;
    
    public Aircraft(String id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public String getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    
    public double distanceTo(Aircraft other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    @Override
    public String toString() {
        return String.format("%s(%.1f, %.1f, %.1f)", id, x, y, z);
    }
}
