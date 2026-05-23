package com.example.frontend;

import com.google.gson.annotations.SerializedName;

public class TeamStanding {
    @SerializedName("team_id")
    private int teamId;

    @SerializedName("team_name")
    private String teamName;

    @SerializedName("team_logo")
    private String teamLogo;

    @SerializedName("team_city")
    private String teamCity;

    @SerializedName("matches_played")
    private int matchesPlayed;

    @SerializedName("wins")
    private int wins;

    @SerializedName("draws")
    private int draws;

    @SerializedName("losses")
    private int losses;

    @SerializedName("goals_scored")
    private int goalsScored;

    @SerializedName("goals_conceded")
    private int goalsConceded;

    @SerializedName("points")
    private int points;

    // Getters
    public int getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public String getTeamLogo() { return teamLogo; }
    public String getTeamCity() { return teamCity; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsScored() { return goalsScored; }
    public int getGoalsConceded() { return goalsConceded; }
    public int getPoints() { return points; }
}