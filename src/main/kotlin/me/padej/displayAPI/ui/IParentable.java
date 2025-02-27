package me.padej.displayAPI.ui;

public interface IParentable {
    Class<? extends WidgetManager> getParentManager();
} 