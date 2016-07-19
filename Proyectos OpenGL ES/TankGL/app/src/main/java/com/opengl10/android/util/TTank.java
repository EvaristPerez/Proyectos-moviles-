package com.opengl10.android.util;

import android.content.Context;
import android.util.Log;

import com.opengl10.android.OpenGLRenderer;
import com.opengl10.android.R;

/**
 * Created by mastermoviles on 7/1/16.
 */
public class TTank {


    float rotacion=0;
    float positionX=0, positionY=0, positionZ=0;
    int frames = 4, frame = 0;


    TObject torreta, rueda, base;
    TObject [] cinta = new TObject[frames];

    public TTank(Context context, OpenGLRenderer openGLRenderer) {

        base = new TObject(context, openGLRenderer, R.raw.base, R.drawable.basetextura);
        torreta = new TObject(context, openGLRenderer, R.raw.torreta, R.drawable.torretatextura);
        rueda = new TObject(context, openGLRenderer, R.raw.ruedas, R.drawable.ruedastextura);



        cinta [0] = new TObject(context, openGLRenderer, R.raw.cinta1, R.drawable.cintatextura);
        cinta [1] = new TObject(context, openGLRenderer, R.raw.cinta2, R.drawable.cintatextura);
        cinta [2] = new TObject(context, openGLRenderer, R.raw.cinta3, R.drawable.cintatextura);
        cinta [3] = new TObject(context, openGLRenderer, R.raw.cinta4, R.drawable.cintatextura);

        base.setRX(-90);
        torreta.setRX(-90);
        rueda.setRX(-90);


        for (int i=0; i<frames; i++)
        {
            cinta[i].setRX(-90);

        }

    }

    public void onSurfaceCreated() {

        base.onSurfaceCreated();
        torreta.onSurfaceCreated();
        rueda.onSurfaceCreated();

        for (int i=0; i<frames; i++)
        {
            cinta[i].onSurfaceCreated();

        }


    }

    public void render(float[] projectionXView) {

        base.render(projectionXView);
        torreta.render(projectionXView);
        rueda.render(projectionXView);

        cinta[frame].render(projectionXView);
    }

    public void rotate(float degrees)
    {
        rotacion+=degrees;

        base.setRY(base.getRY() + degrees);
        torreta.setRY(torreta.getRY() + degrees);
        rueda.setRY(rueda.getRY() + degrees);

        for (int i=0; i<frames; i++)
        {
            cinta[i].setRY(cinta[i].getRY() + degrees);
        }

    }


    public void rotateTorreta(float degrees)
    {
        torreta.setRZ(torreta.getRZ() + degrees);
    }

    public void move(float des, TObject transparente)
    {

        float desX =(float) (des*Math.sin(Math.toRadians(rotacion)));
        float desZ = (float) (des*Math.cos(Math.toRadians(rotacion)));

        positionX+=desX;
        positionZ+=desZ;

        base.move(desX, desZ);
        torreta.move(desX, desZ);
        rueda.move(desX, desZ);
        transparente.move(desX, desZ);

        for (int i=0; i<frames; i++)
        {
            cinta[i].move(desX, desZ);
        }

        if(des<0)
        {
            if(frame<frames-1)
            {
                frame++;
            }

            else
            {
                frame=0;
            }
        }

        else
        {
            if(frame>0)
            {
                frame--;
            }

            else
            {
                frame=frames-1;
            }
        }

    }

    public float[] getPosition()
    {
        return base.getPosition();
    }


}

