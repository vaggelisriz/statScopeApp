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
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MatchDetails";

    private TextView tvScore, tvHomeName, tvAwayName;
    private TextView tvHomeLineupTitle, tvAwayLineupTitle;
    private ImageView ivHomeLogo, ivAwayLogo;
    private RecyclerView rvHome, rvAway;

    private int homeTeamId;
    private int awayTeamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        // UI Binding
        tvScore    = findViewById(R.id.tv_detail_score);
        tvHomeName = findViewById(R.id.tv_detail_home);
        tvAwayName = findViewById(R.id.tv_detail_away);
        ivHomeLogo = findViewById(R.id.iv_detail_home_logo);
        ivAwayLogo = findViewById(R.id.iv_detail_away_logo);

        tvHomeLineupTitle = findViewById(R.id.tv_home_lineup_title);
        tvAwayLineupTitle = findViewById(R.id.tv_away_lineup_title);

        rvHome = findViewById(R.id.rv_home_players);
        rvAway = findViewById(R.id.rv_away_players);
        ImageButton btnBack = findViewById(R.id.btn_back_details);

        rvHome.setLayoutManager(new LinearLayoutManager(this));
        rvHome.setNestedScrollingEnabled(false);
        rvAway.setLayoutManager(new LinearLayoutManager(this));
        rvAway.setNestedScrollingEnabled(false);

        Match selectedMatch = (Match) getIntent().getSerializableExtra("selected_match");

        if (selectedMatch != null) {
            homeTeamId = selectedMatch.getHomeTeamId();
            awayTeamId = selectedMatch.getAwayTeamId();

            tvScore.setText(selectedMatch.getHomeScore() + " - " + selectedMatch.getAwayScore());
            tvHomeName.setText(selectedMatch.getHomeTeam());
            tvAwayName.setText(selectedMatch.getAwayTeam());

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

            // ✅ ΔΙΟΡΘΩΣΗ: καλούμε getMatchLineups αντί για getTeamPlayers
            //    Έτσι εμφανίζεται η 11άδα του αγώνα, όχι ΟΛΟΙ οι παίκτες της ομάδας.
            //    Μετά κάνουμε split βάσει team_id για home/away διαχωρισμό.
            fetchMatchLineups(selectedMatch.getId());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void fetchMatchLineups(int matchId) {
        RetrofitClient.getApiService().getMatchLineups(matchId).enqueue(new Callback<LineupResponse>() {
            @Override
            public void onResponse(Call<LineupResponse> call, Response<LineupResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Player> allPlayers = response.body().getPlayers();

                    List<Player> homePlayers = new ArrayList<>();
                    List<Player> awayPlayers = new ArrayList<>();

                    // Διαχωρισμός παικτών σε home / away βάσει team_id
                    for (Player p : allPlayers) {
                        if (p.getTeamId() == homeTeamId) {
                            homePlayers.add(p);
                        } else if (p.getTeamId() == awayTeamId) {
                            awayPlayers.add(p);
                        }
                    }

                    rvHome.setAdapter(new PlayerAdapter(homePlayers, null));
                    rvAway.setAdapter(new PlayerAdapter(awayPlayers, null));

                } else {
                    Log.e(TAG, "getMatchLineups failed: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LineupResponse> call, Throwable t) {
                Log.e(TAG, "getMatchLineups network error: " + t.getMessage());
            }
        });
    }
}