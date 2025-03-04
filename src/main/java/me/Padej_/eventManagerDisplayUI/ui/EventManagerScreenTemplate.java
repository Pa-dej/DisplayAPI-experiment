package me.Padej_.eventManagerDisplayUI.ui;

import me.Padej_.eventManagerDisplayUI.ui.template.WidgetTemplate;
import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.shapes.StringRectangle;
import me.padej.displayAPI.ui.*;
import me.padej.displayAPI.ui.annotations.AlwaysOnScreen;
import me.padej.displayAPI.ui.annotations.Main;
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
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EventManagerScreenTemplate extends WidgetManager implements IDisplayable, IParentable, Animatable, WidgetTemplate {
    private final StringRectangle display;
    private final Class<? extends EventManagerScreenTemplate> CURRENT_SCREEN_CLASS;
    public static final HashMap<UUID, Class<? extends EventManagerScreenTemplate>> lastScreenMap = new HashMap<>();

    public static boolean isFollowing = false;
    private static Vector savedPosition;
    private static Vector relativePosition;

    @AlwaysOnScreen(EventManagerScreenTemplate.class)
    private TextDisplayButtonWidget followButton;

    public Player viewer;

    public EventManagerScreenTemplate(Player viewer, Location location, String text, float scale) {
        super(viewer, location);
        this.viewer = viewer;
        CURRENT_SCREEN_CLASS = this.getClass();

        display = new StringRectangle(
                scale,
                Color.fromRGB(10, 10, 10),
                230,
                Display.Billboard.FIXED,
                false,
                text
        ) {
        };

        spawn();
    }

    private void spawn() {
        if (display != null) {
            TextDisplay textDisplay = display.spawn(location);
            if (textDisplay != null) {
                textDisplay.setBrightness(new Display.Brightness(15, 15));
                textDisplay.setVisibleByDefault(true);
                viewer.showEntity(DisplayAPI.getInstance(), textDisplay); // Показываем только создателю

                // Поворачиваем дисплей к игроку при создании
                Location viewerLoc = viewer.getLocation().add(0, viewer.getHeight() / 2 + 0.2, 0);
                DisplayUtils.lookAtPos(textDisplay, viewerLoc);
            }
        }
    }

    @Override
    public void remove() {
        if (!isPlayerInSavedRange()) {
            return;
        }

        resetVarsAndBackground();

        softRemove();
        super.remove();
    }

    public void removeWithAnimation() {
        if (!isPlayerInSavedRange()) {
            return;
        }
        resetVarsAndBackground();

        softRemoveWithAnimation();
    }

    public void softRemove() {
        for (Widget widget : children) {
            widget.remove();
        }
        if (display != null) {
            display.removeEntity();
        }
        super.remove();
    }

    @Override
    public void softRemoveWithAnimation() {
        for (Widget widget : children) {
            widget.removeWithAnimation(5);
        }
        if (display != null && display.getTextDisplay() != null) {
            Animation.applyTransformationWithInterpolation(
                    display.getTextDisplay(),
                    new Transformation(
                            display.getTextDisplay().getTransformation().getTranslation().add(0, 0.5f, -1f),
                            display.getTextDisplay().getTransformation().getLeftRotation(),
                            new Vector3f(0, 0, 0),
                            display.getTextDisplay().getTransformation().getRightRotation()
                    ),
                    5
            );
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
                if (display.getTextDisplay() != null) {
                    display.removeEntity();
                }
            }, 6);
        }
    }

    private void resetVarsAndBackground() {
        isFollowing = false;
        savedPosition = null;
    }

    public boolean isPlayerInRange() {
        return viewer.getLocation().distance(location) <= 5;
    }

    @Override
    public TextDisplay getTextDisplay() {
        return display != null ? display.getTextDisplay() : null;
    }

    public ItemDisplayButtonWidget createWidget(ItemDisplayButtonConfig config) {
        Location buttonLoc = location.clone();
        Vector direction = buttonLoc.getDirection();
        Vector right = direction.getCrossProduct(new Vector(0, 1, 0)).normalize();
        Vector up = right.getCrossProduct(direction).normalize();

        WidgetPosition position = config.getPosition();
        if (position != null) {
            buttonLoc.add(right.multiply(position.getRightMultiplier()));
            buttonLoc.add(up.multiply(position.getUpMultiplier()));
            buttonLoc.add(direction.multiply(position.getDepth()));
        }

        ItemDisplayButtonWidget widget = ItemDisplayButtonWidget.create(
                buttonLoc,
                viewer,
                config
        );

        addDrawableChild(widget);
        return widget;
    }

    @Override
    public TextDisplayButtonWidget createTextWidget(TextDisplayButtonConfig config) {
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
            buttonLoc.add(direction.multiply(position.getDepth()));
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

        if (followButton == null) {
            createTitleBarControlWidgets();
        }

        createScreenWidgets(player);
    }

    public void createScreenWidgets(Player player) {
    }

    protected void createTitleBarControlWidgets() {
        if (location == null) {
            return;
        }

        WidgetPosition basePosition = new WidgetPosition(0.52, 0.92);

        TextDisplayButtonConfig closeConfig = new TextDisplayButtonConfig(
                Component.text("✘").color(TextColor.fromHexString("#bf0012")),
                Component.text("✘").color(TextColor.fromHexString("#800018")),
                this::tryClose
        )
                .setPosition(basePosition.clone().addHorizontal(0.14))
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.05)
                .setHoveredTransformation(new Transformation(
                        new Vector3f(0, -.075f, 0),
                        new AxisAngle4f(),
                        new Vector3f(.6f, .6f, 0f),
                        new AxisAngle4f()
                ), 3)
                .setBackgroundAlpha(0)
                .setHoveredBackgroundAlpha(0);

        TextDisplayButtonConfig followConfig = new TextDisplayButtonConfig(
                Component.text("◆").color(TextColor.fromHexString("#b6b6b6")),
                Component.text("◆").color(TextColor.fromHexString("#6d6d6d")),
                this::toggleFollow
        )
                .setPosition(basePosition.clone())
                .setScale(0.75f, 0.75f, 0.75f)
                .setTolerance(0.05)
                .setHoveredTransformation(new Transformation(
                        new Vector3f(0, -.075f, 0),
                        new AxisAngle4f(),
                        new Vector3f(.6f, .6f, 0f),
                        new AxisAngle4f()
                ), 3)
                .setBackgroundAlpha(0)
                .setHoveredBackgroundAlpha(0);

        if (!this.getClass().isAnnotationPresent(Main.class)) {
            TextDisplayButtonConfig returnConfig = new TextDisplayButtonConfig(
                    Component.text("⏪").color(TextColor.fromHexString("#b6b6b6")),
                    Component.text("⏪").color(TextColor.fromHexString("#6d6d6d")),
                    () -> {
                        EventManagerScreenTemplate currentScreen = (EventManagerScreenTemplate) UIManager.getInstance().getActiveScreen(viewer);
                        if (currentScreen != null) {
                            ChangeScreen.switchToParent(viewer, currentScreen.getCurrentScreenClass());
                        }
                    }
            )
                    .setPosition(basePosition.clone().addHorizontal(-0.14f))
                    .setScale(0.75f, 0.75f, 0.75f)
                    .setTolerance(0.05)
                    .setHoveredTransformation(new Transformation(
                            new Vector3f(0, -.075f, 0),
                            new AxisAngle4f(),
                            new Vector3f(.6f, .6f, 0f),
                            new AxisAngle4f()
                    ), 3)
                    .setBackgroundAlpha(0)
                    .setHoveredBackgroundAlpha(0);

            createTextWidget(returnConfig);
        }

        createTextWidget(closeConfig);
        this.followButton = createTextWidget(followConfig);

        if (followButton != null) {
            followButton.getDisplay().setGlowing(isFollowing);
        }
    }

    public void toggleFollow() {
        isFollowing = !isFollowing;
        if (followButton != null) {
            updateFollowButtonText();
        }

        if (isFollowing) {
            Vector playerPos = viewer.getLocation().toVector();
            Vector displayPos = location.toVector();
            relativePosition = displayPos.subtract(playerPos);
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2.0f);
        } else {
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.5f);
        }
    }

    private void updateFollowButtonText() {
        if (followButton != null) {
            String text = isFollowing ? "◇" : "◆";
            followButton.setText(
                    Component.text(text).color(isFollowing ? TextColor.fromHexString("#6d6d6d") : TextColor.fromHexString("#b6b6b6")),
                    Component.text(text).color(isFollowing ? TextColor.fromHexString("#6d6d6d") : TextColor.fromHexString("#b6b6b6"))
            );
        }
    }

    public void tryClose() {
        if (isPlayerInSavedRange()) {
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.BLOCK_WOODEN_DOOR_CLOSE, 0.5f, 1.0f);
            WidgetManager manager = UIManager.getInstance().getActiveScreen(this.viewer);
            if (manager instanceof EventManagerScreenTemplate screen) {
                lastScreenMap.put(this.viewer.getUniqueId(), screen.getCurrentScreenClass());
            }

            this.removeWithAnimation();

            if (onClose != null) onClose.run();
        }
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
            buttonLoc.add(direction.multiply(position.getDepth()));
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
            buttonLoc.add(direction.multiply(position.getDepth()));
        }

        Location currentLoc = widget.getDisplay().getLocation();
        buttonLoc.setYaw(currentLoc.getYaw());
        buttonLoc.setPitch(currentLoc.getPitch());

        widget.getDisplay().teleport(buttonLoc);
    }

    private boolean isPlayerInSavedRange() {
        if (savedPosition == null) return true;
        return viewer.getLocation().toVector().distance(savedPosition) <= 5;
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

    protected EventManagerScreenTemplate() {
        super(null, new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0));
        this.viewer = null;
        display = null;
        CURRENT_SCREEN_CLASS = this.getClass();
    }

    protected EventManagerScreenTemplate(Player viewer, Location location) {
        super(viewer, location);
        this.viewer = viewer;
        display = null;
        CURRENT_SCREEN_CLASS = this.getClass();
    }

    public Class<? extends EventManagerScreenTemplate> getCurrentScreenClass() {
        return CURRENT_SCREEN_CLASS;
    }

    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return null; // По умолчанию null - для главного экрана
    }

    public ItemDisplayButtonConfig[] getBranchWidgets(Player player) {
        return new ItemDisplayButtonConfig[0];
    }

    public TextDisplayButtonConfig[] getTextWidgets(Player player) {
        return new TextDisplayButtonConfig[0];
    }

    @Override
    public void updateDisplayPosition(Location location, float yaw, float pitch) {
        if (display != null && display.getTextDisplay() != null) {
            Location displayLoc = display.getTextDisplay().getLocation();
            displayLoc.setX(location.getX());
            displayLoc.setY(location.getY());
            displayLoc.setZ(location.getZ());
            displayLoc.setYaw(yaw);
            displayLoc.setPitch(pitch);
            display.getTextDisplay().teleport(displayLoc);
        }
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (display != null && display.getTextDisplay() != null) {
            display.getTextDisplay().setBackgroundColor(color);
        }
    }

    @Override
    public Class<? extends WidgetManager> getParentManager() {
        return getParentScreen();
    }

    @Override
    public void createWithAnimation(Player player) {
        createDefaultScreenWithAnimation(this, player);
    }

    public static void createDefaultScreenWithAnimation(EventManagerScreenTemplate screen, Player player) {
        Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
            if (screen.getTextDisplay() != null) {
                Animation.applyTransformationWithInterpolation(screen.getTextDisplay(), new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), new AxisAngle4f(), new Vector3f(10.0F, 4.0F, 1.0F), new AxisAngle4f()), 5);
                screen.setOnClose(() -> {
                    UIManager.getInstance().unregisterScreen(player);
                });
                screen.setupDefaultWidgets(player);
            }
        }, 2L);
        UIManager.getInstance().registerScreen(player, screen);
    }
}