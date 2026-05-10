package com.example.statscopeapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LineupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineup);

        // Enable ActionBar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lineups");
        }

        setupView();
    }

    // Handle back button click
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupView() {
        TextView tvHome = findViewById(R.id.tv_lineup_home);
        TextView tvAway = findViewById(R.id.tv_lineup_away);

        // Get data from Intent
        String homeName = getIntent().getStringExtra("HOME_TEAM");
        String awayName = getIntent().getStringExtra("AWAY_TEAM");

        if (homeName != null) tvHome.setText(homeName);
        if (awayName != null) tvAway.setText(awayName);
    }
}