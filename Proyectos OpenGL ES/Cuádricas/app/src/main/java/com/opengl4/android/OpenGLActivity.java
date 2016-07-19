package com.opengl4.android;

import com.opengl4.android.R;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class OpenGLActivity extends ActionBarActivity {
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int type = getIntent().getExtras().getInt("Tipo");
        float[] params = new float[0];

        switch (type)
        {
            case 0:

                params = new float[] {getIntent().getExtras().getFloat("Radio")};

                break;

            case 1:

                params = new float[] {getIntent().getExtras().getFloat("Radio1"), getIntent().getExtras().getFloat("Radio2")};

                break;

            case 2:

                params = new float[] {getIntent().getExtras().getFloat("Radio1"), getIntent().getExtras().getFloat("Radio2"), getIntent().getExtras().getFloat("Radio3"), getIntent().getExtras().getFloat("S1"), getIntent().getExtras().getFloat("S2")};

                break;
        }

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_open_gl1);
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        final ActivityManager activityManager =
        		(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
        		activityManager.getDeviceConfigurationInfo();
        //final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        final boolean supportsEs2 =
        		configurationInfo.reqGlEsVersion >= 0x20000
        		|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
        		&& (Build.FINGERPRINT.startsWith("generic")
        		|| Build.FINGERPRINT.startsWith("unknown")
        		|| Build.MODEL.contains("google_sdk")
        		|| Build.MODEL.contains("Emulator")
        		|| Build.MODEL.contains("Android SDK built for x86")));
        if (supportsEs2) {
        	// Request an OpenGL ES 2.0 compatible context.
        	glSurfaceView.setEGLContextClientVersion(2);
        	// Para que funcione en el emulador
        	glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        	// Assign our renderer.
        	glSurfaceView.setRenderer(new OpenGLRenderer(this,type, params));
        	rendererSet = true;
        	Toast.makeText(this, "OpenGL ES 2.0 supported",
        			Toast.LENGTH_LONG).show();
        } else {
        	Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
        			Toast.LENGTH_LONG).show();
        	return;
        }
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	if (rendererSet) {
    		glSurfaceView.onPause();
    	}
    }
    		
    @Override
    protected void onResume() {
    	super.onResume();
    	if (rendererSet) {
    		glSurfaceView.onResume();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open_gl1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
   
}
