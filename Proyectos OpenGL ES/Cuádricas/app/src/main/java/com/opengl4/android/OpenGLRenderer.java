package com.opengl4.android;

import static android.opengl.GLES20.*;

import static android.opengl.Matrix.orthoM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.opengl4.android.util.LoggerConfig;
import com.opengl4.android.util.ShaderHelper;
import com.opengl4.android.util.TextResourceReader;
import com.opengl4.android.util.TextureHelper;


public class OpenGLRenderer implements Renderer {

	private static final int BYTES_PER_FLOAT = 4;

	FloatBuffer vertexData;

	private static boolean normales=false;

	// Para paralela
	private static final float TAM = 0.5f;
	// Para perspectiva
	// private static final float TAM = 1.0f;

	private final Context context;
	private int program;

	private static final String U_MVPMATRIX 		= "u_MVPMatrix";
	private static final String U_MVMATRIX 			= "u_MVMatrix";
	private static final String U_COLOR 			= "u_Color";
	private static final String U_TEXTURE 			= "u_TextureUnit";

	// Nombre de los attribute
	static final String A_POSITION = "a_Position";
	static final String A_NORMAL   = "a_Normal";
	static final String A_UV       = "a_UV";

	// Handles para los shaders
	int uMVPMatrixLocation;
	int uMVMatrixLocation;
	int uColorLocation;
	int uTextureUnitLocation;
	int aPositionLocation;
	int aNormalLocation;
	int aUVLocation;

	int	texture;

	// Matrices de proyeccion y de vista
	final float[] projectionMatrix = new float[16];
	final float[] modelMatrix = new float[16];
	final float[] MVP = new float[16];

	private int n=20, m=20;

	float[] tablaVertices;

	Quadrics q;

	public OpenGLRenderer(Context context, int type, float[] params) {

		q = new Quadrics();

		q.normales=normales;

		switch (type)
		{
			case 0:

				tablaVertices=q.Esfera(params[0], n, m);

				break;

			case 1:

				tablaVertices=q.Toroide(params[0], params[1], n, m);

				break;

			case 2:

				tablaVertices=q.SuperCuadratica(params[0], params[1], params[2], params[3], params[4], n, m);

				break;

		}


		vertexData = ByteBuffer
				.allocateDirect(tablaVertices.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		vertexData.put(tablaVertices);
		
		this.context = context;
	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		String vertexShaderSource;
		String fragmentShaderSource;

		int[]	maxVertexTextureImageUnits = new int[1];
		int[]	maxTextureImageUnits       = new int[1];

		// Comprobamos si soporta texturas en el vertex shader
		glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, maxVertexTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w("Renderer", "Max. Vertex Texture Image Units: " + maxVertexTextureImageUnits[0]);
		}
		// Comprobamos si soporta texturas (en el fragment shader)
		glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTextureImageUnits, 0);
		if (LoggerConfig.ON) {
			Log.w("Renderer", "Max. Texture Image Units: "+maxTextureImageUnits[0]);
		}
		// Cargamos la textura desde los recursos
		texture = TextureHelper.loadTexture(context, R.drawable.textura);

		// Leemos los shaders
		if (maxVertexTextureImageUnits[0]>0) {
			// Textura soportada en el vertex shader
			vertexShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.specular_vertex_shader_texture);
			fragmentShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.specular_fragment_shader_texture);
		} else {
			// Textura no soportada en el vertex shader
			vertexShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_vertex_shader);
			fragmentShaderSource = TextResourceReader
					.readTextFileFromResource(context, R.raw.simple_fragment_shader);
		}
		
		// Compilamos los shaders
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		// Enlazamos el programa OpenGL
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		// En depuración validamos el programa OpenGL
		if (LoggerConfig.ON) {
			ShaderHelper.validateProgram(program);
		}
		
		// Activamos el programa OpenGL
		glUseProgram(program);

		uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
		uMVMatrixLocation = glGetUniformLocation(program, U_MVMATRIX);
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE);

		// Capturamos los attributes
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		glEnableVertexAttribArray(aPositionLocation);
		aNormalLocation = glGetAttribLocation(program, A_NORMAL);
		glEnableVertexAttribArray(aNormalLocation);
		aUVLocation = glGetAttribLocation(program, A_UV);
		glEnableVertexAttribArray(aUVLocation);

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
			orthoM(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, -10.0f, 10.0f);
			//frustumM(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, 0.1f, 100.0f);
		} else {
			// Portrait or square
			orthoM(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, -10.0f, 10.0f);
			//frustumM(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, 0.1f, 100.0f);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {


		q.render(this);

	}
}