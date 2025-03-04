package me.Padej_.eventManagerDisplayUI;

import me.Padej_.eventManagerDisplayUI.command.CommandHandler;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.EventManagerMainScreen;
import me.Padej_.eventManagerDisplayUI.ui.screen.mace.MaceMainScreen;
import me.Padej_.eventManagerDisplayUI.ui.screen.mace.MaceSettingsScreen;
import me.padej.displayAPI.ui.UIManager;
import me.padej.displayAPI.ui.WidgetManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class EventListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Удаляем игрока из списка и сбрасываем точку возрождения
        MaceSettingsScreen.spawnPlayers.remove(playerId);
        player.setBedSpawnLocation(null);

        WidgetManager manager = UIManager.getInstance().getActiveScreen(player);
        if (manager instanceof EventManagerScreenTemplate screen) {
            EventManagerScreenTemplate.lastScreenMap.put(playerId, screen.getCurrentScreenClass());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();

        if (action.isLeftClick()) return;

        // Проверяем, что игрок кликает предметом и у него есть права
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
            "EventManager".equals(item.getItemMeta().getDisplayName()) &&
            player.hasPermission("event_manager.event")) {
            
            // Восстанавливаем экран, если он был сохранен
            Class<? extends EventManagerScreenTemplate> lastScreen = EventManagerScreenTemplate.lastScreenMap.get(player.getUniqueId());
            CommandHandler.createUI(player, Objects.requireNonNullElse(lastScreen, EventManagerMainScreen.class));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        WidgetManager manager = UIManager.getInstance().getActiveScreen(player);

        if (manager instanceof EventManagerScreenTemplate screen) {
            if (!screen.isPlayerInRange() && !EventManagerScreenTemplate.isFollowing) {
                screen.removeWithAnimation();
                UIManager.getInstance().unregisterScreen(player);
            } else {
                screen.updatePosition();
            }
        }
    }
}
