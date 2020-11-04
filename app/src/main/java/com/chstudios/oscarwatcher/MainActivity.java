package com.chstudios.oscarwatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> listOfMovieYears = new ArrayList<>();
    List<String> listOfAcademyAwardYears = new ArrayList<>();
    List<String> listOfAcademyAwardCeremonies = new ArrayList<>();
    List<String> listOfAcademyAwardTypes = new ArrayList<>();
    List<String> listOfAcademyAwardWins = new ArrayList<>();
    List<String> listOfAcademyAwardNames = new ArrayList<>();
    List<String> listOfAcademyAwardMovies = new ArrayList<>();
    List<AcademyAwards> listOfWinners = new ArrayList<>();
    List<AcademyAwards> itemsToRemove = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button refreshButton = findViewById(R.id.refresh_button);
        Button filterButton = findViewById(R.id.filter_button);
        EditText searchInput = findViewById(R.id.search_input);


        try{
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.the_oscar_award)));
            List<String> nextLine = new ArrayList<>();
            while (reader.readNext() != null) {

                nextLine = Arrays.asList(reader.readNext());


                listOfMovieYears.add(nextLine.get(0));
                listOfAcademyAwardYears.add(nextLine.get(1));
                listOfAcademyAwardCeremonies.add(nextLine.get(2));
                listOfAcademyAwardTypes.add(nextLine.get(3));
                listOfAcademyAwardNames.add(nextLine.get(4));
                listOfAcademyAwardMovies.add(nextLine.get(5));
                listOfAcademyAwardWins.add(nextLine.get(6));

                Log.d("Fail", String.valueOf((listOfAcademyAwardMovies.size())));
            }

        } catch (IOException e) {

        }


        listOfWinners.add(new AcademyAwards("2022", "2030", "1000", "Amazing", "Bob", "Hello World", "True"));

        for (int i = 0; i < listOfAcademyAwardMovies.size(); i++) {
            if(listOfWinners.get(i).getMovieName() != "") {
                listOfWinners.add(
                        new AcademyAwards(
                                listOfMovieYears.get(i),
                                listOfAcademyAwardYears.get(i),
                                listOfAcademyAwardCeremonies.get(i),
                                listOfAcademyAwardTypes.get(i),
                                listOfAcademyAwardNames.get(i),
                                listOfAcademyAwardMovies.get(i),
                                listOfAcademyAwardWins.get(i))
                );
            }
        }

        Collections.reverse(listOfWinners);

        recyclerView = (RecyclerView) findViewById(R.id.list_kaggle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MovieListAdapter(this, listOfWinners);
        recyclerView.setAdapter(mAdapter);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userSearch = searchInput.getText().toString();
                for (int i = 0; i < listOfWinners.size(); i++){
                    if(!listOfWinners.get(i).getMovieName().contains(userSearch))
                        itemsToRemove.add(listOfWinners.get(i));
                }

                listOfWinners.removeAll(itemsToRemove);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}