package me.padej.displayAPI.event;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.particles.ExampleParticle;
import me.padej.displayAPI.render.shapes.DefaultSquare;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

import static me.padej.displayAPI.DisplayAPI.particles;

public class TestEvent5  implements Listener {
    private final Map<Player, DefaultSquare> playerSquares = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && event.getItem().getType() == Material.WHEAT) {
            Player player = event.getPlayer();
            Location eventLocation = event.getInteractionPoint();
            Location spawnLocation = (eventLocation != null)
                    ? eventLocation
                    : player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(3));
            Action action = event.getAction();

            if (action.isRightClick()) {
                if (playerSquares.containsKey(player)) {
                    DefaultSquare square = playerSquares.get(player);
                    TextDisplay removedDisplay = square.spawn(spawnLocation);

                    if (removedDisplay == null) {
                        playerSquares.remove(player);
                    }
                } else {
                    ExampleParticle particle = new ExampleParticle(spawnLocation);
                    particles.add(particle);
                }
            }
        }
    }
}
