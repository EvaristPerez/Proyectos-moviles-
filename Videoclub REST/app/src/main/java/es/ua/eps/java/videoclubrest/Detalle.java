package es.ua.eps.java.videoclubrest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class Detalle extends AppCompatActivity {

    TextView title, year, director, synopsis;
    ImageView image;

    MovieLoader loader;
    Movie movieItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        title = (TextView) findViewById(R.id.nombre);
        year = (TextView) findViewById(R.id.year);
        director = (TextView) findViewById(R.id.director);
        synopsis = (TextView) findViewById(R.id.synopsis);

        image = (ImageView) findViewById(R.id.imagen);

        Intent intent = getIntent();

        String textTitle = intent.getStringExtra("title");
        String textPoster = intent.getStringExtra("poster");
        String textDirector = intent.getStringExtra("director");
        String textYear = intent.getStringExtra("year");
        String textSynopsis = intent.getStringExtra("synopsis");

        title.setText("Título: " + textTitle);
        director.setText("Director: " + textDirector);
        year.setText("Año: " + textYear);
        synopsis.setText("Synopsis: " + textSynopsis);

        movieItem = new Movie(textTitle, textYear, textDirector, textPoster, false, textSynopsis);

        loader = new MovieLoader();
        loader.execute(movieItem, image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
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
