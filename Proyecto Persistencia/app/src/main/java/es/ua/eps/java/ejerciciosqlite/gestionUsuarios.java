package es.ua.eps.java.ejerciciosqlite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class gestionUsuarios extends AppCompatActivity {

    Button actualizar, eliminar, nuevoUsuario, volver;
    Spinner lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        actualizar = (Button) findViewById(R.id.actualizar);
        eliminar = (Button) findViewById(R.id.eliminar);
        nuevoUsuario = (Button) findViewById(R.id.nuevoUsuario);
        volver = (Button) findViewById(R.id.volver);

        lista = (Spinner) findViewById(R.id.spinner);

        UsuariosSQLiteHelper usdbh = UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
        final SQLiteDatabase db = usdbh.getWritableDatabase();

        setSpinner(db);

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lista.getSelectedItem() !=null)
                {
                    Intent intent = new Intent (gestionUsuarios.this, editarUsuario.class);
                    intent.putExtra("Usuario", lista.getSelectedItem().toString());

                    startActivity(intent);
                    finish();
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lista.getSelectedItem() !=null)
                {
                    removeUser(db, lista.getSelectedItem().toString());
                }

            }
        });

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(gestionUsuarios.this, nuevoUsuario.class));
                finish();


            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(gestionUsuarios.this, MainActivity.class));
                finish();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        UsuariosSQLiteHelper usdbh = UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        setSpinner(db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gestion_usuarios, menu);
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

    public void setSpinner(SQLiteDatabase db)
    {
        ArrayList<String> arraySpinner = new ArrayList<String>() ;

        Cursor c = db.rawQuery("SELECT nombre_usuario FROM Usuarios",null);

        if(c.moveToFirst()) {

            do{

                arraySpinner.add(c.getString(0));

            }while(c.moveToNext());

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        lista.setAdapter(adapter);

    }

    public void removeUser(SQLiteDatabase db, String name)
    {
        db.execSQL("DELETE FROM Usuarios where nombre_usuario='"+name+"'");
        setSpinner(db);
    }
}
