package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticianActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistician);

        ImageButton btnBack = findViewById(R.id.btn_back_dashboard);
        Button btnStartMatch = findViewById(R.id.btn_start_match);

        btnBack.setOnClickListener(v -> finish());
        btnStartMatch.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticianActivity.this, LiveMatchesActivity.class);
            startActivity(intent);
        });
    }
}