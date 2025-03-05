package me.Padej_.eventManagerDisplayUI.ui.screen.sumo;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.util.Area;
import me.Padej_.eventManagerDisplayUI.util.WinnersManager;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SumoSortWinnersScreen extends EventManagerScreenTemplate {

    public SumoSortWinnersScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public SumoSortWinnersScreen() {
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

        createEventerButton("Победители", "Оставить только победителей",
                () -> WinnersManager.teleportNonWinnersFromArea(new Area(146, 143, -556, 160, 149, -542),
                        new Location(Bukkit.getWorld("world"), 162, 152, -549)), center.clone().addVertical(step), 0.44);

        createEventerButton("Не победители", "Оставить только не победителей",
                () -> WinnersManager.teleportWinnersFromArea(new Area(146, 143, -556, 160, 149, -542),
                        new Location(Bukkit.getWorld("world"), 162, 152, -549)), center.clone().addVertical(step * 2), 0.42);
    }
}
