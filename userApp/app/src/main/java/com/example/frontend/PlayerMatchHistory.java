package com.example.frontend;

public class PlayerMatchHistory {
    private int matchId;
    private String homeTeam;
    private String awayTeam;
    private int goals;
    private int assists;
    private int yellowCards;
    private int redCards;

    public PlayerMatchHistory(int matchId, String homeTeam, String awayTeam, int goals, int assists, int yellowCards, int redCards) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.goals = goals;
        this.assists = assists;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }

    public String getMatchTitle() { return homeTeam + " vs " + awayTeam; }
    public int getGoals() { return goals; }
    public int getAssists() { return assists; }
    public int getYellowCards() { return yellowCards; }
    public int getRedCards() { return redCards; }
}