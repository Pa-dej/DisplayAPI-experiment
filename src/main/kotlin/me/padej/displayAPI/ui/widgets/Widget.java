package me.padej.displayAPI.ui.widgets;

public interface Widget {
    void update();
    void handleClick();
    void remove();
    void removeWithAnimation(int ticks);
    boolean isHovered();
    void forceHoverState(boolean hovered);
    Widget setScale(float x, float y, float z);
    Widget setTolerance(double tolerance);
    Widget setTolerance(double horizontalTolerance, double verticalTolerance);
    Widget setToleranceHorizontal(double tolerance);
    Widget setToleranceVertical(double tolerance);
} 