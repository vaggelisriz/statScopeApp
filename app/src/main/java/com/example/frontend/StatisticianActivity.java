package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticianActivity extends AppCompatActivity {

    private Button btnSetMatchLive, btnManageMatches; // ✂️ Αφαιρέθηκε το btnManageStandings
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistician);

        btnBack            = findViewById(R.id.btn_back_dashboard);
        btnSetMatchLive    = findViewById(R.id.btn_set_match_live);
        btnManageMatches   = findViewById(R.id.btn_manage_matches); // 🛠️ Εδώ μπαίνεις για τη διαχείριση των αγώνων

        // Επιστροφή στην MainActivity
        btnBack.setOnClickListener(v -> finish());

        // Ανοίγει τη λίστα Scheduled ματς → για να στήσει ένα ματς live
        btnSetMatchLive.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticianActivity.this, ScheduledMatchesActivity.class);
            startActivity(intent);
        });

        // Ανοίγει τη λίστα Live ματς → για να διαχειριστεί αγώνα που τρέχει (και τα Lineups του)
        btnManageMatches.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticianActivity.this, LiveMatchesActivity.class);
            startActivity(intent);
        });
    }
}