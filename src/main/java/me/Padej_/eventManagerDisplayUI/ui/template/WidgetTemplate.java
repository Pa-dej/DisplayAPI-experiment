package me.Padej_.eventManagerDisplayUI.ui.template;

import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public interface WidgetTemplate {

    TextDisplayButtonWidget createTextWidget(TextDisplayButtonConfig config);

    default double calculateTolerance(String text) {
        int length = text.length();
        if (length <= 5) {
            return 0.21;
        } else if (length <= 8) {
            return 0.25;
        } else if (length <= 12) {
            return 0.42;
        } else {
            return 0.44;
        }
    }

    default TextDisplayButtonWidget createButton(String text, String tooltip, Runnable action, WidgetPosition position, TextColor color1, TextColor color2, double toleranceX) {
        return createTextWidget(new TextDisplayButtonConfig(
                Palette.gradient(text, color1, color2),
                Palette.gradient(text, color1, color2),
                action
        )
                .setPosition(position.clone().addDepth(-0.01f))
                .setTooltip(Component.text(tooltip))
                .setTooltipDelay(50)
                .setScale(.5f, .5f, .5f)
                .setTolerance(toleranceX, 0.042)
                .setHoveredTransformation(new Transformation(
                        new Vector3f(0, -.05f, -0.01f),
                        new AxisAngle4f(),
                        new Vector3f(.4f, .4f, .4f),
                        new AxisAngle4f()
                ), 3)
                .enableTextShadow()
                .setBackgroundAlpha(0)
                .setHoveredBackgroundAlpha(0));
    }

    default TextDisplayButtonWidget createEventerButton(String text, String tooltip, Runnable action, WidgetPosition position) {
        return createEventerButton(text, tooltip, action, position, calculateTolerance(text));
    }

    default TextDisplayButtonWidget createEventerButton(String text, String tooltip, Runnable action, WidgetPosition position, double toleranceX) {
        return createButton(text, tooltip, action, position, TextColor.color(0xe91396), TextColor.color(0xaf0e80), toleranceX);
    }

    default TextDisplayButtonWidget createAdminButton(String text, String tooltip, Runnable action, WidgetPosition position) {
        return createAdminButton(text, tooltip, action, position, calculateTolerance(text));
    }

    default TextDisplayButtonWidget createAdminButton(String text, String tooltip, Runnable action, WidgetPosition position, double toleranceX) {
        return createButton(text, tooltip, action, position, TextColor.color(0xFF0000), TextColor.color(0xbf0012), toleranceX);
    }

    default TextDisplayButtonWidget createModerButton(String text, String tooltip, Runnable action, WidgetPosition position) {
        return createModerButton(text, tooltip, action, position, calculateTolerance(text));
    }

    default TextDisplayButtonWidget createModerButton(String text, String tooltip, Runnable action, WidgetPosition position, double toleranceX) {
        return createButton(text, tooltip, action, position, TextColor.color(0xfa7315), TextColor.color(0xc74204), toleranceX);
    }

    default TextDisplayButtonWidget createTeamButton(String text, String tooltip, Runnable action, WidgetPosition position) {
        return createTeamButton(text, tooltip, action, position, calculateTolerance(text));
    }

    default TextDisplayButtonWidget createTeamButton(String text, String tooltip, Runnable action, WidgetPosition position, double toleranceX) {
        return createButton(text, tooltip, action, position, TextColor.color(0x43c5ff), TextColor.color(0x008bf1), toleranceX);
    }

    default TextDisplayButtonWidget createHelperButton(String text, String tooltip, Runnable action, WidgetPosition position) {
        return createHelperButton(text, tooltip, action, position, calculateTolerance(text));
    }

    default TextDisplayButtonWidget createHelperButton(String text, String tooltip, Runnable action, WidgetPosition position, double toleranceX) {
        return createButton(text, tooltip, action, position, TextColor.color(0x00ff77), TextColor.color(0x00bf6b), toleranceX);
    }

    default TextDisplayButtonWidget createDevButton(String text, String tooltip, Runnable action, WidgetPosition position) {
        return createDevButton(text, tooltip, action, position, calculateTolerance(text));
    }

    default TextDisplayButtonWidget createDevButton(String text, String tooltip, Runnable action, WidgetPosition position, double toleranceX) {
        return createButton(text, tooltip, action, position, TextColor.color(0xff6eaf), TextColor.color(0xff37a2), toleranceX);
    }
} 