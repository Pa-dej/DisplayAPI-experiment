package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.UIManager;
import me.padej.displayAPI.ui.annotations.AlwaysOnScreen;
import me.padej.displayAPI.ui.widgets.Widget;
import me.padej.displayAPI.utils.Animation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChangeScreen {
    private static final Logger LOGGER = LogManager.getLogger(ChangeScreen.class);
    private final Screen screen;
    private final List<Widget> persistentWidgets = new ArrayList<>();

    public ChangeScreen(Screen screen) {
        this.screen = screen;
        savePersistentWidgets();
    }

    private void savePersistentWidgets() {
        for (Field field : Screen.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(AlwaysOnScreen.class)) {
                AlwaysOnScreen annotation = field.getAnnotation(AlwaysOnScreen.class);
                if (annotation.value().isAssignableFrom(screen.getClass())) {
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
    }

    public static void switchTo(Player player, Class<? extends Screen> from, Class<? extends Screen> to) {
        Screen currentScreen = UIManager.getInstance().getActiveScreen(player);
        if (currentScreen != null && currentScreen.getClass() == from) {
            try {
                // Сохраняем локацию, цвет фона и углы поворота текущего экрана
                Location oldLocation = currentScreen.getLocation();
                org.bukkit.Color backgroundColor = null;
                float yaw = 0, pitch = 0;
                
                if (currentScreen.getTextDisplay() != null) {
                    backgroundColor = currentScreen.getTextDisplay().getBackgroundColor();
                    yaw = currentScreen.getTextDisplay().getLocation().getYaw();
                    pitch = currentScreen.getTextDisplay().getLocation().getPitch();
                }
                
                // Удаляем текущий экран
                currentScreen.softRemoveWithAnimation();
                UIManager.getInstance().unregisterScreen(player);
                
                // Создаем новый экран на месте старого
                Screen screen = to.getConstructor(
                        Player.class,
                        Location.class,
                        String.class,
                        float.class
                ).newInstance(
                        player,
                        oldLocation,
                        " ",
                        0.1f
                );

                // Восстанавливаем цвет фона и углы поворота
                if (screen.getTextDisplay() != null) {
                    if (backgroundColor != null) {
                        screen.getTextDisplay().setBackgroundColor(backgroundColor);
                    }
                    Location displayLoc = screen.getTextDisplay().getLocation();
                    displayLoc.setYaw(yaw);
                    displayLoc.setPitch(pitch);
                    screen.getTextDisplay().teleport(displayLoc);
                }

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