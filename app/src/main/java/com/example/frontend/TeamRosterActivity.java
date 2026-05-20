package com.example.frontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeamRosterActivity extends AppCompatActivity {

    private int teamId;

    // Views Metadata & Stats
    private ImageView ivTeamLogo, ivCompArrow;
    private TextView tvTeamName, tvTeamCity, tvWins, tvDraws, tvLosses, tvPts;

    // Accordion Leagues
    private LinearLayout layoutCompHeader, layoutCompContainer;

    // Roster Recycler
    private RecyclerView rvRoster;
    private PlayerAdapter rosterAdapter;
    private final List<Player> playersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_roster);

        // Λήψη του Team ID από το προηγούμενο intent
        teamId = getIntent().getIntExtra("TEAM_ID", -1);

        // Bind Views
        ImageButton btnBack = findViewById(R.id.btn_back_team);
        ivTeamLogo = findViewById(R.id.iv_team_profile_logo);
        tvTeamName = findViewById(R.id.tv_team_profile_name);
        tvTeamCity = findViewById(R.id.tv_team_profile_city);

        tvWins = findViewById(R.id.tv_team_wins);
        tvDraws = findViewById(R.id.tv_team_draws);
        tvLosses = findViewById(R.id.tv_team_losses);
        tvPts = findViewById(R.id.tv_team_pts);

        layoutCompHeader = findViewById(R.id.layout_comp_header);
        layoutCompContainer = findViewById(R.id.layout_comp_container);
        ivCompArrow = findViewById(R.id.iv_comp_arrow);

        rvRoster = findViewById(R.id.rv_team_roster_players);
        rvRoster.setLayoutManager(new LinearLayoutManager(this));
        rosterAdapter = new PlayerAdapter(playersList, "");
        rvRoster.setAdapter(rosterAdapter);

        // Accordion Toggle
        layoutCompHeader.setOnClickListener(v -> {
            if (layoutCompContainer.getVisibility() == View.GONE) {
                layoutCompContainer.setVisibility(View.VISIBLE);
                ivCompArrow.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                layoutCompContainer.setVisibility(View.GONE);
                ivCompArrow.setImageResource(android.R.drawable.arrow_down_float);
            }
        });

        btnBack.setOnClickListener(v -> finish());

        // Φόρτωση Όλων των Δεδομένων Live
        if (teamId != -1) {
            fetchTeamDetailsFromBackend();
        }
    }

    private void fetchTeamDetailsFromBackend() {
        new Thread(() -> {
            try {
                String url = Config.BASE_URL+"/getTeamDetails.php?team_id=" + teamId;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // 1. Parse Metadata
                    JSONObject metaObj = jsonObject.getJSONObject("team_metadata");
                    String name = metaObj.getString("name");
                    String city = metaObj.getString("city");
                    String logoUrl = metaObj.getString("logo");

                    // 2. Parse Stats
                    JSONObject statsObj = jsonObject.getJSONObject("stats");
                    String wins = statsObj.getString("wins");
                    String draws = statsObj.getString("draws");
                    String losses = statsObj.getString("losses");
                    String points = statsObj.getString("points");

                    // 3. Parse Championships (Leagues)
                    JSONArray compArray = jsonObject.getJSONArray("championships");

                    // 4. Parse Roster Players
                    JSONArray rosterArray = jsonObject.getJSONArray("roster");
                    playersList.clear();
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    for (int i = 0; i < rosterArray.length(); i++) {
                        Player p = gson.fromJson(rosterArray.getJSONObject(i).toString(), Player.class);
                        playersList.add(p);
                    }

                    // UI Thread Updates
                    runOnUiThread(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            // Set Metadata
                            tvTeamName.setText(name);
                            tvTeamCity.setText(city);
                            Glide.with(this).load(logoUrl).into(ivTeamLogo);

                            // Set Live Stats
                            tvWins.setText(wins);
                            tvDraws.setText(draws);
                            tvLosses.setText(losses);
                            tvPts.setText(points);

                            // Build Competitions Dropdown
                            layoutCompContainer.removeAllViews();
                            if (compArray.length() == 0) {
                                TextView tvNone = new TextView(this);
                                tvNone.setText("No active competitions");
                                tvNone.setTextColor(Color.GRAY);
                                layoutCompContainer.addView(tvNone);
                            } else {
                                // Αντικατάσταση του loop για τα Championships στο TeamRosterActivity.java
                                for (int i = 0; i < compArray.length(); i++) {
                                    try {
                                        JSONObject champObj = compArray.getJSONObject(i);
                                        int champId = champObj.getInt("id"); // Παίρνουμε το ID του πρωταθλήματος
                                        String champName = champObj.getString("name"); // Παίρνουμε το Όνομα

                                        TextView tvChamp = new TextView(this);
                                        tvChamp.setText("• " + champName);
                                        tvChamp.setTextColor(Color.WHITE);
                                        tvChamp.setTextSize(15);
                                        tvChamp.setPadding(16, 16, 16, 16); // Λίγο παραπάνω padding για να πατιέται εύκολα

                                        // Ripple effect για να καταλαβαίνει ο χρήστης ότι είναι κουμπί
                                        tvChamp.setClickable(true);
                                        tvChamp.setFocusable(true);
                                        tvChamp.setBackgroundResource(android.R.drawable.list_selector_background);

                                        // CLICK LISTENER: Μεταφορά στην ChampionshipActivity
                                        tvChamp.setOnClickListener(v -> {
                                            Intent intent = new Intent(TeamRosterActivity.this, ChampionshipActivity.class);
                                            intent.putExtra("CHAMPIONSHIP_NAME", champName);
                                            intent.putExtra("CHAMPIONSHIP_ID", champId);
                                            startActivity(intent);
                                        });

                                        layoutCompContainer.addView(tvChamp);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // Notify Adapter for Roster
                            rosterAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("TeamDetails", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}