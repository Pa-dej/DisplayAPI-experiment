package me.padej.displayAPI.event;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.shapes.DefaultCube;
import me.padej.displayAPI.utils.AlignmentType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TestEvent3 implements Listener {
    private final Map<Player, DefaultCube> playerCubes = new HashMap<>();
    private final Map<Player, BukkitRunnable> playerTasks = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && event.getItem().getType() == Material.IRON_NUGGET) {
            Player player = event.getPlayer();
            Action action = event.getAction();
            if (action.isRightClick()) {
                if (playerCubes.containsKey(player)) {
                    DefaultCube cube = playerCubes.get(player);
                    BlockDisplay removedDisplay = cube.spawn(player.getLocation());

                    if (removedDisplay == null) {
                        playerCubes.remove(player);
                        stopOrbiting(player);
                    }
                } else {
                    DefaultCube cube = new DefaultCube(0.25f,
                            Material.EMERALD_BLOCK.createBlockData(),
                            AlignmentType.CENTER) {};
                    cube.spawn(player.getLocation());
                    playerCubes.put(player, cube);

                    startOrbiting(player);
                }
            }
        }
    }

    private void startOrbiting(Player player) {
        final double radius = 1;  // Радиус орбиты
        final double speed = 0.09; // Скорость вращения
        final float rotationSpeed = 0.035f; // Скорость вращения вокруг осей

        BukkitRunnable task = new BukkitRunnable() {
            private double angle = 0;
            private float rotationAngle = 0;
            private final boolean CLOCKWISE = new Random().nextBoolean(); // Случайное направление вращения

            @Override
            public void run() {
                if (!player.isOnline()) {
                    stopOrbiting(player);
                    return;
                }

                Location center = player.getLocation().add(0, player.getHeight() / 2, 0); // Центр вращения — над головой игрока
                double x = center.getX() + radius * Math.cos(angle);
                double z = center.getZ() + radius * Math.sin(angle);
                double y = center.getY();

                Location newLocation = new Location(center.getWorld(), x, y, z);
                DefaultCube cube = playerCubes.get(player);
                if (cube != null) {
                    BlockDisplay blockDisplay = cube.getBlockDisplay();
                    if (blockDisplay != null) {
                        blockDisplay.teleport(newLocation);

                        // Вращение вокруг всех осей
                        float rotationAmount = CLOCKWISE ? rotationSpeed : -rotationSpeed;
                        rotationAngle += rotationAmount;

                        Quaternionf leftRotation = new Quaternionf().rotateXYZ(rotationAngle, rotationAngle * 0.5f, rotationAngle * 1.5f);
                        Quaternionf rightRotation = new Quaternionf().rotateXYZ(-rotationAngle * 1.5f, -rotationAngle, -rotationAngle * 0.5f);

                        blockDisplay.setTransformation(new Transformation(
                                cube.getTransformation().getTranslation(),
                                leftRotation,
                                cube.getTransformation().getScale(),
                                rightRotation
                        ));
                    }
                }

                angle += speed;
                if (angle > 2 * Math.PI) {
                    angle = 0;
                }
            }
        };

        task.runTaskTimer(DisplayAPI.getInstance(), 0, 1);
        playerTasks.put(player, task);
    }

    private void stopOrbiting(Player player) {
        BukkitRunnable task = playerTasks.get(player);
        if (task != null) {
            task.cancel();
            playerTasks.remove(player);
        }
    }
}
