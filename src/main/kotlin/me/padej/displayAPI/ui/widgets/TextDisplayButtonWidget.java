package me.padej.displayAPI.ui.widgets;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.PointDetection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
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

import java.time.Duration;

public class TextDisplayButtonWidget implements Widget {
    private TextDisplay display;
    private Player viewer;
    private boolean isHovered = false;
    private boolean wasHovered = false;
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
    private double horizontalTolerance = 0.06;
    private double verticalTolerance = 0.06;

    private WidgetPosition position;

    private org.bukkit.Sound clickSound = org.bukkit.Sound.BLOCK_DISPENSER_FAIL;
    private boolean soundEnabled = true;
    private float soundVolume = 0.5f;
    private float soundPitch = 2.0f;

    private Runnable updateCallback;

    private boolean textShadowEnabled = false;
    private TextDisplay.TextAlignment textAlignment = TextDisplay.TextAlignment.CENTER;
    private int maxLineWidth = 200;

    private Vector3f translation;

    private Transformation hoveredTransformation;
    private int hoveredTransformationDuration;

    private TextDisplayButtonWidget() {
    }

    public static TextDisplayButtonWidget create(Location location, Player viewer, TextDisplayButtonConfig config) {
        TextDisplayButtonWidget widget = new TextDisplayButtonWidget();
        widget.location = location;
        widget.viewer = viewer;
        widget.onClick = config.getOnClick();
        widget.text = config.getText();
        widget.hoveredText = config.getHoveredText();
        widget.scaleX = config.getScaleX();
        widget.scaleY = config.getScaleY();
        widget.scaleZ = config.getScaleZ();
        widget.horizontalTolerance = config.getToleranceHorizontal();
        widget.verticalTolerance = config.getToleranceVertical();
        widget.backgroundColor = config.getBackgroundColor();
        widget.backgroundAlpha = config.getBackgroundAlpha();
        widget.hoveredBackgroundColor = config.getHoveredBackgroundColor();
        widget.hoveredBackgroundAlpha = config.getHoveredBackgroundAlpha();
        
        widget.clickSound = config.getClickSound();
        widget.soundEnabled = config.isSoundEnabled();
        widget.soundVolume = config.getSoundVolume();
        widget.soundPitch = config.getSoundPitch();
        
        widget.textShadowEnabled = config.isTextShadowEnabled();
        widget.textAlignment = config.getTextAlignment();
        widget.maxLineWidth = config.getMaxLineWidth();
        
        widget.translation = config.getTranslation();
        
        widget.hoveredTransformation = config.getHoveredTransformation();
        widget.hoveredTransformationDuration = config.getHoveredTransformationDuration();
        
        widget.spawn();
        
        if (config.getTooltip() != null) {
            widget.setTooltip(config.getTooltip())
                 .setTooltipColor(config.getTooltipColor())
                 .setTooltipDelay(config.getTooltipDelay());
        }
        
        widget.setPosition(config.getPosition());
        
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

        // Если translation не установлен, используем значение по умолчанию
        if (translation == null) {
            translation = new Vector3f(0, -scaleY / 8, 0);
        }

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

        display.setShadowed(textShadowEnabled);
        display.setAlignment(textAlignment);
        display.setLineWidth(maxLineWidth);
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
            display.setGlowing(isHovered);

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

            // Обновляем текст и цвет фона при изменении состояния наведения
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

        // Вызываем callback обновления, если он установлен
        if (updateCallback != null) {
            updateCallback.run();
        }
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

    @Override
    public TextDisplayButtonWidget setTolerance(double tolerance) {
        this.horizontalTolerance = tolerance;
        this.verticalTolerance = tolerance;
        return this;
    }

    @Override
    public TextDisplayButtonWidget setTolerance(double horizontalTolerance, double verticalTolerance) {
        this.horizontalTolerance = horizontalTolerance;
        this.verticalTolerance = verticalTolerance;
        return this;
    }

    @Override
    public TextDisplayButtonWidget setToleranceHorizontal(double tolerance) {
        this.horizontalTolerance = tolerance;
        return this;
    }

    @Override
    public TextDisplayButtonWidget setToleranceVertical(double tolerance) {
        this.verticalTolerance = tolerance;
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

    public TextDisplayButtonWidget setTooltip(Component tooltip) {
        this.tooltip = tooltip;
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

    public TextDisplayButtonWidget setClickSound(org.bukkit.Sound sound) {
        this.clickSound = sound;
        return this;
    }

    public TextDisplayButtonWidget setClickSound(org.bukkit.Sound sound, float volume, float pitch) {
        this.clickSound = sound;
        this.soundVolume = volume;
        this.soundPitch = pitch;
        return this;
    }

    public TextDisplayButtonWidget disableClickSound() {
        this.soundEnabled = false;
        return this;
    }

    public TextDisplayButtonWidget setText(Component text) {
        this.text = text;
        if (display != null && !isHovered) {
            display.text(text);
        }
        return this;
    }

    public TextDisplayButtonWidget setText(Component text, Component hoveredText) {
        this.text = text;
        this.hoveredText = hoveredText;
        if (display != null) {
            display.text(isHovered ? hoveredText : text);
        }
        return this;
    }

    public void setUpdateCallback(Runnable callback) {
        this.updateCallback = callback;
    }

    public TextDisplayButtonWidget enableTextShadow() {
        this.textShadowEnabled = true;
        if (display != null) {
            display.setShadowed(true);
        }
        return this;
    }

    public TextDisplayButtonWidget setTextAlignment(TextDisplay.TextAlignment alignment) {
        this.textAlignment = alignment;
        if (display != null) {
            display.setAlignment(alignment);
        }
        return this;
    }

    public TextDisplayButtonWidget setMaxLineWidth(int width) {
        this.maxLineWidth = width;
        if (display != null) {
            display.setLineWidth(width);
        }
        return this;
    }

    public TextDisplayButtonWidget setTranslation(Vector3f translation) {
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

    public TextDisplayButtonWidget setHoveredTransformation(Transformation transformation, int duration) {
        this.hoveredTransformation = transformation;
        this.hoveredTransformationDuration = duration;
        return this;
    }
} 