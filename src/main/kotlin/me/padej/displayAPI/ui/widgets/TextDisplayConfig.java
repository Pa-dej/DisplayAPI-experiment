package me.padej.displayAPI.ui.widgets;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

public class TextDisplayConfig {
    private Component text;
    private Component hoveredText;
    private Color backgroundColor;
    private int backgroundAlpha;
    private Color hoveredBackgroundColor;
    private int hoveredBackgroundAlpha;
    private Runnable onClick;
    private WidgetPosition position;
    private float scaleX = .15f;
    private float scaleY = .15f;
    private float scaleZ = .15f;
    private double tolerance = 0.06;
    private Component tooltip;
    private int tooltipDelay = 0;
    private TextColor tooltipColor = TextColor.fromHexString("#fcd720");

    public TextDisplayConfig(Component text, Component hoveredText, Runnable onClick) {
        this.text = text;
        this.hoveredText = hoveredText;
        this.onClick = onClick;
        this.backgroundColor = Color.BLACK;
        this.backgroundAlpha = 100;
        this.hoveredBackgroundColor = Color.GRAY;
        this.hoveredBackgroundAlpha = 150;
    }

    public TextDisplayConfig setBackgroundColor(Color color, int alpha) {
        this.backgroundColor = color;
        this.backgroundAlpha = alpha;
        return this;
    }

    public TextDisplayConfig setHoveredBackgroundColor(Color color, int alpha) {
        this.hoveredBackgroundColor = color;
        this.hoveredBackgroundAlpha = alpha;
        return this;
    }

    public TextDisplayConfig setPosition(WidgetPosition position) {
        this.position = position;
        return this;
    }

    public WidgetPosition getPosition() {
        return position;
    }

    public Component getText() {
        return text;
    }

    public Component getHoveredText() {
        return hoveredText;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public Color getHoveredBackgroundColor() {
        return hoveredBackgroundColor;
    }

    public int getHoveredBackgroundAlpha() {
        return hoveredBackgroundAlpha;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public TextDisplayConfig setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
        return this;
    }

    public TextDisplayConfig setTolerance(double tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    public float getScaleX() { return scaleX; }
    public float getScaleY() { return scaleY; }
    public float getScaleZ() { return scaleZ; }
    public double getTolerance() { return tolerance; }

    public TextDisplayConfig setTooltip(String text) {
        this.tooltip = Component.text(text);
        return this;
    }

    public TextDisplayConfig setTooltipDelay(int ticks) {
        this.tooltipDelay = ticks;
        return this;
    }

    public TextDisplayConfig setTooltipColor(TextColor color) {
        this.tooltipColor = color;
        return this;
    }

    public Component getTooltip() {
        return tooltip;
    }

    public int getTooltipDelay() {
        return tooltipDelay;
    }

    public TextColor getTooltipColor() {
        return tooltipColor;
    }
} 