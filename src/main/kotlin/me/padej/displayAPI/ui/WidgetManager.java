package me.padej.displayAPI.ui;

import me.padej.displayAPI.api.events.DisplayClickEvent;
import me.padej.displayAPI.ui.widgets.Widget;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WidgetManager {
    protected final List<Widget> children = new ArrayList<>();
    public Player viewer;
    public Location location;
    
    public WidgetManager(Player viewer, Location location) {
        this.viewer = viewer;
        this.location = location;
    }
    
    public <T extends Widget> T addDrawableChild(T child) {
        children.add(child);
        return child;
    }
    
    public void update() {
        new ArrayList<>(children).forEach(Widget::update);
    }
    
    public void handleClick() {
        new ArrayList<>(children).forEach(child -> {
            if (child.isHovered()) {
                // Создаем и вызываем событие клика
                DisplayClickEvent event = new DisplayClickEvent(viewer, child);
                Bukkit.getPluginManager().callEvent(event);
                
                // Если событие не отменено, обрабатываем клик
                if (!event.isCancelled()) {
                    child.handleClick();
                }
            }
        });
    }
    
    public void remove() {
        new ArrayList<>(children).forEach(Widget::remove);
        children.clear();
    }

    public boolean isLookingAtWidget() {
        return children.stream().anyMatch(Widget::isHovered);
    }
} 