package com.opengl4.android;

import java.util.LinkedList;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by mastermoviles on 17/11/15.
 */


public class Quadrics
{

    static int numVertices=0;

    static boolean normales =true;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int UV_COMPONENT_COUNT = 2;
    // Calculo del tamanyo de los datos (3+3+2 = 8 floats)
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT + UV_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    public class Vertice
    {
        float pX, pY, pZ;
        float nX, nY, nZ;
        float u,v;

        public Vertice()
        {
            pX=pY=pZ=0;
            nX=nY=nZ=0;
            u=v=0;

        }

        private int sgn(float number)
        {
            return number<0?-1:1;
        }

        private float interp_lineal(float ini, float fin, float t)
        {
            return ini*(1.0f-t)+fin*t;
        }

        public Vertice crearVerticeEsfera(int n, int m, float r, float x, float y, float z, int i, int j)
        {
            Vertice v = new Vertice();
            float angulo_ini_m=(float)-Math.PI/2.0f, angulo_fin_m=(float)Math.PI/2.0f;
            float angulo_ini_n=(float)-Math.PI, angulo_fin_n=(float)Math.PI;

            float radians_act_m= interp_lineal (angulo_ini_m, angulo_fin_m, j/(m*1.0f));
            float radians_act_n= interp_lineal (angulo_ini_n, angulo_fin_n, i/(n*1.0f));

            v.pX = (float) (r*Math.cos(radians_act_n)*Math.cos(radians_act_m)+x);
            v.pY = (float) (r*Math.cos(radians_act_n)*Math.sin(radians_act_m)+y);
            v.pZ = (float) (r*Math.sin(radians_act_n)+z);

            float modulo_normal=0;
            float[] punto_final={v.pX, v.pY, v.pZ};
            float[] vector_normal=new float[3];
            float[] punto_origen={x, y, z};

            for(int k=0; k<3; k++)
            {
                vector_normal[k] = punto_final[k] - punto_origen[k];
                modulo_normal+=(vector_normal[k]*vector_normal[k]);
            }

            modulo_normal=(float)Math.sqrt(modulo_normal);

            for(int k=0; k<3; k++)
            {
                vector_normal[k] /= modulo_normal;

            }

            v.nX=vector_normal[0];
            v.nY=vector_normal[1];
            v.nZ=vector_normal[2];

            v.u=i*1.0f/n;
            v.v=j*1.0f/m;

            return v;
        }

        public Vertice crearVerticeToro(int n, int m, float r1, float r2, float x, float y, float z, int i, int j)
        {
            Vertice v = new Vertice();

            float twopi= (float) (2.0f*Math.PI);

            v.pX = (float) ((r1 + r2 * Math.cos(i * twopi / m)) * Math.cos(j * twopi / n))+x;
            v.pY = (float) ((r1 + r2 * Math.cos(i * twopi / m)) * Math.sin(j * twopi / n))+y;
            v.pZ = (float) (r2 * Math.sin(i * twopi / n))+z;

            float modulo_normal=0;
            float[] punto_final={v.pX, v.pY, v.pZ};
            float[] vector_normal=new float[3];
            float[] punto_origen={(float) (x+r1*Math.cos(j*twopi/n)),(float) (y+r1*Math.sin(j*twopi/n)), z};

            for(int k=0; k<3; k++)
            {
                vector_normal[k] = punto_final[k] - punto_origen[k];
                modulo_normal+=(vector_normal[k]*vector_normal[k]);
            }

            modulo_normal=(float)Math.sqrt(modulo_normal);

            for(int k=0; k<3; k++)
            {
                vector_normal[k] /= modulo_normal;

            }

            v.nX=vector_normal[0];
            v.nY=vector_normal[1];
            v.nZ=vector_normal[2];

            v.u=i*1.0f/n;
            v.v=j*1.0f/m;

            return v;
        }

        public Vertice crearVerticesSuperCuadratica(int n, int m, float r1, float r2, float r3, float s1, float s2, float x, float y, float z, int i, int j)
        {
            Vertice v = new Vertice();
            float angulo_ini_m=(float)0.f, angulo_fin_m=(float)Math.PI*2;
            float angulo_ini_n=(float)0.f, angulo_fin_n=(float)Math.PI;

            float radians_act_m= interp_lineal (angulo_ini_m, angulo_fin_m, j/(m*1.0f));
            float radians_act_n= interp_lineal (angulo_ini_n, angulo_fin_n, i/(n*1.0f));

            float cos_v_dv = (float) Math.cos(radians_act_n);
            float cos_u_du = (float) Math.cos(radians_act_m);

            float sin_v_dv = (float) Math.sin(radians_act_n);
            float sin_u_du = (float) Math.sin(radians_act_m);

            v.pX= (float) (r1*sgn(cos_v_dv)*sgn(sin_u_du)*Math.pow(Math.abs(cos_v_dv),s1)*Math.pow(Math.abs(sin_u_du),s2));
            v.pY= (float) (r2*sgn(sin_v_dv)*sgn(sin_u_du)*Math.pow(Math.abs(sin_v_dv), s1)*Math.pow(Math.abs(sin_u_du),s2));
            v.pZ= (float) (r3*sgn(cos_u_du)*Math.pow(Math.abs(cos_u_du), s2));

            float dx_du= (float) (r1*sgn(cos_v_dv)*cos_u_du*Math.pow(Math.abs(cos_v_dv), s1)*Math.pow(Math.abs(sin_u_du), s1 - 1));
            float dy_du= (float) (r2*sgn(sin_v_dv)*cos_u_du*Math.pow(Math.abs(sin_v_dv), s1)*Math.pow(Math.abs(sin_u_du), s1 - 1));
            float dz_du= (float) (-r3*sin_u_du*Math.pow(Math.abs(cos_u_du), s1 - 1));

            float dx_dv= (float) (-r1*sgn(sin_u_du)*Math.pow(Math.abs(sin_u_du), s2)*sin_v_dv*Math.pow(Math.abs(cos_v_dv), s2 - 1));
            float dy_dv= (float) (r2*sgn(sin_u_du)*Math.pow(Math.abs(sin_u_du), s2)*cos_v_dv*Math.pow(Math.abs(sin_v_dv), s2 - 1));

            float[] vector_normal=new float[3];

            vector_normal[0] = -dz_du*dy_dv;
            vector_normal[1] = dx_dv*dz_du;
            vector_normal[2] = dx_du*dy_dv-dx_dv*dy_du;

            float modulo_normal=0;

            for(int k=0; k<3; k++)
            {
                modulo_normal+=(vector_normal[k]*vector_normal[k]);
            }

            modulo_normal=(float)Math.sqrt(modulo_normal);

            for(int k=0; k<3; k++)
            {
                vector_normal[k] /= modulo_normal;

            }

            v.nX=vector_normal[0];
            v.nY=vector_normal[1];
            v.nZ=vector_normal[2];

            v.u=i*1.0f/n;
            v.v=j*1.0f/m;

            return v;
        }


    }


