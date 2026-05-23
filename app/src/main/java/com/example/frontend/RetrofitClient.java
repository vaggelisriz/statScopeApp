package com.example.frontend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // ✅ ΔΙΟΡΘΩΣΗ: Καλούμε το Config.BASE_URL ΔΥΝΑΜΙΚΑ μέσα στη μέθοδο
            // ώστε να είμαστε σίγουροι ότι το String έχει σχηματιστεί σωστά.
            String targetUrl = Config.BASE_URL;

            retrofit = new Retrofit.Builder()
                    .baseUrl(targetUrl)
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