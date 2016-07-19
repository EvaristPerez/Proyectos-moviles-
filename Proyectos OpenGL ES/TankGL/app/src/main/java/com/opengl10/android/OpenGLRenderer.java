package com.opengl10.android;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.opengl10.android.util.LoggerConfig;
import com.opengl10.android.util.ShaderHelper;
import com.opengl10.android.util.TCamera;
import com.opengl10.android.util.TObject;
import com.opengl10.android.util.TTank;
import com.opengl10.android.util.TTriangle;
import com.opengl10.android.util.TextResourceReader;


public class OpenGLRenderer implements Renderer {
	private static final String TAG = "OpenGLRenderer";
	
	// Para paralela
	//private static final float TAM = 1.0f;
	// Para perspectiva
	// private static final float TAM = 1.0f;

	TTank tank;
	TTriangle triangles;
	TObject floor, transparente;

	private final Context context;
	public int program1, program2;
	
	// Nombre de los uniform
	private static final String U_MVPMATRIX 		= "u_MVPMatrix";
	private static final String U_MVMATRIX 			= "u_MVMatrix";
	private static final String U_COLOR 			= "u_Color";
	private static final String U_TEXTURE 			= "u_TextureUnit";

	// Nombre de los attribute
	private static final String A_POSITION = "a_Position";
	private static final String A_NORMAL   = "a_Normal";
	private static final String A_UV       = "a_UV";

	// Handles para los shaders
	public int uMVPMatrixLocation;
	public int uMVMatrixLocation;
	public int uColorLocation;
	public int uTextureUnitLocation;
	public int aPositionLocation, aPositionLocation2;
	public int aNormalLocation;
	public int aUVLocation;

	private int aColorLocation;
	private static final String A_COLOR = "a_Color";

	float [] projectionXview = new float[16];


	// Matrices de proyeccion y de vista
	private final float[] projectionMatrix = new float[16];
	TCamera camera;


	void frustum(float[] m, int offset, float l, float r, float b, float t, float n, float f)
	{
		frustumM(m, offset, l, r, b, t, n, f);
		// Correccion del bug de Android
		m[8] /= 2;
	}
	
