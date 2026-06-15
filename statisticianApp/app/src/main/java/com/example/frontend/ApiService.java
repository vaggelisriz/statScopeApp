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

    @GET("getChampionships.php")
    Call<List<Championship>> getChampionships();

    @GET("getPlayers.php")
    Call<List<Player>> getTeamPlayers(@Query("team_id") int teamId);

    @GET("getMatchLineups.php")
    Call<LineupResponse> getMatchLineups(@Query("match_id") int matchId);


    @GET("getMatchStatistics.php")
    Call<List<MatchStatistic>> getMatchStatistics(@Query("match_id") int matchId);

    @FormUrlEncoded
    @POST("updateMatchStatusAndLineups.php")
    Call<StatusResponse> updateMatchStatusAndLineups(
            @Field("match_id")          int matchId,
            @Field("status")            String status,
            @Field("home_starters[]")   List<Integer> homeStarters,
            @Field("away_starters[]")   List<Integer> awayStarters
    );

    @FormUrlEncoded
    @POST("updateScore.php")
    Call<StatusResponse> updateScore(
            @Field("match_id")    int matchId,
            @Field("home_score")  int homeScore,
            @Field("away_score")  int awayScore
    );

    @FormUrlEncoded
    @POST("saveEvent.php")
    Call<StatusResponse> saveLiveEvent( // 🛠️ ΑΛΛΑΓΗ ΟΝΟΜΑΤΟΣ ΕΔΩ
                                        @Field("match_id") int matchId,
                                        @Field("player_id") int playerId,
                                        @Field("event_type") String eventType,
                                        @Field("outcome") String outcome,
                                        @Field("event_minute") int eventMinute
    );

    @FormUrlEncoded
    @POST("deleteMatchStatistic.php")
    Call<StatusResponse> deleteMatchStatistic(
            @Field("match_id")    int matchId,
            @Field("player_id")   int playerId,
            @Field("event_type")  String eventType,
            @Field("outcome")     String outcome
    );


    @FormUrlEncoded
    @POST("finishMatch.php")
    Call<StatusResponse> finishMatch(@Field("match_id") int matchId);

}