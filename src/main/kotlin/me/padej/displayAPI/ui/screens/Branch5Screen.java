package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Branch5Screen extends Screen {
    public Branch5Screen() {
        super();
    }

    public Branch5Screen(Player viewer, Location location) {
        super(viewer, location);
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return MainScreen.class;
    }

    @Override
    public WidgetConfig[] getBranchWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);

        return new WidgetConfig[]{
                new WidgetConfig(Material.DIAMOND, () -> new ChangeScreen(this).changeToBranch(player, Branch51Screen.class))
                        .setPosition(basePosition.clone())
        };
    }
}
