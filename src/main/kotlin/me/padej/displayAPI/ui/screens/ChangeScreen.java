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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChangeScreen {
    private static final Logger LOGGER = LogManager.getLogger(ChangeScreen.class);
    private final Screen screen;
    private Class<? extends Screen> currentScreenClass;
    private final List<Widget> persistentWidgets = new ArrayList<>();

    public ChangeScreen(Screen screen) {
        this.screen = screen;
        this.currentScreenClass = screen.getClass();
        savePersistentWidgets();
    }

    private void savePersistentWidgets() {
        for (Field field : Screen.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Persistent.class)) {
                field.setAccessible(true);
                try {
                    Object widget = field.get(screen);
                    if (widget instanceof Widget) {
                        persistentWidgets.add((Widget) widget);
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Ошибка при сохранении постоянных виджетов в классе {}", screen.getClass().getSimpleName(), e);
                }
            }
        }
    }

    private void removeNonPersistentWidgets() {
        List<Widget> widgetsToRemove = new ArrayList<>(screen.getChildren());
        widgetsToRemove.removeAll(persistentWidgets);
        
        for (Widget widget : widgetsToRemove) {
            widget.remove();
        }
        screen.getChildren().removeAll(widgetsToRemove);
    }

    private void createBranchWidgets(Player player, Screen branchScreen) {
        LOGGER.debug("Создание виджетов для экрана: {}", branchScreen.getClass().getSimpleName());
        
        // Обработка обычных виджетов
        WidgetConfig[] branchWidgets = branchScreen.getBranchWidgets(player);
        LOGGER.debug("Количество branch виджетов: {}", branchWidgets.length);
        for (WidgetConfig config : branchWidgets) {
            screen.createWidget(config);
        }

        // Обработка текстовых виджетов
        TextDisplayConfig[] textWidgets = branchScreen.getTextWidgets(player);
        LOGGER.debug("Количество текстовых виджетов: {}", textWidgets.length);
        for (TextDisplayConfig config : textWidgets) {
            screen.createTextWidget(config);
        }
        
        LOGGER.debug("Общее количество виджетов после создания: {}", screen.getChildren().size());
    }

    public void changeToBranch(Player player, Class<? extends Screen> branchClass) {
        removeNonPersistentWidgets();

        try {
            Screen branchScreen = branchClass.getDeclaredConstructor().newInstance();
            branchScreen.viewer = player;
            branchScreen.location = screen.getLocation();
            
            // Копируем все необходимые свойства из branchScreen в screen
            screen.viewer = branchScreen.viewer;
            screen.location = branchScreen.location;
            
            createBranchWidgets(player, branchScreen);
            this.currentScreenClass = branchClass;

            if (!branchClass.isAnnotationPresent(Main.class)) {
                createReturnButton(player);
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при смене экрана на {}", branchClass.getSimpleName(), e);
        }
    }

    public void returnToParentScreen(Player player) {
        screen.viewer = player;
        removeNonPersistentWidgets();

        try {
            Screen currentScreen = currentScreenClass.getDeclaredConstructor().newInstance();
            Class<? extends Screen> parentClass = currentScreen.getParentScreen();

            setupParentScreenWidgets(player, parentClass);
            addReturnButtonIfNeeded(player);
        } catch (Exception e) {
            handleReturnError(player, e);
        }
    }

    private void setupParentScreenWidgets(Player player, Class<? extends Screen> parentClass) throws ReflectiveOperationException {
        if (parentClass == null) {
            screen.setupDefaultWidgets(player);
            this.currentScreenClass = MainScreen.class;
            return;
        }

        Screen parentScreen = parentClass.getDeclaredConstructor().newInstance();
        parentScreen.viewer = player;
        parentScreen.location = screen.getLocation();
        this.currentScreenClass = parentClass;

        if (parentClass == MainScreen.class) {
            screen.setupDefaultWidgets(player);
        } else {
            createBranchWidgets(player, parentScreen);
        }
    }

    private void addReturnButtonIfNeeded(Player player) {
        if (currentScreenClass != MainScreen.class) {
            createReturnButton(player);
        }
    }

    private void handleReturnError(Player player, Exception e) {
        LOGGER.error("Ошибка при возврате на родительский экран {}", currentScreenClass.getSimpleName(), e);
        screen.setupDefaultWidgets(player);
    }

    private void createReturnButton(Player player) {
        TextDisplayConfig returnConfig = createReturnButtonConfig(player);
        screen.createTextWidget(returnConfig);
    }

    private TextDisplayConfig createReturnButtonConfig(Player player) {
        WidgetPosition basePosition = new WidgetPosition(0.52, 0.92);

        return new TextDisplayConfig(
                createReturnButtonText(),
                createReturnButtonHoverText(),
                () -> returnToParentScreen(player)
        )
                .setPosition(basePosition.clone().addHorizontal(-0.28))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30))
                .setBackgroundAlpha(0)
                .setHoveredBackgroundAlpha(0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60));
    }

    private Component createReturnButtonText() {
        return Component.text("⏴").color(TextColor.fromHexString("#fafeff"));
    }

    private Component createReturnButtonHoverText() {
        return Component.text("⏴").color(TextColor.fromHexString("#aaaeaf"));
    }
} 