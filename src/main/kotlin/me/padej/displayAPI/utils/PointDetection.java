package me.padej.displayAPI.utils;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PointDetection {
    public static boolean lookingAtPoint(@NotNull Vector eye, @NotNull Vector direction, Vector point, double tolerance) {
        return lookingAtPoint(eye, direction, point, tolerance, tolerance);
    }

    public static boolean lookingAtPoint(@NotNull Vector eye, @NotNull Vector direction, Vector point, double horizontalTolerance, double verticalTolerance) {
        double pointDistance = eye.distance(point);
        Vector lookingAtPoint = eye.clone().add(direction.clone().multiply(pointDistance));
        
        // Разделяем расстояние на горизонтальное и вертикальное
        double horizontalDist = Math.sqrt(Math.pow(lookingAtPoint.getX() - point.getX(), 2) + Math.pow(lookingAtPoint.getZ() - point.getZ(), 2));
        double verticalDist = Math.abs(lookingAtPoint.getY() - point.getY());
        
        return horizontalDist < horizontalTolerance && verticalDist < verticalTolerance;
    }
}
