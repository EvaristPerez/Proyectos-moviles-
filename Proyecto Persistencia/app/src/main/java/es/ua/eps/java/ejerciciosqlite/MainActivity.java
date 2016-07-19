package es.ua.eps.java.ejerciciosqlite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    Button cerrar, acceder;
    EditText usuario, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final UsuariosSQLiteHelper[] usdbh = {UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(this)};

        cerrar = (Button) findViewById(R.id.cerrar);
        acceder = (Button) findViewById(R.id.acceder);
        usuario = (EditText) findViewById(R.id.editTextusuario);
        pass = (EditText) findViewById(R.id.editTextPassword);

        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usdbh[0] = UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(MainActivity.this);
                SQLiteDatabase db = usdbh[0].getWritableDatabase();

                if(!(usuario.getText().toString().matches("")) && !(pass.getText().toString().matches("")))
                {
                    Cursor c;

                    if(db.getVersion()==1)
                    {
                        c = db.rawQuery("SELECT nombre_usuario, password, nombre_completo FROM Usuarios WHERE nombre_usuario = ? AND password = ?", new String[] {usuario.getText().toString(), pass.getText().toString()});
                    }

                    else
                    {
                        c = db.rawQuery("SELECT nombre_usuario, password, nombre_completo, email FROM Usuarios WHERE nombre_usuario = ? AND password = ?", new String[] {usuario.getText().toString(), pass.getText().toString()});

                    }


                    if(c.moveToFirst())
                    {
                        Intent intent = new Intent (MainActivity.this, SMSValidacion.class);
                        intent.putExtra("Usuario", usuario.getText().toString());
                        intent.putExtra("Nombre", c.getString(2));

                        if(db.getVersion()==2)
                        {
                            intent.putExtra("Email", c.getString(3));
                        }

                        startActivity(intent);
                        finish();
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();

                    }

                }

                else
                {
                    Toast.makeText(MainActivity.this, "Usuario/Contraseña sin rellenar", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.crearBackUp) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String sitio = sharedPreferences.getString("LugarGuardar", "SD");

            if(sitio.equals("SD"))
            {
                File ruta_sd = Environment.getExternalStorageDirectory();
                File f = new File (ruta_sd.getAbsolutePath(), "backupBD.txt");
                OutputStreamWriter fout = null;


                    try {

                        fout = new OutputStreamWriter(new FileOutputStream(f, false));

                        UsuariosSQLiteHelper usdbh = UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(MainActivity.this);
                        SQLiteDatabase db = usdbh .getWritableDatabase();

                        Cursor c = db.rawQuery("SELECT * FROM Usuarios", null);


                        if(db.getVersion()==1)
                        {
                            if(c.moveToFirst()) {

                                fout.write("1"+'\n');

                                do {

                                    fout.write(c.getString(1)+"#");
                                    fout.write(c.getString(2)+"#");
                                    fout.write(c.getString(3)+"\n");


                                } while (c.moveToNext());
                            }

                            fout.close();


                        }

                        else
                        {
                            if(c.moveToFirst()) {

                                fout.write("2"+'\n');

                                do {

                                    fout.write(c.getString(1)+"#");
                                    fout.write(c.getString(2)+"#");
                                    fout.write(c.getString(3)+"#");
                                    fout.write(c.getString(4)+"\n");


                                } while (c.moveToNext());
                            }

                            fout.close();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


            }

            else if(sitio.equals("Teléfono"))
            {
                OutputStreamWriter fout = null;

                try
                {

                    fout = new OutputStreamWriter(openFileOutput("backupBD.txt", Context.MODE_PRIVATE));

                    UsuariosSQLiteHelper usdbh = UsuariosSQLiteHelper.CreateUsuarioSQLiteHelper(MainActivity.this);
                    SQLiteDatabase db = usdbh .getWritableDatabase();

                    Cursor c = db.rawQuery("SELECT * FROM Usuarios", null);


                    if(db.getVersion()==1)
                    {
                        if(c.moveToFirst()) {

                            fout.write("1"+'\n');

                            do {

                                fout.write(c.getString(1)+"#");
                                fout.write(c.getString(2)+"#");
                                fout.write(c.getString(3)+"\n");


                            } while (c.moveToNext());
                        }

                        fout.close();


                    }

                    else
                    {
                        if(c.moveToFirst()) {

                            fout.write("2"+'\n');

                            do {

                                fout.write(c.getString(1)+"#");
                                fout.write(c.getString(2)+"#");
                                fout.write(c.getString(3)+"#");
                                fout.write(c.getString(4)+"\n");


                            } while (c.moveToNext());
                        }

                        fout.close();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return true;
        }

        else if (id == R.id.restaurarBackUp) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String nombreBD = sharedPreferences.getString("NombreFichero", "DBUsuarios");
            String lugar = sharedPreferences.getString("LugarGuardar", "SD");

            UsuariosSQLiteHelper usdbh;
            SQLiteDatabase db = null;

            if(lugar.equals("SD"))
            {
                File sdcard = Environment.getExternalStorageDirectory();
                File file = new File(sdcard,"backupBD.txt");

                if(file.exists())
                {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new FileReader(file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    String line, version="";
                    int contador=0;

                    try {
                        while ((line = br.readLine()) != null) {

                            if(contador==0)
                            {
                                sharedPreferences.edit().putString("VersionBD", line).commit();
                                version=line;

                                usdbh = new UsuariosSQLiteHelper(this, nombreBD, null, Integer.parseInt(line));
                                db = usdbh.getWritableDatabase();

                                contador++;
                            }

                            else
                            {
                                if(contador==1)
                                {
                                    db.execSQL("delete from Usuarios");
                                    contador++;
                                }

                                String[] datos = line.split("#");

                                if(version.equals("1"))
                                {
                                    db.execSQL("INSERT INTO Usuarios (nombre_usuario, password, nombre_completo) VALUES ('" + datos[0] + "', '" + datos[1] + "', '" + datos[2] + "')");
                                }

                                else if (version.equals("2"))
                                {
                                    db.execSQL("INSERT INTO Usuarios (nombre_usuario, password, nombre_completo, email) VALUES ('" + datos[0] + "', '" + datos[1] + "', '" + datos[2] + "', '"+ datos[3] +"')");
                                }

                            }


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            else
            {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(openFileInput("backupBD.txt")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String line, version="";
                int contador=0;

                try {
                    while ((line = br.readLine()) != null) {

                        if(contador==0)
                        {
                            sharedPreferences.edit().putString("VersionBD", line).commit();
                            version=line;

                            usdbh = new UsuariosSQLiteHelper(this, nombreBD, null, Integer.parseInt(line));
                            db = usdbh.getWritableDatabase();

                            contador++;
                        }

                        else
                        {
                            if(contador==1)
                            {
                                db.execSQL("delete from Usuarios");
                                contador++;
                            }

                            String[] datos = line.split("#");

                            if(version.equals("1"))
                            {
                                db.execSQL("INSERT INTO Usuarios (nombre_usuario, password, nombre_completo) VALUES ('" + datos[0] + "', '" + datos[1] + "', '" + datos[2] + "')");
                            }

                            else if (version.equals("2"))
                            {
                                db.execSQL("INSERT INTO Usuarios (nombre_usuario, password, nombre_completo, email) VALUES ('" + datos[0] + "', '" + datos[1] + "', '" + datos[2] + "', '"+ datos[3] +"')");
                            }

                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }

        else if (id == R.id.gestionUsuario) {

            startActivity(new Intent(MainActivity.this, gestionUsuarios.class));
            finish();
            return true;
        }

        else if (id == R.id.configuracion) {

            startActivityForResult((new Intent(MainActivity.this, PreferenceActivity.class)), 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String nombreBD = sharedPreferences.getString("NombreFichero", "DBUsuarios");
        String versionBD = sharedPreferences.getString("VersionBD", "1");
        String sitio = sharedPreferences.getString("LugarGuardar", "SD");

        UsuariosSQLiteHelper usdbh = new UsuariosSQLiteHelper(this, nombreBD, null, Integer.parseInt(versionBD));
        SQLiteDatabase db = usdbh.getWritableDatabase();

        if(sitio.equals("SD"))
        {
            File fileCopy = new File (Environment.getExternalStorageDirectory().getAbsolutePath(), "backupBD.txt");
            File fileToCopy = new File (getFilesDir(),"backupBD.txt");

            if(fileToCopy.exists())
            {
                try {
                    InputStream is = openFileInput("backupBD.txt");

                    if(is!=null)
                    {
                        OutputStream os = new FileOutputStream(fileCopy);
                        byte [] data2 = new byte[is.available()];
                        is.read(data2);
                        os.write(data2);
                        is.close();
                        os.close();

                        fileToCopy.delete();

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        else
        {
            File fileCopy = new File (getFilesDir(), "backupBD.txt");
            File fileToCopy = new File (Environment.getExternalStorageDirectory().getAbsolutePath(), "backupBD.txt");

            if(fileToCopy.exists())
            {
                try {
                    FileInputStream is = new FileInputStream(fileToCopy);

                    if(is!=null)
                    {
                        OutputStream os = new FileOutputStream(fileCopy);
                        byte [] data2 = new byte[is.available()];
                        is.read(data2);
                        os.write(data2);
                        is.close();
                        os.close();

                        fileToCopy.delete();

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }



    }


}
