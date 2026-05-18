package com.example.frontend;
import android.os.*;

import com.example.frontend.Championship;

import org.json.*;
import java.util.*;
import okhttp3.*;
public class OkHttpHandler {
    public OkHttpHandler() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    ArrayList<Championship> populateChampionshipSpinner(String url) throws Exception {
        ArrayList<Championship> championshipList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody body = RequestBody.create("",
                MediaType.parse("text/plain"));
        Request request = new Request.Builder().url(url).method("POST",
                body).build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        //System.out.println("My Response: " + data);
        try {
            // 1. Μετατρέπουμε σε JSONArray επειδή η PHP επιστρέφει λίστα [ {...}, {...} ]
            JSONArray jsonArray = new JSONArray(data);

            // 2. Λουπάρουμε όλα τα στοιχεία της λίστας
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // 3. ΕΔΩ ΔΗΛΩΝΟΝΤΑΙ ΟΙ ΜΕΤΑΒΛΗΤΕΣ! Παίρνουμε τα δεδομένα με βάση τα keys της PHP
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");

                // 4. Τώρα η Java ξέρει τι είναι το id και το name και τα δέχεται κανονικά!
                championshipList.add(new Championship(id, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return championshipList;
    }


    public ArrayList<Match> populateMatches(String url) throws Exception {
        ArrayList<Match> matchList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));
        Request request = new Request.Builder().url(url).method("POST", body).build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();

        // ΔΙΑΓΝΩΣΤΙΚΟ Α: Δες τι ακριβώς επιστρέφει το get_matches.php αρχείο σου!
        System.out.println("=== ΔΙΑΓΝΩΣΤΙΚΟ Α === RAW DATA ΑΠΟ PHP: " + data);

        try {
            JSONArray jsonArray = new JSONArray(data);
            System.out.println("=== ΔΙΑΓΝΩΣΤΙΚΟ Β === Το JSONArray έχει μέγεθος: " + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Χρησιμοποιούμε optInt και optString για να ΜΗΝ σταματάει το loop αν κάτι λείπει
                int id = jsonObject.optInt("id", -1);
                int championshipId = jsonObject.optInt("championship_id", -1);
                int homeTeamId = jsonObject.optInt("home_team_id", -1);
                int awayTeamId = jsonObject.optInt("away_team_id", -1);
                int homeScore = jsonObject.optInt("home_score", 0);
                int awayScore = jsonObject.optInt("away_score", 0);
                String status = jsonObject.optString("status", "");

                String homeTeam = jsonObject.optString("home_team", "Team A");
                String awayTeam = jsonObject.optString("away_team", "Team B");
                String homeLogo = jsonObject.optString("home_logo", "");
                String awayLogo = jsonObject.optString("away_logo", "");
                String championshipName = jsonObject.optString("championship_name", "");

                matchList.add(new Match(id, championshipId, homeTeam, awayTeam, homeScore, awayScore,
                        homeTeamId, awayTeamId, status, homeLogo, awayLogo, championshipName));
            }
        } catch (Exception e) {
            System.out.println("=== ΔΙΑΓΝΩΣΤΙΚΟ JSON ERROR === Πρόβλημα στο parsing: " + e.getMessage());
            e.printStackTrace();
        }
        return matchList;
    }
}