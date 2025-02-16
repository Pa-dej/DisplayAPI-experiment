package me.padej.displayAPI.ui.widgets;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.utils.PointDetection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import me.padej.displayAPI.utils.Animation;

import java.time.Duration;

public class TextDisplayButtonWidget implements Widget {
    private TextDisplay display;
    private Player viewer;
    private boolean isHovered = false;
    private Runnable onClick;
    private Location location;
    private Component text;
    private Component hoveredText;
    private Component tooltip;
    private boolean isShowingTooltip = false;
    private int tooltipDelay = 0;
    private int hoverTicks = 0;
    private TextColor tooltipColor = TextColor.fromHexString("#868788");
    private Color backgroundColor;
    private int backgroundAlpha;
    private Color hoveredBackgroundColor;
    private int hoveredBackgroundAlpha;
    
    private float scaleX = .15f;
    private float scaleY = .15f;
    private float scaleZ = .15f;
    private double tolerance = 0.06;
    
    private WidgetPosition position;
    
    private TextDisplayButtonWidget() {}
    
    public static TextDisplayButtonWidget create(Location location, Player viewer, TextDisplayConfig config) {
        TextDisplayButtonWidget widget = new TextDisplayButtonWidget();
        widget.location = location;
        widget.viewer = viewer;
        widget.onClick = config.getOnClick();
        widget.text = config.getText();
        widget.hoveredText = config.getHoveredText();
        widget.backgroundColor = config.getBackgroundColor();
        widget.backgroundAlpha = config.getBackgroundAlpha();
        widget.hoveredBackgroundColor = config.getHoveredBackgroundColor();
        widget.hoveredBackgroundAlpha = config.getHoveredBackgroundAlpha();
        widget.scaleX = config.getScaleX();
        widget.scaleY = config.getScaleY();
        widget.scaleZ = config.getScaleZ();
        widget.tolerance = config.getTolerance();
        widget.position = config.getPosition();
        
        if (config.getTooltip() != null) {
            widget.tooltip = config.getTooltip();
            widget.tooltipDelay = config.getTooltipDelay();
            widget.tooltipColor = config.getTooltipColor();
        }
        
        widget.spawn();
        return widget;
    }
    
    private void spawn() {
        this.display = location.getWorld().spawn(location, TextDisplay.class);
        
        display.text(text);
        display.setBackgroundColor(org.bukkit.Color.fromARGB(
            backgroundAlpha,
            backgroundColor.getRed(),
            backgroundColor.getGreen(),
            backgroundColor.getBlue()
        ));
        display.setBrightness(new Display.Brightness(15, 15));
        
        // Начальная трансформация с нулевым масштабом
        Transformation initialTransform = new Transformation(
            new Vector3f(0, 0, 0),
            new AxisAngle4f(),
            new Vector3f(0, 0, 0),
            new AxisAngle4f()
        );
        display.setTransformation(initialTransform);
        
        // Настройка длительности анимаций
        display.setInterpolationDuration(1);
        display.setTeleportDuration(1);
        
        display.setBillboard(Display.Billboard.FIXED);
        display.setVisibleByDefault(false);
        viewer.showEntity(DisplayAPI.getInstance(), display);

        // Анимация появления
        Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
            Animation.applyTransformationWithInterpolation(
                display,
                new Transformation(
                    new Vector3f(0, -scaleY / 8, 0),
                    new AxisAngle4f(),
                    new Vector3f(scaleX, scaleY, scaleZ),
                    new AxisAngle4f()
                ),
                5
            );
        }, 1);
    }
    
    public void update() {
        Vector eye = viewer.getEyeLocation().toVector();
        Vector direction = viewer.getEyeLocation().getDirection();
        Vector point = display.getLocation().toVector();
        
        boolean isLookingAt = PointDetection.lookingAtPoint(eye, direction, point, tolerance);
        
        if (isLookingAt != isHovered) {
            isHovered = isLookingAt;
            display.setGlowing(isHovered);
            
            if (isHovered) {
                display.text(hoveredText);
                display.setBackgroundColor(org.bukkit.Color.fromARGB(
                    hoveredBackgroundAlpha,
                    hoveredBackgroundColor.getRed(),
                    hoveredBackgroundColor.getGreen(),
                    hoveredBackgroundColor.getBlue()
                ));
            } else {
                display.text(text);
                display.setBackgroundColor(org.bukkit.Color.fromARGB(
                    backgroundAlpha,
                    backgroundColor.getRed(),
                    backgroundColor.getGreen(),
                    backgroundColor.getBlue()
                ));
                hoverTicks = 0;
                if (isShowingTooltip) {
                    hideTooltip();
                }
            }
        }

        if (isHovered && tooltip != null) {
            if (tooltipDelay > 0) {
                hoverTicks++;
                if (hoverTicks >= tooltipDelay && !isShowingTooltip) {
                    showTooltip();
                }
            } else if (!isShowingTooltip) {
                showTooltip();
            }
        }
    }
    
    public void handleClick() {
        if (isHovered) {
            // Проигрываем звук клика
            viewer.playSound(viewer.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.5f, 2.0f);
            onClick.run();
        }
    }
    
    public void remove() {
        if (isShowingTooltip) {
            hideTooltip();
        }
        display.remove();
    }
    
    public TextDisplay getDisplay() {
        return display;
    }
    
    public boolean isHovered() {
        return isHovered;
    }

    public TextDisplayButtonWidget setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
        
        if (display != null) {
            Transformation transformation = new Transformation(
                new Vector3f(0, -y / 2, 0),
                new AxisAngle4f(),
                new Vector3f(x, y, z),
                new AxisAngle4f()
            );
            display.setTransformation(transformation);
        }
        return this;
    }

    public TextDisplayButtonWidget setTolerance(double tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    public WidgetPosition getPosition() {
        return position;
    }

    public void setPosition(WidgetPosition position) {
        this.position = position;
    }

    private void showTooltip() {
        Title title = Title.title(
            Component.empty(),
            tooltip.color(tooltipColor),
            Title.Times.times(
                Duration.ofMillis(200),
                Duration.ofMillis(1000),
                Duration.ofMillis(200)
            )
        );
        viewer.showTitle(title);
        isShowingTooltip = true;
    }

    private void hideTooltip() {
        viewer.clearTitle();
        isShowingTooltip = false;
    }

    public TextDisplayButtonWidget setTooltip(String text) {
        this.tooltip = Component.text(text);
        return this;
    }

    public TextDisplayButtonWidget setTooltipDelay(int ticks) {
        this.tooltipDelay = ticks;
        return this;
    }

    public TextDisplayButtonWidget setTooltipColor(TextColor color) {
        this.tooltipColor = color;
        return this;
    }
} 