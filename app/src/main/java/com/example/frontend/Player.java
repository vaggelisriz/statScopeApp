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

    @SerializedName("number")
    private String number;

    @SerializedName("team_id")
    private int teamId;

    // Runtime flags — Ορίζονται locally στην Activity
    private boolean isStarter = false;
    private boolean isSubstituted = false;

    // ─── Getters ────────────────────────────────────────────────────────────
    public int getId()         { return id; }
    public String getName()    { return name; }
    public String getPosition(){ return position; }
    public String getPhoto()   { return photo; }
    public String getNumber()  { return number; }
    public int getTeamId()     { return teamId; }
    public boolean isStarter() { return isStarter; }
    public boolean isSubstituted() { return isSubstituted; }

    // ─── Setters ─────────────────────────────────────────────────────────────
    public void setId(int id)            { this.id = id; }
    public void setStarter(boolean s)    { this.isStarter = s; }
    public void setTeamId(int teamId)    { this.teamId = teamId; }
    public void setSubstituted(boolean substituted) { this.isSubstituted = substituted; }
}