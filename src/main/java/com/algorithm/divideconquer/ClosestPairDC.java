package com.algorithm.divideconquer;

import java.util.*;

public class ClosestPairDC {
    
    public static AircraftPair findClosestPair(List<Aircraft> aircraft) {
        if (aircraft.size() < 2) return null;
        List<Aircraft> sortedByX = new ArrayList<>(aircraft);
        sortedByX.sort(Comparator.comparingDouble(Aircraft::getX));
        return closestPairRecursive(sortedByX);
    }
    
    private static AircraftPair closestPairRecursive(List<Aircraft> points) {
        int n = points.size();
        if (n <= 3) return bruteForce(points);
        
        int mid = n / 2;
        Aircraft midPoint = points.get(mid);
        List<Aircraft> leftHalf = points.subList(0, mid);
        List<Aircraft> rightHalf = points.subList(mid, n);
        
        AircraftPair leftPair = closestPairRecursive(leftHalf);
        AircraftPair rightPair = closestPairRecursive(rightHalf);
        
        double minDist = Double.MAX_VALUE;
        AircraftPair minPair = null;
        
        if (leftPair != null && leftPair.getDistance() < minDist) {
            minDist = leftPair.getDistance();
            minPair = leftPair;
        }
        if (rightPair != null && rightPair.getDistance() < minDist) {
            minDist = rightPair.getDistance();
            minPair = rightPair;
        }
        
        AircraftPair stripPair = checkStrip(points, midPoint.getX(), minDist);
        if (stripPair != null && stripPair.getDistance() < minDist) {
            return stripPair;
        }
        return minPair;
    }
    
    private static AircraftPair checkStrip(List<Aircraft> points, double midX, double minDist) {
        List<Aircraft> strip = new ArrayList<>();
        for (Aircraft a : points) {
            if (Math.abs(a.getX() - midX) < minDist) {
                strip.add(a);
            }
        }
        strip.sort(Comparator.comparingDouble(Aircraft::getY));
        
        AircraftPair minPair = null;
        double minStripDist = minDist;
        
        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < Math.min(i + 8, strip.size()); j++) {
                AircraftPair pair = new AircraftPair(strip.get(i), strip.get(j));
                if (pair.getDistance() < minStripDist) {
                    minStripDist = pair.getDistance();
                    minPair = pair;
                }
            }
        }
        return minPair;
    }
    
    private static AircraftPair bruteForce(List<Aircraft> points) {
        double minDist = Double.MAX_VALUE;
        AircraftPair minPair = null;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                AircraftPair pair = new AircraftPair(points.get(i), points.get(j));
                if (pair.getDistance() < minDist) {
                    minDist = pair.getDistance();
                    minPair = pair;
                }
            }
        }
        return minPair;
    }
    
    public static AircraftPair findClosestPairBruteForce(List<Aircraft> aircraft) {
        return bruteForce(aircraft);
    }
}
