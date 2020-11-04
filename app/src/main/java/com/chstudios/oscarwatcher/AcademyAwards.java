package com.chstudios.oscarwatcher;

import org.jetbrains.annotations.NotNull;

public class AcademyAwards {
    @NotNull
    private final String yearFilm;
    @NotNull
    private final String yearCeremony;
    @NotNull
    private final String ceremonyNUmber;
    @NotNull
    private final String awardCategory;
    @NotNull
    private final String winnerName;
    @NotNull
    private final String movieName;
    @NotNull
    private final String isWinner;

    @NotNull
    public final String getYearFilm() {
        return this.yearFilm;
    }

    @NotNull
    public final String getYearCeremony() {
        return this.yearCeremony;
    }

    @NotNull
    public final String getCeremonyNUmber() {
        return this.ceremonyNUmber;
    }

    @NotNull
    public final String getAwardCategory() {
        return this.awardCategory;
    }

    @NotNull
    public final String getWinnerName() {
        return this.winnerName;
    }

    @NotNull
    public final String getMovieName() {
        return this.movieName;
    }

    @NotNull
    public final String isWinner() {
        return this.isWinner;
    }

    public AcademyAwards(@NotNull String yearFilm, @NotNull String yearCeremony, @NotNull String ceremonyNUmber,
                         @NotNull String awardCategory, @NotNull String winnerName, @NotNull String movieName,
                         @NotNull String isWinner) {
        this.yearFilm = yearFilm;
        this.yearCeremony = yearCeremony;
        this.ceremonyNUmber = ceremonyNUmber;
        this.awardCategory = awardCategory;
        this.winnerName = winnerName;
        this.movieName = movieName;
        this.isWinner = isWinner;
    }
}
