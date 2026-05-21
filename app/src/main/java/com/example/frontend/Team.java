package com.example.frontend;

public class Team {
    private int id;
    private String name;
    private String city;
    private String logo;

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