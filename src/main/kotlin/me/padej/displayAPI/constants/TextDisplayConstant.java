package me.padej.displayAPI.constants;

import org.joml.Matrix4f;

public class TextDisplayConstant {
//    val textBackgroundTransform: Matrix4f; get() = Matrix4f()
//    .translate(-0.1f + .5f,-0.5f + .5f,0f)
//    .scale(8.0f,4.0f,1f)
    public static Matrix4f getTextBackgroundTransform() {
        return new Matrix4f()
                .translate(-0.1f + .5f,-0.5f + .5f,0f)
                .scale(8.0f,4.0f,1f);
    }

}
