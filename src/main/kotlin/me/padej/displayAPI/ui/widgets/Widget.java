package me.padej.displayAPI.ui.widgets;

public interface Widget {
    void update();
    void handleClick();
    void remove();
    void removeWithAnimation(int ticks);
    boolean isHovered();
    Widget setScale(float x, float y, float z);
    Widget setTolerance(double tolerance);
} 