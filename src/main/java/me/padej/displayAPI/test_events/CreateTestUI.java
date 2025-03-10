package me.padej.displayAPI.test_events;

import me.padej.displayAPI.api.events.DisplayClickEvent;
import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.UIManager;
import me.padej.displayAPI.ui.WidgetManager;
import me.padej.displayAPI.ui.screens.MainScreen;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CreateTestUI implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() == Material.BLUE_DYE &&
                ItemUtil.isExperimental(player.getInventory().getItemInMainHand()) &&
                event.getAction().isRightClick()) {

            WidgetManager currentManager = UIManager.getInstance().getActiveScreen(player);
            if (currentManager != null) {
                currentManager.remove();
            }

            Screen screen = new MainScreen(
                    player,
                    player.getLocation().add(0, player.getHeight() / 2, 0).add(player.getLocation().getDirection().multiply(0.8)),
                    " ",
                    0.1f
            );

            Animation.createDefaultScreenWithAnimation(screen, player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        WidgetManager manager = UIManager.getInstance().getActiveScreen(player);

        if (manager instanceof Screen) {
            Screen screen = (Screen) manager;
            if (Screen.isSaved) return;
            if (!screen.isPlayerInRange() && !screen.isFollowing()) {
                screen.remove();
                UIManager.getInstance().unregisterScreen(player);
            } else {
                screen.updatePosition();
            }
        }
    }

    @EventHandler
    public void onDisplayClick(DisplayClickEvent event) {

    }
}