    void perspective(float[] m, int offset, float fovy, float aspect, float n, float f)
    {	final float d = f-n;
    	final float angleInRadians = (float) (fovy * Math.PI / 180.0);
    	final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));
        
    	m[0] = a/aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0;
        m[9] = 0;
        m[10] = (n - f) / d;
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -2*f*n/d;
        m[15] = 0f;

    }
	
	void perspective2(float[] m, int offset, float fovy, float aspect, float n, float f)
	{	float fH, fW;
		
		fH = (float) Math.tan( fovy / 360 * Math.PI ) * n;
		fW = fH * aspect;
		frustum(m, offset, -fW, fW, -fH, fH, n, f);
		
	}
	void frustum2(float[] m, int offset, float l, float r, float b, float t, float n, float f)
	{
		float d1 = r-l;
		float d2 = t-b;
		float d3 = f-n;

		m[0] = 2*n/d1;
		m[1] = 0f;
		m[2] = 0f;
		m[3] = 0f;
		
		m[4] = 0f;
		m[5] = 2*n/d2;
		m[6] = 0f;
		m[7] = 0f;
		
		m[8] = (r+l)/d1;
		m[9] = (t+b)/d2;
		m[10] = (n-f)/d3;
		m[11] = -1f;
		
		m[12] = 0f;
		m[13] = 0f;
		m[14] = -2*f*n/d3;
		m[15] = 0f;
	}
	
	public OpenGLRenderer(Context context) {

		//TODO Crear objetos

		this.context=context;

		camera = new TCamera();

		float [] targetVector = {0,0, 0};
		float [] upVector = {0,1, 0};

		camera.setTarget(targetVector);
		camera.setUp(upVector);

		camera.setRX(-22);
		camera.setRY(-35);

		triangles = new TTriangle();
		tank = new TTank(context, this);
		floor= new TObject(context, this, R.raw.suelo, R.drawable.floor);
		transparente = new TObject(context, this, R.raw.transparente, R.drawable.ruedastextura);

		floor.setRX(-90);
		transparente.setRX(-90);

	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		String vertexShaderSource;
		String fragmentShaderSource;
			
		glClearColor(0.3f, 0.7f, 0.9f, 0.0f);
		
		int[]	maxVertexTextureImageUnits = new int[1];
		int[]	maxTextureImageUnits       = new int[1];
			
		// Comprobamos si soporta texturas en el vertex shader
		glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, maxVertexTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w(TAG, "Max. Vertex Texture Image Units: "+maxVertexTextureImageUnits[0]);
		}
		// Comprobamos si soporta texturas (en el fragment shader)
		glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w(TAG, "Max. Texture Image Units: "+maxTextureImageUnits[0]);
		}

		// Leemos los shaders
		/*if (maxVertexTextureImageUnits[0]>0) {
			// Textura soportada en el vertex shader
			vertexShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.specular_vertex_shader);
			fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.specular_fragment_shader);
		} else {*/
			// Textura no soportada en el vertex shader
			vertexShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.specular_vertex_shader2);
			fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.specular_fragment_shader2);			
		//}
		
		// Compilamos los shaders
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		// Enlazamos el programa OpenGL
		program1 = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		// En depuracion validamos el programa OpenGL
		if (LoggerConfig.ON) {
			ShaderHelper.validateProgram(program1);
		}

		
		// Capturamos los uniforms
		uMVPMatrixLocation = glGetUniformLocation(program1, U_MVPMATRIX);
		uMVMatrixLocation = glGetUniformLocation(program1, U_MVMATRIX);
		uColorLocation = glGetUniformLocation(program1, U_COLOR);
		uTextureUnitLocation = glGetUniformLocation(program1, U_TEXTURE);
		
		// Capturamos los attributes
		aPositionLocation = glGetAttribLocation(program1, A_POSITION);
		glEnableVertexAttribArray(aPositionLocation);
		aNormalLocation = glGetAttribLocation(program1, A_NORMAL);
		glEnableVertexAttribArray(aNormalLocation);
		aUVLocation = glGetAttribLocation(program1, A_UV);
		glEnableVertexAttribArray(aUVLocation);

		// Leemos los shaders
		String vertexShaderSource2 = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader3);
		String fragmentShaderSource2 = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_fragment_shader3);

		// Compilamos los shaders
		int vertexShader2 = ShaderHelper.compileVertexShader(vertexShaderSource2);
		int fragmentShader2 = ShaderHelper.compileFragmentShader(fragmentShaderSource2);

		// Enlazamos el programa OpenGL
		program2 = ShaderHelper.linkProgram(vertexShader2, fragmentShader2);

		// En depuración validamos el programa OpenGL
		if (LoggerConfig.ON) {
			ShaderHelper.validateProgram(program2);
		}


		// Capturamos el uniform u_Color
		//uColorLocation = glGetUniformLocation(program, U_COLOR);
		aColorLocation = glGetAttribLocation(program2, A_COLOR);

		// Capturamos el attribute a_Position
		aPositionLocation2 = glGetAttribLocation(program2, A_POSITION);

		triangles.onSurfaceCreated(aPositionLocation2, aColorLocation);
		tank.onSurfaceCreated();

		floor.onSurfaceCreated();
		transparente.onSurfaceCreated();

	}
	
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Establecer el viewport de  OpenGL para ocupar toda la superficie.
		glViewport(0, 0, width, height);
		final float aspectRatio = width > height ?
				(float) width / (float) height :
				(float) height / (float) width;
		if (width > height) {
				// Landscape
				//orthoM(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, -100.0f, 100.0f);
				perspective(projectionMatrix, 0, 45f, aspectRatio, 0.01f, 1000f);
				//frustum(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, 1f, 1000.0f);
		} else {
				// Portrait or square
				//orthoM(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, -100.0f, 100.0f);
				perspective(projectionMatrix, 0, 45f, 1f/aspectRatio, 0.01f, 1000f);
				//frustum(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, 1f, 1000.0f);
		}

		multiplyMM(projectionXview, 0, projectionMatrix, 0, camera.getViewMatrix(), 0);

	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
			
		// Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		//glEnable(GL_CULL_FACE);

		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//glEnable(GL_DITHER);
		glLineWidth(2.0f);

		glUseProgram(program1);
		tank.render(projectionXview);
		floor.render(projectionXview);

		glEnable(GL_BLEND);

		transparente.render(projectionXview);

		// Activamos el programa OpenGL
		glUseProgram(program2);
		triangles.cargarBufferShader(aPositionLocation, aColorLocation);
		triangles.render();

		glDisable(GL_BLEND);



	}
	
	public void handleTouchPress(float normalizedX, float normalizedY) {
		if (LoggerConfig.ON) {

			if(normalizedX <= -0.8  && normalizedY <= -0.8 )
			{
				//Log.w("Pulsado", "TI");

				tank.rotateTorreta(10);
				transparente.setRZ(transparente.getRZ() + 10);

			}

			else if(normalizedX >= -0.1 && normalizedX <=0.1  && normalizedY <= -0.8 )
			{
				//Log.w("Pulsado", "R");

				tank.move(0.25f, transparente);
				camera.setTarget(tank.getPosition());

				multiplyMM(projectionXview, 0, projectionMatrix, 0, camera.getViewMatrix(), 0);


			}

			else if(normalizedX >= 0.8  && normalizedY <= -0.8 )
			{
				//Log.w("Pulsado", "TD");

				tank.rotateTorreta(-10);
				transparente.setRZ(transparente.getRZ() - 10);


			}

			else if(normalizedX <=-0.8  && normalizedY >= -0.1 && normalizedY <=0.1 )
			{
				//Log.w("Pulsado", "RI");

				tank.rotate(10);
				transparente.setRY(transparente.getRY() + 10);
				camera.setRX(camera.getRX()-10);

				multiplyMM(projectionXview, 0, projectionMatrix, 0, camera.getViewMatrix(), 0);


			}

			else if(normalizedX >=0.8  && normalizedY >= -0.1 && normalizedY <=0.1 )
			{
				//Log.w("Pulsado", "RD");

				tank.rotate(-10);
				transparente.setRY(transparente.getRY() - 10);

				camera.setRX(camera.getRX() + 10);

				multiplyMM(projectionXview, 0, projectionMatrix, 0, camera.getViewMatrix(), 0);


			}

			else if(normalizedX >= -0.1 && normalizedX <=0.1  && normalizedY >= 0.8 )
			{

				tank.move(-0.25f, transparente);
				camera.setTarget(tank.getPosition());

				multiplyMM(projectionXview, 0, projectionMatrix, 0, camera.getViewMatrix(), 0);


				//Log.w("Pulsado", "A");

			}

		}
	}
	
	public void handleTouchDrag(float normalizedX, float normalizedY) {
		if (LoggerConfig.ON) {
			//Log.w(TAG, "Touch Drag ["+normalizedX+", "+normalizedY+"]");
		}
		camera.setRX(camera.getRX() + (normalizedX*45f));
		camera.setRY(camera.getRY() + (-normalizedY * 45f));

		multiplyMM(projectionXview, 0, projectionMatrix, 0, camera.getViewMatrix(), 0);

	}

	public void setZoom(float factorS)
	{
		camera.setDistancia(factorS);

		multiplyMM(projectionXview, 0, projectionMatrix, 0, camera.getViewMatrix(), 0);


	}
}