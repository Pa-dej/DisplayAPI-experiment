package me.padej.displayAPI.ui;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.utils.PointDetection;
import org.bukkit.Bukkit;
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
    private final Map<Player, Screen> activeScreens = new ConcurrentHashMap<>();
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

    public Screen getActiveScreen(Player player) {
        return activeScreens.get(player);
    }

    public void registerScreen(Player player, Screen screen) {
        activeScreens.put(player, screen);
        startUpdateTaskIfNeeded();
    }

    public void unregisterScreen(Player player) {
        activeScreens.remove(player);
        if (activeScreens.isEmpty()) {
            stopUpdateTask();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Screen screen = activeScreens.get(player);

        if (screen != null) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (screen.isLookingAtWidget()) {
                    event.setCancelled(true);
                    screen.handleClick();
                }
            }
        }
    }

    private void startUpdateTaskIfNeeded() {
        if (!isUpdateTaskRunning) {
            updateTask = Bukkit.getScheduler().runTaskTimer(DisplayAPI.getInstance(), () -> {
                for (Map.Entry<Player, Screen> entry : activeScreens.entrySet()) {
                    Screen screen = entry.getValue();
                    if (screen != null) {
                        screen.update();
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
        Screen screen = activeScreens.get(player);
        if (screen != null) {
            screen.remove(); // Мгновенное удаление при выходе
            unregisterScreen(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Screen screen = activeScreens.get(player);
        if (screen != null) {
            screen.remove(); // Мгновенное удаление при смерти
            unregisterScreen(player);
        }
    }

    public void cleanup() {
        // Удаляем все активные экраны
        for (Map.Entry<Player, Screen> entry : new HashMap<>(activeScreens).entrySet()) {
            Player player = entry.getKey();
            Screen screen = entry.getValue();
            if (screen != null) {
                screen.remove(); // Мгновенное удаление при выключении
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