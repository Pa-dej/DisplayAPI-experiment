package me.padej.displayAPI.utils;

import org.bukkit.util.Vector;

public class PointDetection {
    public static boolean lookingAtPoint(Vector eye, Vector direction, Vector point, double tolerance) {
        double pointDistance = eye.distance(point);
        Vector lookingAtPoint = eye.clone().add(direction.clone().multiply(pointDistance));
        return lookingAtPoint.distance(point) < tolerance;
    }
}
