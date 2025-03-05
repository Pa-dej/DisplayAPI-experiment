package me.Padej_.eventManagerDisplayUI.ui.screen.sumo;

import me.Padej_.eventManagerDisplayUI.EventManagerDisplayUI;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.EventManagerMainScreen;
import me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms.StagePlatformsMainScreen;
import me.Padej_.eventManagerDisplayUI.util.Area;
import me.Padej_.eventManagerDisplayUI.util.CountdownTimer;
import me.Padej_.eventManagerDisplayUI.util.FillRegion;
import me.Padej_.eventManagerDisplayUI.util.WinnersManager;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SumoMainScreen extends EventManagerScreenTemplate {

    public SumoMainScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public SumoMainScreen() {
        super();
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return EventManagerMainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.8f);
        float step = -0.145f;

        createEventerButton("Начать отсчет", "Начать обратный отсчет", () -> startCountdown(player), center.clone().addVertical(step), 0.44);

        createDefaultButton("Стадии платформ", "Управление платформами",
                () -> ChangeScreen.switchTo(player, SumoMainScreen.class, StagePlatformsMainScreen.class),
                center.clone().addVertical(step * 2), 0.44);

        createDefaultButton("Сортировка", "Логика сортировки игроков",
                () -> ChangeScreen.switchTo(player, SumoMainScreen.class, SumoSortWinnersScreen.class),
                center.clone().addVertical(step * 3), 0.44);

        createDefaultButton("Настройки", "Настройки ивента",
                () -> ChangeScreen.switchTo(player, SumoMainScreen.class, SumoSettingsScreen.class),
                center.clone().addVertical(step * 4), 0.44);
    }

    public void startCountdown(Player whoStarted) {
        CountdownTimer.startCountdown(whoStarted, 5, this::updatePlatform);
    }

    void updatePlatform() {
        FillRegion.fillRegion(147, 143, -555, 159, 143, -543, Material.AIR);

        new BukkitRunnable() {
            @Override
            public void run() {
                FillRegion.fillRegion(147, 143, -555, 159, 143, -543, Material.BARRIER);
            }
        }.runTaskLater(EventManagerDisplayUI.getInstance(), 20); // Запуск через 1 секунду (20 тиков)
    }
}
