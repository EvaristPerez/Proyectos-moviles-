package es.ua.eps.java.ejerciciosqlite;

/**
 * Created by mastermoviles on 10/12/15.
 */

import android.os.Bundle;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.opciones);
    }


}

