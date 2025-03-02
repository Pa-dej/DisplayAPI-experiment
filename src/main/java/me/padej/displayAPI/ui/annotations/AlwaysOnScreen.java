package me.padej.displayAPI.ui.annotations;

import me.padej.displayAPI.ui.WidgetManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AlwaysOnScreen {
    Class<? extends WidgetManager> value();
} 