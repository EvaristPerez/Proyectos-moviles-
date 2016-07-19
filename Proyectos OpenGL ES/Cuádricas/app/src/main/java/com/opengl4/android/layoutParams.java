package com.opengl4.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class layoutParams extends ActionBarActivity {

    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int type = getIntent().getExtras().getInt("Cuadrica", 0);
        final Intent intent = new Intent (layoutParams.this, OpenGLActivity.class);

        switch (type)
        {
            case 0:

                setContentView(R.layout.layout_sphere);

                button = (Button) findViewById(R.id.button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText radio = (EditText) findViewById(R.id.editTextSphere);

                        if(!(radio.getText().toString().matches("")))
                        {
                            intent.putExtra("Tipo", 0);
                            intent.putExtra("Radio", Float.parseFloat(radio.getText().toString()));

                            startActivity(intent);
                        }

                        else
                        {
                            Toast.makeText(layoutParams.this, "Radio vacío", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                break;

            case 1:

                setContentView(R.layout.layout_toroide);

                button = (Button) findViewById(R.id.button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText radio1 = (EditText) findViewById(R.id.editTextToro1);
                        EditText radio2 = (EditText) findViewById(R.id.editTextToro2);


                        if (!(radio1.getText().toString().matches("")) && !(radio2.getText().toString().matches(""))) {

                            intent.putExtra("Tipo", 1);

                            intent.putExtra("Radio1", Float.parseFloat(radio1.getText().toString()));
                            intent.putExtra("Radio2", Float.parseFloat(radio2.getText().toString()));

                            startActivity(intent);

                        }

                        else
                        {
                            Toast.makeText(layoutParams.this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                break;

            case 2:

                setContentView(R.layout.layout_super);

                button = (Button) findViewById(R.id.button);

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        EditText radio1 = (EditText) findViewById(R.id.editTextSuper1);
                        EditText radio2 = (EditText) findViewById(R.id.editTextSuper2);
                        EditText radio3 = (EditText) findViewById(R.id.editTextSuper3);

                        EditText s1 = (EditText) findViewById(R.id.editTextSuper4);
                        EditText s2 = (EditText) findViewById(R.id.editTextSuper5);


                        if (!(radio1.getText().toString().matches("")) && !(radio2.getText().toString().matches("")) && !(radio3.getText().toString().matches("")) && !(s1.getText().toString().matches("")) && !(s2.getText().toString().matches(""))) {

                            intent.putExtra("Tipo", 2);

                            intent.putExtra("Radio1", Float.parseFloat(radio1.getText().toString()));
                            intent.putExtra("Radio2", Float.parseFloat(radio2.getText().toString()));
                            intent.putExtra("Radio3", Float.parseFloat(radio3.getText().toString()));

                            intent.putExtra("S1", Float.parseFloat(s1.getText().toString()));
                            intent.putExtra("S2", Float.parseFloat(s2.getText().toString()));

                            startActivity(intent);

                        }

                        else
                        {
                            Toast.makeText(layoutParams.this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_layout_params, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
