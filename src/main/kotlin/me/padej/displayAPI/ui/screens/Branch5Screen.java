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

    public Branch5Screen(Player viewer, Location location, String text, float scale) {
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

        WidgetConfig[] branchButtons = {
                new WidgetConfig(Material.NETHER_STAR, () -> {
                    ChangeScreen.switchTo(player, Branch5Screen.class, Branch51Screen.class);
                })
                        .setPosition(basePosition.clone().addHorizontal(step))
        };

        for (WidgetConfig config : branchButtons) {
            createWidget(config);
        }
    }
}
