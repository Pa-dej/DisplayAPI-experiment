package me.padej.displayAPI.ui;

import io.papermc.paper.entity.LookAnchor;
import me.padej.displayAPI.render.shapes.StringRectangle;
import me.padej.displayAPI.ui.widgets.*;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
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

        // Сбрасываем режимы перед удалением
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
        // Кнопки смены режима игры
        createGamemodeButtons(player);

        // Кнопки управления в заголовке
        createTitleBarControlWidgets();
    }

    private void createGamemodeButtons(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        WidgetConfig[] itemButtons = {
            new WidgetConfig(Material.GRASS_BLOCK, () -> {
                player.performCommand("gamemode creative");
                // Звук смены режима на креатив
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
            })
            .setTooltip("Творческий")
            .setTooltipDelay(10)
            .setPosition(basePosition.clone())
            .setDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI),

            new WidgetConfig(Material.IRON_SWORD, () -> {
                player.performCommand("gamemode survival");
                // Звук смены режима на выживание
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_IRON, 0.5f, 1.0f);
            })
            .setTooltip("Выживание")
            .setTooltipDelay(10)
            .setPosition(basePosition.clone().addVertical(step)),

            new WidgetConfig(Material.FILLED_MAP, () -> {
                player.performCommand("gamemode adventure");
                // Звук смены режима на приключение
                player.playSound(player.getLocation(), org.bukkit.Sound.ITEM_BOOK_PAGE_TURN, 0.5f, 1.0f);
            })
            .setTooltip("Приключение")
            .setTooltipDelay(10)
            .setPosition(basePosition.clone().addVertical(step * 2)),

            new WidgetConfig(Material.ENDER_EYE, () -> {
                player.performCommand("gamemode spectator");
                // Звук смены режима на наблюдателя
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.3f, 1.5f);
            })
            .setTooltip("Наблюдатель")
            .setTooltipDelay(10)
            .setPosition(basePosition.clone().addVertical(step * 3))
        };

        for (WidgetConfig config : itemButtons) {
            ItemDisplayButtonWidget widget = createWidget(config);
            if (config.getDisplayTransform() != ItemDisplay.ItemDisplayTransform.NONE) {
                widget.setDisplayTransform(config.getDisplayTransform());
            }
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
            updateBackgroundColor(null);
        }
        
        isFollowing = !isFollowing;
        followButton.getDisplay().setGlowing(isFollowing);
        
        if (isFollowing) {
            Vector playerPos = viewer.getLocation().toVector();
            Vector displayPos = location.toVector();
            relativePosition = displayPos.subtract(playerPos);
            updateBackgroundColor("#f9c22b");
            // Звук активации следования
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2.0f);
        } else {
            updateBackgroundColor(null);
            // Звук деактивации следования
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
        }
    }

    private void toggleSave() {
        if (isFollowing) {
            isFollowing = false;
            relativePosition = null;
            followButton.getDisplay().setGlowing(false);
            updateBackgroundColor(null);
        }
        
        isSaved = !isSaved;
        saveButton.getDisplay().setGlowing(isSaved);
        
        if (isSaved) {
            savedPosition = location.toVector();
            updateBackgroundColor("#29e64d");
            // Звук активации сохранения
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.5f, 2.0f);
        } else {
            updateBackgroundColor(null);
            // Звук деактивации сохранения
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

    private void tryClose() {
        if (!isSaved || isPlayerInSavedRange()) {
            // Звук успешного закрытия
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.5f, 1.0f);
            this.remove();
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
}
