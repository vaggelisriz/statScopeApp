package com.example.statscopeapp;

public class Match {
    private int id;
    private String homeTeam;
    private String awayTeam;

    // Constructor
    public Match(int id, String homeTeam, String awayTeam) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    // Getters
    public int getId() { return id; }
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
}