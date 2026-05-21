package com.example.frontend;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchLiveControlActivity extends AppCompatActivity {

    private TextView tvHomeName, tvAwayName, tvHomeStatus, tvAwayStatus;
    private ImageView ivHomeLogo, ivAwayLogo;
    private ImageButton btnBack;
    private Button btnStartLive, btnSelectHome, btnSelectAway;
    private Match match;

    private List<Integer> homeSelectedIds = new ArrayList<>();
    private List<Integer> awaySelectedIds = new ArrayList<>();

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

        btnSelectHome.setOnClickListener(v -> showPlayerSelectionDialog(match.getHomeTeamId(), match.getHomeTeam(), true));
        btnSelectAway.setOnClickListener(v -> showPlayerSelectionDialog(match.getAwayTeamId(), match.getAwayTeam(), false));

        btnStartLive.setOnClickListener(v -> updateStatusToLive(match.getId(), homeSelectedIds, awaySelectedIds));
    }

    private void initViews() {
        tvHomeName = findViewById(R.id.tv_home_name_control);
        tvAwayName = findViewById(R.id.tv_away_name_control);
        ivHomeLogo = findViewById(R.id.iv_home_logo_control);
        ivAwayLogo = findViewById(R.id.iv_away_logo_control);
        btnBack = findViewById(R.id.btn_back_control);
        btnStartLive = findViewById(R.id.btn_start_live);
        btnSelectHome = findViewById(R.id.btn_select_home);
        btnSelectAway = findViewById(R.id.btn_select_away);
        tvHomeStatus = findViewById(R.id.tv_home_status);
        tvAwayStatus = findViewById(R.id.tv_away_status);
    }

    private void setupDisplay(Match match) {
        tvHomeName.setText(match.getHomeTeam());
        tvAwayName.setText(match.getAwayTeam());
        Glide.with(this).load(match.getHomeLogo()).into(ivHomeLogo);
        Glide.with(this).load(match.getAwayLogo()).into(ivAwayLogo);
        btnSelectHome.setText(match.getHomeTeam().toUpperCase() + " LINEUP");
        btnSelectAway.setText(match.getAwayTeam().toUpperCase() + " LINEUP");
    }

    private void showPlayerSelectionDialog(int teamId, String teamName, boolean isHome) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_player_selection, null);
        TextView tvDialogTitle = view.findViewById(R.id.tv_dialog_title);
        Button btnDone = view.findViewById(R.id.btn_confirm_selection);
        ImageButton btnClose = view.findViewById(R.id.btn_close_dialog);
        RecyclerView rv = view.findViewById(R.id.rv_dialog_players);

        rv.setLayoutManager(new LinearLayoutManager(this));
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getTeamPlayers(teamId).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlayerAdapter adapter = new PlayerAdapter(response.body(), count -> {
                        tvDialogTitle.setText(teamName + " (" + count + "/11)");
                        updateDialogDoneButton(btnDone, count);
                    });

                    adapter.setSelectedPlayerIds(isHome ? new ArrayList<>(homeSelectedIds) : new ArrayList<>(awaySelectedIds));
                    rv.setAdapter(adapter);
                    updateDialogDoneButton(btnDone, adapter.getSelectedPlayerIds().size());

                    btnDone.setOnClickListener(v -> {
                        if (isHome) {
                            homeSelectedIds = new ArrayList<>(adapter.getSelectedPlayerIds());
                        } else {
                            awaySelectedIds = new ArrayList<>(adapter.getSelectedPlayerIds());
                        }
                        updateStatusTexts();
                        checkOverallStartButton();
                        dialog.dismiss();
                    });
                }
            }
            @Override public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });

        if (btnClose != null) btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateDialogDoneButton(Button btn, int count) {
        boolean ok = (count == 11);
        btn.setEnabled(ok);
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(ok ? "#00E676" : "#444444")));
        btn.setTextColor(Color.parseColor(ok ? "#01051a" : "#FFFFFF"));
    }

    private void updateStatusTexts() {
        if (homeSelectedIds.size() == 11) {
            tvHomeStatus.setText("Lineup added successfully");
            tvHomeStatus.setTextColor(Color.parseColor("#00E676"));
        } else {
            tvHomeStatus.setText("Please add players");
            tvHomeStatus.setTextColor(Color.parseColor("#888888"));
        }

        if (awaySelectedIds.size() == 11) {
            tvAwayStatus.setText("Lineup added successfully");
            tvAwayStatus.setTextColor(Color.parseColor("#00E676"));
        } else {
            tvAwayStatus.setText("Please add players");
            tvAwayStatus.setTextColor(Color.parseColor("#888888"));
        }
    }

    private void checkOverallStartButton() {
        boolean ready = (homeSelectedIds.size() == 11 && awaySelectedIds.size() == 11);
        btnStartLive.setEnabled(ready);
        btnStartLive.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(ready ? "#00E676" : "#444444")));
        btnStartLive.setTextColor(Color.parseColor(ready ? "#01051a" : "#FFFFFF"));
        btnStartLive.setText(ready ? "START MATCH" : "WAITING FOR LINEUPS");
    }

    private void updateStatusToLive(int matchId, List<Integer> home, List<Integer> away) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.updateMatchStatusAndLineups(matchId, "live", home, away).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                // ✅ ΔΙΟΡΘΩΣΗ: Στηριζόμαστε αποκλειστικά στο response.isSuccessful() για την αποφυγή compile errors
                if (response.isSuccessful()) {
                    Toast.makeText(MatchLiveControlActivity.this, "Match is now LIVE!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("SERVER_ERROR", "Code: " + response.code());
                    Toast.makeText(MatchLiveControlActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(MatchLiveControlActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}