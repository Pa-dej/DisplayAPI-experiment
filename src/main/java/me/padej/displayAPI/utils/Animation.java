package me.padej.displayAPI.utils;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.UIManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Animation {

    public static void applyTransformationWithInterpolation(Display display, Transformation transformation, int transformationDuration) {
        if ( transformation == display.getTransformation()) return;
        display.setTransformation(transformation);
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(transformationDuration);
    }

    public static void applyTransformationWithInterpolation(Display display, Transformation transformation) {
        if (transformation == display.getTransformation()) return;
        display.setTransformation(transformation);
        display.setInterpolationDelay(0);
    }

    public static void applyTransformationWithInterpolation(Display display, Matrix4f matrix4f) {
        if (matrix4f == Matrix4fUtil.transformationToMatrix4f(display.getTransformation())) return;
        display.setTransformationMatrix(matrix4f);
        display.setInterpolationDelay(0);
    }

    public static void createDefaultScreenWithAnimation(Screen screen, Player player) {
        Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
            if (screen.getTextDisplay() == null) {
                return;
            }

            Animation.applyTransformationWithInterpolation(
                    screen.getTextDisplay(),
                    new Transformation(
                            new Vector3f(0, 0, 0),
                            new AxisAngle4f(),
                            new Vector3f(10, 4, 1),
                            new AxisAngle4f()
                    ),
                    5
            );

            screen.setOnClose(() -> UIManager.getInstance().unregisterScreen(player));
            screen.setupDefaultWidgets(player);
        }, 2);

        UIManager.getInstance().registerScreen(player, screen);
    }
}
