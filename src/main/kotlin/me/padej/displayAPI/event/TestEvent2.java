package me.padej.displayAPI.event;

import me.padej.displayAPI.render.HighlightStyle;
import me.padej.displayAPI.render.shapes.Highlight;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestEvent2 implements Listener {

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Проверяем, что игрок использует перо
        if (player.getInventory().getItemInMainHand().getType() == Material.COAL) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && player.getCooldown(Material.COAL) < 1) {
                Location location = clickedBlock.getLocation();  // Получаем точку клика по блоку

                // Проверяем, есть ли уже дисплеи для этой локации
                String blockPosKey = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                if (Highlight.blockPosDisplays.containsKey(blockPosKey)) {
                    // Если есть, удаляем их
                    Highlight.removeSelectionOnBlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                } else {
                    // Если нет, создаем новые дисплеи
                    Highlight.createSides(location, HighlightStyle.BRONZE);
                }
                player.setCooldown(Material.COAL, 1);
            }
        }
    }

}
