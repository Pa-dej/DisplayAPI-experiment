package me.padej.displayAPI.test_events;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.particles.PoisonParticle;
import me.padej.displayAPI.render.shapes.StringRectangle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class CreateDisplayParticlesThird implements Listener {
    private final Map<Player, StringRectangle> playerCubes = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && event.getItem().getType() == Material.TURTLE_SCUTE) {
            Player player = event.getPlayer();
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
//                    PoisonParticle particle = new PoisonParticle(player, spawnLocation);
                    PoisonParticle particle = new PoisonParticle(player.getLocation());
                    DisplayAPI.particles.add(particle);
                }
            }
        }
    }
}
