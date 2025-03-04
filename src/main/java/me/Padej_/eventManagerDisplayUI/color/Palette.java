package me.Padej_.eventManagerDisplayUI.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

public class Palette {

    public static final TextColor GREEN_LIGHT = TextColor.fromHexString("#2ECC71");
    public static final Color BUKKIT_GREEN_LIGHT = Color.fromRGB(46, 204, 113);
    public static final java.awt.Color AWT_GREEN_LIGHT = new java.awt.Color(46, 204, 113);
    public static final TextColor GREEN_DARK = TextColor.fromHexString("#27AE60");
    public static final Color BUKKIT_GREEN_DARK = Color.fromRGB(39, 174, 96);
    public static final java.awt.Color AWT_GREEN_DARK = new java.awt.Color(39, 174, 96);

    public static final TextColor BLUE_LIGHT = TextColor.fromHexString("#3498DB");
    public static final Color BUKKIT_BLUE_LIGHT = Color.fromRGB(52, 152, 219);
    public static final java.awt.Color AWT_BLUE_LIGHT = new java.awt.Color(52, 152, 219);
    public static final TextColor BLUE_DARK = TextColor.fromHexString("#2980B9");
    public static final Color BUKKIT_BLUE_DARK = Color.fromRGB(41, 128, 185);
    public static final java.awt.Color AWT_BLUE_DARK = new java.awt.Color(41, 128, 185);

    public static final TextColor PURPLE_LIGHT = TextColor.fromHexString("#9B59B6");
    public static final Color BUKKIT_PURPLE_LIGHT = Color.fromRGB(155, 89, 182);
    public static final java.awt.Color AWT_PURPLE_LIGHT = new java.awt.Color(155, 89, 182);
    public static final TextColor PURPLE_DARK = TextColor.fromHexString("#8E44AD");
    public static final Color BUKKIT_PURPLE_DARK = Color.fromRGB(142, 68, 173);
    public static final java.awt.Color AWT_PURPLE_DARK = new java.awt.Color(142, 68, 173);

    public static final TextColor YELLOW_LIGHT = TextColor.fromHexString("#F1C40F");
    public static final Color BUKKIT_YELLOW_LIGHT = Color.fromRGB(241, 196, 15);
    public static final java.awt.Color AWT_YELLOW_LIGHT = new java.awt.Color(241, 196, 15);
    public static final TextColor YELLOW_DARK = TextColor.fromHexString("#F39C12");
    public static final Color BUKKIT_YELLOW_DARK = Color.fromRGB(243, 156, 18);
    public static final java.awt.Color AWT_YELLOW_DARK = new java.awt.Color(243, 156, 18);

    public static final TextColor RED_LIGHT = TextColor.fromHexString("#E74C3C");
    public static final Color BUKKIT_RED_LIGHT = Color.fromRGB(231, 76, 60);
    public static final java.awt.Color AWT_RED_LIGHT = new java.awt.Color(231, 76, 60);
    public static final TextColor RED_DARK = TextColor.fromHexString("#C0392B");
    public static final Color BUKKIT_RED_DARK = Color.fromRGB(192, 57, 43);
    public static final java.awt.Color AWT_RED_DARK = new java.awt.Color(192, 57, 43);

    public static final TextColor WHITE_LIGHT = TextColor.fromHexString("#228ff1");
    public static final Color BUKKIT_WHITE_LIGHT = Color.fromRGB(236, 240, 241);
    public static final java.awt.Color AWT_WHITE_LIGHT = new java.awt.Color(236, 240, 241);
    public static final TextColor WHITE_DARK = TextColor.fromHexString("#BDC3C7");
    public static final Color BUKKIT_WHITE_DARK = Color.fromRGB(189, 195, 199);
    public static final java.awt.Color AWT_WHITE_DARK = new java.awt.Color(189, 195, 199);

    public static final TextColor GRAY_LIGHT = TextColor.fromHexString("#95A5A6");
    public static final Color BUKKIT_GRAY_LIGHT = Color.fromRGB(149, 165, 166);
    public static final java.awt.Color AWT_GRAY_LIGHT = new java.awt.Color(149, 165, 166);
    public static final TextColor GRAY_DARK = TextColor.fromHexString("#7F8C8D");
    public static final Color BUKKIT_GRAY_DARK = Color.fromRGB(127, 140, 141);
    public static final java.awt.Color AWT_GRAY_DARK = new java.awt.Color(127, 140, 141);

    public static Component gradient(String text, TextColor... colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("Градиент требует минимум 2 цветов!");
        }

        if (text.length() == 1) {
            return Component.text(text).color(colors[0]);
        }

        String[] chars = text.split("");
        Component result = Component.empty();
        int segments = colors.length - 1;

        for (int i = 0; i < chars.length; i++) {
            double progress = (double) i / (chars.length - 1);
            int colorIndex = Math.min((int) (progress * segments), segments - 1);
            double segmentProgress = (progress * segments) - colorIndex;

            TextColor blendedColor = interpolateColor(colors[colorIndex], colors[colorIndex + 1], segmentProgress);
            result = result.append(Component.text(chars[i]).color(blendedColor));
        }
        return result;
    }

    private static TextColor interpolateColor(TextColor start, TextColor end, double ratio) {
        int r = (int) (start.red() + (end.red() - start.red()) * ratio);
        int g = (int) (start.green() + (end.green() - start.green()) * ratio);
        int b = (int) (start.blue() + (end.blue() - start.blue()) * ratio);
        return TextColor.color(r, g, b);
    }
}
