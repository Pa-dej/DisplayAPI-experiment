package me.Padej_.eventManagerDisplayUI.ui.screen.spleef;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.util.FillRegion;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SpleefArenaScreen extends EventManagerScreenTemplate {
    public SpleefArenaScreen() {
        super();
    }

    public SpleefArenaScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return SpleefMainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.8f);
        float step = 0.155f;

        createEventerButton("Очистить", "Очистить арену",
                this::fillAir, center.clone().addVertical(-step * 0));

        createEventerButton("Снег", "Заполнить арену снегом",
                this::fillSnowBlock, center.clone().addVertical(-step * 1).addHorizontal(-0.25f));

        createEventerButton("Бедрок", "Заполнить арену бедроком",
                this::fillBedrock, center.clone().addVertical(-step * 1).addHorizontal(0.25f));

        createEventerButton("Режим кирки вкл", "Включить режим кирки",
                () -> applyPickaxeMod(player), center.clone().addVertical(-step * 2));

        createEventerButton("Режим кирки выкл", "Выключить режим кирки",
                () -> disablePickaxeMod(player), center.clone().addVertical(-step * 3));
    }

    void applyPickaxeMod(Player player) {
        player.performCommand("brush s 0 5");
        player.performCommand("mask snow_block");
    }

    void disablePickaxeMod(Player player) {
        player.performCommand("unbind");
    }

    void fillBedrock() {
        FillRegion.fillRegion(833, 87, -687, 902, 87, -618, Material.BEDROCK);
    }

    void fillSnowBlock() {
        FillRegion.fillRegion(833, 87, -687, 902, 87, -618, Material.SNOW_BLOCK);
    }

    void fillAir() {
        FillRegion.fillRegion(833, 87, -687, 902, 87, -618, Material.AIR);
    }
}
