package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LotusPlatformScreen extends EventManagerScreenTemplate {

    public LotusPlatformScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public LotusPlatformScreen() {
        super();
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return StagePlatformsMainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.75f);
        float verticalStep = -0.145f;
        float horizontalOffset = 0.3f;

        // Первый столбец
        createHelperButton("Вся", "Базовая платформа",
                () -> player.performCommand("stp paste sumo_lotus *"),
                center.clone().addHorizontal(-horizontalOffset));

        createEventerButton("L1-2", "Lotus 1-2",
                () -> player.performCommand("stp paste sumo_lotus L1-2"),
                center.clone().addVertical(verticalStep).addHorizontal(-horizontalOffset));

        createEventerButton("L2", "Lotus 2",
                () -> player.performCommand("stp paste sumo_lotus L2"),
                center.clone().addVertical(verticalStep * 2).addHorizontal(-horizontalOffset));

        createEventerButton("L2-3", "Lotus 2-3",
                () -> player.performCommand("stp paste sumo_lotus L2-3"),
                center.clone().addVertical(verticalStep * 3).addHorizontal(-horizontalOffset));

        // Второй столбец
        createEventerButton("L3", "Lotus 3",
                () -> player.performCommand("stp paste sumo_lotus L3"),
                center.clone().addHorizontal(horizontalOffset));

        createEventerButton("L3-4", "Lotus 3-4",
                () -> player.performCommand("stp paste sumo_lotus L3-4"),
                center.clone().addVertical(verticalStep).addHorizontal(horizontalOffset));

        createEventerButton("L4", "Lotus 4",
                () -> player.performCommand("stp paste sumo_lotus L4"),
                center.clone().addVertical(verticalStep * 2).addHorizontal(horizontalOffset));
    }
}
