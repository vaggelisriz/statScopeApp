package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticianActivity extends AppCompatActivity {

    private Button btnSetMatchLive, btnManageMatches;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistician);

        // UI Initialization
        btnBack = findViewById(R.id.btn_back_dashboard);
        btnSetMatchLive = findViewById(R.id.btn_set_match_live);
        btnManageMatches = findViewById(R.id.btn_manage_matches);

        // 1. Back Button - Επιστροφή στην προηγούμενη οθόνη (Main)
        btnBack.setOnClickListener(v -> finish());

        // 2. Set Match Live - Ανοίγει τη λίστα με τα Scheduled ματς
        btnSetMatchLive.setOnClickListener(v -> {
            // Εδώ στέλνουμε τον χρήστη στην οθόνη επιλογής αγώνα
            Intent intent = new Intent(StatisticianActivity.this, ScheduledMatchesActivity.class);
            startActivity(intent);
        });

        // 3. Manage Matches - Ανοίγει το LiveMatchesActivity (Διαχείριση ήδη Live αγώνων)
        btnManageMatches.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticianActivity.this, LiveMatchesActivity.class);
            startActivity(intent);
        });
    }
}