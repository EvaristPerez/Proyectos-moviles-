package es.ua.eps.java.sockets;

/**
 * Created by mastermoviles on 18/11/15.
 */
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class Servidor extends AppCompatActivity {

    //Boton 'Enviar'
    Button botonEnviar;

    //EditText en el que escribir nuestro mensaje
    EditText mensajeAEnviar;

    //Hilo que se encarga de escuchar y comprobar si hay mensajes nuevos
    escuchaServidor listener;
    hiloEscrituraArchivo hiloAr;
    hiloLecturaArchivo hiloL;

    //DataOutput para enviar mensajes al cliente
    DataOutputStream dataOutputStream = null;

    //DataInput para leer mensajes del servidor
    DataInputStream dataInputStream = null;

    //Variable socket
    Socket socket = null;

    //Variable ServerSocket
    ServerSocket serverSocket;

    //Entero para conocer el numero de clientes
    int clientes=0;
    int aux = 0;
    String nombre;

    LinearLayout linearLayout;

    private static int TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos el layout correspondiente
        setContentView(R.layout.layout_servidor);

        //Asignamos las variables LinearLayout, Button y EditText con sus correspondientes elementos de la interfaz
        linearLayout = (LinearLayout) findViewById(R.id.linearmensaje);
        botonEnviar = (Button) findViewById(R.id.send);
        mensajeAEnviar = (EditText) findViewById(R.id.mensaje);

        //Asignamos la IP al texto encargado de mostrar esa información
        Servidor.this.setTitle(getDireccionIP());

        //Creamos el hilo y lo empezamos
        final hiloServidor socketServerThread = new hiloServidor();
        socketServerThread.start();

        //Al pulsar sobre el boton 'Enviar'
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //Si el numero de clientes es mayor que 0 y la cadena no es vacia
                    if(clientes>0 && (!(mensajeAEnviar.getText().toString().matches(""))))
                    {
                        //Obtenemos el dataOutputStream y le pasamos como parametro el texto que hay en el editText
                        dataOutputStream.writeUTF(mensajeAEnviar.getText().toString());

                        ComponenteMensaje componente = new ComponenteMensaje(getApplicationContext());
                        componente.setText(mensajeAEnviar.getText().toString());
                        componente.setName("Yo");
                        linearLayout.addView(componente);

                        componente.getParent().requestChildFocus(componente, componente);
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
                    hiloAr=new hiloEscrituraArchivo(socket, bmp);
                    hiloAr.start();

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Si el ServerSocket es distinto de null, cerramos el ServerSocket
        if (serverSocket != null) {
            try {
                serverSocket.close();
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

        //Si el Socket es distinto de null, cerramos el Socket
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class hiloServidor extends Thread {

        //Asignamos a la variable el puerto a utilizar
        static final int puertoServidor = 8080;

        @Override
        public void run() {

            try {

                //Creamos, utilizando el puerto, un serverSocket
                serverSocket = new ServerSocket(puertoServidor);

                //En caso de que haya solicitudes de conexion, las aceptamos
                socket = serverSocket.accept();

                //Creamos un dataInputStream obteniendo el inputStream del socket creado
                dataInputStream = new DataInputStream(socket.getInputStream());

                //Creamos un dataOutputStream obteniendo el outStream del socket creado
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                //Al establecer la conexion, auentamos el numero de clientes
                clientes++;

                //Creamos e inicimos el hilo que se encarga de estar escuchando al cliente
                listener= new escuchaServidor(dataInputStream);
                listener.start();


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

    }

    private class escuchaServidor extends Thread {

        //Variable de tipo dataInput
        DataInputStream dataI;

        //Booleano para determinar si el servidor escucha
        Boolean escuchando;

        //String para almacenar el mensaje enviado por el cliente.
        String mensajeCliente;

        //Constructor del hilo
        escuchaServidor(DataInputStream datInput)
        {
            //Asignamos el dataInput que pasamos por parametro a la variable
            dataI = datInput;

            //Asignamos el valor true a la variable booleana
            escuchando = true;

            //Iniciamos a cadena vacia el mensaje del cliente
            mensajeCliente = "";
        }

        public void run()
        {
            //Mientras se escuche
            while(escuchando)
            {
                try {
                    //Leemos y almacenamos en la variable el valor leido
                    mensajeCliente = dataI.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Si se ha recibido algun mensaje y no es una imagen
                if(mensajeCliente.length()>0  && !(mensajeCliente.matches("")) && mensajeCliente.equals("ImagenEnviada")==false)
                {
                    Servidor.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Si es la primera vez
                            if(aux == 0)
                            {
                                //Obtenemos el nombre del cliente
                                nombre = mensajeCliente;
                                aux = 1;
                            }else {
                                //Creamos el mensaje en la interfaz
                                ComponenteRecibido componente = new ComponenteRecibido(getApplicationContext());
                                componente.setText(mensajeCliente);
                                componente.setName(nombre);
                                linearLayout.addView(componente);

                                //Actualizamos la posicion del scroll
                                componente.getParent().requestChildFocus(componente, componente);
                            }
                        }
                    });
                }

                //Si se ha recibido una imagen
                else if(mensajeCliente.length()>0)
                {
                    //Iniciamos el hilo de lectura
                    hiloL=new hiloLecturaArchivo(socket);
                    hiloL.run();
                }
            }

        }
    }

    //Hilo para enviar imagenes al cliente
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

                    Servidor.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            //Actualizamos la interfaz con la imagen recibida
                            ImageView iv = new ImageView(Servidor.this);
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

    //Metodo para obtener la direccion IP
    private String getDireccionIP() {
        String ip = "";

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Error: " + e.toString() + "\n";
        }

        return ip;
    }
}
