package me.padej.displayAPI.render.particles;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {
    public static final List<Particle> particles = new ArrayList<>();

    public static List<Particle> getParticles() {
        return particles;
    }

    public static void addParticle(Particle particle) {
        particles.add(particle);
    }
}
