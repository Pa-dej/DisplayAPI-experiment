package me.Padej_.eventManagerDisplayUI.ui.screen.spleef;

import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.EventManagerMainScreen;
import me.Padej_.eventManagerDisplayUI.ui.screen.spleef.spleefUtils.SpleefUtilsScreen;
import me.Padej_.eventManagerDisplayUI.util.CountdownTimer;
import me.Padej_.eventManagerDisplayUI.util.FillRegion;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SpleefMainScreen extends EventManagerScreenTemplate {
    private TextDisplayButtonWidget doorButton;

    public SpleefMainScreen() {
        super();
    }

    public SpleefMainScreen(Player viewer, Location location, String text, float scale) {
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

        // Кнопка для открытия/закрытия двери
        doorButton = createEventerButton(
                isOpen() ? "Закрыть" : "Открыть",
                "Открыть / Закрыть вход",
                () -> {
                    if (isOpen()) {
                        closeDoor();
                    } else {
                        openDoor();
                    }
                    updateDoorButtonText();
                },
                center.clone().addVertical(step * 2),
                0.25
        );
        createDefaultButton("Арена", "", () -> ChangeScreen.switchTo(player, SpleefMainScreen.class, SpleefArenaScreen.class), center.clone().addVertical(step * 3), 0.21);
        createDefaultButton("Утилиты", "", () -> ChangeScreen.switchTo(player, SpleefMainScreen.class, SpleefUtilsScreen.class), center.clone().addVertical(step * 4), 0.24);

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

    private boolean isOpen() {
        World world = Bukkit.getWorld("world");
        if (world == null) return false;

        Block block = world.getBlockAt(832, 102, -655);
        return block.getType() == Material.AIR;
    }

    private void updateDoorButtonText() {
        doorButton.setText(
                Palette.gradient(isOpen() ? "Открыть" : "Закрыть", TextColor.color(0xe91396), TextColor.color(0xaf0e80)),
                Palette.gradient(isOpen() ? "Открыть" : "Закрыть", TextColor.color(0xe91396), TextColor.color(0xaf0e80))
        );
    }
}
