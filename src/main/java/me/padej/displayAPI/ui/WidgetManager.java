package me.padej.displayAPI.ui;

import me.padej.displayAPI.api.events.DisplayClickEvent;
import me.padej.displayAPI.ui.widgets.ItemDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
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
        // Находим ближайший виджет, на который смотрит игрок
        Widget nearestWidget = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Widget widget : new ArrayList<>(children)) {
            // Временно обновляем состояние наведения для определения потенциальных кандидатов
            widget.update();
            
            if (widget.isHovered()) {
                Location widgetLoc = null;
                if (widget instanceof ItemDisplayButtonWidget) {
                    widgetLoc = ((ItemDisplayButtonWidget) widget).getDisplay().getLocation();
                } else if (widget instanceof TextDisplayButtonWidget) {
                    widgetLoc = ((TextDisplayButtonWidget) widget).getDisplay().getLocation();
                }

                if (widgetLoc != null) {
                    double distance = viewer.getEyeLocation().distance(widgetLoc);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestWidget = widget;
                    }
                }
            }
        }

        // Сбрасываем состояние наведения для всех виджетов и устанавливаем true только для ближайшего
        for (Widget widget : children) {
            if (widget instanceof ItemDisplayButtonWidget) {
                ((ItemDisplayButtonWidget) widget).forceHoverState(widget == nearestWidget);
            } else if (widget instanceof TextDisplayButtonWidget) {
                ((TextDisplayButtonWidget) widget).forceHoverState(widget == nearestWidget);
            }
        }
    }
    
    public void handleClick() {
        Widget nearestWidget = getNearestHoveredWidget();
        if (nearestWidget != null) {
            nearestWidget.handleClick();
        }
    }
    
    public void remove() {
        new ArrayList<>(children).forEach(Widget::remove);
        children.clear();
    }

    public boolean isLookingAtWidget() {
        return getNearestHoveredWidget() != null;
    }

    private Widget getNearestHoveredWidget() {
        Widget nearestWidget = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Widget widget : children) {
            if (widget.isHovered()) {
                Location widgetLoc = null;
                if (widget instanceof ItemDisplayButtonWidget) {
                    widgetLoc = ((ItemDisplayButtonWidget) widget).getDisplay().getLocation();
                } else if (widget instanceof TextDisplayButtonWidget) {
                    widgetLoc = ((TextDisplayButtonWidget) widget).getDisplay().getLocation();
                }

                if (widgetLoc != null) {
                    double distance = viewer.getEyeLocation().distance(widgetLoc);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestWidget = widget;
                    }
                }
            }
        }
        return nearestWidget;
    }
} 