package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PinkPlatformScreen extends EventManagerScreenTemplate {

    public PinkPlatformScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public PinkPlatformScreen() {
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

        createEventerButton("*", "Базовая платформа",
                () -> player.performCommand("stp paste sumo_pink *"),
                center.clone());

        createEventerButton("Pink2", "Pink 2",
                () -> player.performCommand("stp paste sumo_pink Pink2"),
                center.clone().addVertical(verticalStep));

        createEventerButton("Pink3", "Pink 3",
                () -> player.performCommand("stp paste sumo_pink Pink3"),
                center.clone().addVertical(verticalStep * 2));

        createEventerButton("Pink4", "Pink 4",
                () -> player.performCommand("stp paste sumo_pink Pink4"),
                center.clone().addVertical(verticalStep * 3));
    }
}
