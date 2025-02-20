package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.ItemDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Branch2Screen extends Screen {
    public Branch2Screen() {
        super();
    }

    public Branch2Screen(Player viewer, Location location) {
        super(viewer, location);
    }

    public Branch2Screen(Player viewer, Location location, String text, float scale) {
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
                new ItemDisplayButtonConfig(Material.DIAMOND, () ->
                        player.sendMessage("Ветка 3"))
                        .setTooltip("Подветка 3")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone()),

                new ItemDisplayButtonConfig(Material.EMERALD, () ->
                        player.sendMessage("Ветка 4"))
                        .setTooltip("Подветка 4")
                        .setTooltipDelay(30)
                        .setPosition(basePosition.clone().addHorizontal(step))
        };

        for (ItemDisplayButtonConfig config : branchButtons) {
            createWidget(config);
        }
    }
} 