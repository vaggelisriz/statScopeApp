package com.example.frontend;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface to define the API endpoints for the backend communication.
 * This connects to the PHP scripts on your server.
 */
public interface ApiService {

    // The path to the PHP script that returns the matches from the database
    @GET("api/getMatches.php")
    Call<List<Match>> getAllMatches();

    /* You can add more endpoints here later, for example:
       @GET("api/getTeams.php")
       Call<List<Team>> getAllTeams();
    */
}