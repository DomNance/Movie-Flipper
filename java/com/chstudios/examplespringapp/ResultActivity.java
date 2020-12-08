package com.chstudios.examplespringapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    String awardsWon, movieTitle, movieYear, parsedMovieTitle;

    List<String> movieCast = new ArrayList<>();
    List<String> castChars = new ArrayList<>();
    List<String> castAndChars = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //receive movie details being passed from MainActivity
        movieYear = getIntent().getStringExtra("movie_year");
        awardsWon = getIntent().getStringExtra("award_category");
        movieTitle = getIntent().getStringExtra("movie_name");
        parsedMovieTitle = movieTitle.replace(" ", "%20");

        OkHttpClient client = new OkHttpClient();

        getMovie(client);
    }

    //This function makes the first HTTP request to IMDB using the movie title.
    //From the response, we can get the IMDB ID for the movie to make another
    //http request and get more movie details.
    private void getMovie(OkHttpClient client) {

        //Can't make network request on UI thread. Create thread.
        Thread thread = new Thread() {
            public void run() {
                try {
                    //Http request using movie title which is originally supplied from oscar movie API
                    Request request = new Request.Builder()
                            .url(String.format("https://imdb-internet-movie-database-unofficial.p.rapidapi.com/search/%s", parsedMovieTitle))
                            .get()
                            .addHeader("x-rapidapi-key", "5db8e5f3bemsha35561b2b308f11p1528f1jsn0a794a82d5d8")
                            .addHeader("x-rapidapi-host", "imdb-internet-movie-database-unofficial.p.rapidapi.com")
                            .build();

                    //execute the call and catch in response
                    Response response = client.newCall(request).execute();
                    //convert Response to String
                    String responseString = response.body().string();
                    //String content is in JSON form. Collect data using JSONObject
                    JSONObject jObect = new JSONObject(responseString);
                    JSONArray jArray = jObect.getJSONArray("titles");

                    List<String> listOfTitles = new ArrayList<String>();
                    List<String> listOfIDs = new ArrayList<String>();

                    //retieve the IMDB ID
                    for (int i = 0; i < 1; i++) {
                        listOfTitles.add(jArray.getJSONObject(i).getString("title"));
                        listOfIDs.add(jArray.getJSONObject(i).getString("id"));
                    }

                    //make another Http request using IMDB ID
                    getMovieDetails(client, listOfIDs.get(0));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    //This function makes the second Http request to IMDB using the IMDB ID.
    //From the response, we can get the title, year, rating, poster, link to trailer, cast, etc...
    private void getMovieDetails(OkHttpClient client, String imdbID) {

        Thread thread = new Thread() {
            public void run() {
                try {
                    //Http request using movie title which we got from the Http request to IMDB
                    Request request = new Request.Builder()
                            .url(String.format("https://imdb-internet-movie-database-unofficial.p.rapidapi.com/film/%s", imdbID))
                            .get()
                            .addHeader("x-rapidapi-key", "5db8e5f3bemsha35561b2b308f11p1528f1jsn0a794a82d5d8")
                            .addHeader("x-rapidapi-host", "imdb-internet-movie-database-unofficial.p.rapidapi.com")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseString = response.body().string();

                    //Convert JSON movie data to String movie data
                    String movieTitle = new JSONObject(responseString).getString("title");
                    String movieYear = new JSONObject(responseString).getString("year");
                    String moviePlot = new JSONObject(responseString).getString("plot");
                    String movieActorsJSON = new JSONObject(responseString).getString("cast");

                    //Convert JSON movie data to String movie data
                    JSONArray movieActors = new JSONArray(movieActorsJSON);
                    for(int i = 0; i < movieActors.length(); i++)
                    {
                        JSONObject jsonObj = movieActors.getJSONObject(i);
                        movieCast.add(jsonObj.getString("actor"));
                        castChars.add(jsonObj.getString("character"));
                    }

                    //Convert JSON movie data to String movie data
                    for(int i = 0; i < movieCast.size(); i++)
                    {
                        castAndChars.add(String.format("%s (%s)", movieCast.get(i), castChars.get(i)));
                    }

                    //Convert JSON movie data to String movie data
                    String moviePoster = new JSONObject(responseString).getString("poster");
                    String movieRuntime = new JSONObject(responseString).getString("length");
                    String movieRating = new JSONObject(responseString).getString("rating");
                    String trailerObject = new JSONObject(responseString).getString("trailer");
                    String trailerLink = new JSONObject(trailerObject).getString("link");

                    //display data to the user
                    ResultActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            TextView movie_Title = findViewById(R.id.movie_title_and_year);
                            TextView movie_Actors = findViewById(R.id.movie_actors);
                            ImageView movie_Image = findViewById(R.id.movie_poster);
                            TextView movie_plot = findViewById(R.id.movie_plot);
                            TextView movie_Runtime = findViewById(R.id.movie_runtime);
                            TextView movie_Rating = findViewById(R.id.movie_imdb_rating);
                            TextView trailer_link = findViewById(R.id.trailer_link);

                            movie_Title.setText(String.format("Film: %s (%s)", movieTitle, movieYear));
                            movie_Actors.setText(String.format("Actors: %s", castAndChars.toString().replace("[", "").replace("]", "")));
                            movie_plot.setText(String.format("Plot: %s", moviePlot));
                            movie_Runtime.setText(String.format("Run Time: %s", movieRuntime));
                            movie_Rating.setText(String.format("IMDB Rating: %s", movieRating));
                            trailer_link.setText(String.format("%s", trailerLink));

                            //Glide for processing image poster from poster path
                            Glide
                                    .with(ResultActivity.this)
                                    .load(moviePoster)
                                    .fitCenter()
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(movie_Image);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();

                    TextView movie_Title = findViewById(R.id.movie_title_and_year);
                    movie_Title.setText("Unable to find this movie...");
                }
            }
        };

        thread.start();
    }
}