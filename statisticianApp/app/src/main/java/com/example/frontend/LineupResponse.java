package com.example.frontend;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Αντιστοιχεί στο JSON: { "status": "success", "players": [...] }
// που επιστρέφει το getMatchLineups.php
public class LineupResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("players")
    private List<Player> players;

    public String getStatus()       { return status; }
    public List<Player> getPlayers(){ return players; }

    public boolean isSuccess() {
        return "success".equals(status);
    }
}