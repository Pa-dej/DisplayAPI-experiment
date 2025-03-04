package me.Padej_.eventManagerDisplayUI.util;

import me.Padej_.eventManagerDisplayUI.ui.screen.winners.WinnersScreen;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class WinnersManager {

    public static void teleportWinners(Location location) {
        List<String> winners = WinnersScreen.getWinners();
        String finalist = WinnersScreen.getFinalWinner();

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> winners.contains(player.getName()) ||
                        (finalist != null && finalist.equals(player.getName())))
                .forEach(player -> player.teleport(location));
    }

    public static void teleportNonWinnersFromArea(Area area, Location location) {
        List<String> winners = WinnersScreen.getWinners();
        String finalist = WinnersScreen.getFinalWinner();

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> area.contains(player.getLocation()))
                .filter(player -> !winners.contains(player.getName()) &&
                        (finalist == null || !finalist.equals(player.getName())))
                .forEach(player -> player.teleport(location));
    }

    public static void teleportWinnersFromArea(Area area, Location location) {
        List<String> winners = WinnersScreen.getWinners();
        String finalist = WinnersScreen.getFinalWinner();

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> area.contains(player.getLocation()))
                .filter(player -> winners.contains(player.getName()) ||
                        (finalist != null && finalist.equals(player.getName())))
                .forEach(player -> player.teleport(location));
    }
}
