package me.padej.displayAPI.test_events;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.particles.PoisonDisplayParticle;
import me.padej.displayAPI.render.shapes.StringRectangle;
import me.padej.displayAPI.utils.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class CreateDisplayParticleThirdTest implements Listener {
    private final Map<Player, StringRectangle> playerCubes = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.TURTLE_SCUTE && ItemUtil.isExperimental(player.getInventory().getItemInMainHand())) {
            Location spawnLocation = event.getInteractionPoint();
            Action action = event.getAction();
            if (spawnLocation == null) return;

            if (action.isRightClick()) {
                if (playerCubes.containsKey(player)) {
                    StringRectangle rectangle = playerCubes.get(player);
                    TextDisplay removedDisplay = rectangle.spawn(spawnLocation);

                    if (removedDisplay == null) {
                        playerCubes.remove(player);
                    }
                } else {
//                    StringRectangle cube = new StringRectangle(
//                            10, Color.BLACK, 0, Display.Billboard.CENTER, false, "§6@"
//                    ) {};
//                    cube.spawn(spawnLocation);
//                    cube.getTextDisplay().setBrightness(new Display.Brightness(15, 15));
//
//                    Animation.applyAnimationTransform(cube.getTextDisplay(), new Transformation(
//                            new Vector3f(),
//                            new AxisAngle4f(),
//                            new Vector3f(0.5f, 0.5f, 0.5f),
//                            new AxisAngle4f()
//                    ), 20);
//
//                    playerCubes.put(player, cube);
//                    PoisonDisplayParticle particle = new PoisonDisplayParticle(player, spawnLocation);
                    PoisonDisplayParticle particle = new PoisonDisplayParticle(player.getLocation());
                    DisplayAPI.DISPLAY_PARTICLES.add(particle);
                }
            }
        }
    }
}
