package me.Padej_.eventManagerDisplayUI.ui.screen.spleef.spleefUtils;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.spleef.SpleefMainScreen;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpleefUtilsScreen extends EventManagerScreenTemplate {

    public SpleefUtilsScreen() {
        super();
    }

    public SpleefUtilsScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return SpleefMainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.8f);
        float step = -0.145f;

        createDefaultButton("Основные", "Утилиты для основного ивента",
                () -> ChangeScreen.switchTo(player, SpleefUtilsScreen.class, SpleefBaseUtilsScreen.class),
                center.clone().addVertical(step), 0.44);

        createDefaultButton("Фановые", "Утилиты для основного ивента",
                () -> ChangeScreen.switchTo(player, SpleefUtilsScreen.class, SpleefFunUtilsScreen.class),
                center.clone().addVertical(step * 2), 0.44);
    }
}
