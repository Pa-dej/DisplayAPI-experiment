package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.annotations.Main;
import me.padej.displayAPI.ui.annotations.Persistent;
import me.padej.displayAPI.ui.widgets.TextDisplayConfig;
import me.padej.displayAPI.ui.widgets.Widget;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChangeScreen {
    private final Screen screen;
    private Class<? extends Screen> currentScreenClass;
    private final List<Widget> persistentWidgets = new ArrayList<>();
    private static boolean isSettingsScreen = false;

    public ChangeScreen(Screen screen) {
        this.screen = screen;
        this.currentScreenClass = screen.getClass();
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
            // Создаем временный экземпляр нового экрана для получения виджетов
            Screen branchScreen = branchClass.getDeclaredConstructor().newInstance();

            // Получаем и создаем виджеты для нового экрана
            WidgetConfig[] branchWidgets = branchScreen.getBranchWidgets(player);
            for (WidgetConfig config : branchWidgets) {
                screen.createWidget(config);
            }

            // Обновляем текущий класс экрана
            this.currentScreenClass = branchClass;

            // Если это не главный экран, добавляем кнопку возврата
            if (!branchClass.isAnnotationPresent(Main.class)) {
                createReturnButton(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnToParentScreen(Player player) {
        // Сначала устанавливаем viewer
        screen.viewer = player;
        
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
            // Создаем временный экземпляр текущего экрана для получения родительского класса
            Screen currentScreen = currentScreenClass.getDeclaredConstructor().newInstance();
            Class<? extends Screen> parentClass = currentScreen.getParentScreen();

            if (parentClass != null) {
                // Создаем временный экземпляр родительского экрана
                Screen parentScreen = parentClass.getDeclaredConstructor().newInstance();
                
                // Обновляем текущий класс экрана на родительский
                this.currentScreenClass = parentClass;
                
                // Получаем и создаем виджеты для родительского экрана
                if (parentClass == MainScreen.class) {
                    screen.setupDefaultWidgets(player);
                } else {
                    WidgetConfig[] parentWidgets = parentScreen.getBranchWidgets(player);
                    for (WidgetConfig config : parentWidgets) {
                        screen.createWidget(config);
                    }
                }
            } else {
                // Если parentClass == null, значит мы вернулись на главный экран
                screen.setupDefaultWidgets(player);
                this.currentScreenClass = MainScreen.class;
            }

            // Если это не главный экран, добавляем кнопку возврата
            if (currentScreenClass != MainScreen.class) {
                createReturnButton(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // В случае ошибки тоже пытаемся установить виджеты
            screen.setupDefaultWidgets(player);
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