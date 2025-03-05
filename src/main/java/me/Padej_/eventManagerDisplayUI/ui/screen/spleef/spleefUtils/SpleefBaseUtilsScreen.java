package me.Padej_.eventManagerDisplayUI.ui.screen.spleef.spleefUtils;

import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import me.padej.spleefutil.modules.*;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.Padej_.eventManagerDisplayUI.ui.template.ButtonStateUpdater;

public class SpleefBaseUtilsScreen extends EventManagerScreenTemplate implements ButtonStateUpdater {
    private TextDisplayButtonWidget anonymousButton;
    private TextDisplayButtonWidget snowballsButton;
    private TextDisplayButtonWidget destructionButton;
    private TextDisplayButtonWidget pearlsButton;
    private TextDisplayButtonWidget eggsButton;
    private TextDisplayButtonWidget dashButton;

    public SpleefBaseUtilsScreen() {
        super();
    }

    public SpleefBaseUtilsScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return SpleefUtilsScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.8f);
        float step = -0.145f;

        anonymousButton = createButtonWithState("Аноним", "Включить анонимность", Anonim.anonymousEnabled, () -> {
            Anonim.anonymousEnabled = !Anonim.anonymousEnabled; // Переключение состояния
            updateButtonState(anonymousButton, Anonim.anonymousEnabled, "Аноним");
        }, center.clone());

        snowballsButton = createButtonWithState("Снежки", "Включить снежки", DropSnowballs.dropSnowballsEnabled, () -> {
            DropSnowballs.dropSnowballsEnabled = !DropSnowballs.dropSnowballsEnabled; // Переключение состояния
            updateButtonState(snowballsButton, DropSnowballs.dropSnowballsEnabled, "Снежки");
        }, center.clone().addVertical(step));

        destructionButton = createButtonWithState("Разрушение", "Включить разрушение", DestroySnowballsOnHit.destroySnowOnHitEnabled, () -> {
            DestroySnowballsOnHit.destroySnowOnHitEnabled = !DestroySnowballsOnHit.destroySnowOnHitEnabled; // Переключение состояния
            updateButtonState(destructionButton, DestroySnowballsOnHit.destroySnowOnHitEnabled, "Разрушение");
        }, center.clone().addVertical(step * 2));

        pearlsButton = createButtonWithState("Перлы", "Включить перлы", Pearl.enderPearlEnabled, () -> {
            Pearl.enderPearlEnabled = !Pearl.enderPearlEnabled; // Переключение состояния
            updateButtonState(pearlsButton, Pearl.enderPearlEnabled, "Перлы");
        }, center.clone().addVertical(step * 3));

        eggsButton = createButtonWithState("Яйца", "Включить яйца", PowderSnowTrap.powderSnowTrapEnabled, () -> {
            PowderSnowTrap.powderSnowTrapEnabled = !PowderSnowTrap.powderSnowTrapEnabled; // Переключение состояния
            updateButtonState(eggsButton, PowderSnowTrap.powderSnowTrapEnabled, "Яйца");
        }, center.clone().addVertical(step * 4));

        dashButton = createButtonWithState("Рывок", "Включить рывок", FeatherDash.dashEnabled, () -> {
            FeatherDash.dashEnabled = !FeatherDash.dashEnabled; // Переключение состояния
            updateButtonState(dashButton, FeatherDash.dashEnabled, "Рывок");
        }, center.clone().addVertical(step * 5));
    }

    private TextDisplayButtonWidget createButtonWithState(String text, String tooltip, boolean isEnabled, Runnable action, WidgetPosition position) {
        if (isEnabled) {
            return createEventerButton(text, tooltip, action, position);
        } else {
            return createDisabledButton(text, tooltip, action, position);
        }
    }

    @Override
    public void updateButtonState(TextDisplayButtonWidget button, boolean isEnabled, String text) {
        if (isEnabled) {
            button.setText(
                    Palette.gradient(text, TextColor.color(0xe91396), TextColor.color(0xaf0e80)),
                    Palette.gradient(text, TextColor.color(0xe91396), TextColor.color(0xaf0e80))
            );
        } else {
            button.setText(
                    Palette.gradient(text, TextColor.color(0x6d6d6d), TextColor.color(0x494949)),
                    Palette.gradient(text, TextColor.color(0x6d6d6d), TextColor.color(0x494949))
            );
        }
    }
}
