package me.Padej_.eventManagerDisplayUI.color;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Palette {

    public static final TextColor GREEN_LIGHT = TextColor.fromHexString("#2ECC71");
    public static final TextColor GREEN_DARK = TextColor.fromHexString("#1d7138");

    public static final TextColor PURPLE_LIGHT = TextColor.fromHexString("#e91396");
    public static final TextColor PURPLE_DARK = TextColor.fromHexString("#74095f");

    public static final TextColor YELLOW_LIGHT = TextColor.fromHexString("#F1C40F");
    public static final TextColor YELLOW_DARK = TextColor.fromHexString("#cf8111");

    public static final TextColor RED_LIGHT = TextColor.fromHexString("#E74C3C");
    public static final TextColor RED_DARK = TextColor.fromHexString("#80261f");

    public static final TextColor WHITE_LIGHT = TextColor.fromHexString("#228ff1");

    public static final TextColor GRAY_LIGHT = TextColor.fromHexString("#95A5A6");
    public static final TextColor GRAY_DARK = TextColor.fromHexString("#425051");

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
