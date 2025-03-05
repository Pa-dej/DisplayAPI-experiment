package me.Padej_.eventManagerDisplayUI.ui.screen.sumo;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SumoSettingsScreen extends EventManagerScreenTemplate {

    public SumoSettingsScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public SumoSettingsScreen() {
        super();
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return SumoMainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.8f);
        float step = -0.145f;

        createEventerButton("On Sumo Utils", "Включить Sumo Utils",
                () -> player.performCommand("su enable"),
                center.clone().addVertical(step), 0.42);

        createEventerButton("Off Sumo Utils", "Выключить Sumo Utils",
                () -> player.performCommand("su disable"),
                center.clone().addVertical(step * 2), 0.42);
    }
}
