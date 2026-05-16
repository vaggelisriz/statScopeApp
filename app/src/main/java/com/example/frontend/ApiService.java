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
    // ✅ ΔΙΟΡΘΩΣΗ: αλλαγή από "get_team_players.php" → "getPlayers.php"
    //    (αυτό είναι το πραγματικό όνομα του αρχείου στο backend/api/)
    //    Παράμετρος: team_id (int) → http://.../getPlayers.php?team_id=1
    @GET("getPlayers.php")
    Call<List<Player>> getTeamPlayers(@Query("team_id") int teamId);

    // ─── GET: 11άδα ματς ────────────────────────────────────────────────────
    // Επιστρέφει { "status": "success", "players": [...] }
    @GET("getMatchLineups.php")
    Call<LineupResponse> getMatchLineups(@Query("match_id") int matchId);

    // ─── POST: Αποθήκευση 11άδας & αλλαγή status ────────────────────────────
    // Τα @Field ονόματα ΠΡΕΠΕΙ να ταιριάζουν ΑΚΡΙΒΩΣ με $_POST[...] στο PHP
    @FormUrlEncoded
    @POST("updateMatchStatusAndLineups.php")
    Call<StatusResponse> updateMatchStatusAndLineups(
            @Field("match_id")       int matchId,
            @Field("status")         String status,
            @Field("home_starters[]") List<Integer> homeStarters,
            @Field("away_starters[]") List<Integer> awayStarters
    );
}