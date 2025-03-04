package me.Padej_.eventManagerDisplayUI.ui.screen.mace;

import me.Padej_.eventManagerDisplayUI.EventManagerDisplayUI;
import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.EventManagerMainScreen;
import me.Padej_.eventManagerDisplayUI.util.CountdownTimer;
import me.Padej_.eventManagerDisplayUI.util.FillRegion;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MaceMainScreen extends EventManagerScreenTemplate {
    private TextDisplayButtonWidget doorWidget;

    public MaceMainScreen() {
        super();
    }

    public MaceMainScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
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


        doorWidget = createEventerButton(
                isOpen() ? "Открыть" : "Закрыть",
                "Открыть / Закрыть вход",
                () -> {
                    if (isOpen()) fillOrangeStainedGlass();
                    else fillAir();
                },
                center.clone().addVertical(step * 2),
                0.25
        );
        doorWidget.setUpdateCallback(() -> {
            if (viewer != null) {
                doorWidget.setText(
                        Palette.gradient(isOpen() ? "Открыть" : "Закрыть", TextColor.color(0xe91396), TextColor.color(0xaf0e80)),
                        Palette.gradient(isOpen() ? "Открыть" : "Закрыть", TextColor.color(0xe91396), TextColor.color(0xaf0e80))
                );
            }
        });
        createEventerButton("✎ Настройки", "Настройки ивента", () -> ChangeScreen.switchTo(player, MaceMainScreen.class, MaceSettingsScreen.class), center.clone().addVertical(step * 3), 0.44);
    }

    private boolean isOpen() {
        World world = Bukkit.getWorld("world");
        if (world == null) return false;

        Block block = world.getBlockAt(-409, 138, 6617);
        return block.getType() == Material.AIR;
    }

    public void startCountdown(Player whoStarted) {
        CountdownTimer.startCountdown(whoStarted, 5, this::updatePlatform);
    }

    void updatePlatform() {
        fillAir();

        new BukkitRunnable() {
            @Override
            public void run() {
                fillOrangeStainedGlass();
            }
        }.runTaskLater(EventManagerDisplayUI.getInstance(), 20); // Запуск через 1 секунду (20 тиков)
    }

    private void fillAir() {
        // Main square
        FillRegion.fillRegion(-411, 138, 6618, -403, 138, 6626, Material.AIR);

        // Corners
        FillRegion.fillRegion(-409, 138, 6617, -405, 138, 6627, Material.AIR);
        FillRegion.fillRegion(-412, 138, 6620, -402, 138, 6624, Material.AIR);
    }

    private void fillOrangeStainedGlass() {
        // Main square
        FillRegion.fillRegion(-411, 138, 6618, -403, 138, 6626, Material.ORANGE_STAINED_GLASS);

        // Corners
        FillRegion.fillRegion(-409, 138, 6617, -405, 138, 6627, Material.ORANGE_STAINED_GLASS);
        FillRegion.fillRegion(-412, 138, 6620, -402, 138, 6624, Material.ORANGE_STAINED_GLASS);
    }
}

