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

public class editarUsuario extends AppCompatActivity {

    EditText nombreUsuario, pass, nombre, email;
    Button actualizar, volver;
    TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        nombreUsuario = (EditText) findViewById(R.id.editTextNombreUsuario);
        pass = (EditText) findViewById(R.id.editTextPass);
        nombre = (EditText) findViewById(R.id.editTextNombre);
        email = (EditText) findViewById(R.id.editTextEmail);

        textEmail = (TextView) findViewById(R.id.textView7);

        actualizar = (Button) findViewById(R.id.actualizar);
        volver = (Button) findViewById(R.id.volver);

        final String user = getIntent().getExtras().getString("Usuario");

        UsuariosSQLiteHelper usdbh = UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this);
        final SQLiteDatabase db = usdbh.getWritableDatabase();

        if(db.getVersion()==2)
        {
            textEmail.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
        }

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textNombreUsuario = nombreUsuario.getText().toString();
                String textPass = pass.getText().toString();
                String textNombre = nombre.getText().toString();
                String textEmailEdit = email.getText().toString();

                if(!(textNombreUsuario.matches("")) && !(textPass.matches("")) && !(textNombre.matches("")))
                {
                    if(db.getVersion()==2)
                    {
                        if(!(textEmailEdit.matches("")))
                        {
                            db.execSQL("UPDATE Usuarios SET nombre_usuario='"+textNombreUsuario+"', password='"+textPass+"', nombre_completo='"+textNombre+"', email='"+textEmailEdit+"' WHERE nombre_usuario='"+user+"'");
                            Toast.makeText(editarUsuario.this, "Actualizado", Toast.LENGTH_SHORT).show();

                        }

                        else
                        {
                            Toast.makeText(editarUsuario.this, "Email sin rellenar", Toast.LENGTH_SHORT).show();

                        }
                    }

                    else
                    {
                        db.execSQL("UPDATE Usuarios SET nombre_usuario='"+textNombreUsuario+"', password='"+textPass+"', nombre_completo='"+textNombre+"' WHERE nombre_usuario='"+user+"'");
                        Toast.makeText(editarUsuario.this, "Actualizado", Toast.LENGTH_SHORT).show();

                    }
                }

                else
                {
                    Toast.makeText(editarUsuario.this, "Usuario/Contraseña/Nombre sin rellenar", Toast.LENGTH_SHORT).show();

                }
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(editarUsuario.this, gestionUsuarios.class));
                finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar_usuario, menu);
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
