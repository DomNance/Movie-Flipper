package com.chstudios.examplespringapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    List<String> awardTypes = new ArrayList<>();
    List<String> movieYears = new ArrayList<>();
    List<String> ceremonyYears = new ArrayList<>();
    List<String> ceremonyNumbers = new ArrayList<>();
    List<String> winnerNames = new ArrayList<>();
    List<String> winners = new ArrayList<>();
    List<String> listOfAcademyAwardTypes = new ArrayList<>();
    List<String> listOfMovieYears = new ArrayList<>();
    List<String> listOfCeremonyYears = new ArrayList<>();
    List<String> listOfCeremonyNumbers = new ArrayList<>();
    List<String> listOfWinnerNames = new ArrayList<>();
    List<String> listOfWinners = new ArrayList<>();

    JSONArray jsonArr;
    JSONObject jsonObj;
    List<Movie> movieList = new ArrayList<Movie>();
    Movie movie;

    int movieID = 7;

    String movieName = "", movieYear = "", ceremonyYear = "", ceremonyNumber = "",
            awardCategory = "", winnerName = "", isWinner = "", responseString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner =  findViewById(R.id.spinner);
        Spinner spinner2 = findViewById(R.id.spinner2);
        Spinner spinner3 = findViewById(R.id.spinner3);
        Spinner spinner4 = findViewById(R.id.spinner4);
        Spinner spinner5 = findViewById(R.id.spinner5);
        Spinner spinner6 = findViewById(R.id.spinner6);

        CardView filterButton = findViewById(R.id.filter_button);
        CardView refreshButton = findViewById(R.id.refresh_button);

        EditText searchInput = findViewById(R.id.search_input);

        //Unable to make network request on UI thread.
        //Create thread to make GET request
        Thread thread = new Thread() {
            public void run() {

                oscarMoviesGetRequest();
                convertJSONResponseToMovieList();
                updateUIWithMovies(spinner, spinner2, spinner3,
                        spinner4, spinner5, spinner6);

            }
        };
        thread.start();

        //Filter button will trigger get request and response chain of events. GET request will have
        //parameters supplied by user from the dropdown boxes and text input.
        //Response is in form of JSON.
        //Convert JSON to Movie Object
        //Display movie objects to user
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {
                    public void run() {
                        applyUserFilters(searchInput, spinner, spinner2,
                                spinner3, spinner4, spinner5, spinner6);
                        oscarMoviesGetRequest();
                        convertJSONResponseToMovieList();
                        updateUIWithMovies(spinner, spinner2, spinner3,
                                spinner4, spinner5, spinner6);
                    }
                };
                thread.start();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
    }

    //This function retrieves query parameters from the user for GET request
    private void applyUserFilters(EditText searchInput, Spinner spinner,
                                  Spinner spinner2, Spinner spinner3, Spinner spinner4, Spinner spinner5,
                                  Spinner spinner6)
    {
        if(!TextUtils.isEmpty(searchInput.getText().toString()))
            movieName = searchInput.getText().toString();
        if(spinner.getSelectedItemPosition() != 0)
            awardCategory = spinner.getSelectedItem().toString();
        if(spinner2.getSelectedItemPosition() != 0)
            movieYear = spinner2.getSelectedItem().toString();
        if(spinner3.getSelectedItemPosition() != 0)
            ceremonyYear = spinner3.getSelectedItem().toString();
        if(spinner4.getSelectedItemPosition() != 0)
            ceremonyNumber = spinner4.getSelectedItem().toString();
        if(spinner5.getSelectedItemPosition() != 0)
            winnerName = spinner5.getSelectedItem().toString();
        if(spinner6.getSelectedItemPosition() != 0)
            isWinner = spinner6.getSelectedItem().toString();
    }

    //This function updates the UI with the list of Movie object data which was previously converted from
    //the GET request JSON response.
    private void updateUIWithMovies(Spinner spinner, Spinner spinner2, Spinner spinner3,
                                    Spinner spinner4, Spinner spinner5, Spinner spinner6)
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                initializeRV();
                fillAwardTypeDropDownBox(spinner);
                fillMovieYearDropDownBox(spinner2);
                fillCeremonyYearDropDownBox(spinner3);
                fillCeremonyNumberDropDownBox(spinner4);
                fillWinnerNameDropDownBox(spinner5);
                fillWinnersDropDownBox(spinner6);
            }
        });
    }

    //This function converts the GET request response from JSON to List<Movie>
    private void convertJSONResponseToMovieList(){
        try {
            movieList.clear();

            jsonArr = new JSONArray(responseString);
            for (int i = 0; i < jsonArr.length(); i++) {
                jsonObj = jsonArr.getJSONObject(i);
                movie = new Movie(jsonObj.getString("film"), jsonObj.getString("year_film"),
                        jsonObj.getString("year_ceremony"), jsonObj.getString("ceremony"),
                        jsonObj.getString("category"), jsonObj.getString("name"),
                        jsonObj.getString("winner"));
                movieList.add(movie);
            }

//            jsonObj = new JSONObject(responseString);
//            movie = new Movie(jsonObj.getString("film"), jsonObj.getString("year_film"),
//                        jsonObj.getString("year_ceremony"), jsonObj.getString("ceremony"),
//                        jsonObj.getString("category"), jsonObj.getString("name"),
//                        jsonObj.getString("winner"));
//                movieList.add(movie);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //This function performs the GET request to the Oscar Movie API we created using Spring Boot
    private void oscarMoviesGetRequest(){

        //Connection URL for our Oscar Movie API (hosted locally)
        String baseUrl = "http://10.0.2.2:8090";

        //Endpoint that delivers a SINGLETON response. A single movie by it's movieId.
        String movieIdEndpoint = baseUrl + "/movieById/{movieId}";

        //Endpoint that delivers a COLLECTION response. A list of movies that contain the provided movie title within their own titles.
        String movieTitleEndpoint = baseUrl + "/movieByTitle/{movieName}";

        // The connection URL
        String searchAnyFieldEndpoint = baseUrl + String.format("/searchAnyField?film=%s&year_film=%s&year_ceremony=%s&ceremony=%s&category=%s&name=%s&winner=%s",
                movieName, movieYear, ceremonyYear, ceremonyNumber, awardCategory, winnerName, isWinner);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add the String message converter
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        // Make the HTTP GET request, marshaling the response to a String
        responseString = restTemplate.getForObject(searchAnyFieldEndpoint, String.class);
    }

    //This function fills the dropdown box with data from GET request response
    private void fillAwardTypeDropDownBox(Spinner spinner) {
        awardTypes.clear();
        for (int i = 0; i < movieList.size(); i++) {
            if(!awardTypes.contains(movieList.get(i).getAwardCategory()))
                awardTypes.add(movieList.get(i).getAwardCategory());
        }

        for (int i = 0; i < listOfAcademyAwardTypes.size(); i++) {
            if (!awardTypes.contains(listOfAcademyAwardTypes.get(i))) {
                awardTypes.add(listOfAcademyAwardTypes.get(i));
            }
        }

        Collections.sort(awardTypes);
        if(awardTypes.size() == 1)
            awardTypes.add(1, "FILTER BY AWARD CATEGORY");
        else awardTypes.add(0, "FILTER BY AWARD CATEGORY");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, awardTypes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }

    //This function fills the dropdown box with data from GET request response
    private void fillMovieYearDropDownBox(Spinner spinner){
        movieYears.clear();
        for (int i = 0; i < movieList.size(); i++) {
            if(!movieYears.contains(movieList.get(i).getMovieYear()))
                movieYears.add(movieList.get(i).getMovieYear());
        }

        for(int i = 0; i < listOfMovieYears.size(); i++){
            if(!movieYears.contains(listOfMovieYears.get(i))){
                movieYears.add(listOfMovieYears.get(i));
            }
        }

        if(movieYears.size() == 1)
            movieYears.add(1, "FILTER BY MOVIE YEAR");
        else movieYears.add(0, "FILTER BY MOVIE YEAR");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, movieYears);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //This function fills the dropdown box with data from GET request response
    private void fillCeremonyYearDropDownBox(Spinner spinner){
        ceremonyYears.clear();
        for (int i = 0; i < movieList.size(); i++) {
            if(!ceremonyYears.contains(movieList.get(i).getCeremonyYear()))
                ceremonyYears.add(movieList.get(i).getCeremonyYear());
        }

        for(int i = 0; i < listOfCeremonyYears.size(); i++){
            if(!ceremonyYears.contains(listOfCeremonyYears.get(i))){
                ceremonyYears.add(listOfCeremonyYears.get(i));
            }
        }

        if(ceremonyYears.size() == 1)
            ceremonyYears.add(1, "FILTER BY YEAR OF CEREMONY");
        else ceremonyYears.add(0, "FILTER BY YEAR OF CEREMONY");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, ceremonyYears);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //This function fills the dropdown box with data from GET request response
    private void fillCeremonyNumberDropDownBox(Spinner spinner){
        ceremonyNumbers.clear();
        for (int i = 0; i < movieList.size(); i++) {
            if(!ceremonyNumbers.contains(movieList.get(i).getCeremonyNumber()))
                ceremonyNumbers.add(movieList.get(i).getCeremonyNumber());
        }

        for(int i = 0; i < listOfCeremonyNumbers.size(); i++){
            if(!ceremonyNumbers.contains(listOfCeremonyNumbers.get(i))){
                ceremonyNumbers.add(listOfCeremonyNumbers.get(i));
            }
        }

        if(ceremonyNumbers.size() == 1)
            ceremonyNumbers.add(1, "FILTER BY NUMBER OF CEREMONY");
        else ceremonyNumbers.add(0, "FILTER BY NUMBER OF CEREMONY");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, ceremonyNumbers);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //This function fills the dropdown box with data from GET request response
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fillWinnerNameDropDownBox(Spinner spinner){
        winnerNames.clear();
        for (int i = 0; i < movieList.size(); i++) {
            if(!winnerNames.contains(movieList.get(i).getWinnerName()))
                winnerNames.add(movieList.get(i).getWinnerName());
        }

        for(int i = 0; i < listOfWinnerNames.size(); i++){
            if(!winnerNames.contains(listOfWinnerNames.get(i))){
                winnerNames.add(listOfWinnerNames.get(i));
            }
        }

        Collections.sort(winnerNames);
        if(winnerNames.size() == 1)
            winnerNames.add(1, "FILTER BY NAME OF AWARD WINNER");
        else winnerNames.add(0, "FILTER BY NAME OF AWARD WINNER");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, winnerNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //This function fills the dropdown box with data from GET request response
    private void fillWinnersDropDownBox(Spinner spinner){
        winners.clear();
        for (int i = 0; i < movieList.size(); i++) {
            if(!winners.contains(movieList.get(i).getIsWinner()))
                winners.add(movieList.get(i).getIsWinner());
        }

        for(int i = 0; i < listOfWinners.size(); i++){
            if(!winners.contains(listOfWinners.get(i))){
                winners.add(listOfWinners.get(i));
            }
        }
        if(winners.size() == 1)
            winners.add(1, "FILTER BY WINNERS");
        else winners.add(0, "FILTER BY WINNERS");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, winners);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //This function implements Recycler View to handle filling and displaying of Oscar Movie items
    private void initializeRV() {
        recyclerView = (RecyclerView) findViewById(R.id.list_kaggle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        TextView emptyListPrompt = findViewById(R.id.empty_list_prompt);
        if(movieList.size() == 0){
            emptyListPrompt.setVisibility(View.VISIBLE);
        } else emptyListPrompt.setVisibility(View.GONE);

        Collections.reverse(movieList);
        mAdapter = new MovieListAdapter(this, movieList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Nothing to do here. It is handled by the filter button.
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing to do here.
    }
}