    public float[] Toroide(float r1, float r2, int n, int m)
    {
        return Toroide(r1, r2, n, m, 0, 0, 0);
    }

    public float[] Toroide(float r1, float r2, int n, int m, float orig_x, float orig_y, float orig_z)
    {
        numVertices = 0;

        if (n < 3) n = 3;
        if (m < 3) m = 3;

        LinkedList<Vertice> listaVertice = new LinkedList<Vertice>();
        Vertice actual = new Vertice();


        float twopi = (float) (2.f * Math.PI);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j <= n; j++) {

                if(normales)
                {
                    AddNormal(listaVertice, actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j));
                    AddNormal(listaVertice, actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j+1));
                    AddNormal(listaVertice, actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i+1, j+1));

                    AddNormal(listaVertice, actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j));
                    AddNormal(listaVertice, actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i+1, j+1));
                    AddNormal(listaVertice, actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i+1, j));
                }

                else
                {
                    listaVertice.add(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j));
                    listaVertice.add(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j + 1));
                    listaVertice.add(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i + 1, j + 1));


                    listaVertice.add(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i, j));
                    listaVertice.add(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i+1, j+1));
                    listaVertice.add(actual.crearVerticeToro(n, m, r1, r2, orig_x, orig_y, orig_z, i + 1, j));
                }

            }
        }

        float[] array_vertices = new float[(numVertices = listaVertice.size()) * 8];

        int strike = 0;

        for (int i = 0; i < listaVertice.size(); i++) {
            actual = listaVertice.get(i);

            array_vertices[strike] = actual.pX;
            array_vertices[strike + 1] = actual.pY;
            array_vertices[strike + 2] = actual.pZ;
            array_vertices[strike + 3] = actual.nX;
            array_vertices[strike + 4] = actual.nY;
            array_vertices[strike + 5] = actual.nZ;
            array_vertices[strike + 6] = actual.u;
            array_vertices[strike + 7] = actual.v;

            strike += 8;
        }


        return array_vertices;
    }

    public float[] Esfera(float r, int n, int m)
    {
        return Esfera(r, n, m, 0, 0, 0);
    }

    public void AddNormal(LinkedList<Vertice> v, Vertice vertex)
    {
        v.add(vertex);
        Vertice normal=new Vertice();
        normal.pX=vertex.pX+vertex.nX*0.06f;
        normal.pY=vertex.pY+vertex.nY*0.06f;
        normal.pZ=vertex.pZ+vertex.nZ*0.06f;


        v.add(normal);
    }

    public float[] Esfera(float r, int n, int m, float orig_x, float orig_y, float orig_z)
    {
        numVertices=0;

        if(n<3) n=3;
        if(m<3) m=3;

        LinkedList <Vertice> listaVertice = new LinkedList<Vertice>();
        Vertice actual=new Vertice();

        for(int i=0; i<=m; i++)
        {
            for(int j=0; j<=n; j++)
            {
                if(j!=n)
                {

                    if(normales)
                    {
                        AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j));
                        AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j + 1));
                        AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j + 1));
                    }else
                    {
                        listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i, j));
                        listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i, j+1));
                        listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i+1, j+1));
                    }


                    if(j!=0)
                    {
                        if(normales)
                        {
                            AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j));
                            AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j + 1));
                            AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j));
                        }else
                        {
                            listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i, j));
                            listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i+1, j+1));
                            listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i+1, j));
                        }

                    }

                } else {

                    if(normales) {
                        AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j));
                        AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i, j + 1));
                        AddNormal(listaVertice, actual.crearVerticeEsfera(n, m, r, orig_x, orig_y, orig_z, i + 1, j));
                    }else
                    {
                        listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i, j));
                        listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i, j+1));
                        listaVertice.add(actual.crearVerticeEsfera(n,m,r, orig_x, orig_y, orig_z, i+1, j));

                    }

                }
            }
        }



        float[] array_vertices = new float[(numVertices=listaVertice.size())*8];

        int strike=0;

        for (int i=0; i<listaVertice.size(); i++) {
            actual = listaVertice.get(i);

            array_vertices[strike] = actual.pX;
            array_vertices[strike + 1] = actual.pY;
            array_vertices[strike + 2] = actual.pZ;
            array_vertices[strike + 3] = actual.nX;
            array_vertices[strike + 4] = actual.nY;
            array_vertices[strike + 5] = actual.nZ;
            array_vertices[strike + 6] = actual.u;
            array_vertices[strike + 7] = actual.v;

            strike +=8;
        }



        return array_vertices;
    }

    public float[] SuperCuadratica(float r1, float r2, float r3, float s1, float s2, int n, int m)
    {
        return SuperCuadratica(r1, r2, r3, s1, s2, n, m,0,0,0);
    }

    public float[] SuperCuadratica(float r1, float r2, float r3, float s1, float s2, int n, int m, float orig_x, float orig_y, float orig_z)
    {
        numVertices=0;

        if(n<3) n=3;
        if(m<3) m=3;

        LinkedList <Vertice> listaVertice = new LinkedList<Vertice>();
        Vertice actual=new Vertice();

        for(int i=0; i<=m; i++)
        {
            for(int j=0; j<=n; j++)
            {
                if(j!=n)
                {

                    if(normales)
                    {
                        AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                        AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j + 1));
                        AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j + 1));
                    }else
                    {
                        listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                        listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j + 1));
                        listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j + 1));
                    }


                    if(j!=0)
                    {
                        if(normales)
                        {
                            AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                            AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j + 1));
                            AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j));
                        }else
                        {
                            listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                            listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j + 1));
                            listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i+1, j));
                        }

                    }

                } else {

                    if(normales) {
                        AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                        AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j + 1));
                        AddNormal(listaVertice, actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i + 1, j));
                    }else
                    {
                        listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j));
                        listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i, j + 1));
                        listaVertice.add(actual.crearVerticesSuperCuadratica(n, m, r1, r2, r3, s1, s2, orig_x, orig_y, orig_z, i+1, j));

                    }

                }
            }
        }



        float[] array_vertices = new float[(numVertices=listaVertice.size())*8];

        int strike=0;

        for (int i=0; i<listaVertice.size(); i++) {
            actual = listaVertice.get(i);

            array_vertices[strike] = actual.pX;
            array_vertices[strike + 1] = actual.pY;
            array_vertices[strike + 2] = actual.pZ;
            array_vertices[strike + 3] = actual.nX;
            array_vertices[strike + 4] = actual.nY;
            array_vertices[strike + 5] = actual.nZ;
            array_vertices[strike + 6] = actual.u;
            array_vertices[strike + 7] = actual.v;

            strike += 8;
        }



        return array_vertices;
    }

    public void render(OpenGLRenderer renderer)
    {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        glLineWidth(2.0f);

        // Creamos la matriz del modelo
        setIdentityM(renderer.modelMatrix, 0);

        // Rotacion alrededor del eje x e y
        rotateM(renderer.modelMatrix, 0, 1.0f, 0f, 1f, 0f);
        rotateM(renderer.modelMatrix, 0, 0.5f, 1f, 0f, 0f);

        multiplyMM(renderer.MVP, 0, renderer.projectionMatrix, 0, renderer.modelMatrix, 0);
        System.arraycopy(renderer.MVP, 0, renderer.projectionMatrix, 0, renderer.MVP.length);

        // Envia la matriz de proyeccion multiplicada por modelMatrix al shader
        glUniformMatrix4fv(renderer.uMVPMatrixLocation, 1, false, renderer.MVP, 0);
        // Envia la matriz modelMatrix al shader
        glUniformMatrix4fv(renderer.uMVMatrixLocation, 1, false, renderer.modelMatrix, 0);
        // Actualizamos el color (Marron)
        //glUniform4f(uColorLocation, 0.78f, 0.49f, 0.12f, 1.0f);
        glUniform4f(renderer.uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);

        // Pasamos la textura
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, renderer.texture);
        glUniform1f(renderer.uTextureUnitLocation, 0);

        // Dibujamos el objeto

        // Asociando vertices con su attribute
        renderer.vertexData.position(0);
        glVertexAttribPointer(renderer.aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, renderer.vertexData);

        // Asociamos el vector de normales
        renderer.vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(renderer.aNormalLocation, NORMAL_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, renderer.vertexData);

        // Asociamos el vector de UVs
        renderer.vertexData.position(POSITION_COMPONENT_COUNT+NORMAL_COMPONENT_COUNT);
        glVertexAttribPointer(renderer.aUVLocation, NORMAL_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, renderer.vertexData);
        glDrawArrays(GL_TRIANGLES, 0, numVertices);

    }
}
