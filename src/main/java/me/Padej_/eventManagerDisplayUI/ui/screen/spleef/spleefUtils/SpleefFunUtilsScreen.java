package me.Padej_.eventManagerDisplayUI.ui.screen.spleef.spleefUtils;

import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import me.padej.spleefutil.modules.Anonim;
import me.padej.spleefutil.modules.LowGravity;
import me.padej.spleefutil.modules.RandomEffect;
import me.padej.spleefutil.modules.RegenBlocks;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.Padej_.eventManagerDisplayUI.ui.template.ButtonStateUpdater;

public class SpleefFunUtilsScreen extends EventManagerScreenTemplate implements ButtonStateUpdater {
    private TextDisplayButtonWidget randomEffectButton;
    private TextDisplayButtonWidget lowGravityButton;
    private TextDisplayButtonWidget regenButton;

    public SpleefFunUtilsScreen() {
        super();
    }

    public SpleefFunUtilsScreen(Player viewer, Location location, String text, float scale) {
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

        randomEffectButton = createButtonWithState("Эффекты", "Случайные эффекты при разрушение блоков", RandomEffect.randomEffectEnabled, () -> {
            RandomEffect.randomEffectEnabled = !RandomEffect.randomEffectEnabled; // Переключение состояния
            updateButtonState(randomEffectButton, RandomEffect.randomEffectEnabled, "Эффекты");
        }, center.clone().addVertical(step));

        lowGravityButton = createButtonWithState("Гравитация", "Слабая гравитация", LowGravity.lowGravity, () -> {
            LowGravity.lowGravity = !LowGravity.lowGravity; // Переключение состояния
            updateButtonState(lowGravityButton, LowGravity.lowGravity, "Гравитация");
        }, center.clone().addVertical(step * 2));

        regenButton = createButtonWithState("Реген арены", "Арена восстанавливается", RegenBlocks.blocksRegen, () -> {
            RegenBlocks.blocksRegen = !RegenBlocks.blocksRegen; // Переключение состояния
            updateButtonState(regenButton, RegenBlocks.blocksRegen, "Реген арены");
        }, center.clone().addVertical(step * 3));
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
