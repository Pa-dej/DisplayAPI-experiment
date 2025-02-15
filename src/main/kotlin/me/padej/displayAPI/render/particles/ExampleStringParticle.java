package me.padej.displayAPI.render.particles;

import me.padej.displayAPI.render.shapes.DefaultSquare;
import me.padej.displayAPI.render.shapes.StringRectangle;
import me.padej.displayAPI.utils.Animation;
import me.padej.displayAPI.utils.ColorUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Random;

import static me.padej.displayAPI.DisplayAPI.particles;

public class ExampleStringParticle implements Particle {
    private int age;
    private static final Random random = new Random();
    private static final int MIN_LIFE = 200;
    private static final int MAX_LIFE = 300;

    private static final double GRAVITY_ACCELERATION = 0.001;
    private static final double AIR_DRAG_COEFFICIENT = 0.0015;
    private static final double SPEED_DAMPENING = 0.04;
    private static final double SWAY_AMPLITUDE = 0.005;

    private static final double MIN_SCALE = 0.5;

    private final Vector velocity;
    private final Location position;
    private final Location source;
    private final DefaultSquare square;
    private final int maxAge;
    private boolean isRapidSpeed;  // Переменная для отслеживания первых 10 тиков

    public ExampleStringParticle(Player player, Location spawnLocation) {
        this.age = 0;
        this.source = spawnLocation.clone();
        this.position = spawnLocation.clone();
        this.maxAge = random.nextInt(MAX_LIFE - MIN_LIFE + 1) + MIN_LIFE;
        this.isRapidSpeed = true;  // Устанавливаем начальную скорость высокой

        this.velocity = getInitialVelocity(player); // Используем скорость с разбросом
        this.square = new StringRectangle(
                0, // Исправленная строка
                Color.BLACK, // background color
                0, // background alpha
                Display.Billboard.CENTER,
                false,
                getRandomColoredChar()
        ) {};
        square.spawn(spawnLocation);

        square.getTextDisplay().setBrightness(new Display.Brightness(15, 15));
    }

    // Метод для вычисления начальной скорости с разбросом
    private Vector getInitialVelocity(Player player) {
        Vector direction = player.getEyeLocation().getDirection().normalize(); // Вектор взгляда игрока

        // Разброс: конусный угол (30 градусов в радианах)
        double spread = Math.toRadians(30);
        double randomYaw = (random.nextDouble() - 0.5) * spread; // Отклонение по горизонтали
        double randomPitch = (random.nextDouble() - 0.5) * spread; // Отклонение по вертикали

        // Создаем случайное вращение вектора взгляда
        Vector spreadVector = direction.clone().rotateAroundY(randomYaw).rotateAroundX(randomPitch);

        // Увеличенная начальная скорость (для первых 10 тиков)
        double speed = isRapidSpeed ? 0.25 + (0.2 - 0.1) * random.nextDouble() : 0.005 + (0.015 - 0.005) * random.nextDouble();
        return spreadVector.multiply(speed);
    }

    private String getRandomChar() {
        String[] chars = {"❤", "\uD83D\uDD25", "★", "☠", "█", "☯", "☀", "☽", "♦", "☂", "\uD83C\uDF0A", "♪", "♬", "☁", "⛏", "☄", "■", "\uD83E\uDDEA", "\uD83C\uDF56", "☃"};
        return chars[random.nextInt(chars.length)];
    }

    private String getValentineChar() {
        String[] chars = {
                "§l🥰", "§l😍", "§l😘", "§l💌", "§l💘",
                "💝", "💖", "§l💗", "§l💓", "§l💞",
                "§l💕", "§l💟", "❣", "❤", "§l🧡",
                "§l💛", "§l💚", "§l💙", "§l🩵", "§l💜",
                "§l🤎", "§l🖤", "§l🩶", "§l🤍", "❀",
                "✿", "✦", "♥", "♡", "✯"};
        return chars[random.nextInt(chars.length)];
    }

    private String getRandomColoredChar() {
        return ChatColor.of(ColorUtil.getRandomValentineRGBColor()) + getValentineChar();
    }

    @Override
    public void update() {
        age++;
        if (age > maxAge) {
            square.removeEntity();
            particles.remove(this);
            return;
        }

        // Первые 10 тиков с высокой скоростью
        if (isRapidSpeed && age > 1) {
            isRapidSpeed = false;
        }

        if (age >= 10 && age <= 30) {
            velocity.multiply(0.9);
        }

        if (age == 5) {
            float scale = (float) (random.nextDouble() + MIN_SCALE);
            Animation.applyTransformationWithInterpolation(square.getTextDisplay(), new Transformation(
                    new Vector3f(),
                    new AxisAngle4f(),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f()
            ), 5);
        }

        if (age == maxAge - 12) {
            Animation.applyTransformationWithInterpolation(square.getTextDisplay(), new Transformation(
                    new Vector3f(),
                    new AxisAngle4f(),
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f()
            ), 5);
        }

        if (!position.clone().subtract(0, 0.1, 0).getBlock().getType().isSolid()) {
            velocity.setY(velocity.getY() - GRAVITY_ACCELERATION);
        } else {
            velocity.setX(0).setY(0).setZ(0);
            return;
        }

        double swayX = SWAY_AMPLITUDE * (random.nextDouble() * 2 - 1);
        double swayZ = SWAY_AMPLITUDE * (random.nextDouble() * 2 - 1);
        velocity.setX(velocity.getX() + swayX);
        velocity.setZ(velocity.getZ() + swayZ);

        velocity.multiply(1 - AIR_DRAG_COEFFICIENT);  // Замедление из-за сопротивления воздуха

        Location oldPosition = position.clone();
        position.add(velocity);

        if (position.getBlock().getType().isSolid()) {
            velocity.multiply(0.3);
        }

        if (oldPosition.getY() > source.getY() && position.getY() <= source.getY()) {
            position.setY(source.getY());
            velocity.setX(velocity.getX() * SPEED_DAMPENING);
            velocity.setZ(velocity.getZ() * SPEED_DAMPENING);
        }

        square.getTextDisplay().teleport(position);
    }
}

