package es.ua.eps.java.sockets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by mastermoviles on 24/11/15.
 */
public class ComponenteMensaje extends LinearLayout
{
    public ComponenteMensaje(Context context) {
        super(context);

        // Creamos la interfaz a partir del layout
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.layout_componente, this, true);

        // Obtenemos las referencias a las vistas hijas

    }

    public ComponenteMensaje(Context context, AttributeSet atts) {
        super(context, atts);
    }

    public void setText(String a)
    {
        TextView texto = (TextView) findViewById(R.id.mensaje);
        texto.setText(a);

        Calendar c = Calendar.getInstance();
        int horas = c.get(Calendar.HOUR_OF_DAY);
        int minutos = c.get(Calendar.MINUTE);

        String hora=(horas+":"+minutos);
        TextView textoHora = (TextView) findViewById(R.id.hora);
        textoHora.setText(hora);
    }

    public void setName(String name)
    {
        TextView nombre = (TextView) findViewById(R.id.nombre);
        nombre.setText(name);
    }
}
