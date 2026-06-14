package com.example.frontend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
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