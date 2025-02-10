package me.padej.displayAPI.render;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class GroupEntityRenderer implements Closeable {

    private final Map<Object, Entity> rendered = new HashMap<>();
    private final Set<Object> used = new HashSet<>();

    public Map<Object, Entity> getRendered() {
        return rendered;
    }

    public void detachEntity(Object id) {
        rendered.remove(id);
    }

    @Override
    public void close() {
        for (Entity entity : rendered.values()) {
            entity.remove();
        }
        rendered.clear();
        used.clear();
    }

    public void render(RenderEntityGroup group) {
        for (Map.Entry<Object, RenderEntity<?>> entry : group.getItems().entrySet()) {
            renderPart(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Object, RenderEntity<?>> entry : group.getItems().entrySet()) {
            Entity entity = rendered.get(entry.getKey());
            if (entity != null) {
                preUpdate(entry.getValue(), entity);
            }
        }

        for (Map.Entry<Object, RenderEntity<?>> entry : group.getItems().entrySet()) {
            Entity entity = rendered.get(entry.getKey());
            if (entity != null) {
                update(entry.getValue(), entity);
            }
        }

        Set<Object> toRemove = new HashSet<>(rendered.keySet());
        toRemove.removeAll(used);

        for (Object key : toRemove) {
            Entity entity = rendered.get(key);
            if (entity != null) {
                entity.remove();
                rendered.remove(key);
            }
        }

        used.clear();
    }

    public <T extends Entity> void render(RenderEntity<T> part) {
        RenderEntityGroup group = new RenderEntityGroup();
        group.add(0, part);
        render(group);
    }

    private <T extends Entity> void renderPart(Object id, RenderEntity<T> template) {
        used.add(id);

        Entity oldEntity = rendered.get(id);
        if (oldEntity != null) {
            // Check if the entity is of the same type
            if (oldEntity.getType().getEntityClass() == template.getClazz()) {
                oldEntity.teleport(template.getLocation());
                return;
            }

            oldEntity.remove();
            rendered.remove(id);
        }

        Entity entity = spawnEntity(template.getLocation(), template.getClazz(), template.getInit());
        rendered.put(id, entity);
    }

    private <T extends Entity> void preUpdate(RenderEntity<T> renderEntity, Entity entity) {
        renderEntity.getPreUpdate().accept((T) entity);
    }

    private <T extends Entity> void update(RenderEntity<T> renderEntity, Entity entity) {
        renderEntity.getUpdate().accept((T) entity);
    }

    private <T extends Entity> Entity spawnEntity(Location location, Class<T> clazz, Consumer<T> initializer) {
        T entity = createEntity(location, clazz); // Замените на ваш метод создания сущности
        if (entity == null) {
            throw new IllegalStateException("Failed to create entity of type " + clazz.getSimpleName());
        }
        initializer.accept(entity);
        return entity;
    }

    private <T extends Entity> T createEntity(Location location, Class<T> clazz) {
        // Реализуйте логику создания сущности здесь
        // Например, используйте рефлексию или фабрику
        return null; // Заглушка
    }
}
