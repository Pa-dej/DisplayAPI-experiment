package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.UIManager;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.utils.Animation;
import org.joml.Vector3f;

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

    public static Screen createScreen(Player player, Class<? extends Screen> screenClass) {
        Screen currentScreen = UIManager.getInstance().getActiveScreen(player);
        if (currentScreen != null) {
            currentScreen.remove();
        }

        try {
            Screen screen = screenClass.getConstructor(
                    Player.class,
                    Location.class,
                    String.class,
                    float.class
            ).newInstance(
                    player,
                    player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.8)),
                    " ",
                    0.1f
            );

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
            return screen;
        } catch (Exception e) {
            LOGGER.error("Ошибка при создании экрана {}", screenClass.getSimpleName(), e);
            return null;
        }
    }

    public static void switchTo(Player player, Class<? extends Screen> from, Class<? extends Screen> to) {
        Screen currentScreen = UIManager.getInstance().getActiveScreen(player);
        if (currentScreen != null && currentScreen.getClass() == from) {
            try {
                // Сохраняем локацию и поворот текущего экрана
                Location oldLocation = currentScreen.getLocation();
                
                // Удаляем текущий экран
                currentScreen.remove();
                UIManager.getInstance().unregisterScreen(player);
                
                // Создаем новый экран на месте старого
                Screen screen = to.getConstructor(
                        Player.class,
                        Location.class,
                        String.class,
                        float.class
                ).newInstance(
                        player,
                        oldLocation, // Используем сохраненную локацию
                        " ",
                        0.1f
                );

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
                            6
                    );

                    screen.setOnClose(() -> UIManager.getInstance().unregisterScreen(player));
                    screen.setupDefaultWidgets(player);
                }, 2);

                UIManager.getInstance().registerScreen(player, screen);
            } catch (Exception e) {
                LOGGER.error("Ошибка при переключении с {} на {}", from.getSimpleName(), to.getSimpleName(), e);
            }
        }
    }

    public static void switchToParent(Player player, Class<? extends Screen> screen) {
        try {
            Screen tempScreen = screen.getDeclaredConstructor().newInstance();
            Class<? extends Screen> parentClass = tempScreen.getParentScreen();
            
            if (parentClass != null) {
                switchTo(player, screen, parentClass);
            } else {
                LOGGER.warn("Попытка переключения на родительский экран для экрана {} не удалась - родительский экран не найден", 
                        screen.getSimpleName());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении родительского экрана для {}", screen.getSimpleName(), e);
        }
    }
} 