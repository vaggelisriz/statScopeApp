package com.example.statscopeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticianActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistician);

        setupDashboardActions();
    }

    private void setupDashboardActions() {
        Button btnStart = findViewById(R.id.btn_start_match);
        Button btnHistory = findViewById(R.id.btn_view_history);
        Button btnTeams = findViewById(R.id.btn_manage_teams);

        // Click Logic with Animation
        btnStart.setOnClickListener(v -> animateAndToast(v, "Starting New Match..."));
        btnHistory.setOnClickListener(v -> animateAndToast(v, "Loading History..."));
        btnTeams.setOnClickListener(v -> animateAndToast(v, "Opening Team Manager..."));
    }

    private void animateAndToast(View view, String message) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }).start();
    }
}