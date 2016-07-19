package es.ua.eps.java.sockets;

/**
 * Created by mastermoviles on 18/11/15.
 */
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Cliente extends AppCompatActivity {

    //Boton 'Enviar'
    Button botonEnviar;

    //EditText en el que escribir nuestro mensaje
    EditText mensajeAEnviar;

    //AsynkTask en el que se llevan a cabo las operaciones de recepcion y nevio
    TareasCliente tareasCliente;

    //Hilo que se encarga de escuchar y comprobar si hay mensajes nuevos
    escuchaCliente listener;

    hiloLecturaArchivo hiloAr;
    hiloEscrituraArchivo hiloEs;

    //DataOutput para enviar mensajes al servidor
    DataOutputStream dataOutputStream = null;

    //DataInput para leer mensajes del servidor
    DataInputStream dataInputStream = null;

    //Variable socket
    Socket socket = null;

    //Variable LinearLayout
    LinearLayout linearLayout;

    //Variable utilizada para la toma de fotos
    private static int TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_clientemensaje);

        //Asignamos las variables LinearLayout, Button y EditText con sus correspondientes elementos de la interfaz
        linearLayout = (LinearLayout) findViewById(R.id.linearmensaje);
        mensajeAEnviar = (EditText) findViewById(R.id.mensaje);
        botonEnviar = (Button) findViewById(R.id.send);

        //Creamos y ejecutamos el asynkTask, pasandole la IP, el puerto y el mensaje que deseamos
        tareasCliente = new TareasCliente(getIntent().getExtras().getString("IP"), Integer.parseInt(getIntent().getExtras().getString("Puerto")), getIntent().getExtras().getString("Nombre"));
        tareasCliente.execute();

        //Al pulsar sobre el boton 'Enviar'
        botonEnviar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Si la cadena no es vacia
                    if(!(mensajeAEnviar.getText().toString().matches("")))
                    {
                        //Obtenemos el dataOutputStream y le pasamos como parametro el texto que hay en el editText
                        dataOutputStream.writeUTF(mensajeAEnviar.getText().toString());

                        //Añadimos a nuestra vista el mensaje a enviar
                        ComponenteMensaje componente = new ComponenteMensaje(getApplicationContext());
                        componente.setText(mensajeAEnviar.getText().toString());
                        linearLayout.addView(componente);

                        componente.getParent().requestChildFocus(componente,componente);
                        componente.setName(getIntent().getExtras().getString("Nombre"));
                        mensajeAEnviar.setText("");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Si pulsamos sobre el icono 'Adjuntar'
        if (id == R.id.adjuntar) {

            //Iniciamos un intent y comenzamos la captura de imagen
            Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            int code = TAKE_PICTURE;
            startActivityForResult(intent, code);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Si se ha capturado una imagen no nula
        if (requestCode == TAKE_PICTURE) {
            if (data != null) {
                if (data.getExtras().get("data") != null) {

                    //Almacenamos en un bitmap los datos
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");

                    //Creamos un imagen view con los datos y lo incluimos a la vista
                    ImageView iv = new ImageView(this);
                    iv.setImageBitmap(bmp);
                    linearLayout.addView(iv);
                    iv.getParent().requestChildFocus(iv, iv);

                    //Iniciamos el hilo de escritura para enviar la imagen al cliente
                    hiloEs=new hiloEscrituraArchivo(socket, bmp);
                    hiloEs.start();

                }
            }
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();

        //Si el socket es distinto de null, cerramos el socket
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //Si el dataOutputStream es distinto de null, cerramos el dataOutputStream
        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //Si el dataInputStream es distinto de null, cerramos el dataInputStream
        if (dataInputStream != null) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public class TareasCliente extends AsyncTask<Void, Void, Void> {

        //String en el que almacenar la direccion IP
        String IP;

        //Entero en el que almacenar el puerto
        int puerto;

        //String en el que almacenar el mensaje a enviar
        String miMensaje;

        //Constructor al que se le pasan tres parametros
        TareasCliente(String addr, int port, String msgTo){

            //Asignamos el primer parametro con la variable IP
            IP = addr;

            //Asignamos el segundo parametro con la variable puerto
            puerto = port;

            //Asignamos el tercer parametro con la variable miMensaje
            miMensaje = msgTo;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {

                //Creamos el socket con la IP y el puerto correspondientes
                socket = new Socket(IP, puerto);

                //Creamos un dataInputStream obteniendo el inputStream del socket creado
                dataInputStream = new DataInputStream(socket.getInputStream());

                //Creamos e inicimos el hilo que se encarga de estar escuchando al cliente
                listener= new escuchaCliente(dataInputStream);
                listener.start();

                //Creamos un dataOutputStream obteniendo el outStream del socket creado
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                //Si el mensajes a enviar es distinto de null
                if(miMensaje != null){

                    //Escribimos el mensaje
                    dataOutputStream.writeUTF(miMensaje);

                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }

    }

    private class escuchaCliente extends Thread {

        //Variable de tipo dataInput
        DataInputStream dataI;

        //Booleano para determinar si el cliente escucha
        Boolean escuchando;

        //String para almacenar el mensaje enviado por el servidor.
        String mensajeServidor;

        //Constructor del hilo
        escuchaCliente(DataInputStream datInput)
        {
            //Asignamos el dataInput que pasamos por parametro a la variable
            dataI = datInput;

            //Asignamos el valor true a la variable booleana
            escuchando = true;

            //Iniciamos a cadena vacia el mensaje del servidor
            mensajeServidor = "";
        }

        public void run()
        {
            //Mientras se escuche
            while(escuchando)
            {
                try {
                    //Leemos y almacenamos en la variable el valor leido
                    mensajeServidor = dataI.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Si se ha recibido algun mensaje y no tiene la identificacion de imagen
                if(mensajeServidor.length()>0 && mensajeServidor.equals("ImagenEnviada")==false)
                {
                    Cliente.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            //Creamos el mensaje en la interfaz
                            ComponenteRecibido componente = new ComponenteRecibido(getApplicationContext());
                            componente.setText(mensajeServidor);
                            componente.setName("Servidor");
                            linearLayout.addView(componente);

                            componente.getParent().requestChildFocus(componente, componente);
                        }
                    });
                }

                //Si se esta recibiendo una imagen
                else if(mensajeServidor.length()>0)
                {
                    //Creamos y ejecutamos el hilo de lectura
                    hiloAr=new hiloLecturaArchivo(socket);
                    hiloAr.run();
                }
            }

        }
    }

    private class hiloLecturaArchivo extends Thread {

        //Variable de tipo socket
        Socket socket;

        hiloLecturaArchivo(Socket socket){

            //Asignamos a nuestra variable, el socket pasado por parametro
            this.socket= socket;
        }

        @Override
        public void run() {

            //Iniciamos la longitud
            int length = 0;

            try {

                //Leemos los datos
                length = dataInputStream.readInt();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(length>0)
            {
                //Si la longitud es mayor que 0, creamos un array de byte de la longitud especificada
                byte [] mensaje = new byte[length];

                    try {

                        //Almacenamos en el array, los datos y posteriormente, los convertimos a Bitmap
                        dataInputStream.readFully(mensaje, 0, length);
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(mensaje, 0, mensaje.length);

                        Cliente.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                //Actualizamos la interfaz con la imagen recibida
                                ImageView iv = new ImageView(Cliente.this);
                                iv.setImageBitmap(bitmap);
                                linearLayout.addView(iv);
                                iv.getParent().requestChildFocus(iv, iv);
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        }
    }

    private class hiloEscrituraArchivo extends Thread {

        //Variable de tipo socket
        Socket socket;

        //Variable de tipo bitmat
        Bitmap bm;

        hiloEscrituraArchivo(Socket socket, Bitmap bitmap){

            //Asignamos a nuestra variable, el socket pasado por parametro
            this.socket= socket;

            //Asignamos a nuestra variable, el bitmap pasado por parametro
            bm=bitmap;
        }

        @Override
        public void run() {

            //Creamos e inicializamos una variable de tipo ByteArrayOutputStream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //Comprimimos el bitmap
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Almacenamos los datos anterior en un array de byte
            byte[] bytes = stream.toByteArray();

            try {

                //Mandamos al cliente el codigo "ImagenEnviada" para saber que va a recibir una imagen
                dataOutputStream.writeUTF("ImagenEnviada");

                //Enviamos los datos de la imagen
                dataOutputStream.writeInt(bytes.length);
                dataOutputStream.write(bytes);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

}
