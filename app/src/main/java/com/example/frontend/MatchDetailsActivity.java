package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchDetailsActivity extends AppCompatActivity {

    private TextView tvScore, tvHomeName, tvAwayName;
    private TextView tvHomeLineupTitle, tvAwayLineupTitle; // SOS: For dynamic lineup names
    private ImageView ivHomeLogo, ivAwayLogo; // SOS: Required for team logos
    private RecyclerView rvHome, rvAway;
    private PlayerAdapter homeAdapter, awayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        // 1. UI Binding
        tvScore = findViewById(R.id.tv_detail_score);
        tvHomeName = findViewById(R.id.tv_detail_home);
        tvAwayName = findViewById(R.id.tv_detail_away);
        ivHomeLogo = findViewById(R.id.iv_detail_home_logo);
        ivAwayLogo = findViewById(R.id.iv_detail_away_logo);

        // Dynamic Lineup Titles Binding
        tvHomeLineupTitle = findViewById(R.id.tv_home_lineup_title);
        tvAwayLineupTitle = findViewById(R.id.tv_away_lineup_title);

        rvHome = findViewById(R.id.rv_home_players);
        rvAway = findViewById(R.id.rv_away_players);
        ImageButton btnBack = findViewById(R.id.btn_back_details);

        // 2. RecyclerView Setup
        rvHome.setLayoutManager(new LinearLayoutManager(this));
        rvHome.setNestedScrollingEnabled(false); // Smooth scrolling inside NestedScrollView
        rvAway.setLayoutManager(new LinearLayoutManager(this));
        rvAway.setNestedScrollingEnabled(false);

        // 3. Get Data from Intent
        Match selectedMatch = (Match) getIntent().getSerializableExtra("selected_match");

        if (selectedMatch != null) {
            // Set Score and Team Names
            tvScore.setText(selectedMatch.getHomeScore() + " - " + selectedMatch.getAwayScore());
            tvHomeName.setText(selectedMatch.getHomeTeam());
            tvAwayName.setText(selectedMatch.getAwayTeam());

            // SOS: Set Dynamic Lineup Titles (Green part from XML)
            tvHomeLineupTitle.setText(selectedMatch.getHomeTeam().toUpperCase());
            tvAwayLineupTitle.setText(selectedMatch.getAwayTeam().toUpperCase());

            // SOS: Load Team Logos from GitHub URLs via Glide
            Glide.with(this)
                    .load(selectedMatch.getHomeLogo())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivHomeLogo);

            Glide.with(this)
                    .load(selectedMatch.getAwayLogo())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivAwayLogo);

            // 4. API Calls for Players
            fetchPlayersForTeam(selectedMatch.getHomeTeamId(), true);
            fetchPlayersForTeam(selectedMatch.getAwayTeamId(), false);
        }

        // Back Navigation
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * SOS: Fetch players from API and update the respective RecyclerView
     */
    private void fetchPlayersForTeam(int teamId, boolean isHome) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getTeamPlayers(teamId).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> players = response.body();

                    if (isHome) {
                        homeAdapter = new PlayerAdapter(players);
                        rvHome.setAdapter(homeAdapter);
                    } else {
                        awayAdapter = new PlayerAdapter(players);
                        rvAway.setAdapter(awayAdapter);
                    }
                } else {
                    Log.e("API_ERROR", "Response unsuccessful for team ID: " + teamId);
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e("API_FAILURE", "Failed to fetch players: " + t.getMessage());
            }
        });
    }
}