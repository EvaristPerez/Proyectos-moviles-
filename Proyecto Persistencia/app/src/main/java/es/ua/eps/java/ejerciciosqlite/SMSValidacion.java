package es.ua.eps.java.ejerciciosqlite;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SMSValidacion extends AppCompatActivity {

    Button validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsvalidacion);

        final String user = getIntent().getExtras().getString("Usuario");
        final String nombre = getIntent().getExtras().getString("Nombre");
        final String mail = getIntent().getExtras().getString("Email");

        validation = (Button) findViewById(R.id.validation);

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*ContentValues values = new ContentValues();

                values.put(Telephony.Sms.ADDRESS, tlf);
                values.put(Telephony.Sms.BODY, "Hola\n Código de verificación: @@A3489HG@@ \n Gracias");
                getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);*/


                String sms = getSmsFromProvider();

                if (sms.matches("")) {
                    Toast.makeText(SMSValidacion.this, "Código no recibido", Toast.LENGTH_SHORT).show();
                } else {
                    String[] codigo = sms.split("@@");

                    if (codigo[1].matches("A3489HG")) {
                        Intent intent = new Intent(SMSValidacion.this, pantallaAcceso.class);
                        intent.putExtra("Usuario", user);
                        intent.putExtra("Nombre", nombre);
                        intent.putExtra("Email", mail);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SMSValidacion.this, "Código incorrecto", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smsvalidacion, menu);
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

    public String getSmsFromProvider() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String tlf = sharedPreferences.getString("SMS", "12345678");

        List<String> lstSms = new ArrayList<String>();
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[]{Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.DATE_SENT},
                Telephony.Sms.Inbox.ADDRESS + "= '" + tlf + "'",
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        int totalSMS = c.getCount();

        String fecha = (String) android.text.format.DateFormat.format("yyyy-MM-dd", new Date());

        long mseconds = milliseconds(fecha);

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                if(Long.parseLong(c.getString(2))>=mseconds)
                {
                    lstSms.add(c.getString(0));

                }
                c.moveToNext();
            }
        }
        c.close();

        if(lstSms.isEmpty())
        {
            return "";
        }


        return lstSms.get(0);
    }

    public long milliseconds(String date) {
        String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }
}


