package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.annotations.Persistent;
import me.padej.displayAPI.ui.widgets.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChangeScreen {
    private final Screen screen;
    private final List<Widget> persistentWidgets = new ArrayList<>();
    private static boolean isSettingsScreen = false; // Флаг для отслеживания текущего экрана

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

    public void changeToSettingsScreen(Player player) {
        if (isSettingsScreen) return;

        // Удаляем только не-persistent виджеты
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

        // Создаем новые виджеты для экрана настроек
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        // Пример кнопок настроек (без кнопки возврата)
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
        screen.updateReturnButton(); // Добавляем синюю кнопку возврата
    }

    public void returnToMainScreen(Player player) {
        if (!isSettingsScreen) return;

        // Удаляем только не-persistent виджеты
        List<Widget> currentWidgets = new ArrayList<>(screen.getChildren());
        for (Widget widget : currentWidgets) {
            if (!persistentWidgets.contains(widget)) {
                widget.remove();
                screen.getChildren().remove(widget);
            }
        }

        isSettingsScreen = false;
        screen.updateReturnButton(); // Удаляем синюю кнопку возврата
        screen.createGamemodeButtons(player);
    }

    public static boolean isSettingsScreen() {
        return isSettingsScreen;
    }

    // Добавляем сеттер для isSettingsScreen
    public static void setSettingsScreen(boolean value) {
        isSettingsScreen = value;
    }
} 