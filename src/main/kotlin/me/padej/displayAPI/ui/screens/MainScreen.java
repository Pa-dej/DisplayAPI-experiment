package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.annotations.Main;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Main
public class MainScreen extends Screen {
    public MainScreen() {
        super(); // Используем конструктор для временных экранов
    }

    public MainScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public void setupDefaultWidgets(Player player) {
        // Создаем кнопки заголовка
        createTitleBarControlWidgets();

        // Создаем кнопки ветвления вместо кнопок геймода
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        WidgetConfig[] branchButtons = {
            new WidgetConfig(Material.COMPASS, () -> {
                new ChangeScreen(this).changeToBranch(player, Branch12Screen.class);
            })
            .setTooltip("Ветка 1-2")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone()),

            new WidgetConfig(Material.MAP, () -> {
                new ChangeScreen(this).changeToBranch(player, Branch34Screen.class);
            })
            .setTooltip("Ветка 3-4")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone().addVertical(step))
        };

        for (WidgetConfig config : branchButtons) {
            createWidget(config);
        }
    }
} 