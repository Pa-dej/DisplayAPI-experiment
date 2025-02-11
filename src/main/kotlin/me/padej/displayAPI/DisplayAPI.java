package me.padej.displayAPI;

import me.padej.displayAPI.render.shapes.Highlight;
import org.bukkit.plugin.java.JavaPlugin;

/*
TODO:
    Гизмо:
    - Если у игрока в руке спектральная стрела, то он видит простое белое гизмо
    Анимированный setBlock
 */
@SuppressWarnings("unused")
public class DisplayAPI extends JavaPlugin {

    @Override
    public void onEnable() {
//        getServer().getPluginManager().registerEvents(new TestEvent(), this);
//        getServer().getPluginManager().registerEvents(new TestEvent2(), this);
//        getServer().getPluginManager().registerEvents(new TestEvent3(), this);

        Highlight.removeAllSelections();
        Highlight.startColorUpdateTask();
    }

    public static JavaPlugin getInstance() {
        return JavaPlugin.getPlugin(DisplayAPI.class);
    }

}
