package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveMatchActionsActivity extends AppCompatActivity {

    private static final String TAG = "LiveMatchActions";

    private Match selectedMatch; //
    private TextView tvHome, tvAway, tvScore, tvStatus, tvChamp; //
    private ImageView ivHome, ivAway; //
    private MaterialButton btnLineups, btnStats, btnFinishMatch; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_match_actions); //

        selectedMatch = (Match) getIntent().getSerializableExtra("selected_match"); //

        initViews(); //

        findViewById(R.id.btn_back_actions).setOnClickListener(v -> finish()); //

        // Κλικ στα στατιστικά
        btnStats.setOnClickListener(v -> {
            if (selectedMatch != null) { //
                Intent intent = new Intent(LiveMatchActionsActivity.this, LiveStatsActivity.class); //
                intent.putExtra("match_id",       selectedMatch.getId()); //
                intent.putExtra("home_team_id",  selectedMatch.getHomeTeamId()); //
                intent.putExtra("away_team_id",  selectedMatch.getAwayTeamId()); //
                intent.putExtra("home_team_name", selectedMatch.getHomeTeam()); //
                intent.putExtra("away_team_name", selectedMatch.getAwayTeam()); //
                intent.putExtra("home_logo",      selectedMatch.getHomeLogo()); //
                intent.putExtra("away_logo",      selectedMatch.getAwayLogo()); //

                intent.putExtra("home_score",     selectedMatch.getHomeScore()); //
                intent.putExtra("away_score",     selectedMatch.getAwayScore()); //
                startActivity(intent); //
            }
        });

        // ΚΛΙΚ ΣΤΑ LINEUPS (Σύνδεση με UpdateLineupsActivity και σωστά Keys)
        btnLineups.setOnClickListener(v -> {
            if (selectedMatch != null) {
                Intent intent = new Intent(LiveMatchActionsActivity.this, UpdateLineupsActivity.class);

                // Χρήση των ακριβών keys που ζητάει η UpdateLineupsActivity
                intent.putExtra(UpdateLineupsActivity.EXTRA_MATCH_ID, selectedMatch.getId());
                intent.putExtra(UpdateLineupsActivity.EXTRA_HOME_TEAM_ID, selectedMatch.getHomeTeamId());
                intent.putExtra(UpdateLineupsActivity.EXTRA_AWAY_TEAM_ID, selectedMatch.getAwayTeamId());
                intent.putExtra(UpdateLineupsActivity.EXTRA_HOME_TEAM, selectedMatch.getHomeTeam());
                intent.putExtra(UpdateLineupsActivity.EXTRA_AWAY_TEAM, selectedMatch.getAwayTeam());

                startActivity(intent);
            }
        });

        // ΥΛΟΠΟΙΗΣΗ: Λογική για τη λήξη του αγώνα (Finish Match)
        btnFinishMatch.setOnClickListener(v -> {
            if (selectedMatch == null) return;

            new AlertDialog.Builder(this)
                    .setTitle("Finish Match")
                    .setMessage("Are you sure you want to end this match? This will complete the game and calculate league standings.")
                    .setPositiveButton("Yes", (dialog, which) -> sendFinishMatchToBackend())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    // Αποστολή αιτήματος λήξης στο Backend
    private void sendFinishMatchToBackend() {
        RetrofitClient.getApiService().finishMatch(selectedMatch.getId()).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(LiveMatchActionsActivity.this, "Match finished successfully!", Toast.LENGTH_LONG).show();

                    // Κλείνουμε την οθόνη και επιστρέφουμε στο Dashboard
                    finish();
                } else {
                    String error = (response.body() != null && response.body().getError() != null) ? response.body().getError() : "Unknown server error";
                    Toast.makeText(LiveMatchActionsActivity.this, "Failed: " + error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(LiveMatchActionsActivity.this, "Network Error: Could not end match.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume(); //
        refreshMatchFromServer(); //
    }

    private void initViews() {
        View card = findViewById(R.id.included_match_card); //

        tvHome  = card.findViewById(R.id.tv_home_name); //
        tvAway  = card.findViewById(R.id.tv_away_name); //
        tvScore = card.findViewById(R.id.tv_score); //
        tvStatus = card.findViewById(R.id.tv_status); //
        tvChamp = card.findViewById(R.id.tv_championship_name); //
        ivHome  = card.findViewById(R.id.iv_home_logo); //
        ivAway  = card.findViewById(R.id.iv_away_logo); //

        btnLineups     = findViewById(R.id.btn_update_lineups); //
        btnStats       = findViewById(R.id.btn_add_statistics); //
        btnFinishMatch = findViewById(R.id.btn_finish_match); // ✅ Bind του σωστού ID από το XML
    }

    private void setupMatchCard() {
        if (selectedMatch == null) return; //

        tvHome.setText(selectedMatch.getHomeTeam()); //
        tvAway.setText(selectedMatch.getAwayTeam()); //
        tvScore.setText(selectedMatch.getHomeScore() + " - " + selectedMatch.getAwayScore()); //
        tvStatus.setText(selectedMatch.getStatus().toUpperCase()); //
        tvChamp.setText(selectedMatch.getChampionshipName()); //

        Glide.with(this) //
                .load(selectedMatch.getHomeLogo()) //
                .placeholder(android.R.drawable.ic_menu_gallery) //
                .into(ivHome); //

        Glide.with(this) //
                .load(selectedMatch.getAwayLogo()) //
                .placeholder(android.R.drawable.ic_menu_gallery) //
                .into(ivAway); //
    }

    private void refreshMatchFromServer() {
        if (selectedMatch == null) return; //

        RetrofitClient.getApiService().getAllMatches().enqueue(new Callback<List<Match>>() { //
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) { //
                    for (Match m : response.body()) { //
                        if (m.getId() == selectedMatch.getId()) { //
                            selectedMatch.setHomeScore(m.getHomeScore()); //
                            selectedMatch.setAwayScore(m.getAwayScore()); //
                            selectedMatch.setStatus(m.getStatus()); //
                            setupMatchCard(); //
                            break; //
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e(TAG, "Failed to refresh match: " + t.getMessage()); //
                setupMatchCard(); //
            }
        });
    }
}