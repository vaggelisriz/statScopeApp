package com.example.frontend;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("getMatches.php")
    Call<List<Match>> getAllMatches();

    @GET("get_team_players.php")
    Call<List<Player>> getTeamPlayers(@Query("team_id") int teamId);
}