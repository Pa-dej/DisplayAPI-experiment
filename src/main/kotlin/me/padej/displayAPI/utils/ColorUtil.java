package me.padej.displayAPI.utils;

import org.bukkit.Color;

public class ColorUtil {

    public static Color formARGBColor(int alpha, Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        return Color.fromARGB(alpha, r, g, b);
    }
}
