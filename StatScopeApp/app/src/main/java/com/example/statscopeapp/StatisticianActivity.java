package com.example.statscopeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticianActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistician);

        setupDashboardActions();
        setupBackNavigation();
    }

    private void setupDashboardActions() {
        ImageButton btnBack = findViewById(R.id.btn_back_dashboard);
        Button btnStart = findViewById(R.id.btn_start_match);
        Button btnTeams = findViewById(R.id.btn_manage_teams);
        Button btnChampionships = findViewById(R.id.btn_manage_championships);

        if (btnBack != null) btnBack.setOnClickListener(v -> handleBackAction());

        // Section: MATCH DAY
        if (btnStart != null) {
            btnStart.setOnClickListener(v -> animateAndStart(v, new Intent(this, MatchSelectionActivity.class)));
        }

        // Section: ADMINISTRATION
        if (btnTeams != null) {
            btnTeams.setOnClickListener(v -> animateAndStart(v, new Intent(this, TeamListActivity.class)));
        }

        if (btnChampionships != null) {
            btnChampionships.setOnClickListener(v -> animateAndShowMessage(v, "Opening Championship Setup..."));
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackAction();
            }
        });
    }

    private void handleBackAction() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void animateAndStart(View view, Intent intent) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            startActivity(intent);
        }).start();
    }

    private void animateAndShowMessage(View view, String text) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }).start();
    }
}