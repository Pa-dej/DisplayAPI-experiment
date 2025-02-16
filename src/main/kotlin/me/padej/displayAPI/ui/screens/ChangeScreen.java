package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.UIManager;
import me.padej.displayAPI.ui.WidgetManager;
import me.padej.displayAPI.ui.annotations.Main;
import me.padej.displayAPI.ui.annotations.Persistent;
import me.padej.displayAPI.ui.annotations.ParentUI;
import me.padej.displayAPI.ui.widgets.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChangeScreen {
    private final Screen screen;
    private final List<Widget> persistentWidgets = new ArrayList<>();
    private static boolean isSettingsScreen = false;

    public ChangeScreen(Screen screen) {
        this.screen = screen;
        savePersistentWidgets();
    }

    private void savePersistentWidgets() {
        // Сохраняем виджеты с аннотацией @Persistent
        for (Field field : Screen.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Persistent.class)) {
                field.setAccessible(true);
                try {
                    Object widget = field.get(screen);
                    if (widget instanceof Widget) {
                        persistentWidgets.add((Widget) widget);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void changeToBranch(Player player, Class<? extends Screen> branchClass) {
        // 1. Удаляем все не-persistent виджеты
        List<Widget> widgetsToRemove = new ArrayList<>();
        for (Widget widget : screen.getChildren()) {
            if (!persistentWidgets.contains(widget)) {
                widgetsToRemove.add(widget);
                widget.remove();
            }
        }
        screen.getChildren().removeAll(widgetsToRemove);

        try {
            if (branchClass == Branch12Screen.class) {
                // Создаем виджеты для Branch12Screen
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
                    screen.createWidget(config);
                }
            } else if (branchClass == Branch34Screen.class) {
                // Создаем виджеты для Branch34Screen
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
                    screen.createWidget(config);
                }
            }

            // Если это не главный экран, добавляем кнопку возврата
            if (!branchClass.isAnnotationPresent(Main.class)) {
                createReturnButton(player);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnToParentScreen(Player player) {
        // 1. Удаляем все не-persistent виджеты
        List<Widget> widgetsToRemove = new ArrayList<>();
        for (Widget widget : screen.getChildren()) {
            if (!persistentWidgets.contains(widget)) {
                widgetsToRemove.add(widget);
                widget.remove();
            }
        }
        screen.getChildren().removeAll(widgetsToRemove);

        try {
            // 2. Создаем виджеты главного экрана
            WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
            float step = 0.15f;

            WidgetConfig[] mainButtons = {
                new WidgetConfig(Material.COMPASS, () -> {
                    new ChangeScreen(screen).changeToBranch(player, Branch12Screen.class);
                })
                .setTooltip("Ветка 1-2")
                .setTooltipDelay(30)
                .setPosition(basePosition.clone()),

                new WidgetConfig(Material.MAP, () -> {
                    new ChangeScreen(screen).changeToBranch(player, Branch34Screen.class);
                })
                .setTooltip("Ветка 3-4")
                .setTooltipDelay(30)
                .setPosition(basePosition.clone().addVertical(step))
            };

            for (WidgetConfig config : mainButtons) {
                screen.createWidget(config);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createReturnButton(Player player) {
        WidgetPosition basePosition = new WidgetPosition(0.52, 0.92);
        TextDisplayConfig returnConfig = new TextDisplayConfig(
                Component.text("⏴").color(TextColor.fromHexString("#fafeff")),
                Component.text("⏴").color(TextColor.fromHexString("#aaaeaf")),
                () -> returnToParentScreen(player)
        )
                .setPosition(basePosition.clone().addHorizontal(-0.28))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30), 0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60), 0);

        screen.createTextWidget(returnConfig);
    }

    public void changeToSettingsScreen(Player player) {
        if (isSettingsScreen) return;

        // 1. Удаляем все не-persistent виджеты
        List<Widget> widgetsToRemove = new ArrayList<>();
        for (Widget widget : screen.getChildren()) {
            if (!persistentWidgets.contains(widget)) {
                widgetsToRemove.add(widget);
            }
        }
        
        for (Widget widget : widgetsToRemove) {
            widget.remove();
            screen.getChildren().remove(widget);
        }

        // 2. Создаем виджеты для экрана настроек
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        WidgetConfig[] settingsButtons = {
            new WidgetConfig(Material.REDSTONE, () -> {
                if (!isSettingsScreen) return;
                player.sendMessage("Настройка 1");
            })
            .setTooltip("Настройка 1")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone()),

            new WidgetConfig(Material.GLOWSTONE_DUST, () -> {
                if (!isSettingsScreen) return;
                player.sendMessage("Настройка 2");
            })
            .setTooltip("Настройка 2")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone().addVertical(step))
        };

        for (WidgetConfig config : settingsButtons) {
            screen.createWidget(config);
        }

        isSettingsScreen = true;
        createReturnButton(player);
    }

    // Геттер для проверки текущего экрана
    public static boolean isSettingsScreen() {
        return isSettingsScreen;
    }

    // Сеттер для установки флага экрана настроек
    public static void setSettingsScreen(boolean value) {
        isSettingsScreen = value;
    }
} 