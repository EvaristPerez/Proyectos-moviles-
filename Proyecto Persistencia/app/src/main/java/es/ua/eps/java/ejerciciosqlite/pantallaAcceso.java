package es.ua.eps.java.ejerciciosqlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class pantallaAcceso extends AppCompatActivity {

    TextView bienvenido, usuario, email;
    Button salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_acceso);

        bienvenido = (TextView) findViewById(R.id.textoBienvenido);
        usuario = (TextView) findViewById(R.id.textoUsuario);
        email = (TextView) findViewById(R.id.textoEmail);
        salir = (Button) findViewById(R.id.salir);


        String user = getIntent().getExtras().getString("Usuario");
        String nombre = getIntent().getExtras().getString("Nombre");
        String mail = getIntent().getExtras().getString("Email");

        bienvenido.append(nombre);
        usuario.append(user);

        if(mail!=null && !(mail.matches("")))
        {
            email.setVisibility(View.VISIBLE);
            email.append(mail);
        }

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                System.exit(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pantalla_acceso, menu);
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
