package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

public class LiveMatchActionsActivity extends AppCompatActivity {

    private Match selectedMatch;
    private TextView tvHome, tvAway, tvScore, tvStatus, tvChamp;
    private ImageView ivHome, ivAway;
    private MaterialButton btnLineups, btnStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_match_actions);

        // Λήψη του αγώνα από το προηγούμενο Activity
        selectedMatch = (Match) getIntent().getSerializableExtra("selected_match");

        initViews();

        if (selectedMatch != null) {
            setupMatchCard();
        }

        // 1. Back Button - Επιστροφή στην προηγούμενη οθόνη
        findViewById(R.id.btn_back_actions).setOnClickListener(v -> finish());

        // 2. Κουμπί για Διαχείριση Ενδεκάδας (Αλλαγές) - ΕΝΕΡΓΟΠΟΙΗΘΗΚΕ 🚀
        btnLineups.setOnClickListener(v -> {
            Intent intent = new Intent(LiveMatchActionsActivity.this, UpdateLineupsActivity.class);
            intent.putExtra(UpdateLineupsActivity.EXTRA_MATCH_ID,     selectedMatch.getId());
            intent.putExtra(UpdateLineupsActivity.EXTRA_HOME_TEAM_ID, selectedMatch.getHomeTeamId());
            intent.putExtra(UpdateLineupsActivity.EXTRA_AWAY_TEAM_ID, selectedMatch.getAwayTeamId());
            intent.putExtra(UpdateLineupsActivity.EXTRA_HOME_TEAM,    selectedMatch.getHomeTeam());
            intent.putExtra(UpdateLineupsActivity.EXTRA_AWAY_TEAM,    selectedMatch.getAwayTeam());
            startActivity(intent);
        });

        // 3. Κουμπί για Προσθήκη Στατιστικών (Γκολ, Κάρτες κλπ)
        btnStats.setOnClickListener(v -> {
            // Εδώ θα ανοίγεις την οθόνη των στατιστικών (όταν τη φτιάξεις)
            // Intent intent = new Intent(this, AddStatisticsActivity.class);
            // intent.putExtra("selected_match", selectedMatch);
            // startActivity(intent);
        });
    }

    private void initViews() {
        // Βρίσκουμε το included layout της κάρτας
        View card = findViewById(R.id.included_match_card);

        // Στοιχεία μέσα στην κάρτα
        tvHome = card.findViewById(R.id.tv_home_name);
        tvAway = card.findViewById(R.id.tv_away_name);
        tvScore = card.findViewById(R.id.tv_score);
        tvStatus = card.findViewById(R.id.tv_status);
        tvChamp = card.findViewById(R.id.tv_championship_name);
        ivHome = card.findViewById(R.id.iv_home_logo);
        ivAway = card.findViewById(R.id.iv_away_logo);

        // Κουμπιά ενεργειών
        btnLineups = findViewById(R.id.btn_update_lineups);
        btnStats = findViewById(R.id.btn_add_statistics);
    }

    private void setupMatchCard() {
        tvHome.setText(selectedMatch.getHomeTeam());
        tvAway.setText(selectedMatch.getAwayTeam());

        // Μορφοποίηση Score: π.χ. "2 - 1"
        String currentScore = selectedMatch.getHomeScore() + " - " + selectedMatch.getAwayScore();
        tvScore.setText(currentScore);

        tvStatus.setText(selectedMatch.getStatus().toUpperCase());
        tvChamp.setText(selectedMatch.getChampionshipName());

        // Φόρτωση Logos με Glide
        Glide.with(this)
                .load(selectedMatch.getHomeLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivHome);

        Glide.with(this)
                .load(selectedMatch.getAwayLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivAway);
    }
}