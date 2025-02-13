package me.padej.displayAPI.event;

import me.padej.displayAPI.render.particles.ExampleSquareParticle;
import me.padej.displayAPI.render.particles.ExampleStringParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static me.padej.displayAPI.DisplayAPI.particles;

public class TestEvent6 implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && event.getItem().getType() == Material.LEATHER) {
            Player player = event.getPlayer();
            Location eventLocation = event.getInteractionPoint();
            Location spawnLocation = (eventLocation != null)
                    ? eventLocation
                    : player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.3));
            Action action = event.getAction();

            if (action.isRightClick()) {
                for (int i = 0; i < 25; i++) {
                    ExampleStringParticle particle = new ExampleStringParticle(player, spawnLocation); // Передаем игрока
                    particles.add(particle);
                }
            }

            player.getWorld().spawnParticle(Particle.EXPLOSION, spawnLocation, 1, 0, 0, 0, 0);
        }
    }

}
