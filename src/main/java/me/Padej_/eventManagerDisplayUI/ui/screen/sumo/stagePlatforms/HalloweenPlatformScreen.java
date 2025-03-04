package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HalloweenPlatformScreen extends EventManagerScreenTemplate {

    public HalloweenPlatformScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public HalloweenPlatformScreen() {
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
        createEventerButton("*", "Базовая платформа",
                () -> player.performCommand("stp paste sumo_halloween *"),
                center.clone().addHorizontal(-horizontalOffset));

        createEventerButton("hl1-2", "Halloween 1-2",
                () -> player.performCommand("stp paste sumo_halloween hl1-2"),
                center.clone().addVertical(verticalStep).addHorizontal(-horizontalOffset));

        createEventerButton("hl2", "Halloween 2",
                () -> player.performCommand("stp paste sumo_halloween hl2"),
                center.clone().addVertical(verticalStep * 2).addHorizontal(-horizontalOffset));

        createEventerButton("hl2-3", "Halloween 2-3",
                () -> player.performCommand("stp paste sumo_halloween hl2-3"),
                center.clone().addVertical(verticalStep * 3).addHorizontal(-horizontalOffset));

        createEventerButton("hl3", "Halloween 3",
                () -> player.performCommand("stp paste sumo_halloween hl3"),
                center.clone().addVertical(verticalStep * 4).addHorizontal(-horizontalOffset));

        // Второй столбец
        createEventerButton("hl3-4", "Halloween 3-4",
                () -> player.performCommand("stp paste sumo_halloween hl3-4"),
                center.clone().addHorizontal(horizontalOffset));

        createEventerButton("hl4", "Halloween 4",
                () -> player.performCommand("stp paste sumo_halloween hl4"),
                center.clone().addVertical(verticalStep).addHorizontal(horizontalOffset));

        createEventerButton("hl4-5", "Halloween 4-5",
                () -> player.performCommand("stp paste sumo_halloween hl4-5"),
                center.clone().addVertical(verticalStep * 2).addHorizontal(horizontalOffset));

        createEventerButton("hl5", "Halloween 5",
                () -> player.performCommand("stp paste sumo_halloween hl5"),
                center.clone().addVertical(verticalStep * 3).addHorizontal(horizontalOffset));

        createEventerButton("hl5-6", "Halloween 5-6",
                () -> player.performCommand("stp paste sumo_halloween hl5-6"),
                center.clone().addVertical(verticalStep * 4).addHorizontal(horizontalOffset));
    }
}
