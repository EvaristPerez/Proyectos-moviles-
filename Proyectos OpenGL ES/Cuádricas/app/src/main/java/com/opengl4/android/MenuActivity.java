package com.opengl4.android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


    }

    public void crearCuadrica(View v)
    {
        Intent intent = new Intent (MenuActivity.this, layoutParams.class);

        switch (v.getId())
        {
            case R.id.buttonEsfera:


                intent.putExtra("Cuadrica", 0);

                break;

            case R.id.buttonToroide:

                intent.putExtra("Cuadrica", 1);

                break;

            case R.id.buttonSuper:

                intent.putExtra("Cuadrica", 2);

                break;

        }

        startActivity(intent);
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
