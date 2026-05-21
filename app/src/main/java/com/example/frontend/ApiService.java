package com.example.frontend;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // ─── GET: Όλα τα ματς ───────────────────────────────────────────────────
    @GET("getMatches.php")
    Call<List<Match>> getAllMatches();

    // ─── GET: Παίκτες ομάδας ────────────────────────────────────────────────
    @GET("getPlayers.php")
    Call<List<Player>> getTeamPlayers(@Query("team_id") int teamId);

    // ─── GET: 11άδα ματς ────────────────────────────────────────────────────
    @GET("getMatchLineups.php")
    Call<LineupResponse> getMatchLineups(@Query("match_id") int matchId);

    // ─── GET: Στατιστικά ματς ───────────────────────────────────────────────
    // ✅ ΝΕΟΣ ENDPOINT: getMatchStatistics.php?match_id=X
    @GET("getMatchStatistics.php")
    Call<List<MatchStatistic>> getMatchStatistics(@Query("match_id") int matchId);

    // ─── POST: Αποθήκευση 11άδας & αλλαγή status ────────────────────────────
    @FormUrlEncoded
    @POST("updateMatchStatusAndLineups.php")
    Call<StatusResponse> updateMatchStatusAndLineups(
            @Field("match_id")          int matchId,
            @Field("status")            String status,
            @Field("home_starters[]")   List<Integer> homeStarters,
            @Field("away_starters[]")   List<Integer> awayStarters
    );

    // ─── POST: Αποθήκευση event (γκολ, κάρτα κλπ) ───────────────────────────
    // ✅ ΝΕΟΣ ENDPOINT: saveEvent.php — ενημερώνει αυτόματα το σκορ αν είναι γκολ
    @FormUrlEncoded
    @POST("addMatchStatistic.php")
    Call<StatusResponse> addMatchStatistic(

            @Field("match_id") int matchId,
            @Field("player_id") int playerId,
            @Field("team_id") int teamId,
            @Field("event_type") String eventType,
            @Field("outcome") String outcome
    );

    // ─── POST: Ανανέωση σκορ (direct override) ──────────────────────────────
    // ✅ ΝΕΟΣ ENDPOINT: updateScore.php — χρησιμοποιείται ΜΟΝΟ για διόρθωση σκορ,
    //    ΟΧΙ για καταγραφή γκολ (για γκολ χρησιμοποίησε saveEvent)
    @FormUrlEncoded
    @POST("updateScore.php")
    Call<StatusResponse> updateScore(
            @Field("match_id")    int matchId,
            @Field("home_score")  int homeScore,
            @Field("away_score")  int awayScore
    );

    // ─── POST: Διαγραφή στατιστικού ─────────────────────────────────────────
    // ✅ ΝΕΟΣ ENDPOINT: deleteMatchStatistic.php
    @FormUrlEncoded
    @POST("deleteMatchStatistic.php")
    Call<StatusResponse> deleteMatchStatistic(
            @Field("match_id")    int matchId,
            @Field("player_id")   int playerId,
            @Field("event_type")  String eventType,
            @Field("outcome")     String outcome
    );
}