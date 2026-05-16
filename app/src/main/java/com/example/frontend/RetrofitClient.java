package com.example.frontend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // ✅ ΔΙΟΡΘΩΣΗ: Το BASE_URL δείχνει στο φάκελο όπου βρίσκονται τα PHP αρχεία
    // στον Apache του emulator το localhost είναι 10.0.2.2
    // Δομή αρχείων: /var/www/html/frontend/api/getPlayers.php
    //               → http://10.0.2.2/frontend/api/
    // Αν τρέχεις σε πραγματική συσκευή, αλλάζεις σε http://<IP_ΜΗΧΑΝΗΜΑΤΟΣ>/frontend/api/
    private static final String BASE_URL = "http://10.0.2.2/statScopeApp/backend/api/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Helper για εύκολη πρόσβαση στο ApiService
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}