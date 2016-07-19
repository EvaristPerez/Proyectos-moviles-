package com.opengl10.android.util;

import android.content.Context;

import com.opengl10.android.OpenGLRenderer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by mastermoviles on 21/12/15.
 */
public class TObject {

    Resource3DSReader obj3DS;
    int texture;
    OpenGLRenderer rendererGL;
    Context context;

    private final float[] modelMatrix = new float[16];
    private final float[] MVP = new float[16];


    // Rotacion alrededor de los ejes
    private float rX = 0f;
    private float rY = 0f;
    private float rZ = 0f;

    private float [] position = new float[3];

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int UV_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + UV_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    public TObject(Context context, OpenGLRenderer renderer, int resource3DS, int textureLoad)
    {
        // Lee un archivo 3DS desde un recurso
        obj3DS = new Resource3DSReader();
        obj3DS.read3DSFromResource(context, resource3DS);

        texture = textureLoad;
        rendererGL = renderer;
        this.context=context;

    }



    public void onSurfaceCreated()
    {
        texture = TextureHelper.loadTexture(context,texture);
    }

    public void render(float[] projectionXView)
    {
        multiplyMM(MVP, 0, projectionXView, 0, modelMatrix, 0);

        // Envia la matriz de proyeccion multiplicada por modelMatrix al shader
        glUniformMatrix4fv(rendererGL.uMVPMatrixLocation, 1, false, MVP, 0);
        // Envia la matriz modelMatrix al shader
        glUniformMatrix4fv(rendererGL.uMVMatrixLocation, 1, false, modelMatrix, 0);
        // Actualizamos el color (Marron)
        //glUniform4f(uColorLocation, 0.78f, 0.49f, 0.12f, 1.0f);
        glUniform4f(rendererGL.uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);

        // Pasamos la textura
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        glUniform1f(rendererGL.uTextureUnitLocation, 0);

        // Dibujamos el objeto
        for (int i=0; i<obj3DS.numMeshes; i++) {
            // Asociando vertices con su attribute
            cargarBufferShader(obj3DS, i);
            glDrawArrays(GL_TRIANGLES, 0, obj3DS.numVertices[i]);
        }
    }

    public void cargarBufferShader(Resource3DSReader obj3DS, int i)
    {
        obj3DS.dataBuffer[i].position(0);
        glVertexAttribPointer(rendererGL.aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, obj3DS.dataBuffer[i]);

        // Asociamos el vector de normales
        obj3DS.dataBuffer[i].position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(rendererGL.aNormalLocation, NORMAL_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, obj3DS.dataBuffer[i]);

        // Asociamos el vector de UVs
        obj3DS.dataBuffer[i].position(POSITION_COMPONENT_COUNT+NORMAL_COMPONENT_COUNT);
        glVertexAttribPointer(rendererGL.aUVLocation, NORMAL_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, obj3DS.dataBuffer[i]);
    }

    public void recalculate()
    {
        /*setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, position[0], position[1], position[2]);
        rotateM(modelMatrix, 0, rY, 0f, 1f, 0f);
        rotateM(modelMatrix, 0, rX, 1f, 0f, 0f);*/

        float [] rotationMatrix = new float[16];
        setIdentityM(rotationMatrix, 0);
        rotateM(rotationMatrix, 0, rY, 0f, 1f, 0f);
        rotateM(rotationMatrix, 0, rX, 1f, 0f, 0f);
        rotateM(rotationMatrix, 0, rZ, 0f, 0f, 1f);


        float [] translationMatrix = new float[16];
        setIdentityM(translationMatrix, 0);
        translateM(translationMatrix, 0, position[0], position[1], position[2]);

        multiplyMM(modelMatrix, 0, translationMatrix, 0, rotationMatrix, 0);

    }

    public float getRX()
    {
        return rX;
    }

    public void setRX(float newRX)
    {
        rX = newRX;

        recalculate();
    }

    public float getRY()
    {
        return rY;
    }

    public void setRY(float newRY)
    {
        rY = newRY;

        recalculate();
    }

    public float getRZ()
    {
        return rZ;
    }

    public void setRZ(float newRZ)
    {
        rZ = newRZ;

        recalculate();
    }

    public void setPosition(float [] positionObject)
    {
        position = positionObject;

        recalculate();
    }

    public float[] getPosition()
    {
        return position;
    }

    public void move(float desX, float desZ)
    {
        position[0] +=desZ;
        position[2] +=desX;

        recalculate();
    }

}
