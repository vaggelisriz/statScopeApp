package com.example.frontend;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlayerResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Player> data;

    public List<Player> getData() { return data; }
}