package me.padej.displayAPI.test_events;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

import me.padej.displayAPI.api.events.DisplayClickEvent;
import me.padej.displayAPI.ui.widgets.Widget;

public class CreateTestUI implements Listener {
    private final Map<Player, Screen> playerScreens = new HashMap<>();
    private static boolean wasInSettingsScreen = false;

    // Добавляем сеттер для wasInSettingsScreen
    public static void setWasInSettingsScreen(boolean value) {
        wasInSettingsScreen = value;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Screen screen = playerScreens.get(player);
            if (screen != null) {
                if (screen.isLookingAtWidget()) {
                    event.setCancelled(true);
                    screen.handleClick();
                }
            }
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.BLUE_DYE && 
            ItemUtil.isExperimental(player.getInventory().getItemInMainHand()) && 
            event.getAction().isRightClick()) {
            
            // Удаляем предыдущий экран, если он существует
            if (playerScreens.containsKey(player)) {
                playerScreens.get(player).remove();
            }

            Screen screen = new Screen(
                player,
                player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)),
                " ",
                0.1f
            );

            Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
                if (screen.getTextDisplay() == null) {
                    return;
                }

                Animation.applyTransformationWithInterpolation(
                    screen.getTextDisplay(),
                    new Transformation(
                        new Vector3f(0, 0, 0),
                        new AxisAngle4f(),
                        new Vector3f(10, 4, 1),
                        new AxisAngle4f()
                    ),
                    5
                );

                screen.setOnClose(() -> playerScreens.remove(player));
                screen.setupDefaultWidgets(player);
                
                // Если предыдущий экран был экраном настроек, открываем его снова
                if (wasInSettingsScreen) {
                    new ChangeScreen(screen).changeToSettingsScreen(player);
                }
            }, 2);

            playerScreens.put(player, screen);
            
            Bukkit.getScheduler().runTaskTimer(DisplayAPI.getInstance(), () -> {
                if (playerScreens.containsKey(player)) {
                    screen.update();
                }
            }, 0, 1);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Screen screen = playerScreens.get(player);
        
        if (screen != null) {
            if (Screen.isSaved) return;
            if (!screen.isPlayerInRange()) {
                screen.remove();
                playerScreens.remove(player);
            } else {
                screen.updatePosition();
            }
        }
    }

    @EventHandler
    public void onDisplayClick(DisplayClickEvent event) {
        Player player = event.getPlayer();
        Widget widget = event.getWidget();
        
        if (!player.hasPermission("display.interact")) {
            event.setCancelled(true);
            player.sendMessage("У вас нет прав для взаимодействия с этим элементом!");
        }
    }
}
