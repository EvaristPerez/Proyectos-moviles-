precision mediump float;			// Precisi�n media

varying vec4 v_Color;				// in: color recibido desde el vertex shader

void main()
{
	gl_FragColor = v_Color;
}