package me.Padej_.eventManagerDisplayUI.ui.screen;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.mace.MaceMainScreen;
import me.Padej_.eventManagerDisplayUI.ui.screen.spleef.SpleefScreen;
import me.Padej_.eventManagerDisplayUI.ui.screen.sumo.SumoScreen;
import me.Padej_.eventManagerDisplayUI.ui.screen.winners.WinnersScreen;
import me.padej.displayAPI.ui.annotations.Main;
import me.padej.displayAPI.ui.screens.ChangeScreen;
import me.padej.displayAPI.ui.widgets.ItemDisplayButtonConfig;
import me.padej.displayAPI.ui.widgets.WidgetPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.UUID;

@Main
public class EventManagerMainScreen extends EventManagerScreenTemplate {
    public EventManagerMainScreen() {
        super();
    }

    public EventManagerMainScreen(Player viewer, Location location, String text, float scale) {
        super(viewer, location, text, scale);
    }

    @Override
    public void createScreenWidgets(Player player) {
        WidgetPosition leftTopEdge = new WidgetPosition(-0.23f, 0.9f);
        float step = 0.155f;
        float depthOffset = -0.02f;

        createEventerButton("Mace", "",
                () -> ChangeScreen.switchTo(player, EventManagerMainScreen.class, MaceMainScreen.class),
                leftTopEdge.clone().addHorizontal(0.01f).addDepth(depthOffset));

        createWidget(new ItemDisplayButtonConfig(
                        Material.MACE, () -> {
                }
                )
                        .setScale(.1f, .1f, .1f)
                        .setTranslation(new Vector3f(0.2f, 0, 0))
                        .setPosition(leftTopEdge.clone().addVertical(.1f / 8).addDepth(depthOffset))
                        .setTolerance(0)
        );

        createEventerButton("Sumo", "",
                () -> ChangeScreen.switchTo(player, EventManagerMainScreen.class, SumoScreen.class),
                leftTopEdge.clone().addVertical(-step).addHorizontal(0.01f).addDepth(depthOffset));

        createWidget(new ItemDisplayButtonConfig(
                        Material.BAMBOO, () -> {
                }
                )
                        .setScale(.1f, .1f, .1f)
                        .setTranslation(new Vector3f(0.2f, 0, 0))
                        .setPosition(leftTopEdge.clone().addVertical(-step).addVertical(.1f / 8).addDepth(depthOffset))
                        .setTolerance(0)
        );

        createEventerButton("Spleef", "",
                () -> ChangeScreen.switchTo(player, EventManagerMainScreen.class, SpleefScreen.class),
                leftTopEdge.clone().addVertical(-step * 2).addHorizontal(0.06f).addDepth(depthOffset));

        createWidget(new ItemDisplayButtonConfig(
                        Material.IRON_SHOVEL, () -> {
                }
                )
                        .setScale(.1f, .1f, .1f)
                        .setTranslation(new Vector3f(0.2f, 0, 0))
                        .setPosition(leftTopEdge.clone().addVertical(-step * 2).addVertical(.1f / 8).addDepth(depthOffset))
                        .setTolerance(0)
        );

        createEventerButton("Winners", "",
                () -> ChangeScreen.switchTo(player, EventManagerMainScreen.class, WinnersScreen.class),
                leftTopEdge.clone().addVertical(-step * 4.5 - 0.1).addHorizontal(0.1f).addDepth(depthOffset));

        createWidget(new ItemDisplayButtonConfig(
                        Material.PLAYER_HEAD, () -> {
                }
                )
                        .setScale(.1f, .1f, .015f)
                        .setTranslation(new Vector3f(0.2f, 0, 0))
                        .setPosition(leftTopEdge.clone().addVertical(-step * 4.5 - 0.1).addVertical(.1f / 8).addDepth(depthOffset))
                        .setTolerance(0)
                        .setItemMeta((meta) -> {
                            SkullMeta skullMeta = (SkullMeta) meta;
                            PlayerProfile profile = Bukkit.createProfile(new UUID(-639232153L, -1560524484L), "");
                            profile.setProperties(Collections.singletonList(
                                    new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQzMDhhZTI3YjU4YjY5NjQ1NDk3ZjlkYTg2NTk3ZWRhOTQ3ZWFjZDEwYzI5ZTNkNGJiZjNiYzc2Y2ViMWVhYiJ9fX0=")
                            ));
                            skullMeta.setPlayerProfile(profile);
                            return meta;
                        })
                        .setDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI)
        );
    }
}
