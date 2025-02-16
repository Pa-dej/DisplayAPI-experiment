package me.padej.displayAPI.ui.config;

import org.bukkit.Color;
import org.bukkit.entity.Display;

public interface DisplayConfig {
    float getScale();
    Color getColor();
    int getOpacity();
    Display.Billboard getBillboard();
} 