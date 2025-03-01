package me.padej.displayAPI.ui;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.api.events.DisplayClickEvent;
import me.padej.displayAPI.ui.widgets.ItemDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.Widget;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UIManager implements Listener {
    private static UIManager instance;
    private final Map<Player, WidgetManager> activeScreens = new ConcurrentHashMap<>();
    private BukkitTask updateTask;
    private boolean isUpdateTaskRunning = false;

    private UIManager() {
        // Регистрируем слушатель событий
        Bukkit.getPluginManager().registerEvents(this, DisplayAPI.getInstance());
    }

    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }

    public WidgetManager getActiveScreen(Player player) {
        return activeScreens.get(player);
    }

    public void registerScreen(Player player, WidgetManager manager) {
        activeScreens.put(player, manager);
        startUpdateTaskIfNeeded();
    }

    public void unregisterScreen(Player player) {
        activeScreens.remove(player);
        if (activeScreens.isEmpty()) {
            stopUpdateTask();
        }
    }

    private Widget getNearestHoveredWidget(WidgetManager manager) {
        Widget nearestWidget = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Widget widget : manager.children) {
            if (widget.isHovered()) {
                // Получаем позицию виджета
                Location widgetLoc = null;
                if (widget instanceof ItemDisplayButtonWidget) {
                    widgetLoc = ((ItemDisplayButtonWidget) widget).getDisplay().getLocation();
                } else if (widget instanceof TextDisplayButtonWidget) {
                    widgetLoc = ((TextDisplayButtonWidget) widget).getDisplay().getLocation();
                }

                if (widgetLoc != null) {
                    // Вычисляем расстояние от глаз игрока до виджета
                    double distance = manager.viewer.getEyeLocation().distance(widgetLoc);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestWidget = widget;
                    }
                }
            }
        }
        return nearestWidget;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        WidgetManager manager = activeScreens.get(player);

        if (manager != null) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Widget nearestWidget = getNearestHoveredWidget(manager);
                if (nearestWidget != null) {
                    event.setCancelled(true);
                    // Создаем и вызываем событие клика только для ближайшего виджета
                    DisplayClickEvent clickEvent = new DisplayClickEvent(player, nearestWidget);
                    Bukkit.getPluginManager().callEvent(clickEvent);
                    if (!clickEvent.isCancelled()) {
                        nearestWidget.handleClick();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityAttack(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        WidgetManager manager = activeScreens.get(player);

        if (manager != null) {
            Widget nearestWidget = getNearestHoveredWidget(manager);
            if (nearestWidget != null) {
                event.setCancelled(true);
                // Создаем и вызываем событие клика только для ближайшего виджета
                DisplayClickEvent clickEvent = new DisplayClickEvent(player, nearestWidget);
                Bukkit.getPluginManager().callEvent(clickEvent);
                if (!clickEvent.isCancelled()) {
                    nearestWidget.handleClick();
                }
            }
        }
    }

    private void startUpdateTaskIfNeeded() {
        if (!isUpdateTaskRunning) {
            updateTask = Bukkit.getScheduler().runTaskTimer(DisplayAPI.getInstance(), () -> {
                for (Map.Entry<Player, WidgetManager> entry : activeScreens.entrySet()) {
                    WidgetManager manager = entry.getValue();
                    if (manager != null) {
                        manager.update();
                    }
                }
            }, 0L, 1L);
            isUpdateTaskRunning = true;
        }
    }

    private void stopUpdateTask() {
        if (isUpdateTaskRunning && updateTask != null) {
            updateTask.cancel();
            isUpdateTaskRunning = false;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        WidgetManager manager = activeScreens.get(player);
        if (manager != null) {
            manager.remove(); // Мгновенное удаление при выходе
            unregisterScreen(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        WidgetManager manager = activeScreens.get(player);
        if (manager != null) {
            manager.remove(); // Мгновенное удаление при смерти
            unregisterScreen(player);
        }
    }

    public void cleanup() {
        // Удаляем все активные экраны
        for (Map.Entry<Player, WidgetManager> entry : new HashMap<>(activeScreens).entrySet()) {
            Player player = entry.getKey();
            WidgetManager manager = entry.getValue();
            if (manager != null) {
                manager.remove(); // Мгновенное удаление при выключении
            }
        }
        
        // Останавливаем задачу обновления и очищаем список
        stopUpdateTask();
        activeScreens.clear();
    }

    // Добавим метод для проверки наличия активных экранов
    public boolean hasActiveScreens() {
        return !activeScreens.isEmpty();
    }
} 