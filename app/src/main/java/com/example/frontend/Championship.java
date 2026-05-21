package com.example.frontend;

public class Championship {
    private int id;
    private String name;

    public Championship(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Το Spinner χρησιμοποιεί την toString() για να δείξει το κείμενο στην οθόνη!
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return this.id;
    }
}
