package com.chstudios.oscarwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivty extends AppCompatActivity {

    String awardsWon, movieTitle, movieYear, parsedMovieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        movieYear = getIntent().getStringExtra("movie_year");
        awardsWon = getIntent().getStringExtra("award_category");
        movieTitle = getIntent().getStringExtra("movie_name");
        parsedMovieTitle = movieTitle.replace(" ", "%20");

        Log.d("Why", String.format("parsedMovieTitle = %s, movieYear = %s", parsedMovieTitle, movieYear));


        OkHttpClient client = new OkHttpClient();

        getMovie(client);
    }

    private void getMovie(OkHttpClient client) {

        Thread thread = new Thread() {
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(String.format("https://movie-database-imdb-alternative.p.rapidapi.com/?page=1&y=%s&r=json&s=%s", movieYear, parsedMovieTitle))
                            .get()
                            .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "5db8e5f3bemsha35561b2b308f11p1528f1jsn0a794a82d5d8")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseString = response.body().string();

                    JSONObject jObect = new JSONObject(responseString);
                    Log.d("Why", jObect.toString());
                    JSONArray jArray = jObect.getJSONArray("Search");

                    List<String> listOfTitles = new ArrayList<String>();
                    List<String> listOfYears = new ArrayList<String>();
                    List<String> listOfIDs = new ArrayList<String>();

                    for (int i = 0; i < 1; i++) {
                        listOfTitles.add(jArray.getJSONObject(i).getString("Title"));
                        listOfYears.add(jArray.getJSONObject(i).getString("Year"));
                        listOfIDs.add(jArray.getJSONObject(i).getString("imdbID"));
                    }

                    getMovieDetails(client, listOfIDs.get(0).toString());


                } catch (Exception e) {
                    Log.d("Http Request", "Failed");
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private void getMovieDetails(OkHttpClient client, String imdbID) {

        Thread thread = new Thread() {
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(String.format("https://movie-database-imdb-alternative.p.rapidapi.com/?i=%s&r=json", imdbID))
                            .get()
                            .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "5db8e5f3bemsha35561b2b308f11p1528f1jsn0a794a82d5d8")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseString = response.body().string();
                    Log.d("Http Request", responseString);

                    String movieTitle = new JSONObject(responseString).getString("Title");
                    String movieYear = new JSONObject(responseString).getString("Year");
                    String moviePlot = new JSONObject(responseString).getString("Plot");
                    String movieActors = new JSONObject(responseString).getString("Actors");
                    String movieAwards = new JSONObject(responseString).getString("Awards").replace(".", ",");
                    String moviePoster = new JSONObject(responseString).getString("Poster");
                    String movieRuntime = new JSONObject(responseString).getString("Runtime");
                    String movieRating = new JSONObject(responseString).getString("imdbRating");
                    String movieDirector = new JSONObject(responseString).getString("Director");
                    String movieGenre = new JSONObject(responseString).getString("Genre");

                    ResultActivty.this.runOnUiThread(new Runnable() {
                        public void run() {
                            TextView movie_Title = findViewById(R.id.movie_title_and_year);
                                    TextView movie_Actors = findViewById(R.id.movie_actors);
                                    TextView movie_Awards = findViewById(R.id.movie_awards);
                                    ImageView movie_Image = findViewById(R.id.movie_poster);
                                    TextView movie_plot = findViewById(R.id.movie_plot);
                                    TextView movie_Runtime = findViewById(R.id.movie_runtime);
                                    TextView movie_Rating = findViewById(R.id.movie_imdb_rating);
                                    TextView movie_Director = findViewById(R.id.movie_director);
                                    TextView movie_Genre = findViewById(R.id.movie_genre);

                            movie_Title.setText(String.format("Film: %s", movieTitle));
                            movie_Awards.setText(String.format("Awards: %s", movieAwards));
                            movie_Actors.setText(String.format("Actors: %s", movieActors));
                            movie_plot.setText(String.format("Plot: %s", moviePlot));
                            movie_Runtime.setText(String.format("Run Time: %s", movieRuntime));
                            movie_Rating.setText(String.format("IMDB Rating: %s", movieRating));
                            movie_Director.setText(String.format("Director: %s", movieDirector));
                            movie_Genre.setText(String.format("Genre: %s", movieGenre));

                            Glide
                                    .with(ResultActivty.this)
                                    .load(moviePoster)
                                    .fitCenter()
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(movie_Image);
                        }
                    });

                } catch (Exception e) {
                    Log.d("Http Request", "Failed");
                    e.printStackTrace();

                    TextView movie_Title = findViewById(R.id.movie_title_and_year);
                    movie_Title.setText("Unable to find this movie...");
                }
            }
        };

        thread.start();
    }
}