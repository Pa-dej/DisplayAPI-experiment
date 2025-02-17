package me.padej.displayAPI.ui.screens;

import me.padej.displayAPI.ui.Screen;
import me.padej.displayAPI.ui.annotations.ParentUI;
import me.padej.displayAPI.ui.widgets.WidgetConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@ParentUI(Branch12Screen.class)
public class Branch34Screen extends Screen {
    public Branch34Screen() {
        super(); // Используем конструктор для временных экранов
    }

    public Branch34Screen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends Screen> getParentScreen() {
        return Branch12Screen.class;
    }

    @Override
    protected void createScreenWidgets(Player player) {
        // Создаем кнопки подветвей
        WidgetPosition basePosition = new WidgetPosition(-0.42f, 0.3f);
        float step = 0.15f;

        WidgetConfig[] branchButtons = {
            new WidgetConfig(Material.DIAMOND, () -> {
                player.sendMessage("Ветка 3");
            })
            .setTooltip("Подветка 3")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone()),

            new WidgetConfig(Material.EMERALD, () -> {
                player.sendMessage("Ветка 4");
            })
            .setTooltip("Подветка 4")
            .setTooltipDelay(30)
            .setPosition(basePosition.clone().addVertical(step))
        };

        for (WidgetConfig config : branchButtons) {
            createWidget(config);
        }
    }
} 