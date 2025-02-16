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
import me.padej.displayAPI.ui.UIManager;
import me.padej.displayAPI.ui.screens.MainScreen;

public class CreateTestUI implements Listener {
    private static boolean wasInSettingsScreen = false;

    public static void setWasInSettingsScreen(boolean value) {
        wasInSettingsScreen = value;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() == Material.BLUE_DYE && 
            ItemUtil.isExperimental(player.getInventory().getItemInMainHand()) && 
            event.getAction().isRightClick()) {
            
            // Сохраняем состояние предыдущего экрана
            wasInSettingsScreen = ChangeScreen.isSettingsScreen();
            
            // Получаем текущий экран из UIManager
            Screen currentScreen = UIManager.getInstance().getActiveScreen(player);
            if (currentScreen != null) {
                currentScreen.remove();
            }

            Screen screen = new MainScreen(
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

                screen.setOnClose(() -> UIManager.getInstance().unregisterScreen(player));
                screen.setupDefaultWidgets(player);
                
                if (wasInSettingsScreen) {
                    new ChangeScreen(screen).changeToSettingsScreen(player);
                }
            }, 2);

            // Регистрируем экран в UIManager
            UIManager.getInstance().registerScreen(player, screen);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Screen screen = UIManager.getInstance().getActiveScreen(player);
        
        if (screen != null) {
            if (Screen.isSaved) return;
            if (!screen.isPlayerInRange()) {
                screen.remove();
                UIManager.getInstance().unregisterScreen(player);
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
