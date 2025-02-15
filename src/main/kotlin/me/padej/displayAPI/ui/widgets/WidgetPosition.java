package me.padej.displayAPI.ui.widgets;

public class WidgetPosition {
    private double horizontal;  // rightMultiplier -> horizontal
    private double vertical;    // upMultiplier -> vertical

    public WidgetPosition(double horizontal, double vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public WidgetPosition add(double horizontal, double vertical) {
        this.horizontal += horizontal;
        this.vertical += vertical;
        return this;
    }

    public WidgetPosition addHorizontal(double value) {
        this.horizontal += value;
        return this;
    }

    public WidgetPosition addVertical(double value) {
        this.vertical += value;
        return this;
    }

    public WidgetPosition clone() {
        return new WidgetPosition(horizontal, vertical);
    }

    public double getHorizontal() {
        return horizontal;
    }

    public double getVertical() {
        return vertical;
    }

    public WidgetPosition setHorizontal(double horizontal) {
        this.horizontal = horizontal;
        return this;
    }

    public WidgetPosition setVertical(double vertical) {
        this.vertical = vertical;
        return this;
    }

    // Для обратной совместимости
    public double getRightMultiplier() {
        return horizontal;
    }

    public double getUpMultiplier() {
        return vertical;
    }
} 