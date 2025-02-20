package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.ItemDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Color;
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

    public Branch3Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return MainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        ItemDisplayButtonConfig[] branchButtons = {
                new ItemDisplayButtonConfig(Material.GOLDEN_AXE, () ->
                        player.sendMessage("Ветка 3"))
                        .setScale(.11f, .11f, .11f)
                        .setGlowColor(Color.LIME)
                        .setTooltip("Подветка 3")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone()),

                new ItemDisplayButtonConfig(Material.IRON_BOOTS, () ->
                        player.sendMessage("Ветка 4"))
                        .setTooltip("Подветка 4")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone().addVertical(0.15f))
        };

        for (ItemDisplayButtonConfig config : branchButtons) {
            createWidget(config);
        }
    }
}
