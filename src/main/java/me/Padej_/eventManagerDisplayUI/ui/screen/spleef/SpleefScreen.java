package me.Padej_.eventManagerDisplayUI.ui.screen.spleef;

import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.EventManagerMainScreen;
import me.Padej_.eventManagerDisplayUI.util.CountdownTimer;
import me.Padej_.eventManagerDisplayUI.util.FillRegion;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SpleefScreen extends EventManagerScreenTemplate {
    private TextDisplayButtonWidget doorButton;

    public SpleefScreen() {
        super();
    }

    public SpleefScreen(Player viewer, Location location, String text, float scale) {
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

        createEventerButton("Арена", "", () -> ChangeScreen.switchTo(player, SpleefScreen.class, SpleefArenaScreen.class), center.clone(), 0.21);
        createEventerButton("Утилиты", "", () -> {}, center.clone().addVertical(step), 0.24);
        createEventerButton("Начать отсчет", "Начать обратный отсчет", () -> startCountdown(player), center.clone().addVertical(step * 2), 0.44);

        // Кнопка для открытия/закрытия двери
        doorButton = createEventerButton(
                isDoorOpen() ? "Закрыть" : "Открыть",
                "Открыть / Закрыть вход",
                () -> {
                    if (isDoorOpen()) {
                        closeDoor();
                    } else {
                        openDoor();
                    }
                    updateDoorButtonText();
                },
                center.clone().addVertical(step * 3),
                0.25
        );

        updateDoorButtonText();
    }

    public void startCountdown(Player whoStarted) {
        CountdownTimer.startCountdown(whoStarted, 5, this::fillSnowBlock);
    }

    private void fillSnowBlock() {
        FillRegion.fillRegion(833, 87, -687, 902, 87, -618, Material.SNOW_BLOCK);
    }

    private void openDoor() {
        FillRegion.fillRegion(832, 102, -655, 832, 107, -650, Material.AIR);
    }

    private void closeDoor() {
        FillRegion.fillRegion(832, 102, -655, 832, 107, -650, Material.BLUE_STAINED_GLASS);
    }

    private boolean isDoorOpen() {
        World world = Bukkit.getWorld("world");
        if (world == null) return false;

        Block block = world.getBlockAt(832, 102, -655);
        return block.getType() == Material.AIR;
    }

    private void updateDoorButtonText() {
        doorButton.setText(
                Component.text(isDoorOpen() ? "Закрыть" : "Открыть").color(Palette.WHITE_LIGHT),
                Component.text(isDoorOpen() ? "Закрыть" : "Открыть").color(Palette.WHITE_LIGHT)
        );
    }
}
