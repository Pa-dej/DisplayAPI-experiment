package me.padej.displayAPI.event;

import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.render.shapes.DefaultCube;
import me.padej.displayAPI.utils.AlignmentType;
import me.padej.displayAPI.utils.Segment;
import me.padej.displayAPI.utils.VertexUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TestEvent implements Listener {
    private final Map<Player, DefaultCube> playerCubes = new HashMap<>();
    private final Map<Player, BukkitRunnable> playerTasks = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && event.getItem().getType() == Material.SPECTRAL_ARROW) {
            Player player = event.getPlayer();
            Location spawnLocation = event.getInteractionPoint();
            Action action = event.getAction();
            if (spawnLocation == null) return;

            if (action.isRightClick()) {
                if (playerCubes.containsKey(player)) {
                    DefaultCube cube = playerCubes.get(player);
                    BlockDisplay removedDisplay = cube.spawn(spawnLocation);

                    if (removedDisplay == null) {
                        playerCubes.remove(player);
                        // Останавливаем задачу для игрока
                        stopPlayerTask(player);
                    }
                } else {
                    DefaultCube cube = new DefaultCube(1 + 1e-3f,
                            Material.WHITE_STAINED_GLASS.createBlockData(),
                            AlignmentType.BOTTOM) {};
                    cube.spawn(spawnLocation);
                    playerCubes.put(player, cube);

                    startPlayerTask(player, cube);
                }
            }
        }
    }

    private void startPlayerTask(Player player, DefaultCube cube) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Location pointA = player.getEyeLocation(); // Местоположение глаз игрока
                Location pointB = pointA.clone().add(player.getLocation().getDirection().multiply(6));
                Location[] cubeVertexes = VertexUtil.getCubeVertexes(cube);
                boolean isCubeInSight = VertexUtil.doesSegmentIntersectArea(new Segment(pointA, pointB), cubeVertexes);

                if (cube.getBlockDisplay() == null) return;
                cube.getBlockDisplay().setBlock(isCubeInSight ? Material.RED_STAINED_GLASS.createBlockData() : Material.WHITE_STAINED_GLASS.createBlockData());
            }
        };
        task.runTaskTimer(DisplayAPI.getInstance(), 0, 1);
        playerTasks.put(player, task);
    }

    // Метод для остановки задачи для игрока
    private void stopPlayerTask(Player player) {
        BukkitRunnable task = playerTasks.get(player);
        if (task != null) {
            task.cancel(); // Останавливаем задачу
            playerTasks.remove(player);
        }
    }
}