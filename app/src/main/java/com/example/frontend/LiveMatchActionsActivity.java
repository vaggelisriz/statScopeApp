package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveMatchActionsActivity extends AppCompatActivity {

    private static final String TAG = "LiveMatchActions";

    private Match selectedMatch;
    private TextView tvHome, tvAway, tvScore, tvStatus, tvChamp;
    private ImageView ivHome, ivAway;
    private MaterialButton btnLineups, btnStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_match_actions);

        selectedMatch = (Match) getIntent().getSerializableExtra("selected_match");

        initViews();

        findViewById(R.id.btn_back_actions).setOnClickListener(v -> finish());

        btnLineups.setOnClickListener(v -> {
            Intent intent = new Intent(LiveMatchActionsActivity.this, UpdateLineupsActivity.class);
            intent.putExtra(UpdateLineupsActivity.EXTRA_MATCH_ID,     selectedMatch.getId());
            intent.putExtra(UpdateLineupsActivity.EXTRA_HOME_TEAM_ID, selectedMatch.getHomeTeamId());
            intent.putExtra(UpdateLineupsActivity.EXTRA_AWAY_TEAM_ID, selectedMatch.getAwayTeamId());
            intent.putExtra(UpdateLineupsActivity.EXTRA_HOME_TEAM,    selectedMatch.getHomeTeam());
            intent.putExtra(UpdateLineupsActivity.EXTRA_AWAY_TEAM,    selectedMatch.getAwayTeam());
            startActivity(intent);
        });

        // ✅ ΔΙΟΡΘΩΣΗ: ήταν εντελώς σχολιασμένο — τώρα ανοίγει την οθόνη στατιστικών
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(LiveMatchActionsActivity.this, StatisticsActivity.class);
            intent.putExtra("selected_match", selectedMatch);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ ΔΙΟΡΘΩΣΗ: το σκορ ανανεώνεται κάθε φορά που επιστρέφουμε σε αυτή την οθόνη
        //    (π.χ. μετά από καταγραφή γκολ στα στατιστικά)
        refreshMatchFromServer();
    }

    private void initViews() {
        View card = findViewById(R.id.included_match_card);

        tvHome  = card.findViewById(R.id.tv_home_name);
        tvAway  = card.findViewById(R.id.tv_away_name);
        tvScore = card.findViewById(R.id.tv_score);
        tvStatus = card.findViewById(R.id.tv_status);
        tvChamp = card.findViewById(R.id.tv_championship_name);
        ivHome  = card.findViewById(R.id.iv_home_logo);
        ivAway  = card.findViewById(R.id.iv_away_logo);

        btnLineups = findViewById(R.id.btn_update_lineups);
        btnStats   = findViewById(R.id.btn_add_statistics);
    }

    private void setupMatchCard() {
        if (selectedMatch == null) return;

        tvHome.setText(selectedMatch.getHomeTeam());
        tvAway.setText(selectedMatch.getAwayTeam());
        tvScore.setText(selectedMatch.getHomeScore() + " - " + selectedMatch.getAwayScore());
        tvStatus.setText(selectedMatch.getStatus().toUpperCase());
        tvChamp.setText(selectedMatch.getChampionshipName());

        Glide.with(this)
                .load(selectedMatch.getHomeLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivHome);

        Glide.with(this)
                .load(selectedMatch.getAwayLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivAway);
    }

    /**
     * ✅ ΔΙΟΡΘΩΣΗ: Φέρνει ξανά τα δεδομένα του αγώνα από τον server ώστε το σκορ
     * να είναι πάντα ενημερωμένο. Χωρίς αυτό, το σκορ παρέμενε στατικό
     * (αυτό που πέρασε το Intent) ακόμα κι αν είχε γίνει γκολ.
     */
    private void refreshMatchFromServer() {
        if (selectedMatch == null) return;

        RetrofitClient.getApiService().getAllMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Match m : response.body()) {
                        if (m.getId() == selectedMatch.getId()) {
                            // Ενημερώνουμε μόνο τα δεδομένα που μπορεί να άλλαξαν
                            selectedMatch.setHomeScore(m.getHomeScore());
                            selectedMatch.setAwayScore(m.getAwayScore());
                            selectedMatch.setStatus(m.getStatus());
                            setupMatchCard();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                // Αν αποτύχει το refresh, εμφανίζουμε τα τελευταία γνωστά δεδομένα
                Log.e(TAG, "Failed to refresh match: " + t.getMessage());
                setupMatchCard();
            }
        });
    }
}