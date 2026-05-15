package com.example.frontend;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchLiveControlActivity extends AppCompatActivity {

    private TextView tvHomeName, tvAwayName, tvStatus;
    private ImageView ivHomeLogo, ivAwayLogo;
    private ImageButton btnBack;
    private Button btnStartLive;
    private Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_live_control);

        initViews();

        match = (Match) getIntent().getSerializableExtra("selected_match");

        if (match != null) {
            setupDisplay(match);
        }

        btnBack.setOnClickListener(v -> finish());

        btnStartLive.setOnClickListener(v -> {
            if (match != null) {
                updateStatusToLive(match.getId());
            }
        });
    }

    private void initViews() {
        tvHomeName = findViewById(R.id.tv_home_name_control);
        tvAwayName = findViewById(R.id.tv_away_name_control);
        tvStatus = findViewById(R.id.tv_status_control);
        ivHomeLogo = findViewById(R.id.iv_home_logo_control);
        ivAwayLogo = findViewById(R.id.iv_away_logo_control);
        btnBack = findViewById(R.id.btn_back_control);
        btnStartLive = findViewById(R.id.btn_start_live);
    }

    private void setupDisplay(Match match) {
        tvHomeName.setText(match.getHomeTeam());
        tvAwayName.setText(match.getAwayTeam());
        tvStatus.setText("Status: " + match.getStatus());

        Glide.with(this).load(match.getHomeLogo()).into(ivHomeLogo);
        Glide.with(this).load(match.getAwayLogo()).into(ivAwayLogo);
    }

    private void updateStatusToLive(int matchId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.updateMatchStatus(matchId, "live").enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MatchLiveControlActivity.this, "Live Match Started!", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {
                        finish();
                    }, 1000);
                } else {
                    Toast.makeText(MatchLiveControlActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MatchLiveControlActivity.this, "Σφάλμα σύνδεσης", Toast.LENGTH_SHORT).show();
            }
        });
    }
}