package com.example.frontend;

import com.google.gson.annotations.SerializedName;

/**
 * Model class representing a Match.
 * The SerializedName values must match the JSON keys from your PHP script.
 */
public class Match {

    // These names must match your database columns (home_team, away_team, etc.)
    @SerializedName("home_team")
    private String homeTeam;

    @SerializedName("away_team")
    private String awayTeam;

    @SerializedName("home_score")
    private int homeScore;

    @SerializedName("away_score")
    private int awayScore;

    // Default constructor
    public Match() {}

    // Getters - Used by the Adapter to display data
    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }
}