package es.ua.eps.java.ejerciciosqlite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class nuevoUsuario extends AppCompatActivity {

    Button nuevoUsuario, volver;
    EditText nombreUsuario, pass, nombre, email;
    TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String nombreBD = sharedPreferences.getString("NombreFichero", "DBUsuarios");
        String versionBD = sharedPreferences.getString("VersionBD", "1");

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, nombreBD, null, Integer.parseInt(versionBD));
        final SQLiteDatabase db = usdbh.getWritableDatabase();


        nombreUsuario = (EditText) findViewById(R.id.editTextNombreUsuario);
        pass = (EditText) findViewById(R.id.editTextPass);
        nombre = (EditText) findViewById(R.id.editTextNombre);

        nuevoUsuario = (Button) findViewById(R.id.nuevoUsuario);
        volver = (Button) findViewById(R.id.volver);

        email = (EditText) findViewById(R.id.editTextEmail);

        textEmail = (TextView) findViewById(R.id.textView7);


        if(db.getVersion()==2)
        {
            textEmail.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
        }

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textNombreUsuario = nombreUsuario.getText().toString();
                String textPass = pass.getText().toString();
                String textNombreCompleto = nombre.getText().toString();
                String textEmailEdit = email.getText().toString();


                if (!(textNombreUsuario.matches("")) && !(textPass.matches("")) && !(textNombreCompleto.matches(""))) {

                    if(db.getVersion()==2)
                    {
                        if(!(textEmailEdit.matches("")))
                        {
                            db.execSQL("INSERT INTO Usuarios (nombre_usuario, password, nombre_completo, email) VALUES ('" + textNombreUsuario + "', '" + textPass + "', '" + textNombreCompleto + "', '"+textEmailEdit+"')");

                            Toast.makeText(nuevoUsuario.this, "Creado", Toast.LENGTH_SHORT).show();

                        }

                        else
                        {
                            Toast.makeText(nuevoUsuario.this, "Email sin rellenar", Toast.LENGTH_SHORT).show();

                        }
                    }

                    else
                    {
                        db.execSQL("INSERT INTO Usuarios (nombre_usuario, password, nombre_completo) VALUES ('" + textNombreUsuario + "', '" + textPass + "', '" + textNombreCompleto + "')");

                        Toast.makeText(nuevoUsuario.this, "Creado", Toast.LENGTH_SHORT).show();

                    }

                    Intent intent = new Intent (nuevoUsuario.this, pantallaAcceso.class);
                    intent.putExtra("Usuario", textNombreUsuario);
                    intent.putExtra("Nombre", textNombreCompleto);
                    intent.putExtra("Email", textEmailEdit);

                    if(db.getVersion()==1 || (db.getVersion()==2 && !(textEmailEdit.matches(""))))
                    {
                        startActivity(intent);
                        finish();
                    }

                }

                else
                {
                    Toast.makeText(nuevoUsuario.this, "Usuario/Contraseña/Nombre sin rellenar", Toast.LENGTH_SHORT).show();
                }

            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(nuevoUsuario.this, gestionUsuarios.class));
                finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nuevo_usuario, menu);
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
