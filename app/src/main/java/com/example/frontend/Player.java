package com.example.frontend;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Player implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("position")
    private String position;

    @SerializedName("photo")
    private String photo;

    // Constructor
    public Player(int id, String name, String position, String photo) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.photo = photo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getPhoto() {
        return photo;
    }

    // Setters (αν χρειάζονται)
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}