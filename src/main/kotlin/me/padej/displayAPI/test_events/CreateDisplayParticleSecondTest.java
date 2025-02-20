package me.padej.displayAPI.test_events;

import me.padej.displayAPI.render.particles.ExampleComponentParticle;
import me.padej.displayAPI.utils.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.padej.displayAPI.DisplayAPI.particles;

public class CreateDisplayParticleSecondTest implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.PINK_DYE && ItemUtil.isExperimental(player.getInventory().getItemInMainHand())) {
            Location eventLocation = event.getInteractionPoint();
            Location spawnLocation = (eventLocation != null)
                    ? eventLocation
                    : player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.3));
            Action action = event.getAction();

            if (action.isRightClick()) {
                for (int i = 0; i < 40; i++) {
                    ExampleComponentParticle particle = new ExampleComponentParticle(player, spawnLocation); // Передаем игрока
                    particles.add(particle);
                }
                player.getWorld().spawnParticle(Particle.EXPLOSION, spawnLocation, 1, 0, 0, 0, 0);
            }
        }
    }

}
