package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.annotations.Main;
import me.padej.displayAPI.ui.widgets.ItemDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
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
    public void createScreenWidgets(Player player) {
        // Создаем кнопки ветвления
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        ItemDisplayButtonConfig[] branchButtons = {
                new ItemDisplayButtonConfig(Material.COMPASS, () -> {
                    ChangeScreen.switchTo(player, MainScreen.class, Branch1Screen.class);
                })
                        .setTooltip("Ветка 1")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone()),

                new ItemDisplayButtonConfig(Material.MAP, () -> {
                    ChangeScreen.switchTo(player, MainScreen.class, Branch2Screen.class);
                })
                        .setTooltip("Ветка 2")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone().addVertical(step)),

                new ItemDisplayButtonConfig(Material.SNOW_BLOCK, () -> {
                    ChangeScreen.switchTo(player, MainScreen.class, Branch3Screen.class);
                })
                        .setDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE)
                        .setGlowColor(Color.RED)
                        .setTooltip("Ветка 3")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone().addVertical(step * 2)),

                new ItemDisplayButtonConfig(Material.AMETHYST_SHARD, () -> {
                    ChangeScreen.switchTo(player, MainScreen.class, Branch4Screen.class);
                })
                        .setTooltip("Ветка 4")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone().addVertical(step * 3)),

                new ItemDisplayButtonConfig(Material.GOLD_INGOT, () -> {
                    ChangeScreen.switchTo(player, MainScreen.class, Branch5Screen.class);
                })
                        .setPosition(basePosition.clone().addVertical(-step))
        };

        for (ItemDisplayButtonConfig config : branchButtons) {
            createWidget(config);
        }
    }
} 