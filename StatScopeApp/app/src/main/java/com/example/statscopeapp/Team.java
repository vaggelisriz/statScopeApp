package com.example.statscopeapp;

public class Team {
    private final int id;
    private final String name;
    private final String city;
    private final String logo;

    public Team(int id, String name, String city, String logo) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.logo = logo;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getLogo() { return logo; }
}