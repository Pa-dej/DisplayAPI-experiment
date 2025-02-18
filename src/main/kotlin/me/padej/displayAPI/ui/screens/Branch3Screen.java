package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Branch3Screen extends Screen {
    public Branch3Screen() {
        super();
    }

    public Branch3Screen(Player viewer, Location location) {
        super(viewer, location);
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return MainScreen.class;
    }

    @Override
    public WidgetConfig[] getBranchWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);

        return new WidgetConfig[] {
                new WidgetConfig(Material.GOLDEN_AXE, () ->
                        player.sendMessage("Ветка 3"))
                        .setTooltip("Подветка 3")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone()),

                new WidgetConfig(Material.IRON_BOOTS, () ->
                        player.sendMessage("Ветка 4"))
                        .setTooltip("Подветка 4")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone().addVertical(0.15f))
        };
    }
}
