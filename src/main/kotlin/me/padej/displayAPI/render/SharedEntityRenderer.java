package me.padej.displayAPI.render;

import org.bukkit.entity.Entity;

import java.util.Map;

import static me.padej.displayAPI.utils.SchedulerUtil.onDisablePlugin;
import static me.padej.displayAPI.utils.SchedulerUtil.onTickEnd;

public class SharedEntityRenderer {

    private static final GroupEntityRenderer renderer = new GroupEntityRenderer();
    private static RenderEntityGroup group = new RenderEntityGroup();

    public static void render(Object id, RenderEntityGroup group) {
        SharedEntityRenderer.group.add(id, group);
    }

    public static void render(Object id, RenderEntity<? extends Entity> entity) {
        SharedEntityRenderer.group.add(id, entity);
    }

    public static void flush() {
        renderer.render(group);
        group = new RenderEntityGroup();
    }

    public static Map<Object, Entity> getRendered() {
        return renderer.getRendered();
    }

    public static void detach(Object id) {
        renderer.detachEntity(id);
    }

    static {
        onTickEnd(closeable -> flush());  // Передаём Consumer<Closeable>

        onDisablePlugin(() -> renderer.close());  // Этот метод принимает Runnable, тут всё нормально
    }

}
