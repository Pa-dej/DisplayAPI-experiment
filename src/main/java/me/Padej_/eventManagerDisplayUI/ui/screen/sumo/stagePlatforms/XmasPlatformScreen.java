package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class XmasPlatformScreen extends EventManagerScreenTemplate {

    public XmasPlatformScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public XmasPlatformScreen() {
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
        float horizontalStep = 0.4f;

        // Первый столбец (левый)
        createHelperButton("Вся", "Базовая платформа",
                () -> player.performCommand("stp paste sumo_xmas *"),
                center.clone().addHorizontal(-horizontalStep));

        createEventerButton("ne1-2", "New Year 1-2",
                () -> player.performCommand("stp paste sumo_xmas ne1-2"),
                center.clone().addVertical(verticalStep).addHorizontal(-horizontalStep));

        createEventerButton("ne2", "New Year 2",
                () -> player.performCommand("stp paste sumo_xmas ne2"),
                center.clone().addVertical(verticalStep * 2).addHorizontal(-horizontalStep));

        createEventerButton("ne2-3", "New Year 2-3",
                () -> player.performCommand("stp paste sumo_xmas ne2-3"),
                center.clone().addVertical(verticalStep * 3).addHorizontal(-horizontalStep));

        createEventerButton("ne3", "New Year 3",
                () -> player.performCommand("stp paste sumo_xmas ne3"),
                center.clone().addVertical(verticalStep * 4).addHorizontal(-horizontalStep));

        // Второй столбец (центральный)
        createEventerButton("ne3-4", "New Year 3-4",
                () -> player.performCommand("stp paste sumo_xmas ne3-4"),
                center.clone());

        createEventerButton("ne4", "New Year 4",
                () -> player.performCommand("stp paste sumo_xmas ne4"),
                center.clone().addVertical(verticalStep));

        createEventerButton("ne4-5", "New Year 4-5",
                () -> player.performCommand("stp paste sumo_xmas ne4-5"),
                center.clone().addVertical(verticalStep * 2));

        createEventerButton("ne5", "New Year 5",
                () -> player.performCommand("stp paste sumo_xmas ne5"),
                center.clone().addVertical(verticalStep * 3));

        createEventerButton("ne5-6", "New Year 5-6",
                () -> player.performCommand("stp paste sumo_xmas ne5-6"),
                center.clone().addVertical(verticalStep * 4));

        // Третий столбец (правый)
        createEventerButton("ne6", "New Year 6",
                () -> player.performCommand("stp paste sumo_xmas ne6"),
                center.clone().addHorizontal(horizontalStep));

        createEventerButton("ne6-7", "New Year 6-7",
                () -> player.performCommand("stp paste sumo_xmas ne6-7"),
                center.clone().addVertical(verticalStep).addHorizontal(horizontalStep));

        createEventerButton("ne7", "New Year 7",
                () -> player.performCommand("stp paste sumo_xmas ne7"),
                center.clone().addVertical(verticalStep * 2).addHorizontal(horizontalStep));

        createEventerButton("ne7-8", "New Year 7-8",
                () -> player.performCommand("stp paste sumo_xmas ne7-8"),
                center.clone().addVertical(verticalStep * 3).addHorizontal(horizontalStep));

        createEventerButton("ne8", "New Year 8",
                () -> player.performCommand("stp paste sumo_xmas ne8"),
                center.clone().addVertical(verticalStep * 4).addHorizontal(horizontalStep));
    }
}
