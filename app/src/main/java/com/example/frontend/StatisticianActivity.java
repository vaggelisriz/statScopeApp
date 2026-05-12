package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticianActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnStartMatch;
    private Button btnManageTeams;
    private Button btnManageChampionships;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistician);

        initDashboardComponents();
        setupDashboardActions();
    }

    /**
     * Initialize UI components from the layout
     */
    private void initDashboardComponents() {
        btnBack = findViewById(R.id.btn_back_dashboard);
        btnStartMatch = findViewById(R.id.btn_start_match);
        btnManageTeams = findViewById(R.id.btn_manage_teams);
        btnManageChampionships = findViewById(R.id.btn_manage_championships);
    }

    /**
     * Define the behavior for each button click
     */
    private void setupDashboardActions() {
        // Return to the previous screen (MainActivity)
        btnBack.setOnClickListener(v -> finish());

        // Navigation to Live Matches screen
        btnStartMatch.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticianActivity.this, LiveMatchesActivity.class);
            startActivity(intent);
        });

        // Placeholders for other features
        btnManageTeams.setOnClickListener(v ->
                Toast.makeText(this, "Opening Team Management...", Toast.LENGTH_SHORT).show());

        btnManageChampionships.setOnClickListener(v ->
                Toast.makeText(this, "Opening Championship Setup...", Toast.LENGTH_SHORT).show());
    }
}