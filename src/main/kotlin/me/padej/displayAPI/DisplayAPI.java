package me.padej.displayAPI;

import me.padej.displayAPI.render.particles.Particle;
import me.padej.displayAPI.render.shapes.Highlight;
import me.padej.displayAPI.test_events.GizmoTest;
import me.padej.displayAPI.test_events.PointDetectFirstTest;
import me.padej.displayAPI.test_events.PointDetectSecondTest;
import me.padej.displayAPI.test_events.SmoothMotionAndRotationTest;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/*
TODO:
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
        getServer().getPluginManager().registerEvents(new SmoothMotionAndRotationTest(), this);
//        getServer().getPluginManager().registerEvents(new PointDetectFirstTest(), this);
        getServer().getPluginManager().registerEvents(new PointDetectSecondTest(), this);
        getServer().getPluginManager().registerEvents(new GizmoTest(), this);

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
