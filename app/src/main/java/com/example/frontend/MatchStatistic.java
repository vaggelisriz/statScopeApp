package com.example.frontend;

import com.google.gson.annotations.SerializedName;

// Αντιστοιχεί στις εγγραφές που επιστρέφει το getMatchStatistics.php
public class MatchStatistic {

    @SerializedName("id")
    private int id;

    @SerializedName("match_id")
    private int matchId;

    @SerializedName("player_id")
    private int playerId;

    @SerializedName("team_id")
    private int teamId;

    @SerializedName("event_type")
    private String eventType;

    @SerializedName("outcome")
    private String outcome;

    @SerializedName("player_name")
    private String playerName;

    @SerializedName("team_name")
    private String teamName;

    // Getters
    public int getId()            { return id; }
    public int getMatchId()       { return matchId; }
    public int getPlayerId()      { return playerId; }
    public int getTeamId()        { return teamId; }
    public String getEventType()  { return eventType; }
    public String getOutcome()    { return outcome; }
    public String getPlayerName() { return playerName; }
    public String getTeamName()   { return teamName; }
}