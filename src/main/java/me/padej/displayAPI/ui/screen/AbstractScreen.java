package me.padej.displayAPI.ui.screen;

import me.padej.displayAPI.core.display.DisplayManager;
import me.padej.displayAPI.ui.WidgetManager;
import me.padej.displayAPI.ui.widgets.Widget;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractScreen extends WidgetManager {
    protected final DisplayManager displayManager;
    protected final Map<String, Widget> widgets = new HashMap<>();
    
    public AbstractScreen(Player viewer, Location location, DisplayManager displayManager) {
        super(viewer, location);
        this.displayManager = displayManager;
    }
    
    public abstract void init();
    public abstract void update();
    public abstract void onClose();
} 