package me.padej.displayAPI.test_events;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.TextDisplayConfig;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import java.awt.Color;

import java.util.HashMap;
import java.util.Map;

public class CreateTestUI implements Listener {
    private final Map<Player, Screen> playerScreens = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Screen screen = playerScreens.get(player);
            if (screen != null) {
                // Проверяем, смотрит ли игрок на какой-либо виджет
                if (screen.isLookingAtWidget()) {
                    event.setCancelled(true); // Отменяем событие если смотрим на виджет
                }
                screen.handleClick();
            }
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.BLUE_DYE && ItemUtil.isExperimental(player.getInventory().getItemInMainHand()) && event.getAction().isRightClick()) {
            // Удаляем предыдущий экран, если он существует
            if (playerScreens.containsKey(player)) {
                playerScreens.get(player).remove();
            }

            // Создаем новый экран перед игроком
            Screen screen = new Screen(
                player,
                player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)),
                " ",
                0.1f
            );
            
            Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(DisplayAPI.class), () -> {
                Animation.applyTransformationWithInterpolation(screen.getTextDisplay(), new Transformation(
                        new Vector3f(0, 0, 0),
                        new AxisAngle4f(),
                        new Vector3f(10, 4, 1),
                        new AxisAngle4f()
                ), 5);

                screen.setOnClose(() -> playerScreens.remove(player));
                screen.setupDefaultWidgets(player);
            }, 2);
            
            playerScreens.put(player, screen);
            
            // Запускаем задачу обновления состояния кнопки
            Bukkit.getScheduler().runTaskTimer(DisplayAPI.getPlugin(DisplayAPI.class), () -> {
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
}
