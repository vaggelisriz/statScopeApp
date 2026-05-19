package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class FanMatchDetailsActivity extends AppCompatActivity {

    private RecyclerView rvHomePlayers, rvAwayPlayers;
    private ImageView ivHomeArrow, ivAwayArrow;

    private int matchId, homeTeamId, awayTeamId;

    private String homeTeam, awayTeam;
    private PlayerAdapter homeAdapter, awayAdapter;

    private final List<Player> homePlayersList = new ArrayList<>();
    private final List<Player> awayPlayersList = new ArrayList<>();

    // Δήλωση των TextViews για τα στατιστικά
    private TextView tvStatHomeShots, tvStatAwayShots;
    private TextView tvStatHomePasses, tvStatAwayPasses;
    private TextView tvStatHomeFouls, tvStatAwayFouls;
    private TextView tvStatHomeCards, tvStatAwayCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanmatchdetails);

        // 1. Σύνδεση των βασικών Views
        ImageButton btnBack = findViewById(R.id.btn_back_details);
        TextView tvHomeTeam = findViewById(R.id.tv_detail_home);
        TextView tvAwayTeam = findViewById(R.id.tv_detail_away);
        TextView tvScore = findViewById(R.id.tv_detail_score);
        ImageView ivHomeLogo = findViewById(R.id.iv_detail_home_logo);
        ImageView ivAwayLogo = findViewById(R.id.iv_detail_away_logo);

        // 2. Σύνδεση των Expandable Views (Headers & Arrows)
        LinearLayout layoutHomeHeader = findViewById(R.id.layout_home_header);
        LinearLayout layoutAwayHeader = findViewById(R.id.layout_away_header);
        TextView tvHomeHeaderTitle = findViewById(R.id.tv_home_header_title);
        TextView tvAwayHeaderTitle = findViewById(R.id.tv_away_header_title);
        ivHomeArrow = findViewById(R.id.iv_home_arrow);
        ivAwayArrow = findViewById(R.id.iv_away_arrow);

        // 3. Σύνδεση των νέων Views για τα Stats
        tvStatHomeShots = findViewById(R.id.tv_stat_home_shots);
        tvStatAwayShots = findViewById(R.id.tv_stat_away_shots);
        tvStatHomePasses = findViewById(R.id.tv_stat_home_passes);
        tvStatAwayPasses = findViewById(R.id.tv_stat_away_passes);
        tvStatHomeFouls = findViewById(R.id.tv_stat_home_fouls);
        tvStatAwayFouls = findViewById(R.id.tv_stat_away_fouls);
        tvStatHomeCards = findViewById(R.id.tv_stat_home_cards);
        tvStatAwayCards = findViewById(R.id.tv_stat_away_cards);

        // 4. Λήψη και εμφάνιση δεδομένων από το Intent
        if (getIntent() != null) {
            // Διορθώθηκε: Διαβάζουμε τα IDs ως int για να γεμίσουν οι παγκόσμιες μεταβλητές
            matchId = getIntent().getIntExtra("MATCH_ID", -1);
            homeTeamId = getIntent().getIntExtra("HOME_TEAM_ID", -1);
            awayTeamId = getIntent().getIntExtra("AWAY_TEAM_ID", -1);

            homeTeam = getIntent().getStringExtra("HOME_TEAM");
            awayTeam = getIntent().getStringExtra("AWAY_TEAM");
            Log.d("DETAILS_CHECK", "Home Team Name received: " + homeTeam + " | Away Team Name received: " + awayTeam);
            int homeScore = getIntent().getIntExtra("HOME_SCORE", 0);
            int awayScore = getIntent().getIntExtra("AWAY_SCORE", 0);
            String homeLogo = getIntent().getStringExtra("HOME_LOGO");
            String awayLogo = getIntent().getStringExtra("AWAY_LOGO");


            // Εμφάνιση στο Scoreboard
            tvHomeTeam.setText(homeTeam);
            tvAwayTeam.setText(awayTeam);
            tvScore.setText(homeScore + " - " + awayScore);

            // Δυναμική μετονομασία των πτυσσόμενων κουμπιών με το όνομα της ομάδας
            tvHomeHeaderTitle.setText(homeTeam);
            tvAwayHeaderTitle.setText(awayTeam);

            Glide.with(this).load(homeLogo).into(ivHomeLogo);
            Glide.with(this).load(awayLogo).into(ivAwayLogo);
        }

        // 5. Setup των RecyclerViews
        setupLineupsRecyclerViews();

        // 6. Φόρτωμα των δεδομένων live μέσω OkHttp
        fetchLineupsFromBackend();

        // 7. Click Listeners για το Άνοιγμα/Κλείσιμο (Expand/Collapse)
        layoutHomeHeader.setOnClickListener(v -> toggleLayout(rvHomePlayers, ivHomeArrow));
        layoutAwayHeader.setOnClickListener(v -> toggleLayout(rvAwayPlayers, ivAwayArrow));

        // Back Button
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupLineupsRecyclerViews() {
        rvHomePlayers = findViewById(R.id.rv_home_players);
        rvAwayPlayers = findViewById(R.id.rv_away_players);

        rvHomePlayers.setLayoutManager(new LinearLayoutManager(this));
        rvAwayPlayers.setLayoutManager(new LinearLayoutManager(this));

        homeAdapter = new PlayerAdapter(homePlayersList,homeTeam);
        awayAdapter = new PlayerAdapter(awayPlayersList,awayTeam);

        rvHomePlayers.setAdapter(homeAdapter);
        rvAwayPlayers.setAdapter(awayAdapter);
    }

    private void fetchLineupsFromBackend() {
        new Thread(() -> {
            try {
                String url = "http://10.140.7.36/statScopeApp/backend/api/getMatchLineups.php?match_id=" + matchId;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("players");

                    homePlayersList.clear();
                    awayPlayersList.clear();

                    // Αρχικοποίηση του Gson για αυτόματο mapping
                    com.google.gson.Gson gson = new com.google.gson.Gson();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject playerObj = jsonArray.getJSONObject(i);

                        // ΜΑΓΙΚΗ ΓΡΑΜΜΗ: Το Gson μετατρέπει το JSON σε Player χωρίς να χρειάζεται Setters!
                        Player player = gson.fromJson(playerObj.toString(), Player.class);

                        // Φιλτράρισμα με βάση το team_id του παίκτη (που πλέον διαβάζεται κανονικά)
                        if (player.getTeamId() == homeTeamId) {
                            homePlayersList.add(player);
                        } else if (player.getTeamId() == awayTeamId) {
                            awayPlayersList.add(player);
                        }
                    }

                    // Ανανέωση των Adapters στο UI Thread
                    // Ανανέωση των Adapters στο UI Thread με την έτοιμη μέθοδο του Android
                    runOnUiThread(() -> {
                        homeAdapter.notifyDataSetChanged();
                        awayAdapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                Log.e("Lineups", "Error fetching lineups: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void toggleLayout(RecyclerView recyclerView, ImageView arrowIcon) {
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
            arrowIcon.setImageResource(android.R.drawable.arrow_up_float);
        } else {
            recyclerView.setVisibility(View.GONE);
            arrowIcon.setImageResource(android.R.drawable.arrow_down_float);
        }
    }
}