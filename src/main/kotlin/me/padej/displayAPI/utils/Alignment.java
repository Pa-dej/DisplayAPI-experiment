package me.padej.displayAPI.utils;

import org.joml.Vector3f;

public abstract class Alignment {
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
