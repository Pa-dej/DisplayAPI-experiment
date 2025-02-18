package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Branch34Screen extends Screen {
    public Branch34Screen() {
        super(); // Конструктор для временных экранов
    }

    public Branch34Screen(Player viewer, Location location) {
        super(viewer, location);
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return MainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        // Создаем кнопки подветвей
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        WidgetConfig[] branchButtons = {
            new WidgetConfig(Material.DIAMOND, () -> {
                player.sendMessage("Ветка 3");
            })
            .setTooltip("Подветка 3")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone()),

            new WidgetConfig(Material.EMERALD, () -> {
                player.sendMessage("Ветка 4");
            })
            .setTooltip("Подветка 4")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone().addVertical(step))
        };

        for (WidgetConfig config : branchButtons) {
            createWidget(config);
        }
    }

    @Override
    public WidgetConfig[] getBranchWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        return new WidgetConfig[] {
            new WidgetConfig(Material.DIAMOND, () -> {
                player.sendMessage("Ветка 3");
            })
            .setTooltip("Подветка 3")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone()),

            new WidgetConfig(Material.EMERALD, () -> {
                player.sendMessage("Ветка 4");
            })
            .setTooltip("Подветка 4")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone().addVertical(step))
        };
    }
} 