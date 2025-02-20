package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.widgets.ItemDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Branch51Screen extends Screen {
    public Branch51Screen() {
        super();
    }

    public Branch51Screen(Player viewer, Location location) {
        super(viewer, location);
    }

    public Branch51Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return Branch5Screen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        ItemDisplayButtonConfig[] branchButtons = {
                new ItemDisplayButtonConfig(Material.EMERALD, () -> {
                    ChangeScreen.switchTo(player, Branch5Screen.class, Branch51Screen.class);
                })
                        .setPosition(basePosition.clone())
        };

        for (ItemDisplayButtonConfig config : branchButtons) {
            createWidget(config);
        }
    }
}
