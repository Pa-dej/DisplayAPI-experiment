package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Branch4Screen extends Screen {
    public Branch4Screen() {
        super();
    }

    public Branch4Screen(Player viewer, Location location) {
        super(viewer, location);
    }

    public Branch4Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return MainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        TextColor defaultColor = TextColor.fromHexString("#FFFFFF");
        TextColor hoverColor = TextColor.fromHexString("#FCD720");

        TextDisplayButtonConfig[] branchButtons = {
                new TextDisplayButtonConfig(
                        Component.text("Выживание").color(defaultColor),
                        Component.text("Выживание").color(hoverColor),
                        () -> player.setGameMode(GameMode.SURVIVAL))
                        .setPosition(basePosition.clone())
                        .setTooltip(Component.text("Режим выживания"))
                        .setTooltipDelay(30)
                        .setBackgroundColor(Color.fromRGB(40, 40, 40))
                        .setBackgroundAlpha(150)
                        .setHoveredBackgroundColor(Color.fromRGB(60, 60, 60))
                        .setHoveredBackgroundAlpha(180),

                new TextDisplayButtonConfig(
                        Component.text("Креатив").color(defaultColor),
                        Component.text("Креатив").color(hoverColor),
                        () -> player.setGameMode(GameMode.CREATIVE))
                        .setPosition(basePosition.clone().addVertical(0.15f))
                        .setTooltip(Component.text("Творческий режим"))
                        .setTooltipDelay(30)
                        .setBackgroundColor(Color.fromRGB(40, 40, 40))
                        .setBackgroundAlpha(150)
                        .setHoveredBackgroundColor(Color.fromRGB(60, 60, 60))
                        .setHoveredBackgroundAlpha(180),

                new TextDisplayButtonConfig(
                        Component.text("Приключение").color(defaultColor),
                        Component.text("Приключение").color(hoverColor),
                        () -> player.setGameMode(GameMode.ADVENTURE))
                        .setPosition(basePosition.clone().addVertical(0.30f))
                        .setTooltip(Component.text("Режим приключения"))
                        .setTooltipDelay(30)
                        .setBackgroundColor(Color.fromRGB(40, 40, 40))
                        .setBackgroundAlpha(150)
                        .setHoveredBackgroundColor(Color.fromRGB(60, 60, 60))
                        .setHoveredBackgroundAlpha(180),

                new TextDisplayButtonConfig(
                        Component.text("Наблюдатель").color(defaultColor),
                        Component.text("Наблюдатель").color(hoverColor),
                        () -> player.setGameMode(GameMode.SPECTATOR))
                        .setPosition(basePosition.clone().addVertical(0.45f))
                        .setTooltip(Component.text("Режим наблюдателя"))
                        .setTooltipDelay(30)
                        .setBackgroundColor(Color.fromRGB(40, 40, 40))
                        .setBackgroundAlpha(150)
                        .setHoveredBackgroundColor(Color.fromRGB(60, 60, 60))
                        .setHoveredBackgroundAlpha(180)
                        .setHoveredBackgroundAlpha(180)
        };

        for (TextDisplayButtonConfig config : branchButtons) {
            createTextWidget(config);
        }
    }
} 