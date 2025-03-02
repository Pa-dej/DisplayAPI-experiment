package me.padej.displayAPI.ui.widgets;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Функциональный интерфейс для модификации ItemMeta
 */
@FunctionalInterface
public interface ItemMetaModifier {
    ItemMeta modify(ItemMeta meta);
}

