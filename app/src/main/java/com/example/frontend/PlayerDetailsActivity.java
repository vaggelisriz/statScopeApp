package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayerDetailsActivity extends AppCompatActivity {

    private TextView tvPlayerAge;

    // Τα νέα TextViews για τα στατιστικά
    private TextView tvAppearances, tvGoals, tvAssists, tvYellowCards, tvRedCards;

    private RecyclerView rvHistory;
    private PlayerHistoryAdapter historyAdapter;
    private final List<PlayerMatchHistory> matchHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_details);

        ImageButton btnBack = findViewById(R.id.btn_back_player);
        ImageView ivPlayerPhoto = findViewById(R.id.iv_details_player_photo);
        TextView tvPlayerName = findViewById(R.id.tv_details_player_name);
        TextView tvPlayerPosition = findViewById(R.id.tv_details_player_position);
        TextView tvPlayerNumber = findViewById(R.id.tv_details_player_number);
        TextView tvPlayerTeam = findViewById(R.id.tv_details_player_team);
        tvPlayerAge = findViewById(R.id.tv_details_player_age);

        // Αρχικοποίηση των Textbviews στατιστικών
        tvAppearances = findViewById(R.id.tv_details_player_appearances);
        tvGoals = findViewById(R.id.tv_details_player_goals);
        tvAssists = findViewById(R.id.tv_details_player_assists);
        tvYellowCards = findViewById(R.id.tv_details_player_yellow_cards);
        tvRedCards = findViewById(R.id.tv_details_player_red_cards);

        //Αρχικοποίηση των στοιχείων στατιστικών ανά αγώνα
        rvHistory = findViewById(R.id.rv_player_match_history);
        rvHistory.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        historyAdapter = new PlayerHistoryAdapter(matchHistoryList);
        rvHistory.setAdapter(historyAdapter);

        if (getIntent() != null) {
            int playerId = getIntent().getIntExtra("PLAYER_ID", -1);
            String name = getIntent().getStringExtra("PLAYER_NAME");
            String position = getIntent().getStringExtra("PLAYER_POSITION");
            String photo = getIntent().getStringExtra("PLAYER_PHOTO");
            String number = getIntent().getStringExtra("PLAYER_NUMBER");
            String teamName = getIntent().getStringExtra("PLAYER_TEAM_NAME");

            tvPlayerName.setText(name);
            tvPlayerPosition.setText(position != null ? position : "N/A");
            tvPlayerNumber.setText(number != null && !number.isEmpty() ? "#" + number : "");
            tvPlayerTeam.setText(teamName != null ? teamName : "Unknown Team");

            Glide.with(this)
                    .load(photo)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .circleCrop()
                    .into(ivPlayerPhoto);

            if (playerId != -1) {
                // Φέρνουμε την ηλικία (όπως το είχες)
                fetchPlayerAgeFromServer(playerId);
                // Φέρνουμε και τα στατιστικά από το match_events
                fetchPlayerStatsFromServer(playerId);
                fetchPlayerMatchHistoryFromServer(playerId);
            }
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchPlayerAgeFromServer(int playerId) {
        new Thread(() -> {
            try {
                String url = "http://10.140.9.120/statScopeApp/backend/api/getPlayerDetails.php?player_id=" + playerId;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    int age = jsonObject.getInt("age");

                    runOnUiThread(() -> {
                        if (age > 0) {
                            tvPlayerAge.setText(age + " years");
                        } else {
                            tvPlayerAge.setText("N/A");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("PlayerDetails", "Error fetching age: " + e.getMessage());
                runOnUiThread(() -> tvPlayerAge.setText("N/A"));
            }
        }).start();
    }

    // Η ΝΕΑ ΜΕΘΟΔΟΣ ΓΙΑ ΤΑ ΣΤΑΤΙΣΤΙΚΑ (MATCH_EVENTS)
    private void fetchPlayerStatsFromServer(int playerId) {
        new Thread(() -> {
            try {
                // Χτυπάμε το νέο endpoint
                String url = "http://10.140.9.120/statScopeApp/backend/api/getPlayerStats.php?player_id=" + playerId;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Διαβάζουμε τα πεδία που θα υπολογίσει η PHP
                    int goals = jsonObject.optInt("total_goals", 0);
                    int assists = jsonObject.optInt("total_assists", 0);
                    int yellowCards = jsonObject.optInt("total_yellow_cards", 0);
                    int redCards = jsonObject.optInt("total_red_cards", 0);
                    int appearances = jsonObject.optInt("appearances", 0);

                    // Ανανέωση του UI με ασφάλεια στο κεντρικό thread
                    runOnUiThread(() -> {
                        tvAppearances.setText(String.valueOf(appearances));
                        tvGoals.setText(String.valueOf(goals));
                        tvAssists.setText(String.valueOf(assists));
                        tvYellowCards.setText(String.valueOf(yellowCards));
                        tvRedCards.setText(String.valueOf(redCards));
                    });
                }
            } catch (Exception e) {
                Log.e("PlayerDetails", "Error fetching stats: " + e.getMessage());
                runOnUiThread(() -> {
                    tvAppearances.setText("0");
                    tvGoals.setText("0");
                    tvAssists.setText("0");
                    tvYellowCards.setText("0");
                    tvRedCards.setText("0");
                });
            }
        }).start();
    }

    private void fetchPlayerMatchHistoryFromServer(int playerId) {
        new Thread(() -> {
            try {
                String url = "http://10.140.9.120/statScopeApp/backend/api/getPlayerMatchHistory.php?player_id=" + playerId;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    org.json.JSONArray jsonArray = new org.json.JSONArray(jsonResponse);

                    matchHistoryList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        org.json.JSONObject obj = jsonArray.getJSONObject(i);

                        matchHistoryList.add(new PlayerMatchHistory(
                                obj.getInt("match_id"),
                                obj.getString("home_team"),
                                obj.getString("away_team"),
                                obj.getInt("match_goals"),
                                obj.getInt("match_assists"),
                                obj.getInt("match_yellow_cards"),
                                obj.getInt("match_red_cards")
                        ));
                    }

                    // Ενημέρωση της λίστας στο Main Thread
                    runOnUiThread(() -> historyAdapter.notifyDataSetChanged());
                }
            } catch (Exception e) {
                Log.e("PlayerDetails", "Error fetching match history: " + e.getMessage());
            }
        }).start();
    }
}