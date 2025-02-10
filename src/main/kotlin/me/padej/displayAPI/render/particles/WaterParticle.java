package me.padej.displayAPI.render.particles;

import me.padej.displayAPI.render.SharedEntityRenderer;
import me.padej.displayAPI.utils.DisplayExtensions;
import me.padej.displayAPI.utils.Math;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Quaterniond;

import java.util.Random;
import java.util.function.Consumer;

import static me.padej.displayAPI.constants.TextDisplayConstant.getTextBackgroundTransform;
import static me.padej.displayAPI.render.EntityRenderer.textRenderEntity;
import static me.padej.displayAPI.render.particles.ParticleManager.particles;
import static me.padej.displayAPI.utils.Math.FORWARD_VECTOR;

public class WaterParticle implements Particle {

    private final World world;
    private final Vector source;
    private final Vector position;
    private final double minSize;
    private final double maxSize;
    private final double minSpeed;
    private final double maxSpeed;
    private final Color color;

    private double gravityAcceleration = 0.08;
    private double airDragCoefficient = 0.06;

    private int age = 0;
    private final int maxAge;
    private final Vector velocity;

    private final float rotSpeed;
    private final float size;

    public WaterParticle(World world, Vector source, Vector position, double minSize, double maxSize,
                         double minSpeed, double maxSpeed, double maxUpAngle, int upAngleBias) {
        this.world = world;
        this.source = source;
        this.position = position;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;

        this.color = Color.fromRGB(105, 156, 201);

        this.maxAge = new Random().nextInt(10) + 15; // Random range [15,25]

        this.velocity = Math.rotate(FORWARD_VECTOR.multiply(new Random().nextDouble(minSpeed, maxSpeed + 0.000001)), new Quaterniond()
                .rotateY(position.clone().subtract(source).getY())
                .rotateX(-maxUpAngle * (1 - java.lang.Math.pow(new Random().nextDouble(), upAngleBias))));

        this.rotSpeed = (float) new Random().nextDouble(-0.2, 0.2);
        this.size = (float) new Random().nextDouble(minSize, maxSize + 0.000001);
    }

    @Override
    public void update() {
        age++;
        if (age > maxAge) {
            particles.remove(this);
            return;
        }

        // Ускорение под действием гравитации
        velocity.setY(velocity.getY() - gravityAcceleration);

        // Замедление в воде
        if (position.getY() < source.getY() && velocity.getY() < 0) {
            velocity.multiply(0.2);
            age += 3;
        }

        // Сопротивление воздуха
        velocity.multiply(1 - airDragCoefficient);

        // Движение
        Vector oldPosition = position.clone();
        position.add(velocity);

        boolean justEnteredWater = oldPosition.getY() > source.getY() && position.getY() <= source.getY();
        if (justEnteredWater && new Random().nextBoolean()) {
            // Отскок
            position.setY(source.getY());
            velocity.setY(-velocity.getY() * 0.5);
            velocity.setX(velocity.getX() * 0.3);
            velocity.setZ(velocity.getZ() * 0.3);
        }

        // Отрисовка
        SharedEntityRenderer.render(this, textRenderEntity(
                (Location) world, (Consumer<TextDisplay>) position,
                entity -> {
                    entity.setText(" ");
                    entity.setBillboard(Display.Billboard.CENTER);
                    entity.setInterpolationDuration(1);
                    entity.setTeleportDuration(1);
                },
                entity -> {
                    Matrix4f transform = new Matrix4f()
                            .rotateZ(age * rotSpeed)
                            .scale(size)
                            .translate(-0.5f, -0.5f, 0f)
                            .mul(getTextBackgroundTransform());

                    DisplayExtensions.interpolateTransform(entity, transform);
                    entity.setBackgroundColor(color.setAlpha((int) (color.getAlpha() * (1 - (double) age / maxAge))));
                }
        ));
    }
}
