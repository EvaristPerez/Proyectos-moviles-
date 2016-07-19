package com.opengl10.android.util;

/**
 * Created by mastermoviles on 7/1/16.
 */

import static android.opengl.Matrix.*;

public class TCamera {

    float[] mViewMatrix = new float[16];

    float[] eye = new float[3];
    float [] target = new float [3];
    float [] up = new float [3];

    float rX, rY;

    float distancia = 6;

    float minRy = -80, maxRy = 20;

    public void recalcularViewMatrix() {

        float [] vz= new float [3];

        for (int i=0; i<3; i++)
        {
            vz[i] = eye[i] - target[i];
        }

        vz = normalize(vz);

        float [] vx = normalize(crossProduct(up, vz));

        float [] vy = crossProduct(vz, vx);

        for (int i = 0; i<3; i++)
        {
            mViewMatrix[i] = vx [i];
        }

            mViewMatrix[3] = 0;

        for (int i = 4; i<7; i++)
        {
            mViewMatrix[i] = vy [i-4];
        }

        mViewMatrix[7] = 0;

        for (int i = 8; i<11; i++)
        {
            mViewMatrix[i] = vz [i-8];
        }

        mViewMatrix[11] = 0;

        for (int i = 12; i<15; i++)
        {
            mViewMatrix[i] = eye [i-12];
        }

        mViewMatrix[15] = 1;

        invertM(mViewMatrix, 0, mViewMatrix, 0);

    }

    private void calcularEyePoint()
    {
        float radiansRx = (float) Math.toRadians(rX);
        float radiansRy = (float) Math.toRadians(rY);

        eye [0] = (float) (distancia * -Math.sin(radiansRx) * Math.cos(radiansRy));
        eye [1] = (float) (distancia * -Math.sin(radiansRy));
        eye [2] = (float) (-distancia * Math.cos(radiansRx) * Math.cos(radiansRy));

        for(int i=0; i<3; i++)
        {
            eye[i] += target[i];
        }

        recalcularViewMatrix();

    }

    public float[] getViewMatrix()
    {

        return mViewMatrix;
    }

    public float[] getEye() {

        return eye;
    }

    public void setEye(float[] eye) {

        this.eye = eye;

        recalcularViewMatrix();
    }

    public float[] getTarget() {
        return target;
    }

    public void setTarget(float[] target) {
        this.target = target;

        calcularEyePoint();
        recalcularViewMatrix();

    }

    public float[] getUp() {
        return up;
    }

    public void setUp(float[] up) {
        this.up = up;

        recalcularViewMatrix();
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;

        calcularEyePoint();
    }

    public float getRX()
    {
        return rX;
    }

    public void setRX(float newRX)
    {
        rX = newRX;

       calcularEyePoint();

    }

    public float getRY()
    {
        return rY;
    }

    public void setRY(float newRY)
    {

        rY = Math.max(minRy, Math.min(newRY, maxRy));

        calcularEyePoint();
    }

    float[] crossProduct(float[] a, float[] b)
    {
        float [] r = new float[3];

        r[0] = a[1]*b[2]-a[2]*b[1];
        r[1] = a[2]*b[0]-a[0]*b[2];
        r[2] = a[0]*b[1]-a[1]*b[0];

        return r;
    }

    float [] normalize (float[] v)
    {
        float [] vNormalized = new float[3];

        float length = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

        if(length != 0){
            vNormalized [0] = v[0]/length;
            vNormalized  [1]= v[1]/length;
            vNormalized [2]= v[2]/length;
        }

        return vNormalized;
    }


}
