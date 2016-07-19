package es.ua.eps.java.videoclubrest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    infoMovies info;
    List<Movie> movieList = new ArrayList<Movie>();
    MovieAdapter adapter;
    Movie itemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView);

        info =  new infoMovies();
        info.execute("http://gbrain.dlsi.ua.es/videoclub/api/v1/catalog");



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, Detalle.class);

                itemSelected = (Movie) adapter.getItem(position);

                intent.putExtra("title", itemSelected.getTitle());
                intent.putExtra("year", itemSelected.getYear());
                intent.putExtra("poster", itemSelected.getPoster());
                intent.putExtra("director", itemSelected.getDirector());
                intent.putExtra("synopsis", itemSelected.getSynopsis());


                startActivity(intent);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String peticionGET( String strUrl )
    {
        HttpURLConnection http = null;
        String content = null;
        try {
            URL url = new URL( strUrl );
            http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Accept", "application/json");

            if( http.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader( http.getInputStream() ));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                content = sb.toString();
                reader.close();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if( http != null ) http.disconnect();
        }
        return content;
    }

    private class infoMovies extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {

            return peticionGET(urls[0]);
        }

        @Override
        protected void onPreExecute()
        {
            // Inicializar campos y valores necesarios
        }

        @Override
        protected void onPostExecute(String contenido)
        {
            try {
                JSONArray jsonArray = new JSONArray(contenido);

                    if (jsonArray != null) {
                        int len = jsonArray.length();

                        for (int i=0;i<len;i++){

                            JSONObject peli = jsonArray.getJSONObject(i);

                            movieList.add(new Movie(peli.getString("title"), peli.getString("year"), peli.getString("director"), peli.getString("poster"), Boolean.valueOf(peli.getString("rented")) , peli.getString("synopsis") ));


                        }

                        adapter = new MovieAdapter(getApplicationContext(), movieList);

                        lv.setAdapter(adapter);
                        lv.setOnScrollListener(adapter);

                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onCancelled()
        {
            // Tarea cancelada, lo dejamos como estaba
        }
    }
}
