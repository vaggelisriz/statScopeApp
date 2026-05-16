package com.example.frontend;

import com.google.gson.annotations.SerializedName;

// Αντιστοιχεί στο JSON: { "status": "success" }
// που επιστρέφει το updateMatchStatusAndLineups.php
public class StatusResponse {

    @SerializedName("status")
    private String status;

    public String getStatus() { return status; }

    public boolean isSuccess() {
        return "success".equals(status);
    }
}