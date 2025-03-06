package me.padej.displayAPI.ui.widgets;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.PointDetection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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
    private TextColor tooltipColor = TextColor.fromHexString("#868788"); // Цвет по умолчанию
    private float scaleX = .15f;
    private float scaleY = .15f;
    private float scaleZ = 1e-6f;
    private double horizontalTolerance = 0.06;
    private double verticalTolerance = 0.06;
    private WidgetPosition position;
    private ItemDisplay.ItemDisplayTransform displayTransform = ItemDisplay.ItemDisplayTransform.NONE;
    private org.bukkit.Sound clickSound = org.bukkit.Sound.UI_BUTTON_CLICK;
    private boolean soundEnabled = true;
    private float soundVolume = 0.5f;
    private float soundPitch = 1.0f;
    private Runnable updateCallback;
    private org.bukkit.inventory.meta.ItemMeta itemMeta;
    private Vector3f translation;
    private Transformation hoveredTransformation;
    private int hoveredTransformationDuration;
    private boolean wasHovered = false;
    private boolean glowOnHover = true;
    private ItemDisplayButtonConfig config;
    
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
    
    public static ItemDisplayButtonWidget create(Location location, Player viewer, ItemDisplayButtonConfig config) {
        ItemDisplayButtonWidget widget = new ItemDisplayButtonWidget();
        widget.location = location;
        widget.viewer = viewer;
        widget.config = config;
        widget.onClick = config.getOnClick();
        widget.itemType = config.getMaterial();
        widget.scaleX = config.getScaleX();
        widget.scaleY = config.getScaleY();
        widget.scaleZ = config.getScaleZ();
        widget.displayTransform = config.getDisplayTransform();
        widget.horizontalTolerance = config.getToleranceHorizontal();
        widget.verticalTolerance = config.getToleranceVertical();
        
        widget.clickSound = config.getClickSound();
        widget.soundEnabled = config.isSoundEnabled();
        widget.soundVolume = config.getSoundVolume();
        widget.soundPitch = config.getSoundPitch();
        
        widget.translation = config.getTranslation();
        
        widget.hoveredTransformation = config.getHoveredTransformation();
        widget.hoveredTransformationDuration = config.getHoveredTransformationDuration();
        
        if (config.getItemMeta() != null) {
            widget.setItemMeta(config.getItemMeta());
        }
        
        widget.glowOnHover = config.isGlowOnHover();
        
        widget.spawn();
        
        if (config.getGlowColor() != null) {
            widget.setGlowColor(config.getGlowColor());
        }
        
        if (config.hasTooltip()) {
            widget.setTooltip(config.getTooltip())
                 .setTooltipColor(config.getTooltipColor())
                 .setTooltipDelay(config.getTooltipDelay());
        }
        
        widget.setPosition(config.getPosition());
        
        return widget;
    }
    
    private void spawn() {
        this.display = location.getWorld().spawn(location, ItemDisplay.class);
        
        ItemStack itemStack = new ItemStack(itemType);
        if (itemMeta != null) {
            itemStack.setItemMeta(itemMeta);
        }
        display.setItemStack(itemStack);
        display.setBrightness(new Display.Brightness(15, 15));
        display.setItemDisplayTransform(displayTransform);
        
        // Начальная трансформация с нулевым масштабом
        display.setTransformationMatrix(new Matrix4f().translate(0, 0, 0.017f).scale(0, 0, 0));
        
        // Настройка длительности анимаций
        display.setInterpolationDuration(1);
        display.setTeleportDuration(1);
        
        // Применяем настройку приватной видимости
        if (config != null && config.isPrivateVisible()) {
            display.setVisibleByDefault(false);
            viewer.showEntity(DisplayAPI.getInstance(), display);
        }

        // Если translation не установлен, используем значение по умолчанию
        if (translation == null) {
            translation = new Vector3f(0, -scaleY / 8, 0);
        }

        // Используем translation в трансформации
        Animation.applyTransformationWithInterpolation(
            display,
            new Transformation(
                translation,
                new AxisAngle4f(),
                new Vector3f(scaleX, scaleY, scaleZ),
                new AxisAngle4f()
            ),
            5
        );
    }
    
    @Override
    public void update() {
        // Используем getEyeLocation() для определения точки пересечения взгляда
        Vector eye = viewer.getEyeLocation().toVector();
        Vector direction = viewer.getEyeLocation().getDirection();
        Vector point = display.getLocation().toVector();
        
        boolean isLookingAt = PointDetection.lookingAtPoint(eye, direction, point, horizontalTolerance, verticalTolerance);
        
        if (isLookingAt != wasHovered) {
            wasHovered = isLookingAt;
            isHovered = isLookingAt;
            if (glowOnHover) {
                display.setGlowing(isHovered);
            }
            
            // Применяем hoveredTransformation только при изменении состояния
            if (isHovered && hoveredTransformation != null) {
                Animation.applyTransformationWithInterpolation(
                    display,
                    hoveredTransformation,
                    hoveredTransformationDuration
                );
            } else {
                // Возвращаем обычную трансформацию
                Animation.applyTransformationWithInterpolation(
                    display,
                    new Transformation(
                        translation != null ? translation : new Vector3f(0, -scaleY / 8, 0),
                        new AxisAngle4f(),
                        new Vector3f(scaleX, scaleY, scaleZ),
                        new AxisAngle4f()
                    ),
                    hoveredTransformationDuration
                );
            }
            
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

        // Вызываем callback обновления, если он установлен
        if (updateCallback != null) {
            updateCallback.run();
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
            if (soundEnabled) {
                viewer.playSound(viewer.getLocation(), clickSound, soundVolume, soundPitch);
            }
            onClick.run();
        }
    }
    
    @Override
    public void remove() {
        if (display != null) {
            if (isShowingTooltip) {
                hideTooltip();
            }
            display.remove();
        }
    }
    
    @Override
    public void removeWithAnimation(int ticks) {
        if (display != null) {
            // Анимация исчезновения
            Animation.applyTransformationWithInterpolation(
                display,
                new Transformation(
                    display.getTransformation().getTranslation(),
                    display.getTransformation().getLeftRotation(),
                    new Vector3f(0, 0, 0), // Нулевой масштаб
                    display.getTransformation().getRightRotation()
                ),
                ticks
            );
            
            // Удаляем после анимации
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
                if (isShowingTooltip) {
                    hideTooltip();
                }
                display.remove();
            }, ticks);
        }
    }
    
    public ItemDisplay getDisplay() {
        return display;
    }
    
    public boolean isHovered() {
        return isHovered;
    }
    
    @Override
    public ItemDisplayButtonWidget setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
        
        if (display != null) {
            Animation.applyTransformationWithInterpolation(
                display,
                new Transformation(
                    new Vector3f(0, 0, -0.001f),
                    new AxisAngle4f(),
                    new Vector3f(x, y, z),
                    new AxisAngle4f()
                ),
                5
            );
        }
        return this;
    }
    
    @Override
    public ItemDisplayButtonWidget setTolerance(double tolerance) {
        this.horizontalTolerance = tolerance;
        this.verticalTolerance = tolerance;
        return this;
    }

    @Override
    public ItemDisplayButtonWidget setTolerance(double horizontalTolerance, double verticalTolerance) {
        this.horizontalTolerance = horizontalTolerance;
        this.verticalTolerance = verticalTolerance;
        return this;
    }

    @Override
    public ItemDisplayButtonWidget setToleranceHorizontal(double tolerance) {
        this.horizontalTolerance = tolerance;
        return this;
    }

    @Override
    public ItemDisplayButtonWidget setToleranceVertical(double tolerance) {
        this.verticalTolerance = tolerance;
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
    
    public ItemDisplayButtonWidget setGlowColor(org.bukkit.Color color) {
        if (display != null) {
            display.setGlowColorOverride(color);
        }
        return this;
    }
    
    public ItemDisplayButtonWidget setClickSound(org.bukkit.Sound sound) {
        this.clickSound = sound;
        return this;
    }
    
    public ItemDisplayButtonWidget setClickSound(org.bukkit.Sound sound, float volume, float pitch) {
        this.clickSound = sound;
        this.soundVolume = volume;
        this.soundPitch = pitch;
        return this;
    }
    
    public ItemDisplayButtonWidget disableClickSound() {
        this.soundEnabled = false;
        return this;
    }
    
    public ItemDisplayButtonWidget setItemMeta(org.bukkit.inventory.meta.ItemMeta meta) {
        this.itemMeta = meta;
        if (display != null) {
            ItemStack itemStack = display.getItemStack();
            itemStack.setItemMeta(meta);
            display.setItemStack(itemStack);
        }
        return this;
    }

    public ItemDisplayButtonWidget setMaterial(Material material) {
        this.itemType = material;
        if (display != null) {
            ItemStack itemStack = new ItemStack(material);
            if (itemMeta != null) {
                itemStack.setItemMeta(itemMeta);
            }
            display.setItemStack(itemStack);
        }
        return this;
    }

    public void setUpdateCallback(Runnable callback) {
        this.updateCallback = callback;
    }

    public ItemDisplayButtonWidget setTranslation(Vector3f translation) {
        this.translation = translation;
        if (display != null) {
            Animation.applyTransformationWithInterpolation(
                display,
                new Transformation(
                    translation,
                    display.getTransformation().getLeftRotation(),
                    new Vector3f(scaleX, scaleY, scaleZ),
                    display.getTransformation().getRightRotation()
                ),
                5
            );
        }
        return this;
    }

    public ItemDisplayButtonWidget setHoveredTransformation(Transformation transformation, int duration) {
        this.hoveredTransformation = transformation;
        this.hoveredTransformationDuration = duration;
        return this;
    }

    public ItemDisplayButtonWidget disableHoverGlow() {
        this.glowOnHover = false;
        if (display != null) {
            display.setGlowing(false);
        }
        return this;
    }

    public void forceHoverState(boolean hovered) {
        if (hovered != wasHovered) {
            wasHovered = hovered;
            isHovered = hovered;
            if (glowOnHover) {
                display.setGlowing(isHovered);
            }
            
            if (isHovered && hoveredTransformation != null) {
                Animation.applyTransformationWithInterpolation(
                    display,
                    hoveredTransformation,
                    hoveredTransformationDuration
                );
            } else {
                Animation.applyTransformationWithInterpolation(
                    display,
                    new Transformation(
                        translation != null ? translation : new Vector3f(0, -scaleY / 8, 0),
                        new AxisAngle4f(),
                        new Vector3f(scaleX, scaleY, scaleZ),
                        new AxisAngle4f()
                    ),
                    hoveredTransformationDuration
                );
            }
        }
    }

    @Override
    public boolean isValid() {
        return display != null && display.isValid() && !display.isDead();
    }
}
