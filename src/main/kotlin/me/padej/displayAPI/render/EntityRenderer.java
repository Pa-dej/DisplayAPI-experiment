package me.padej.displayAPI.render;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class EntityRenderer {

    public static RenderEntity<TextDisplay> textRenderEntity(
            Location location,
            Consumer<TextDisplay> init,
            Consumer<TextDisplay> preUpdate,
            Consumer<TextDisplay> update) {
        return new RenderEntity<>(
                TextDisplay.class,
                location,
                init::accept,
                preUpdate::accept,
                update::accept
        );
    }

    public static TextDisplay textRenderEntity(
            World world,
            Vector position,
            Consumer<TextDisplay> init,
            Consumer<TextDisplay> preUpdate,
            Consumer<TextDisplay> update) {
        return (TextDisplay) textRenderEntity(
                position.toLocation(world),
                init,
                preUpdate,
                update
        );
    }
}
