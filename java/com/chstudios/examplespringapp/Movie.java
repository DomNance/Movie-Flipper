package com.chstudios.examplespringapp;

public class Movie {

    public String movieName = "";
    public String movieYear = "";
    public String ceremonyYear = "";
    public String ceremonyNumber = "";
    public String awardCategory = "";
    public String winnerName = "";
    public String isWinner = "";

    public Movie(String movieName, String movieYear, String ceremonyYear, String ceremonyNumber, String awardCategory, String winnerName, String isWinner) {
        this.movieName = movieName;
        this.movieYear = movieYear;
        this.ceremonyYear = ceremonyYear;
        this.ceremonyNumber = ceremonyNumber;
        this.awardCategory = awardCategory;
        this.winnerName = winnerName;
        this.isWinner = isWinner;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMovieYear() {
        return movieYear;
    }

    public String getCeremonyYear() {
        return ceremonyYear;
    }

    public String getCeremonyNumber() {
        return ceremonyNumber;
    }

    public String getAwardCategory() {
        return awardCategory;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getIsWinner() {
        return isWinner;
    }

}
