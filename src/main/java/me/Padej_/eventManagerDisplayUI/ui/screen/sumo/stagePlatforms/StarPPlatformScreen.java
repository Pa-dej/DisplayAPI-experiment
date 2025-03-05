package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class StarPPlatformScreen extends EventManagerScreenTemplate {

    public StarPPlatformScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public StarPPlatformScreen() {
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

        createHelperButton("Вся", "Базовая платформа",
                () -> player.performCommand("stp paste sumo_starP *"),
                center.clone());

        createEventerButton("star2", "Star Premium 2",
                () -> player.performCommand("stp paste sumo_starP star2"),
                center.clone().addVertical(verticalStep));

        createEventerButton("star3", "Star Premium 3",
                () -> player.performCommand("stp paste sumo_starP star3"),
                center.clone().addVertical(verticalStep * 2));

        createEventerButton("star4", "Star Premium 4",
                () -> player.performCommand("stp paste sumo_starP star4"),
                center.clone().addVertical(verticalStep * 3));
    }
}
