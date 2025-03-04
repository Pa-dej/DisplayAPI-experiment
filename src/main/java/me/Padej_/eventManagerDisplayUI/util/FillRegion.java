package me.Padej_.eventManagerDisplayUI.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class FillRegion {
    public static void fillRegion(Area area, Material material) {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        for (int x = area.getMinX(); x <= area.getMaxX(); x++) {
            for (int y = area.getMinY(); y <= area.getMaxY(); y++) {
                for (int z = area.getMinZ(); z <= area.getMaxZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(material);
                }
            }
        }
    }

    public static void fillRegion(int x1, int y1, int z1, int x2, int y2, int z2, Material material) {
        fillRegion(new Area(x1, y1, z1, x2, y2, z2), material);
    }

    public static void fillCircle(Location center, int radius, Material material) {
        World world = center.getWorld();
        if (world == null) return;

        int xc = center.getBlockX();
        int yc = center.getBlockY();
        int zc = center.getBlockZ();

        for (int x = xc - radius; x <= xc + radius; x++) {
            for (int z = zc - radius; z <= zc + radius; z++) {
                if ((x - xc) * (x - xc) + (z - zc) * (z - zc) <= radius * radius) {
                    world.getBlockAt(x, yc, z).setType(material);
                }
            }
        }
    }
}
