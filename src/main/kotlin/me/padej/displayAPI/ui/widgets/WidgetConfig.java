package me.padej.displayAPI.ui.widgets;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public class WidgetConfig {
    private Material material;
    private Runnable onClick;
    private String tooltip;
    private TextColor tooltipColor;
    private int tooltipDelay;
    private boolean hasTooltip;
    private WidgetPosition position;

    public WidgetConfig(Material material, Runnable onClick) {
        this.material = material;
        this.onClick = onClick;
        this.hasTooltip = false;
        this.tooltipColor = TextColor.fromHexString("#fcd720"); // Цвет по умолчанию
    }

    public WidgetConfig setTooltip(String tooltip) {
        this.tooltip = tooltip;
        this.hasTooltip = true;
        return this;
    }

    public WidgetConfig setTooltipColor(TextColor color) {
        this.tooltipColor = color;
        return this;
    }

    public WidgetConfig setTooltipDelay(int ticks) {
        this.tooltipDelay = ticks;
        return this;
    }

    public WidgetConfig setPosition(WidgetPosition position) {
        this.position = position;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public String getTooltip() {
        return tooltip;
    }

    public TextColor getTooltipColor() {
        return tooltipColor;
    }

    public int getTooltipDelay() {
        return tooltipDelay;
    }

    public boolean hasTooltip() {
        return hasTooltip;
    }

    public WidgetPosition getPosition() {
        return position;
    }
} 