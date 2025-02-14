package me.padej.displayAPI.utils;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

public class Animation {

    // Heledron's method(no runnable)
    public static void applyTransformationWithInterpolation(Display display, Transformation transformation, int transformationDuration) {
        if (transformation == display.getTransformation()) return;
        display.setTransformation(transformation);
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(transformationDuration);
    }

    public static void applyTransformationWithInterpolation(Display display, Transformation transformation) {
        if (transformation == display.getTransformation()) return;
        display.setTransformation(transformation);
        display.setInterpolationDelay(0);
    }
}
