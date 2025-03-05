package me.Padej_.eventManagerDisplayUI.ui.template;

import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;

public interface ButtonStateUpdater {
    void updateButtonState(TextDisplayButtonWidget button, boolean isEnabled, String text);
} 