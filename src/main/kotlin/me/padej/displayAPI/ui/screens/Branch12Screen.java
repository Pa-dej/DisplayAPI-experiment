package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Branch12Screen extends Screen {
    public Branch12Screen() {
        super(); // Используем конструктор для временных экранов
    }

    public Branch12Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public void setupDefaultWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        WidgetConfig[] branchButtons = {
            new WidgetConfig(Material.REDSTONE, () -> {
                player.sendMessage("Ветка 1");
            })
            .setTooltip("Подветка 1")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone()),

            new WidgetConfig(Material.GLOWSTONE_DUST, () -> {
                player.sendMessage("Ветка 2");
            })
            .setTooltip("Подветка 2")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone().addVertical(step))
        };

        for (WidgetConfig config : branchButtons) {
            createWidget(config);
        }
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return MainScreen.class;
    }

    @Override
    public WidgetConfig[] getBranchWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        return new WidgetConfig[] {
            new WidgetConfig(Material.REDSTONE, () -> {
                player.sendMessage("Ветка 1");
            })
            .setTooltip("Подветка 1")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone()),

            new WidgetConfig(Material.GLOWSTONE_DUST, () -> {
                player.sendMessage("Ветка 2");
            })
            .setTooltip("Подветка 2")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone().addVertical(step))
        };
    }
} 