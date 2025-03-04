package me.Padej_.eventManagerDisplayUI;

import me.Padej_.eventManagerDisplayUI.command.CommandHandler;
import me.Padej_.eventManagerDisplayUI.ui.screen.winners.WinnersScreen;
import org.bukkit.plugin.java.JavaPlugin;


public final class EventManagerDisplayUI extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("em-ui").setExecutor(new CommandHandler());

        getServer().getPluginManager().registerEvents(new WinnersScreen(), this);
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static JavaPlugin getInstance() {
        return JavaPlugin.getPlugin(EventManagerDisplayUI.class);
    }
}
