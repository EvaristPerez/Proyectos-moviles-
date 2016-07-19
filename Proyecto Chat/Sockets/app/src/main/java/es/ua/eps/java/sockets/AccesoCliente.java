package es.ua.eps.java.sockets;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AccesoCliente extends AppCompatActivity {

    //Variable de interfaz
    EditText editTextAddress, editTextPort;
    Button buttonConnect;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cliente);

        //Asignamos a la variable, los elementos correspondientes
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        name = (EditText) findViewById(R.id.Usuario);

        //Ponemos el foco en el primer editText
        editTextAddress.requestFocus();

        //Si pulsamos el boton 'Conectar'
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Llevamos a cabo la validacion de datos
                if(!name.getText().toString().matches("") && android.text.TextUtils.isDigitsOnly(editTextPort.getText().toString()) == true && !((editTextPort.getText().toString()).matches("")))
                {

                    //Creamos un intent y pasamos los datos: ip, puerto y nombre
                    Intent intent = new Intent(AccesoCliente.this, Cliente.class);
                    intent.putExtra("IP", editTextAddress.getText().toString());
                    intent.putExtra("Puerto", editTextPort.getText().toString());
                    intent.putExtra("Nombre", name.getText().toString());
                    startActivity(intent);

                }

                //Si alguno de los datos son incorrectos, mostramos un toast informativo
                else
                {
                    Toast.makeText(getApplicationContext(), "Datos incorrectos", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acceso_cliente, menu);
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
