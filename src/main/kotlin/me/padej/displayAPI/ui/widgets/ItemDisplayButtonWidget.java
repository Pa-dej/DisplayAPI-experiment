package me.padej.displayAPI.ui.widgets;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.utils.PointDetection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.time.Duration;

public class ItemDisplayButtonWidget implements Widget {
    private ItemDisplay display;
    private Player viewer;
    private boolean isHovered = false;
    private Runnable onClick;
    private Component tooltip;
    private boolean isShowingTooltip = false;
    private int tooltipDelay = 0;
    private int hoverTicks = 0;
    private Material itemType;
    private Location location;
    private TextColor tooltipColor = TextColor.fromHexString("#fcd720"); // Цвет по умолчанию
    private float scaleX = .15f;
    private float scaleY = .15f;
    private float scaleZ = 1e-6f;
    private double tolerance = 0.06;
    private WidgetPosition position;
    private ItemDisplay.ItemDisplayTransform displayTransform = ItemDisplay.ItemDisplayTransform.NONE;
    
    private ItemDisplayButtonWidget() {} // Приватный конструктор
    
    public static ItemDisplayButtonWidget create(Location location, Player viewer, Material item, Runnable onClick) {
        ItemDisplayButtonWidget widget = new ItemDisplayButtonWidget();
        widget.location = location;
        widget.viewer = viewer;
        widget.onClick = onClick;
        widget.itemType = item;
        widget.spawn();
        return widget;
    }
    
    private void spawn() {
        this.display = location.getWorld().spawn(location, ItemDisplay.class);
        
        display.setItemStack(new ItemStack(itemType));
        display.setBrightness(new Display.Brightness(15, 15));
        display.setTransformationMatrix(new Matrix4f().translate(0, 0, -0.001f).scale(scaleX, scaleY, scaleZ));
        display.setItemDisplayTransform(displayTransform);
        
        // Настройка длительности анимаций
        display.setInterpolationDuration(1);
        display.setTeleportDuration(1);
        
        // Делаем виджет видимым только для создателя
        display.setVisibleByDefault(false);
        viewer.showEntity(DisplayAPI.getInstance(), display);
    }
    
    public void update() {
        Vector eye = viewer.getEyeLocation().toVector();
        Vector direction = viewer.getEyeLocation().getDirection();
        Vector point = display.getLocation().toVector();
        
        boolean isLookingAt = PointDetection.lookingAtPoint(eye, direction, point, tolerance);
        
        if (isLookingAt != isHovered) {
            isHovered = isLookingAt;
            display.setGlowing(isHovered);
            
            if (!isHovered) {
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
    
    public ItemDisplayButtonWidget setTooltip(String text) {
        this.tooltip = Component.text(text);
        return this;
    }
    
    public ItemDisplayButtonWidget setTooltipDelay(int ticks) {
        this.tooltipDelay = ticks;
        return this;
    }
    
    public ItemDisplayButtonWidget setTooltipColor(TextColor color) {
        this.tooltipColor = color;
        return this;
    }
    
    public void handleClick() {
        if (isHovered) {
            // Проигрываем звук клика
            viewer.playSound(viewer.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            onClick.run();
        }
    }
    
    public void remove() {
        if (isShowingTooltip) {
            hideTooltip();
        }
        display.remove();
    }
    
    public ItemDisplay getDisplay() {
        return display;
    }
    
    public boolean isHovered() {
        return isHovered;
    }
    
    public ItemDisplayButtonWidget setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
        display.setTransformationMatrix(new Matrix4f().scale(scaleX, scaleY, scaleZ));
        return this;
    }
    
    public ItemDisplayButtonWidget setTolerance(double tolerance) {
        this.tolerance = tolerance;
        return this;
    }
    
    public WidgetPosition getPosition() {
        return position;
    }
    
    public void setPosition(WidgetPosition position) {
        this.position = position;
    }
    
    public ItemDisplayButtonWidget setDisplayTransform(ItemDisplay.ItemDisplayTransform transform) {
        this.displayTransform = transform;
        if (display != null) {
            display.setItemDisplayTransform(transform);
        }
        return this;
    }
}
