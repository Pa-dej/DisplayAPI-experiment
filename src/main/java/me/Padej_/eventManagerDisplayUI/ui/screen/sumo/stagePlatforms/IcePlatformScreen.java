package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IcePlatformScreen extends EventManagerScreenTemplate {

    public IcePlatformScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public IcePlatformScreen() {
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
                () -> player.performCommand("stp paste sumo_ice *"),
                center.clone());

        createEventerButton("small", "Маленькая платформа",
                () -> player.performCommand("stp paste sumo_ice small"),
                center.clone().addVertical(verticalStep));

        createEventerButton("medium", "Средняя платформа",
                () -> player.performCommand("stp paste sumo_ice medium"),
                center.clone().addVertical(verticalStep * 2));

        createEventerButton("big", "Большая платформа",
                () -> player.performCommand("stp paste sumo_ice big"),
                center.clone().addVertical(verticalStep * 3));
    }
}
