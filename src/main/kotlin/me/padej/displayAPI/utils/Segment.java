package me.padej.displayAPI.utils;

import org.bukkit.Location;

public class Segment {
    private final Location pointA;
    private final Location pointB;

    public Segment(Location pointA, Location pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public double length() {
        return pointA.distance(pointB);
    }

    public Location getMidpoint() {
        double midX = (pointA.getX() + pointB.getX()) / 2;
        double midY = (pointA.getY() + pointB.getY()) / 2;
        double midZ = (pointA.getZ() + pointB.getZ()) / 2;
        return new Location(pointA.getWorld(), midX, midY, midZ);
    }

    public Location[] getMinMaxPoints() {
        double minX = Math.min(pointA.getX(), pointB.getX());
        double maxX = Math.max(pointA.getX(), pointB.getX());
        double minY = Math.min(pointA.getY(), pointB.getY());
        double maxY = Math.max(pointA.getY(), pointB.getY());
        double minZ = Math.min(pointA.getZ(), pointB.getZ());
        double maxZ = Math.max(pointA.getZ(), pointB.getZ());

        Location minPoint = new Location(pointA.getWorld(), minX, minY, minZ);
        Location maxPoint = new Location(pointA.getWorld(), maxX, maxY, maxZ);

        return new Location[]{minPoint, maxPoint};
    }

    public Location getPointOnSegment(double t) {
        double x = pointA.getX() + (pointB.getX() - pointA.getX()) * t;
        double y = pointA.getY() + (pointB.getY() - pointA.getY()) * t;
        double z = pointA.getZ() + (pointB.getZ() - pointA.getZ()) * t;
        return new Location(pointA.getWorld(), x, y, z);
    }
}
