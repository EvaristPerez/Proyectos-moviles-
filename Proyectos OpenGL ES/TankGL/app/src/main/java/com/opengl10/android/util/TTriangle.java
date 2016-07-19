package com.opengl10.android.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;


/**
 * Created by mastermoviles on 22/12/15.
 */
public class TTriangle {

    private FloatBuffer vertexData;
    private static final int BYTES_PER_FLOAT = 4;

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 4;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    float[] tablaVertices = {
            // Abanico de triángulos, x, y, R, G, B, A
            -0.1f, 0.8f, 0.0f, 0.0f, 1.0f, 0.5f,
            0.1f, 0.8f, 0.0f, 0.0f, 1.0f, 0.5f,
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.5f,

            -0.8f, 0.1f, 0.0f, 0.0f, 1.0f, 0.5f,
            -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.5f,
            -0.8f,-0.1f, 0.0f, 0.0f, 1.0f, 0.5f,

            1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.5f,
            0.8f, 0.1f, 0.0f, 0.0f, 1.0f,0.5f,
            0.8f,-0.1f, 0.0f, 0.0f, 1.0f,0.5f,

            0.1f, -0.8f, 0.0f, 0.0f, 1.0f,0.5f,
            -0.1f, -0.8f, 0.0f, 0.0f, 1.0f,0.5f,
            0.0f, -1.0f, 0.0f, 0.0f, 1.0f,0.5f,

            -1.0f, -0.9f, 0.0f, 1.0f, 0.0f,0.5f,
            -0.8f, -1f, 0.0f, 1.0f, 0.0f,0.5f,
            -0.8f,-0.8f, 0.0f, 1.0f, 0.0f,0.5f,

            1.0f, -0.9f, 0.0f, 1.0f, 0.0f,0.5f,
            0.8f, -0.8f, 0.0f, 1.0f, 0.0f,0.5f,
            0.8f, -1f, 0.0f, 1.0f, 0.0f,0.5f,


    };

    public TTriangle()
    {
        vertexData = ByteBuffer
                .allocateDirect(tablaVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tablaVertices);


    }

    public void onSurfaceCreated(int aPositionLocation, int aColorLocation) {

        glEnableVertexAttribArray(aPositionLocation);

        cargarBufferShader(aPositionLocation, aColorLocation);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    }

    public void cargarBufferShader(int aPositionLocation, int aColorLocation)
    {
        glEnableVertexAttribArray(aPositionLocation);

        // Asociando vértices con su attribute
        vertexData.position(0);

        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        // Habilitamos el vector de vértices
        glEnableVertexAttribArray(aPositionLocation);

        // Asociamos el vector de colores
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }


    public void render()
    {
        glDrawArrays(GL_TRIANGLES, 0, tablaVertices.length/6);

    }
}
