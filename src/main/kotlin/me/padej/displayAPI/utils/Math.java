package me.padej.displayAPI.utils;

import org.bukkit.util.Vector;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class Math {

    public static Vector FORWARD_VECTOR = new Vector(0, 0, 1);

    public static Vector rotate(Vector vector, Quaterniond quaternion) {
        Vector3d rotated = new Vector3d(vector.getX(), vector.getY(), vector.getZ()).rotate(quaternion);
        return new Vector(rotated.x, rotated.y, rotated.z);
    }
}
