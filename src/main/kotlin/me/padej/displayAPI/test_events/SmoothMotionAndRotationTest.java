package me.padej.displayAPI.test_events;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.shapes.DefaultCube;
import me.padej.displayAPI.utils.AlignmentType;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SmoothMotionAndRotationTest implements Listener {
    private final Map<Player, DefaultCube> playerCubes = new HashMap<>();
    private final Map<Player, BukkitRunnable> playerTasks = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.IRON_NUGGET && ItemUtil.isExperimental(player.getInventory().getItemInMainHand())) {
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
                    DefaultCube cube = new DefaultCube(0.375f,
                            Material.SHROOMLIGHT.createBlockData(),
                            AlignmentType.CENTER) {};
                    cube.spawn(player.getLocation());
                    cube.getBlockDisplay().setBrightness(new Display.Brightness(15, 15));
                    playerCubes.put(player, cube);

                    startOrbiting(player);
                }
            }
        }
    }

    private void startOrbiting(Player player) {
        final double radius = 1.2;  // Радиус орбиты
        final double speed = 0.05; // Скорость вращения
        final float rotationSpeed = 0.05f; // Скорость вращения вокруг осей

        Location center = player.getLocation().add(0, player.getHeight() / 2, 0);

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

//                Location center = player.getLocation().add(0, player.getHeight() / 2, 0); // Центр вращения — над головой игрока
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

                        Animation.applyTransformationWithInterpolation(blockDisplay, new Transformation(
                                cube.getTransformation().getTranslation(),
                                leftRotation,
                                cube.getTransformation().getScale(),
                                rightRotation)
                        );
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
