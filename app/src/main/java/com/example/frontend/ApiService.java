package com.example.frontend;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("getMatches.php")
    Call<List<Match>> getAllMatches();

    @GET("getPlayers.php")
    Call<List<Player>> getTeamPlayers(@Query("team_id") int teamId);

    @FormUrlEncoded
    @POST("updateMatchStatusAndLineups.php")
    Call<Void> updateMatchStatusAndLineups(
            @Field("match_id") int matchId,
            @Field("status") String status,
            @Field("home_starters[]") List<Integer> homeStarters,
            @Field("away_starters[]") List<Integer> awayStarters
    );
}