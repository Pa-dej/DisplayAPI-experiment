package me.Padej_.eventManagerDisplayUI.util;

import me.Padej_.eventManagerDisplayUI.EventManagerDisplayUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTimer {

    private static boolean countdownActive = false;

    public static void startCountdown(Player player, int seconds, Runnable onComplete) {
        if (countdownActive) {
            player.sendActionBar("§cОтсчёт уже активен.");
            return;
        }

        countdownActive = true;
        player.sendActionBar("§eОтсчёт начинается...");

        new BukkitRunnable() {
            int countdown = seconds;

            @Override
            public void run() {
                if (countdown > 0) {
                    for (Player p : player.getWorld().getPlayers()) {
                        if (p.getLocation().distance(player.getLocation()) <= 20) {
                            p.sendTitle("§6" + countdown, "", 10, 10, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1f, 1f);
                        }
                    }
                    countdown--;
                } else {
                    for (Player p : player.getWorld().getPlayers()) {
                        if (p.getLocation().distance(player.getLocation()) <= 20) {
                            p.sendTitle("§aВперёд!", "", 10, 40, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                        }
                    }

                    countdownActive = false;
                    cancel();
                    onComplete.run();
                }
            }
        }.runTaskTimer(EventManagerDisplayUI.getInstance(), 0, 20); // Запуск каждую секунду (20 тиков)
    }
}
