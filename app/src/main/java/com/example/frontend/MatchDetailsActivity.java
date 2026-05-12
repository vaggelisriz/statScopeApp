package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchDetailsActivity extends AppCompatActivity {

    private TextView tvHomeTeam, tvAwayTeam, tvScore, tvHomeLineup, tvAwayLineup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        // UI Initialization
        tvHomeTeam = findViewById(R.id.tv_detail_home);
        tvAwayTeam = findViewById(R.id.tv_detail_away);
        tvScore = findViewById(R.id.tv_detail_score);
        tvHomeLineup = findViewById(R.id.tv_home_lineup);
        tvAwayLineup = findViewById(R.id.tv_away_lineup);

        // Get the match object from the previous screen
        Match match = (Match) getIntent().getSerializableExtra("selected_match");

        if (match != null) {
            tvHomeTeam.setText(match.getHomeTeam());
            tvAwayTeam.setText(match.getAwayTeam());
            tvScore.setText(match.getHomeScore() + " - " + match.getAwayScore());

            // Fetch players for both teams
            fetchPlayers(match.getHomeTeamId(), tvHomeLineup);
            fetchPlayers(match.getAwayTeamId(), tvAwayLineup);
        }
    }

    private void fetchPlayers(int teamId, TextView targetTextView) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Calling getTeamPlayers which returns a simple List<Player>
        apiService.getTeamPlayers(teamId).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> players = response.body();
                    StringBuilder sb = new StringBuilder();

                    if (players.isEmpty()) {
                        targetTextView.setText("No players found.");
                    } else {
                        for (Player p : players) {
                            sb.append(p.getName())
                                    .append(" (")
                                    .append(p.getPosition())
                                    .append(")\n");
                        }
                        targetTextView.setText(sb.toString());
                    }
                } else {
                    targetTextView.setText("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e("MatchDetails", "Network Error: " + t.getMessage());
                targetTextView.setText("Connection failed.");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}