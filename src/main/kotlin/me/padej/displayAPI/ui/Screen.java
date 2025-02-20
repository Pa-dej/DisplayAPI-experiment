package me.padej.displayAPI.ui;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.shapes.StringRectangle;
import me.padej.displayAPI.ui.annotations.Main;
import me.padej.displayAPI.ui.annotations.Persistent;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.*;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.DisplayUtils;
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
    private final Class<? extends Screen> CURRENT_SCREEN_CLASS;

    private static boolean isFollowing = false;
    public static boolean isSaved = false;
    private static Vector relativePosition;
    private static Vector savedPosition;

    @Persistent("Control buttons")
    private TextDisplayButtonWidget followButton;
    @Persistent("Control buttons")
    private TextDisplayButtonWidget saveButton;
    @Persistent("Control buttons")
    private TextDisplayButtonWidget closeButton;
    
    public Player viewer;

    public Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location);
        this.viewer = viewer;
        CURRENT_SCREEN_CLASS = this.getClass();

        display = new StringRectangle(
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
                DisplayUtils.lookAtPos(textDisplay, viewerLoc);
            }
        }
    }
    
    @Override
    public void remove() {
        if (isSaved && !isPlayerInSavedRange()) {
            return;
        }

        isFollowing = false;
        isSaved = false;
        relativePosition = null;
        savedPosition = null;

        if (followButton != null) {
            followButton.getDisplay().setGlowing(false);
        }
        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(false);
        }

        updateBackgroundColor(null);

        for (Widget widget : children) {
            widget.remove();
        }
        if (display != null) {
            display.removeEntity();
        }
        super.remove();
    }

    public void removeWithAnimation() {
        if (isSaved && !isPlayerInSavedRange()) {
            return;
        }

        isFollowing = false;
        isSaved = false;
        relativePosition = null;
        savedPosition = null;

        if (followButton != null) {
            followButton.getDisplay().setGlowing(false);
        }

        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(false);
        }

        updateBackgroundColor(null);

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

            for (Widget widget : children) {
                widget.removeWithAnimation(5);
            }

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

        if (display != null && display.getTextDisplay() != null) {
            widget.getDisplay().setRotation(
                    display.getTextDisplay().getLocation().getYaw(),
                    display.getTextDisplay().getLocation().getPitch()
            );
            widget.getDisplay().setBillboard(Display.Billboard.FIXED);
        }

        return addDrawableChild(widget);
    }

    public void setupDefaultWidgets(Player player) {
        if (location == null) {
            return;
        }

        if (followButton == null || saveButton == null || closeButton == null) {
            createTitleBarControlWidgets();
        }

        createScreenWidgets(player);
    }

    public void createScreenWidgets(Player player) {}

    protected void createTitleBarControlWidgets() {
        if (location == null) {
            return;
        }
        
        WidgetPosition basePosition = new WidgetPosition(0.52, 0.92);

        // Добавляем кнопку возврата для экранов без аннотации @Main
        if (!this.getClass().isAnnotationPresent(Main.class)) {
            TextDisplayConfig returnConfig = new TextDisplayConfig(
                    Component.text("⏴").color(TextColor.fromHexString("#fafeff")),
                    Component.text("⏴").color(TextColor.fromHexString("#aaaeaf")),
                    () -> {
                        Screen currentScreen = UIManager.getInstance().getActiveScreen(viewer);
                        if (currentScreen != null) {
                            ChangeScreen.switchToParent(viewer, currentScreen.getCurrentScreenClass());
                        }
                    }
            )
                    .setPosition(basePosition.clone().addHorizontal(-0.28))
                    .setScale(0.75f, 0.75f, 0.75f)
                    .setTolerance(0.035)
                    .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30))
                    .setBackgroundAlpha(0)
                    .setHoveredBackgroundAlpha(0)
                    .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60));

            createTextWidget(returnConfig);
        }

        TextDisplayConfig closeConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#ff2147")),
                Component.text("⏺").color(TextColor.fromHexString("#af2141")),
                this::tryClose
        )
                .setPosition(basePosition.clone().addHorizontal(0.14))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30))
                .setBackgroundAlpha(0)
                .setHoveredBackgroundAlpha(0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60));

        TextDisplayConfig followConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#ffc72c")),
                Component.text("⏺").color(TextColor.fromHexString("#af802b")),
                this::toggleFollow
        )
                .setPosition(basePosition.clone())
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30))
                .setBackgroundAlpha(0)
                .setHoveredBackgroundAlpha(0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60));

        TextDisplayConfig saveConfig = new TextDisplayConfig(
                Component.text("⏺").color(TextColor.fromHexString("#2aff55")),
                Component.text("⏺").color(TextColor.fromHexString("#29af48")),
                this::toggleSave
        )
                .setPosition(basePosition.clone().addHorizontal(-0.14))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.035)
                .setBackgroundColor(org.bukkit.Color.fromRGB(30, 30, 30))
                .setBackgroundAlpha(0)
                .setHoveredBackgroundAlpha(0)
                .setHoveredBackgroundColor(org.bukkit.Color.fromRGB(60, 60, 60));

        this.closeButton = createTextWidget(closeConfig);
        this.followButton = createTextWidget(followConfig);
        this.saveButton = createTextWidget(saveConfig);

        if (followButton != null) {
            followButton.getDisplay().setGlowing(isFollowing);
        }

        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(isSaved);
        }
    }

    private void toggleFollow() {
        if (isSaved) {
            isSaved = false;
            savedPosition = null;
            if (saveButton != null) {
                saveButton.getDisplay().setGlowing(false);
            }
        }

        isFollowing = !isFollowing;
        if (followButton != null) {
            followButton.getDisplay().setGlowing(isFollowing);
        }

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
        if (isFollowing) {
            isFollowing = false;
            relativePosition = null;
            if (followButton != null) {
                followButton.getDisplay().setGlowing(false);
            }
        }

        isSaved = !isSaved;
        if (saveButton != null) {
            saveButton.getDisplay().setGlowing(isSaved);
        }

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
                textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(100, 0, 0, 0));
            } else {
                java.awt.Color color = java.awt.Color.decode(hexColor);
                textDisplay.setBackgroundColor(org.bukkit.Color.fromARGB(
                        50,
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue()
                ));
            }
        }
    }

    public void tryClose() {
        if (!isSaved || isPlayerInSavedRange()) {
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.5f, 1.0f);

            this.removeWithAnimation();

            if (onClose != null) onClose.run();
        }
    }

    private boolean isPlayerInSavedRange() {
        if (!isSaved || savedPosition == null) return true;
        return viewer.getLocation().toVector().distance(savedPosition) <= 5;
    }

    public void updatePosition() {
        if (!isFollowing) return;

        Location newLoc = viewer.getLocation().clone();
        Vector newPos = newLoc.toVector().add(relativePosition);
        location.setX(newPos.getX());
        location.setY(newPos.getY());
        location.setZ(newPos.getZ());

        if (display != null && display.getTextDisplay() != null) {
            TextDisplay textDisplay = display.getTextDisplay();
            Location displayLoc = textDisplay.getLocation();
            displayLoc.setX(location.getX());
            displayLoc.setY(location.getY());
            displayLoc.setZ(location.getZ());
            textDisplay.teleport(displayLoc);
        }

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

        Location currentLoc = widget.getDisplay().getLocation();
        buttonLoc.setYaw(currentLoc.getYaw());
        buttonLoc.setPitch(currentLoc.getPitch());

        widget.getDisplay().teleport(buttonLoc);
    }

    private Runnable onClose;

    public void setOnClose(Runnable callback) {
        this.onClose = callback;
    }

    public List<Widget> getChildren() {
        return children;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (display != null && display.getTextDisplay() != null) {
            display.getTextDisplay().teleport(location);
        }
    }

    protected Screen() {
        super(null, new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
        this.viewer = null;
        display = null;
        CURRENT_SCREEN_CLASS = this.getClass();
    }

    protected Screen(Player viewer, Location location) {
        super(viewer, location);
        this.viewer = viewer;
        display = null;
        CURRENT_SCREEN_CLASS = this.getClass();
    }

    public Class<? extends Screen> getCurrentScreenClass() {
        return CURRENT_SCREEN_CLASS;
    }

    public Class<? extends Screen> getParentScreen() {
        return null; // По умолчанию null - для главного экрана
    }

    /**
     * Возвращает конфигурации виджетов для данного экрана
     * @param player Игрок, для которого создаются виджеты
     * @return Массив конфигураций виджетов
     */
    public WidgetConfig[] getBranchWidgets(Player player) {
        return new WidgetConfig[0];
    }

    public TextDisplayConfig[] getTextWidgets(Player player) {
        return new TextDisplayConfig[0];
    }
}
