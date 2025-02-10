package me.padej.displayAPI.utils;

import me.padej.displayAPI.DisplayAPI;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SchedulerUtil {

    private static final List<Runnable> closeableList = new ArrayList<>();
    private static final TickSchedule tickSchedule = new TickSchedule();

    // Метод для создания интервальной задачи
    public static Closeable interval(Plugin plugin, long delay, long period, Consumer<Closeable> task) {
        final Closeable[] closeable = new Closeable[1];
        final BukkitTask[] handler = new BukkitTask[1];

        closeable[0] = (Closeable) () -> {
            if (handler[0] != null) {
                handler[0].cancel();
            }
        };

        handler[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> task.accept(closeable[0]), delay, period);

        return closeable[0];
    }

    // Метод для выполнения задачи на каждом тике
    public static Closeable onTick(Consumer<Closeable> task) {
        return tickSchedule.schedule(TickSchedule.MAIN, task);
    }

    // Метод для выполнения задачи в конце каждого тика
    public static Closeable onTickEnd(Consumer<Closeable> task) {
        return tickSchedule.schedule(TickSchedule.END, task);
    }

    // Метод для добавления задачи, которая выполнится при отключении плагина
    public static void onDisablePlugin(Runnable task) {
        closeableList.add(task);
    }

    // Метод для выполнения всех задач при отключении плагина
    public static void closeCurrentPlugin() {
        closeableList.forEach(Runnable::run);
    }

    // Внутренний класс для управления задачами на тиках
    private static class TickSchedule {
        public static final List<Runnable> MAIN = new ArrayList<>();
        public static final List<Runnable> END = new ArrayList<>();

        // Метод для добавления задачи в список
        public Closeable schedule(List<Runnable> list, Consumer<Closeable> task) {
            final Closeable[] closeable = new Closeable[1];
            Runnable handler = () -> task.accept(closeable[0]);

            closeable[0] = (Closeable) () -> list.remove(handler);
            list.add(handler);

            return closeable[0];
        }

        // Инициализация интервальной задачи для выполнения задач на тиках
        static {
            interval(DisplayAPI.getInstance(), 0, 1, closeable -> {
                MAIN.forEach(Runnable::run);
                END.forEach(Runnable::run);
            });
        }
    }
}
