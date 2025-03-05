package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ClassicPlatformScreen extends EventManagerScreenTemplate {

    public ClassicPlatformScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public ClassicPlatformScreen() {
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
                () -> player.performCommand("stp paste sumo_classic *"),
                center.clone());

        createEventerButton("green", "Зеленая платформа",
                () -> player.performCommand("stp paste sumo_classic green"),
                center.clone().addVertical(verticalStep));

        createEventerButton("yellow", "Желтая платформа",
                () -> player.performCommand("stp paste sumo_classic yellow"),
                center.clone().addVertical(verticalStep * 2));

        createEventerButton("orange", "Оранжевая платформа",
                () -> player.performCommand("stp paste sumo_classic orange"),
                center.clone().addVertical(verticalStep * 3));
    }
}
