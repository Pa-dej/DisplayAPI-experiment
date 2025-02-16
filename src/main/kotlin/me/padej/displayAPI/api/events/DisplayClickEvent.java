package me.padej.displayAPI.api.events;

import me.padej.displayAPI.ui.widgets.Widget;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DisplayClickEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Widget widget;
    private boolean cancelled;

    public DisplayClickEvent(Player player, Widget widget) {
        this.player = player;
        this.widget = widget;
    }

    public Player getPlayer() {
        return player;
    }

    public Widget getWidget() {
        return widget;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
} 