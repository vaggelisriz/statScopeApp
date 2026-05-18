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
    private TextView tvHomeLineupTitle, tvAwayLineupTitle;
    private ImageView ivHomeLogo, ivAwayLogo;
    private RecyclerView rvHome, rvAway;
    private PlayerAdapter homeAdapter, awayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        // 1. UI Binding - Προσοχή: Χρησιμοποιούμε τα IDs που έχεις στο XML σου
        tvScore = findViewById(R.id.tv_detail_score);
        tvHomeName = findViewById(R.id.tv_detail_home);
        tvAwayName = findViewById(R.id.tv_detail_away);
        ivHomeLogo = findViewById(R.id.iv_detail_home_logo);
        ivAwayLogo = findViewById(R.id.iv_detail_away_logo);

        tvHomeLineupTitle = findViewById(R.id.tv_home_lineup_title);
        tvAwayLineupTitle = findViewById(R.id.tv_away_lineup_title);

        rvHome = findViewById(R.id.rv_home_players);
        rvAway = findViewById(R.id.rv_away_players);
        ImageButton btnBack = findViewById(R.id.btn_back_details);

        // 2. RecyclerView Setup
        rvHome.setLayoutManager(new LinearLayoutManager(this));
        rvHome.setNestedScrollingEnabled(false);
        rvAway.setLayoutManager(new LinearLayoutManager(this));
        rvAway.setNestedScrollingEnabled(false);

        // 3. Get Data from Intent
        Match selectedMatch = (Match) getIntent().getSerializableExtra("selected_match");

        if (selectedMatch != null) {
            tvScore.setText(selectedMatch.getHomeScore() + " - " + selectedMatch.getAwayScore());
            tvHomeName.setText(selectedMatch.getHomeTeam());
            tvAwayName.setText(selectedMatch.getAwayTeam());

            // Μετατροπή σε κεφαλαία για τον τίτλο της ενδεκάδας
            if (selectedMatch.getHomeTeam() != null)
                tvHomeLineupTitle.setText(selectedMatch.getHomeTeam().toUpperCase());
            if (selectedMatch.getAwayTeam() != null)
                tvAwayLineupTitle.setText(selectedMatch.getAwayTeam().toUpperCase());

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

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void fetchPlayersForTeam(int teamId, boolean isHome) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getTeamPlayers(teamId).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> players = response.body();

                    if (isHome) {
                        // Εδώ είναι η διόρθωση: Περνάμε null στον Listener
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