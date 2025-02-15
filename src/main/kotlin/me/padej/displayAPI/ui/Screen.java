package me.padej.displayAPI.ui;

import io.papermc.paper.entity.LookAnchor;
import me.padej.displayAPI.render.shapes.StringRectangle;
import me.padej.displayAPI.ui.widgets.*;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.bukkit.Material;
import me.padej.displayAPI.DisplayAPI;
import net.kyori.adventure.text.Component;

public class Screen extends WidgetManager {
    private final StringRectangle display;

    private boolean isFollowing = false;
    public static boolean isSaved = false;
    private Vector relativePosition;  // Позиция дисплея относительно игрока
    private Vector savedPosition;  // Позиция для режима сохранения

    private TextDisplayButtonWidget followButton;
    private TextDisplayButtonWidget saveButton;

    public Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location);

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
            return; // Блокируем удаление если активно сохранение и игрок далеко
        }
        super.remove();
        if (display != null) {
            display.removeEntity();
        }
    }

    public boolean isPlayerInRange() {
        return viewer.getLocation().distance(location) <= 5;
    }

    public TextDisplay getTextDisplay() {
        return display != null ? display.getTextDisplay() : null;
    }

    public ItemDisplayButtonWidget createWidget(WidgetConfig config) {
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

        return addDrawableChild(widget);
    }

    public TextDisplayButtonWidget createTextWidget(TextDisplayConfig config) {
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
        // Предметные кнопки с красителями
        createColorDyeButtons(player);

        // Кнопка закрытия в виде кружка
        createTitleBarControlWidgets();
    }

    private void createColorDyeButtons(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.44f, 0.92f);
        float step = 0.22f;

        WidgetConfig[] itemButtons = {
                new WidgetConfig(Material.RED_DYE, () -> {
                    player.sendMessage("Красный краситель");
                })
                        .setTooltip("красный")
                        .setTooltipDelay(10)
                        .setPosition(basePosition.clone()),

                new WidgetConfig(Material.ORANGE_DYE, () -> {
                    player.sendMessage("Оранжевый краситель");
                })
                        .setTooltip("оранжевый")
                        .setTooltipDelay(10)
                        .setPosition(basePosition.clone().addHorizontal(step)),

                new WidgetConfig(Material.YELLOW_DYE, () -> {
                    player.sendMessage("Желтый краситель");
                })
                        .setTooltip("желтый")
                        .setTooltipDelay(10)
                        .setPosition(basePosition.clone().addHorizontal(step * 2)),

                new WidgetConfig(Material.LIME_DYE, () -> {
                    player.sendMessage("Лаймовый краситель");
                })
                        .setTooltip("лаймовый")
                        .setTooltipDelay(10)
                        .setPosition(basePosition.clone().addHorizontal(step * 3))
        };

        for (WidgetConfig config : itemButtons) {
            createWidget(config);
        }
    }

    private void createTitleBarControlWidgets() {
        WidgetPosition basePosition = new WidgetPosition(0.52, 0.92);

        // Кнопка закрытия
        TextDisplayConfig closeButton = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#ef2142")),
                Component.text("⏺").color(TextColor.fromHexString("#9a080f")),
                this::tryClose
        )
                .setPosition(basePosition.clone().addHorizontal(0.14))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30), 0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60), 0);

        // Кнопка следования
        TextDisplayConfig followConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#f9c22b")),
                Component.text("⏺").color(TextColor.fromHexString("#fb6b1d")),
                this::toggleFollow
        )
                .setPosition(basePosition.clone())
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30), 0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60), 0);

        // Кнопка сохранения
        TextDisplayConfig saveConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#29e64d")),
                Component.text("⏺").color(TextColor.fromHexString("#11aa38")),
                this::toggleSave
        )
                .setPosition(basePosition.clone().addHorizontal(-0.14))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30), 0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60), 0);

        createTextWidget(closeButton);
        this.followButton = createTextWidget(followConfig);
        this.saveButton = createTextWidget(saveConfig);
    }

    private void toggleFollow() {
        if (isSaved) {
            isSaved = false;
            savedPosition = null;
            saveButton.getDisplay().setGlowing(false);
            updateBackgroundColor(null); // Сбрасываем цвет фона
        }

        isFollowing = !isFollowing;
        followButton.getDisplay().setGlowing(isFollowing);

        if (isFollowing) {
            Vector playerPos = viewer.getLocation().toVector();
            Vector displayPos = location.toVector();
            relativePosition = displayPos.subtract(playerPos);
            updateBackgroundColor("#f9c22b"); // Оранжевый цвет для режима следования
        } else {
            updateBackgroundColor(null); // Сбрасываем цвет фона при отключении
        }
    }

    private void toggleSave() {
        if (isFollowing) {
            isFollowing = false;
            relativePosition = null;
            followButton.getDisplay().setGlowing(false);
            updateBackgroundColor(null); // Сбрасываем цвет фона
        }

        isSaved = !isSaved;
        saveButton.getDisplay().setGlowing(isSaved);

        if (isSaved) {
            savedPosition = location.toVector();
            updateBackgroundColor("#29e64d"); // Зеленый цвет для режима сохранения
        } else {
            updateBackgroundColor(null); // Сбрасываем цвет фона при отключении
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

    private void tryClose() {
        if (isSaved) return;
        if (isPlayerInSavedRange()) {
            this.remove();
            if (onClose != null) onClose.run();
        } else {
            // Отправляем сообщение игроку, что нужно вернуться к сохраненной позиции
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
}
