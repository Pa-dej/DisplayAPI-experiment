package me.padej.displayAPI.render.shapes;

import me.padej.displayAPI.utils.AlignmentType;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public abstract class DefaultDisplay {
    private Display display;
    public Transformation emptyTransformation = new Transformation(
            new Vector3f(),
            new AxisAngle4f(),
            new Vector3f(),
            new AxisAngle4f()
    );

    public Display getDisplay() {
        return display;
    }

    public Transformation getTransformation() {
        return display != null ? display.getTransformation() : new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(), new AxisAngle4f());
    }

    public Location getLocation() {
        return display != null ? display.getLocation() : null;
    }

    public static Vector3f getOffset(AlignmentType type, float scale) {
        switch (type) {
            case TOP:
                return new Vector3f(-scale / 2, -scale, -scale / 2);
            case BOTTOM:
                return new Vector3f(-scale / 2, 0, -scale / 2);
            case CENTER:
            default:
                return new Vector3f(-scale / 2, -scale / 2, -scale / 2);
        }
    }
}
