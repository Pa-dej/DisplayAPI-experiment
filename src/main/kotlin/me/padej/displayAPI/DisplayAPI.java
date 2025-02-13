package me.padej.displayAPI;

import me.padej.displayAPI.event.*;
import me.padej.displayAPI.render.particles.Particle;
import me.padej.displayAPI.render.shapes.Highlight;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/*
TODO:
    Сделать CharDisplay
    Баги:
    - Точка вращения у left / right Rotation находится не в позиции, которую задает translation, а в минимальном углу
    Гизмо:
    - Если у игрока в руке спектральная стрела, то он видит простое белое гизмо
    Анимированный setBlock
 */
@SuppressWarnings("unused")
public class DisplayAPI extends JavaPlugin {

    public static final List<Particle> particles = new ArrayList<>();

    @Override
    public void onEnable() {
//        getServer().getPluginManager().registerEvents(new TestEvent(), this);
//        getServer().getPluginManager().registerEvents(new TestEvent2(), this);
//        getServer().getPluginManager().registerEvents(new TestEvent3(), this);
//        getServer().getPluginManager().registerEvents(new TestEvent4(), this);
//        getServer().getPluginManager().registerEvents(new TestEvent5(), this);
//        getServer().getPluginManager().registerEvents(new TestEvent6(), this);

        Highlight.removeAllSelections();
        Highlight.startColorUpdateTask();
        startParticleTask();
    }

    public static JavaPlugin getInstance() {
        return JavaPlugin.getPlugin(DisplayAPI.class);
    }

    private void startParticleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (particles.isEmpty()) return;
                for (Particle particle : new ArrayList<>(particles)) {
                    particle.update();
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }
}
