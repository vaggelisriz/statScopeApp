package com.example.frontend;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Match implements Serializable {
    @SerializedName("id")
    private int id;

    // Προσθήκη του ID πρωταθλήματος για το φιλτράρισμα
    @SerializedName("championship_id")
    private int championshipId;

    @SerializedName("home_team")
    private String homeTeam;

    @SerializedName("away_team")
    private String awayTeam;

    @SerializedName("home_score")
    private int homeScore;

    @SerializedName("away_score")
    private int awayScore;

    @SerializedName("home_team_id")
    private int homeTeamId;

    @SerializedName("away_team_id")
    private int awayTeamId;

    @SerializedName("status")
    private String status;

    @SerializedName("home_logo")
    private String homeLogo;

    @SerializedName("away_logo")
    private String awayLogo;

    @SerializedName("championship_name")
    private String championshipName;

    // ΚΑΤΑΣΚΕΥΑΣΤΗΣ (Constructor) για τον OkHttpHandler
    public Match(int id, int championshipId, String homeTeam, String awayTeam, int homeScore, int awayScore,
                 int homeTeamId, int awayTeamId, String status, String homeLogo, String awayLogo, String championshipName) {
        this.id = id;
        this.championshipId = championshipId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.status = status;
        this.homeLogo = homeLogo;
        this.awayLogo = awayLogo;
        this.championshipName = championshipName;
    }

    // Getters
    public int getId() { return id; }
    public int getChampionshipId() { return championshipId; } // Νέος Getter
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public int getHomeScore() { return homeScore; }
    public int getAwayScore() { return awayScore; }
    public int getHomeTeamId() { return homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }
    public String getStatus() { return status; }
    public String getHomeLogo() { return homeLogo; }
    public String getAwayLogo() { return awayLogo; }
    public String getChampionshipName() { return championshipName; }
}