package me.padej.displayAPI.ui.widgets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

public class TextDisplayButtonConfig {
    private Component text;
    private Component hoveredText;
    private Runnable onClick;
    private Component tooltip;
    private TextColor tooltipColor;
    private int tooltipDelay;
    private WidgetPosition position;
    private Color backgroundColor = Color.fromRGB(40, 40, 40);
    private int backgroundAlpha = 150;
    private Color hoveredBackgroundColor = Color.fromRGB(60, 60, 60);
    private int hoveredBackgroundAlpha = 180;
    private float scaleX = .15f;
    private float scaleY = .15f;
    private float scaleZ = .15f;
    private double tolerance = 0.06;

    public TextDisplayButtonConfig(Component text, Component hoveredText, Runnable onClick) {
        this.text = text;
        this.hoveredText = hoveredText;
        this.onClick = onClick;
        this.tooltipColor = TextColor.fromHexString("#868788");
    }

    public TextDisplayButtonConfig setTooltip(String tooltip) {
        this.tooltip = Component.text(tooltip);
        return this;
    }

    public TextDisplayButtonConfig setTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public TextDisplayButtonConfig setTooltipColor(TextColor color) {
        this.tooltipColor = color;
        return this;
    }

    public TextDisplayButtonConfig setTooltipDelay(int ticks) {
        this.tooltipDelay = ticks;
        return this;
    }

    public TextDisplayButtonConfig setPosition(WidgetPosition position) {
        this.position = position;
        return this;
    }

    public TextDisplayButtonConfig setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public TextDisplayButtonConfig setBackgroundAlpha(int alpha) {
        this.backgroundAlpha = alpha;
        return this;
    }

    public TextDisplayButtonConfig setHoveredBackgroundColor(Color color) {
        this.hoveredBackgroundColor = color;
        return this;
    }

    public TextDisplayButtonConfig setHoveredBackgroundAlpha(int alpha) {
        this.hoveredBackgroundAlpha = alpha;
        return this;
    }

    public TextDisplayButtonConfig setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
        return this;
    }

    public TextDisplayButtonConfig setTolerance(double tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    // Геттеры
    public Component getText() { return text; }
    public Component getHoveredText() { return hoveredText; }
    public Runnable getOnClick() { return onClick; }
    public Component getTooltip() { return tooltip; }
    public TextColor getTooltipColor() { return tooltipColor; }
    public int getTooltipDelay() { return tooltipDelay; }
    public WidgetPosition getPosition() { return position; }
    public Color getBackgroundColor() { return backgroundColor; }
    public int getBackgroundAlpha() { return backgroundAlpha; }
    public Color getHoveredBackgroundColor() { return hoveredBackgroundColor; }
    public int getHoveredBackgroundAlpha() { return hoveredBackgroundAlpha; }
    public float getScaleX() { return scaleX; }
    public float getScaleY() { return scaleY; }
    public float getScaleZ() { return scaleZ; }
    public double getTolerance() { return tolerance; }
} 