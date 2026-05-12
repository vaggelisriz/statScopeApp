package com.example.frontend;

import com.google.gson.annotations.SerializedName;

public class Player {
    @SerializedName("name")
    private String name;

    @SerializedName("position")
    private String position;

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }
}