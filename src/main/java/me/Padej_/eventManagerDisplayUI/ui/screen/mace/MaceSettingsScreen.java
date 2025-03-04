package me.Padej_.eventManagerDisplayUI.ui.screen.mace;

import me.Padej_.eventManagerDisplayUI.color.Palette;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.padej.displayAPI.ui.widgets.TextDisplayButtonWidget;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MaceSettingsScreen extends EventManagerScreenTemplate {
    private static final List<UUID> anonymousPlayers = new ArrayList<>();
    public static final Set<UUID> spawnPlayers = new HashSet<>();
    private static final int ANONYMOUS_RADIUS = 100;

    private TextDisplayButtonWidget weatherWidget;

    public MaceSettingsScreen() {
        super();
    }

    public MaceSettingsScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public Class<? extends EventManagerScreenTemplate> getParentScreen() {
        return MaceMainScreen.class;
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition center = new WidgetPosition(0.115, 0.8f);
        float step = -0.145f;

        createEventerButton("Изменить кит", "Редактировать кит", () -> player.performCommand("kiteditor bulava"), center.clone(), 0.42);

        weatherWidget = createEventerButton(
                isStormy() ? "Солнце" : "Шторм",
                "Изменить погоду",
                () -> player.performCommand(isStormy() ? "weather sun" : "weather storm"),
                center.clone().addVertical(step),
                0.2
        );
        weatherWidget.setUpdateCallback(() -> {
            if (viewer != null) {
                weatherWidget.setText(
                        Palette.gradient(isStormy() ? "Солнце" : "Шторм", TextColor.color(0xe91396), TextColor.color(0xaf0e80)),
                        Palette.gradient(isStormy() ? "Солнце" : "Шторм", TextColor.color(0xe91396), TextColor.color(0xaf0e80))
                );
            }
        });

        // Кнопки анонимности в два столбца
        createEventerButton("+ anon", "Выдать анонимность", () -> giveAnonymousEffects(player), center.clone().addVertical(step * 2).addHorizontal(-0.25f), 0.21);
        createEventerButton("- anon", "Убрать анонимность", this::removeAnonymousEffects, center.clone().addVertical(step * 2).addHorizontal(0.25), 0.21);

        // Кнопки установки точки спавна в два столбца
        createEventerButton("Set spawn", "Установить точку возрождения", () -> setSpawn(player), center.clone().addVertical(step * 3), 0.21);
        createEventerButton("Reset spawn", "Сбросить точку возрождения", () -> resetSpawn(player), center.clone().addVertical(step * 4), 0.21);
    }

    private void giveAnonymousEffects(Player source) {
        Location sourceLocation = source.getLocation();
        source.getWorld().getNearbyPlayers(sourceLocation, ANONYMOUS_RADIUS).forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
            anonymousPlayers.add(player.getUniqueId());
        });
    }

    private void removeAnonymousEffects() {
        new ArrayList<>(anonymousPlayers).forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.removePotionEffect(PotionEffectType.GLOWING);
            }
            anonymousPlayers.remove(uuid);
        });
    }

    private void setSpawn(Player player) {
        Location spawnLocation = new Location(Bukkit.getWorld("world"), -407, 141, 6614);
        player.setBedSpawnLocation(spawnLocation, true);
        spawnPlayers.add(player.getUniqueId());
    }

    private void resetSpawn(Player player) {
        player.setBedSpawnLocation(new Location(Bukkit.getWorld("world"), 2000, 90, -2000), true);
        spawnPlayers.remove(player.getUniqueId());
    }

    private boolean isStormy() {
        World world = Bukkit.getWorld("world");
        return world != null && world.hasStorm();
    }
}
