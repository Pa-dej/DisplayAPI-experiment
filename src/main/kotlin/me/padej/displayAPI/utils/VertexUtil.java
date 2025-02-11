package me.padej.displayAPI.utils;

import me.padej.displayAPI.render.shapes.DefaultCube;
import org.bukkit.Location;
import org.joml.Vector3f;

public class VertexUtil {

    // Метод для получения координат углов куба
    public static Location[] getCubeVertexes(DefaultCube cube) {
        float size = cube.getScale();
        Location[] corners = new Location[8];

        Vector3f offset = Alignment.getOffset(cube.getAlignmentType(), size);

        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    double xOffset = offset.x + (x == 0 ? -size / 2 : size / 2);
                    double yOffset = offset.y + (y == 0 ? -size / 2 : size / 2);
                    double zOffset = offset.z + (z == 0 ? -size / 2 : size / 2);

                    corners[x * 4 + y * 2 + z] = cube.getLocation().clone().add(xOffset + size / 2, yOffset + size / 2, zOffset + size / 2);
                }
            }
        }
        return corners;
    }

    // Метод для проверки, находится ли точка внутри области, образованной вершинами
    public static boolean isInsideVertexesArea(Location targetLocation, Location[] cubeVertexes) {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (Location vertex : cubeVertexes) {
            minX = java.lang.Math.min(minX, vertex.getX());
            maxX = java.lang.Math.max(maxX, vertex.getX());
            minY = java.lang.Math.min(minY, vertex.getY());
            maxY = java.lang.Math.max(maxY, vertex.getY());
            minZ = java.lang.Math.min(minZ, vertex.getZ());
            maxZ = java.lang.Math.max(maxZ, vertex.getZ());
        }

        return targetLocation.getX() >= minX && targetLocation.getX() <= maxX &&
                targetLocation.getY() >= minY && targetLocation.getY() <= maxY &&
                targetLocation.getZ() >= minZ && targetLocation.getZ() <= maxZ;
    }

    // Метод для проверки пересечения отрезка с кубом
    public static boolean doesSegmentIntersectArea(Segment segment, Location[] cubeVertexes) {
        Location[] segmentMinMax = segment.getMinMaxPoints();
        Location minPoint = segmentMinMax[0];
        Location maxPoint = segmentMinMax[1];

        // Получаем минимальные и максимальные границы куба
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (Location vertex : cubeVertexes) {
            minX = java.lang.Math.min(minX, vertex.getX());
            maxX = java.lang.Math.max(maxX, vertex.getX());
            minY = java.lang.Math.min(minY, vertex.getY());
            maxY = java.lang.Math.max(maxY, vertex.getY());
            minZ = java.lang.Math.min(minZ, vertex.getZ());
            maxZ = java.lang.Math.max(maxZ, vertex.getZ());
        }

        return (minPoint.getX() <= maxX && maxPoint.getX() >= minX) &&
                (minPoint.getY() <= maxY && maxPoint.getY() >= minY) &&
                (minPoint.getZ() <= maxZ && maxPoint.getZ() >= minZ);
    }
}
