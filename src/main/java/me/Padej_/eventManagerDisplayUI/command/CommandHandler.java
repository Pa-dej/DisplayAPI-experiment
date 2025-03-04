package me.Padej_.eventManagerDisplayUI.command;

import me.Padej_.eventManagerDisplayUI.ui.EventManagerScreenTemplate;
import me.Padej_.eventManagerDisplayUI.ui.screen.EventManagerMainScreen;
import me.padej.displayAPI.DisplayAPI;
import me.padej.displayAPI.ui.UIManager;
import me.padej.displayAPI.utils.Animation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("em-ui")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("main")) {
                createUI((Player) sender, EventManagerMainScreen.class);
                return true;
            }
        }
        return false;
    }

    public static void createUI(Player player, Class<? extends EventManagerScreenTemplate> from) {
        EventManagerScreenTemplate currentScreen = (EventManagerScreenTemplate) UIManager.getInstance().getActiveScreen(player);
        if (currentScreen != null) {
            currentScreen.remove();
        }

        EventManagerScreenTemplate screen;
        try {
            screen = from.getConstructor(Player.class, Location.class, String.class, float.class)
                    .newInstance(
                            player,
                            player.getLocation().add(0, player.getHeight() / 2 + 0.2, 0).add(player.getLocation().getDirection().multiply(0.8)),
                            " ",
                            0.1f
                    );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Bukkit.getScheduler().runTaskLater(DisplayAPI.getInstance(), () -> {
            if (screen.getTextDisplay() == null) {
                return;
            }

            Animation.applyTransformationWithInterpolation(
                    screen.getTextDisplay(),
                    new Transformation(
                            new Vector3f(0, 0, 0),
                            new AxisAngle4f(),
                            new Vector3f(10, 4, 1),
                            new AxisAngle4f()
                    ),
                    5
            );

            screen.setOnClose(() -> UIManager.getInstance().unregisterScreen(player));
            screen.setupDefaultWidgets(player);
        }, 2);

        UIManager.getInstance().registerScreen(player, screen);
    }
}
