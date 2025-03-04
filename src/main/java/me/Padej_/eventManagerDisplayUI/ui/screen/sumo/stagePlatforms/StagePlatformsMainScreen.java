package me.Padej_.eventManagerDisplayUI.ui.screen.sumo.stagePlatforms;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.sumo.SumoScreen;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/*
Clear  Reload
Classic Star
Halloween Ice
StarP Xmas
Lotus Pink
 */
public class StagePlatformsMainScreen  extends EventManagerScreenTemplate {

    public StagePlatformsMainScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    public StagePlatformsMainScreen() {
        super();
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return SumoScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.75f);
        float verticalStep = -0.145f;
        float horizontalOffset = 0.3f;

        // Первая строка
        createModerButton("Clear", "Очистить платформу",
                () -> player.performCommand("stp clear"),
                center.clone().addHorizontal(-horizontalOffset));

        createModerButton("Reload", "Перезагрузить платформу",
                () -> player.performCommand("stp reload"),
                center.clone().addHorizontal(horizontalOffset));

        // Вторая строка
        createEventerButton("Classic", "Классическая платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, ClassicPlatformScreen.class),
                center.clone().addVertical(verticalStep).addHorizontal(-horizontalOffset));

        createEventerButton("Star", "Звездная платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, StarPlatformScreen.class),
                center.clone().addVertical(verticalStep).addHorizontal(horizontalOffset));

        // Третья строка
        createEventerButton("Halloween", "Хэллоуин платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, HalloweenPlatformScreen.class),
                center.clone().addVertical(verticalStep * 2).addHorizontal(-horizontalOffset));

        createEventerButton("Ice", "Ледяная платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, IcePlatformScreen.class),
                center.clone().addVertical(verticalStep * 2).addHorizontal(horizontalOffset));

        // Четвертая строка
        createEventerButton("StarP", "Star Premium платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, StarPPlatformScreen.class),
                center.clone().addVertical(verticalStep * 3).addHorizontal(-horizontalOffset));

        createEventerButton("Xmas", "Рождественская платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, XmasPlatformScreen.class),
                center.clone().addVertical(verticalStep * 3).addHorizontal(horizontalOffset));

        // Пятая строка
        createEventerButton("Lotus", "Лотос платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, LotusPlatformScreen.class),
                center.clone().addVertical(verticalStep * 4).addHorizontal(-horizontalOffset));

        createEventerButton("Pink", "Розовая платформа",
                () -> ChangeScreen.switchTo(player, StagePlatformsMainScreen.class, PinkPlatformScreen.class),
                center.clone().addVertical(verticalStep * 4).addHorizontal(horizontalOffset));
    }
}
