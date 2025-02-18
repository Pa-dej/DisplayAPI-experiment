package me.padej.displayAPI.ui;

import io.papermc.paper.entity.LookAnchor;
import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.shapes.StringRectangle;
import me.padej.displayAPI.test_events.CreateTestUI;
import me.padej.displayAPI.ui.annotations.Persistent;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.*;
import me.padej.displayAPI.utils.Animation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.List;

public abstract class Screen extends WidgetManager {
    private final StringRectangle display;
    private Class<? extends Screen> currentScreenClass;

    // Делаем состояния статическими, чтобы они были общими для всех экранов
    private static boolean isFollowing = false;
    public static boolean isSaved = false;
    private static Vector relativePosition;  // Позиция дисплея относительно игрока
    private static Vector savedPosition;  // Позиция для режима сохранения

    @Persistent("Control buttons")
    private TextDisplayButtonWidget followButton;
    @Persistent("Control buttons")
    private TextDisplayButtonWidget saveButton;
    @Persistent("Control buttons")
    private TextDisplayButtonWidget closeButton;
    
    public Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location);
        this.currentScreenClass = this.getClass();
        
        // Создаем текстовый дисплей
        this.display = new StringRectangle(
                scale,
                Color.BLACK,
                100,
                Display.Billboard.FIXED,
                false,
                text
        ) {};
        
        spawn();
    }
    
    private void spawn() {
        if (display != null) {
            TextDisplay textDisplay = display.spawn(location);
            if (textDisplay != null) {
                textDisplay.setBrightness(new Display.Brightness(15, 15));
                textDisplay.setVisibleByDefault(false); // Делаем невидимым по умолчанию
                viewer.showEntity(DisplayAPI.getInstance(), textDisplay); // Показываем только создателю
                
                // Поворачиваем дисплей к игроку при создании
                Location viewerLoc = viewer.getEyeLocation();
                textDisplay.lookAt(viewerLoc.getX(), viewerLoc.getY(), viewerLoc.getZ(), LookAnchor.EYES);
            }
        }
    }
    
    @Override
    public void remove() {
        if (isSaved && !isPlayerInSavedRange()) {
            return;
        }

        // Сбрасываем режимы и состояния
        isFollowing = false;
        isSaved = false;
        relativePosition = null;
        savedPosition = null;

        // Сбрасываем подсветку кнопок
        if (followButton != null) {
            followButton.getDisplay().setGlowing(false);
        }
        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(false);
        }

        // Возвращаем стандартный цвет фона
        updateBackgroundColor(null);

        // Мгновенное удаление всех виджетов и дисплея
        for (Widget widget : children) {
            widget.remove();
        }
        if (display != null) {
            display.removeEntity();
        }
        super.remove();
    }

    // Новый метод для удаления с анимацией
    public void removeWithAnimation() {
        if (isSaved && !isPlayerInSavedRange()) {
            return;
        }

        // Сбрасываем режимы и состояния
        isFollowing = false;
        isSaved = false;
        relativePosition = null;
        savedPosition = null;

        // Сбрасываем подсветку кнопок
        if (followButton != null) {
            followButton.getDisplay().setGlowing(false);
        }
        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(false);
        }

        // Возвращаем стандартный цвет фона
        updateBackgroundColor(null);

        // Анимация исчезновения основного дисплея и всех виджетов
        if (display != null && display.getTextDisplay() != null) {
            Animation.applyTransformationWithInterpolation(
                display.getTextDisplay(),
                new Transformation(
                    display.getTextDisplay().getTransformation().getTranslation(),
                    display.getTextDisplay().getTransformation().getLeftRotation(),
                    new Vector3f(0, 0, 0),
                    display.getTextDisplay().getTransformation().getRightRotation()
                ),
                5
            );
            
            // Удаляем все виджеты с анимацией
            for (Widget widget : children) {
                widget.removeWithAnimation(5);
            }
            
            // Удаляем основной дисплей после анимации
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
                super.remove();
                display.removeEntity();
            }, 5);
        }
    }

    public boolean isPlayerInRange() {
        return viewer.getLocation().distance(location) <= 5;
    }

    public TextDisplay getTextDisplay() {
        return display != null ? display.getTextDisplay() : null;
    }

    public void createWidget(WidgetConfig config) {
        Location buttonLoc = location.clone();
        Vector direction = buttonLoc.getDirection();
        Vector right = direction.getCrossProduct(new Vector(0, 1, 0)).normalize();
        Vector up = right.getCrossProduct(direction).normalize();

        WidgetPosition position = config.getPosition();
        if (position != null) {
            buttonLoc.add(right.multiply(position.getRightMultiplier()));
            buttonLoc.add(up.multiply(position.getUpMultiplier()));
        }

        ItemDisplayButtonWidget widget = ItemDisplayButtonWidget.create(
                buttonLoc,
                viewer,
                config.getMaterial(),
                config.getOnClick()
        );

        widget.setPosition(position);

        if (config.hasTooltip()) {
            widget.setTooltip(config.getTooltip())
                    .setTooltipColor(config.getTooltipColor())
                    .setTooltipDelay(config.getTooltipDelay());
        }

        addDrawableChild(widget);
    }

    public TextDisplayButtonWidget createTextWidget(TextDisplayConfig config) {
        if (location == null) {
            return null;
        }
        
        Location buttonLoc = location.clone();
        Vector direction = buttonLoc.getDirection();
        Vector right = direction.getCrossProduct(new Vector(0, 1, 0)).normalize();
        Vector up = right.getCrossProduct(direction).normalize();

        WidgetPosition position = config.getPosition();
        if (position != null) {
            buttonLoc.add(right.multiply(position.getRightMultiplier()));
            buttonLoc.add(up.multiply(position.getUpMultiplier()));
        }

        TextDisplayButtonWidget widget = TextDisplayButtonWidget.create(
                buttonLoc,
                viewer,
                config
        );

        // Копируем поворот с основного дисплея
        if (display != null && display.getTextDisplay() != null) {
            widget.getDisplay().setRotation(
                    display.getTextDisplay().getLocation().getYaw(),
                    display.getTextDisplay().getLocation().getPitch()
            );
            // Устанавливаем тот же тип биллборда
            widget.getDisplay().setBillboard(Display.Billboard.FIXED);
        }

        return addDrawableChild(widget);
    }

    public void setupDefaultWidgets(Player player) {
        if (location == null) {
            return; // Пропускаем создание виджетов, если нет location
        }
        
        // Создаем кнопки заголовка только при первом создании экрана
        if (followButton == null || saveButton == null || closeButton == null) {
            createTitleBarControlWidgets();
        }
        
        // Создаем основные виджеты экрана
        createScreenWidgets(player);
    }

    // Новый метод для создания виджетов конкретного экрана
    public void createScreenWidgets(Player player) {
        // По умолчанию пустой, переопределяется в подклассах
    }

    protected void createTitleBarControlWidgets() {
        if (location == null) {
            return; // Пропускаем создание кнопок, если нет location
        }
        
        WidgetPosition basePosition = new WidgetPosition(0.52, 0.92);

        // Кнопка закрытия (красная)
        TextDisplayConfig closeConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#ff2147")),
                Component.text("⏺").color(TextColor.fromHexString("#af2141")),
                this::tryClose
        )
                .setPosition(basePosition.clone().addHorizontal(0.14))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30), 0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60), 0);

        // Кнопка следования (желтая)
        TextDisplayConfig followConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#ffc72c")),
                Component.text("⏺").color(TextColor.fromHexString("#af802b")),
                this::toggleFollow
        )
                .setPosition(basePosition.clone())
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30), 0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60), 0);

        // Кнопка сохранения (зеленая)
        TextDisplayConfig saveConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#2aff55")),
                Component.text("⏺").color(TextColor.fromHexString("#29af48")),
                this::toggleSave
        )
                .setPosition(basePosition.clone().addHorizontal(-0.14))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30), 0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60), 0);

        this.closeButton = createTextWidget(closeConfig);
        this.followButton = createTextWidget(followConfig);
        this.saveButton = createTextWidget(saveConfig);

        // Устанавливаем начальное состояние кнопок в соответствии с глобальными флагами
        if (followButton != null) {
            followButton.getDisplay().setGlowing(isFollowing);
        }
        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(isSaved);
        }
    }

    private void toggleFollow() {
        // Если включен безопасный режим, выключаем его
        if (isSaved) {
            isSaved = false;
            savedPosition = null;
            if (saveButton != null) {
                saveButton.getDisplay().setGlowing(false);
            }
        }

        // Переключаем режим следования
        isFollowing = !isFollowing;
        if (followButton != null) {
            followButton.getDisplay().setGlowing(isFollowing);
        }

        // Обновляем цвет фона и проигрываем звук
        if (isFollowing) {
            Vector playerPos = viewer.getLocation().toVector();
            Vector displayPos = location.toVector();
            relativePosition = displayPos.subtract(playerPos);
            updateBackgroundColor("#f9c22b");
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2.0f);
        } else {
            updateBackgroundColor(null);
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
        }
    }

    private void toggleSave() {
        // Если включен режим следования, выключаем его
        if (isFollowing) {
            isFollowing = false;
            relativePosition = null;
            if (followButton != null) {
                followButton.getDisplay().setGlowing(false);
            }
        }

        // Переключаем безопасный режим
        isSaved = !isSaved;
        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(isSaved);
        }

        // Обновляем цвет фона и проигрываем звук
        if (isSaved) {
            savedPosition = location.toVector();
            updateBackgroundColor("#29e64d");
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.5f, 2.0f);
        } else {
            updateBackgroundColor(null);
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_LAND, 0.3f, 1.5f);
        }
    }

    private void updateBackgroundColor(String hexColor) {
        if (display != null && display.getTextDisplay() != null) {
            TextDisplay textDisplay = display.getTextDisplay();
            if (hexColor == null) {
                // Возвращаем стандартный цвет фона
                textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(100, 0, 0, 0));
            } else {
                // Конвертируем hex в RGB и устанавливаем новый цвет с прозрачностью
                java.awt.Color color = java.awt.Color.decode(hexColor);
                textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(
                        50, // Меньшая прозрачность для цветного фона
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue()
                ));
            }
        }
    }

    public void tryClose() {
        if (!isSaved || isPlayerInSavedRange()) {
            // Сохраняем состояние текущего экрана перед закрытием
            CreateTestUI.setWasInSettingsScreen(ChangeScreen.isSettingsScreen());

            // Сбрасываем флаг текущего экрана
            ChangeScreen.setSettingsScreen(false);

            // Звук закрытия для обоих экранов
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.5f, 1.0f);

            // Используем удаление с анимацией
            this.removeWithAnimation();

            if (onClose != null) onClose.run();
        } else {
            // Звук ошибки при попытке закрыть
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
            viewer.sendMessage(Component.text("Вернитесь к сохраненной позиции для закрытия!")
                .color(TextColor.fromHexString("#ef2142")));
        }
    }

    private boolean isPlayerInSavedRange() {
        if (!isSaved || savedPosition == null) return true;
        return viewer.getLocation().toVector().distance(savedPosition) <= 5;
    }

    public void updatePosition() {
        if (!isFollowing) return;

        // Обновляем только позицию дисплея относительно игрока
        Location newLoc = viewer.getLocation().clone();
        Vector newPos = newLoc.toVector().add(relativePosition);
        location.setX(newPos.getX());
        location.setY(newPos.getY());
        location.setZ(newPos.getZ());

        // Обновляем позицию основного дисплея без изменения поворота
        if (display != null && display.getTextDisplay() != null) {
            TextDisplay textDisplay = display.getTextDisplay();
            Location displayLoc = textDisplay.getLocation();
            displayLoc.setX(location.getX());
            displayLoc.setY(location.getY());
            displayLoc.setZ(location.getZ());
            textDisplay.teleport(displayLoc);
        }

        // Обновляем позиции всех виджетов
        for (Widget widget : children) {
            if (widget instanceof ItemDisplayButtonWidget) {
                updateWidgetPosition((ItemDisplayButtonWidget) widget);
            } else if (widget instanceof TextDisplayButtonWidget) {
                updateWidgetPosition((TextDisplayButtonWidget) widget);
            }
        }
    }

    private void updateWidgetPosition(ItemDisplayButtonWidget widget) {
        Location buttonLoc = location.clone();
        Vector direction = buttonLoc.getDirection();
        Vector right = direction.getCrossProduct(new Vector(0, 1, 0)).normalize();
        Vector up = right.getCrossProduct(direction).normalize();

        WidgetPosition position = widget.getPosition();
        if (position != null) {
            buttonLoc.add(right.multiply(position.getRightMultiplier()));
            buttonLoc.add(up.multiply(position.getUpMultiplier()));
        }

        // Сохраняем текущий поворот виджета
        Location currentLoc = widget.getDisplay().getLocation();
        buttonLoc.setYaw(currentLoc.getYaw());
        buttonLoc.setPitch(currentLoc.getPitch());

        widget.getDisplay().teleport(buttonLoc);
    }

    private void updateWidgetPosition(TextDisplayButtonWidget widget) {
        Location buttonLoc = location.clone();
        Vector direction = buttonLoc.getDirection();
        Vector right = direction.getCrossProduct(new Vector(0, 1, 0)).normalize();
        Vector up = right.getCrossProduct(direction).normalize();

        WidgetPosition position = widget.getPosition();
        if (position != null) {
            buttonLoc.add(right.multiply(position.getRightMultiplier()));
            buttonLoc.add(up.multiply(position.getUpMultiplier()));
        }

        // Сохраняем текущий поворот виджета
        Location currentLoc = widget.getDisplay().getLocation();
        buttonLoc.setYaw(currentLoc.getYaw());
        buttonLoc.setPitch(currentLoc.getPitch());

        widget.getDisplay().teleport(buttonLoc);
    }

    private Runnable onClose;

    public void setOnClose(Runnable callback) {
        this.onClose = callback;
    }

    // Геттер для доступа к списку виджетов
    public List<Widget> getChildren() {
        return children;
    }

    // Добавляем геттер для location
    public Location getLocation() {
        return location;
    }

    // Добавляем метод для установки location
    public void setLocation(Location location) {
        this.location = location;
        if (display != null && display.getTextDisplay() != null) {
            display.getTextDisplay().teleport(location);
        }
    }

    // Изменяем protected конструктор
    protected Screen() {
        super(null, new Location(Bukkit.getWorlds().get(0), 0, 0, 0)); // Используем дефолтную локацию
        this.display = null;
        this.currentScreenClass = this.getClass();
    }

    // Добавляем новый конструктор с viewer
    protected Screen(Player viewer, Location location) {
        super(viewer, location);
        this.display = null;
        this.currentScreenClass = this.getClass();
    }

    // Добавляем геттер для получения текущего класса экрана
    public Class<? extends Screen> getCurrentScreenClass() {
        return currentScreenClass;
    }

    /**
     * Возвращает класс родительского экрана.
     * Если экран является главным, возвращает null.
     */
    public Class<? extends Screen> getParentScreen() {
        return null; // По умолчанию null - для главного экрана
    }

    /**
     * Возвращает конфигурации виджетов для данного экрана
     * @param player Игрок, для которого создаются виджеты
     * @return Массив конфигураций виджетов
     */
    public WidgetConfig[] getBranchWidgets(Player player) {
        return new WidgetConfig[0]; // По умолчанию пустой массив
    }
}
