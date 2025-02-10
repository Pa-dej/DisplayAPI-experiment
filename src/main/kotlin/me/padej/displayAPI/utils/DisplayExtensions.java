package me.padej.displayAPI.utils;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;

public class DisplayExtensions {

    public static void interpolateTransform(Display display, Transformation transformation) {
        if (display.getTransformation().equals(transformation)) return;
        display.setTransformation(transformation);
        display.setInterpolationDelay(0);
    }

    public static void interpolateTransform(Display display, Matrix4f matrix) {
        Transformation oldTransform = display.getTransformation();
        display.setTransformationMatrix(matrix);

        if (oldTransform.equals(display.getTransformation())) return;
        display.setInterpolationDelay(0);
    }
}
