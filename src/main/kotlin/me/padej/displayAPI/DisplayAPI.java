package me.padej.displayAPI;

import me.padej.displayAPI.render.particles.Particle;
import me.padej.displayAPI.render.shapes.Highlight;
import me.padej.displayAPI.test_events.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DisplayAPI extends JavaPlugin {

    public static final List<Particle> particles = new ArrayList<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ApplyHighlightToBlockTest(), this);
        getServer().getPluginManager().registerEvents(new CreateDisplayParticleFirstTest(), this);
        getServer().getPluginManager().registerEvents(new CreateDisplayParticleSecondTest(), this);
        getServer().getPluginManager().registerEvents(new CreateDisplayParticleThirdTest(), this);
        getServer().getPluginManager().registerEvents(new CreateTestUI(), this);
        getServer().getPluginManager().registerEvents(new GizmoTest(), this);
        getServer().getPluginManager().registerEvents(new PointDetectFirstTest(), this);
        getServer().getPluginManager().registerEvents(new PointDetectSecondTest(), this);
        getServer().getPluginManager().registerEvents(new RotationRelativeToCenterPointTest(), this);
        getServer().getPluginManager().registerEvents(new SmoothMotionAndRotationTest(), this);

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
