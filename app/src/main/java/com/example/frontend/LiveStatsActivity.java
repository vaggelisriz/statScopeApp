package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveStatsActivity extends AppCompatActivity {

    private static final String TAG = "LiveStatsActivity";

    // Intent Data Variables
    private int matchId, homeTeamId, awayTeamId;
    private String homeTeamName, awayTeamName, homeLogoPath, awayLogoPath;

    // Scoreboard UI Views
    private TextView tvHomeName, tvAwayName, tvHomeScore, tvAwayScore;
    private ImageView ivHomeLogo, ivAwayLogo;

    // Custom Target Selectors
    private TextView tvSelectorTeam, tvSelectorPlayer;

    // Live Feed Log RecyclerView
    private RecyclerView rvLiveFeed;
    private LiveFeedAdapter feedAdapter;
    private final List<LogItem> feedLogList = new ArrayList<>();

    // State Variables
    private final List<Player> homeStarters = new ArrayList<>();
    private final List<Player> awayStarters = new ArrayList<>();
    private boolean isTeamSelected = false;
    private boolean isHomeSelected = true;
    private Player selectedPlayer = null;
    private int homeScoreCount = 0;
    private int awayScoreCount = 0;

    // API Service
    private ApiService apiService;

    // Εσωτερικό Μοντέλο για το Live Feed Log της οθόνης
    public static class LogItem {
        String message, eventType, outcome;
        int playerId;
        boolean isHome, isGoal;

        LogItem(String m, String e, String o, int pId, boolean h, boolean g) {
            this.message = m;
            this.eventType = e;
            this.outcome = o;
            this.playerId = pId;
            this.isHome = h;
            this.isGoal = g;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stats);

        apiService = RetrofitClient.getApiService();

        // Λήψη δεδομένων από το Intent
        matchId = getIntent().getIntExtra("match_id", -1);
        homeTeamId = getIntent().getIntExtra("home_team_id", -1);
        awayTeamId = getIntent().getIntExtra("away_team_id", -1);
        homeTeamName = getIntent().getStringExtra("home_team_name");
        awayTeamName = getIntent().getStringExtra("away_team_name");
        homeLogoPath = getIntent().getStringExtra("home_logo");
        awayLogoPath = getIntent().getStringExtra("away_logo");

        // ✅ ΒΗΜΑ 1: Συνδέουμε τα Views με το XML (Τρέχει πρώτο!)
        initViews();

        // ✅ ΒΗΜΑ 2: Τώρα που τα Views δεν είναι null, διαβάζουμε το σκορ από το Intent με ασφάλεια
        homeScoreCount = getIntent().getIntExtra("home_score", 0);
        awayScoreCount = getIntent().getIntExtra("away_score", 0);

        // ✅ ΒΗΜΑ 3: Τοποθετούμε τις τιμές στα TextViews
        tvHomeScore.setText(String.valueOf(homeScoreCount));
        tvAwayScore.setText(String.valueOf(awayScoreCount));

        // Ρύθμιση Πίνακα Αποτελεσμάτων (Ονόματα & Logos)
        setupScoreboard();

        // Στήσιμο του Live Feed RecyclerView
        feedAdapter = new LiveFeedAdapter(feedLogList, this::showUndoDialog);
        rvLiveFeed.setLayoutManager(new LinearLayoutManager(this));
        rvLiveFeed.setAdapter(feedAdapter);

        // Φόρτωση Δεδομένων από το Backend
        loadMatchLineups(matchId);
        loadExistingStats();

        // Ενεργοποίηση Listener για Selectors & Κουμπιά Ενεργειών
        setupCustomSelectors();
        setupActionButtons();

        // Back Button
        findViewById(R.id.btn_back_control).setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvHomeName = findViewById(R.id.tv_live_home_name);
        tvAwayName = findViewById(R.id.tv_live_away_name);
        tvHomeScore = findViewById(R.id.tv_live_home_score);
        tvAwayScore = findViewById(R.id.tv_live_away_score);
        ivHomeLogo = findViewById(R.id.iv_live_home_logo);
        ivAwayLogo = findViewById(R.id.iv_live_away_logo);
        tvSelectorTeam = findViewById(R.id.tv_selector_team);
        tvSelectorPlayer = findViewById(R.id.tv_selector_player);
        rvLiveFeed = findViewById(R.id.rv_live_feed);
    }

    private void setupScoreboard() {
        tvHomeName.setText(homeTeamName != null ? homeTeamName : "HOME");
        tvAwayName.setText(awayTeamName != null ? awayTeamName : "AWAY");

        String finalHomeLogo = homeLogoPath;
        String finalAwayLogo = awayLogoPath;

        if (homeLogoPath != null && !homeLogoPath.startsWith("http")) {
            String baseUrl = Config.BASE_URL.replace("api/", "");
            finalHomeLogo = baseUrl + homeLogoPath;
        }

        if (awayLogoPath != null && !awayLogoPath.startsWith("http")) {
            String baseUrl = Config.BASE_URL.replace("api/", "");
            finalAwayLogo = baseUrl + awayLogoPath;
        }

        Glide.with(this)
                .load(finalHomeLogo)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivHomeLogo);

        Glide.with(this)
                .load(finalAwayLogo)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivAwayLogo);
    }

    private void loadMatchLineups(int mId) {
        apiService.getMatchLineups(mId).enqueue(new Callback<LineupResponse>() {
            @Override
            public void onResponse(@NonNull Call<LineupResponse> call, @NonNull Response<LineupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    homeStarters.clear();
                    awayStarters.clear();

                    if (response.body().getPlayers() != null) {
                        for (Player p : response.body().getPlayers()) {
                            // 🛠️ ΕΔΩ ΕΙΝΑΙ Η ΛΥΣΗ: Μετατρέπουμε ρητά το Integer της Gson σε primitive int
                            // για να σπάσει το reference mismatch στη σύγκριση με το homeTeamId/awayTeamId
                            int playerTeamId = p.getTeamId();

                            if (playerTeamId == homeTeamId) {
                                homeStarters.add(p);
                            } else if (playerTeamId == awayTeamId) {
                                awayStarters.add(p);
                            }
                        }
                    }
                    Log.d(TAG, "Lineups loaded. Home starters: " + homeStarters.size() + " | Away: " + awayStarters.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LineupResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to load lineups: " + t.getMessage());
            }
        });
    }

    private void loadExistingStats() {
        apiService.getMatchStatistics(matchId).enqueue(new Callback<List<MatchStatistic>>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchStatistic>> call, @NonNull Response<List<MatchStatistic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    feedLogList.clear();
                    homeScoreCount = 0;
                    awayScoreCount = 0;

                    for (MatchStatistic stat : response.body()) {
                        boolean wasGoal = stat.getOutcome() != null && stat.getOutcome().toLowerCase().contains("goal");

                        feedLogList.add(0, new LogItem(
                                "[" + stat.getTeamName() + "] " + stat.getPlayerName() + ": " + stat.getOutcome().toUpperCase(),
                                stat.getEventType(), stat.getOutcome(), stat.getPlayerId(),
                                stat.getTeamId() == homeTeamId, wasGoal
                        ));

                        if (wasGoal) {
                            if (stat.getTeamId() == homeTeamId) homeScoreCount++;
                            else awayScoreCount++;
                        }
                    }

                    tvHomeScore.setText(String.valueOf(homeScoreCount));
                    tvAwayScore.setText(String.valueOf(awayScoreCount));
                    MatchScoreManager.getInstance().updateScore(matchId, homeScoreCount, awayScoreCount);
                    feedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MatchStatistic>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch existing stats: " + t.getMessage());
            }
        });
    }

    private void setupCustomSelectors() {
        tvSelectorTeam.setOnClickListener(v -> {
            String homeName = (homeTeamName != null) ? homeTeamName : "Home Team";
            String awayName = (awayTeamName != null) ? awayTeamName : "Away Team";
            CharSequence[] teams = {homeName, awayName};

            new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                    .setItems(teams, (d, w) -> {
                        isHomeSelected = (w == 0);
                        isTeamSelected = true;
                        tvSelectorTeam.setText(teams[w]);
                        selectedPlayer = null;
                        tvSelectorPlayer.setText("Select Player");
                    }).show();
        });

        tvSelectorPlayer.setOnClickListener(v -> {
            if (!isTeamSelected) {
                Toast.makeText(this, "Please select a team first!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Player> currentList = isHomeSelected ? homeStarters : awayStarters;

            if (currentList == null || currentList.isEmpty()) {
                Toast.makeText(this, "No players available. Lineup empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> validNames = new ArrayList<>();
            List<Player> validPlayers = new ArrayList<>();

            for (Player p : currentList) {
                if (p != null && p.getName() != null) {
                    validNames.add(p.getName());
                    validPlayers.add(p);
                }
            }

            if (validNames.isEmpty()) {
                Toast.makeText(this, "No valid players found.", Toast.LENGTH_SHORT).show();
                return;
            }

            CharSequence[] playerNames = validNames.toArray(new CharSequence[0]);

            new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                    .setItems(playerNames, (d, w) -> {
                        selectedPlayer = validPlayers.get(w);
                        tvSelectorPlayer.setText(playerNames[w]);
                    }).show();
        });
    }

    private void setupActionButtons() {
        // ✅ ΔΙΟΡΘΩΘΗΚΕ: Τα πεδία μετατράπηκαν σε lowercase για να τα αναγνωρίζει το saveEvent.php
        findViewById(R.id.btn_live_shot).setOnClickListener(v ->
                handleAction("shot", new String[]{"saved", "missed", "goal"}));

        findViewById(R.id.btn_live_tackle).setOnClickListener(v ->
                handleAction("tackle", new String[]{"success", "unsuccess"}));

        findViewById(R.id.btn_live_pass).setOnClickListener(v ->
                handleAction("pass", new String[]{"accurate", "inaccurate"}));

        findViewById(R.id.btn_live_cross).setOnClickListener(v ->
                handleAction("cross", new String[]{"success", "unsuccess"}));

        findViewById(R.id.btn_live_assist).setOnClickListener(v ->
                handleAction("assist", new String[]{"goal assist"}));

        findViewById(R.id.btn_live_turnover).setOnClickListener(v ->
                handleAction("mistake", new String[]{"turnover", "error"}));

        findViewById(R.id.btn_live_fouls).setOnClickListener(v ->
                handleAction("foul", new String[]{"committed", "won"}));

        findViewById(R.id.btn_live_corner).setOnClickListener(v ->
                handleAction("corner", new String[]{"taken"}));

        findViewById(R.id.btn_live_cards).setOnClickListener(v ->
                handleAction("card", new String[]{"yellow", "red"}));
    }

    private void handleAction(String type, String[] options) {
        if (selectedPlayer == null) {
            Toast.makeText(this, "Select a player first!", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                // ✅ ΔΙΟΡΘΩΘΗΚΕ: Έλεγχος ισότητας με το πεζό "goal"
                .setItems(options, (d, w) -> sendStatisticToBackend(type, options[w], options[w].equals("goal")))
                .show();
    }

    private void sendStatisticToBackend(String type, String outcome, boolean isGoal) {
        if (selectedPlayer == null) return;

        // ✅ ΔΙΟΡΘΩΘΗΚΕ: Το saveEvent.php δεν χρειάζεται team_id, αλλά περιμένει event_minute (βάζουμε 0)
        apiService.saveLiveEvent(matchId, selectedPlayer.getId(), type, outcome, 0).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    if (isGoal) {
                        if (isHomeSelected) homeScoreCount++; else awayScoreCount++;

                        runOnUiThread(() -> {
                            tvHomeScore.setText(String.valueOf(homeScoreCount));
                            tvAwayScore.setText(String.valueOf(awayScoreCount));
                        });

                        MatchScoreManager.getInstance().updateScore(matchId, homeScoreCount, awayScoreCount);
                    }

                    // Προσθήκη live στο Feed Log της οθόνης
                    runOnUiThread(() -> {
                        String teamLabel = isHomeSelected ? homeTeamName : awayTeamName;
                        String displayMsg = "[" + teamLabel + "] " + selectedPlayer.getName() + ": " + type.toUpperCase() + " -> " + outcome.toUpperCase();

                        feedLogList.add(0, new LogItem(displayMsg, type, outcome, selectedPlayer.getId(), isHomeSelected, isGoal));
                        feedAdapter.notifyItemInserted(0);
                        rvLiveFeed.scrollToPosition(0);
                    });
                } else {
                    String serverError = (response.body() != null && response.body().getError() != null) ? response.body().getError() : "Rejected by server";
                    Toast.makeText(LiveStatsActivity.this, "Error: " + serverError, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                Log.e("ADD_STAT_ERROR", "Reason: " + t.getMessage(), t);
                Toast.makeText(LiveStatsActivity.this, "Network Error: Cannot save event.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUndoDialog(int position) {
        LogItem item = feedLogList.get(position);

        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    // Κλήση API για διαγραφή του επιλεγμένου event
                    apiService.deleteMatchStatistic(matchId, item.playerId, item.eventType, item.outcome).enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                if (item.isGoal) {
                                    if (item.isHome) {
                                        if (homeScoreCount > 0) homeScoreCount--;
                                    } else {
                                        if (awayScoreCount > 0) awayScoreCount--;
                                    }

                                    runOnUiThread(() -> {
                                        tvHomeScore.setText(String.valueOf(homeScoreCount));
                                        tvAwayScore.setText(String.valueOf(awayScoreCount));
                                    });

                                    MatchScoreManager.getInstance().updateScore(matchId, homeScoreCount, awayScoreCount);
                                }

                                runOnUiThread(() -> {
                                    feedLogList.remove(position);
                                    feedAdapter.notifyItemRemoved(position);
                                    Toast.makeText(LiveStatsActivity.this, "Event reverted.", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                Toast.makeText(LiveStatsActivity.this, "Failed to revert event.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                            Toast.makeText(LiveStatsActivity.this, "Network error: Undo failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ─── RecyclerView Adapter ─────────────────────────────────────────────────
    public static class LiveFeedAdapter extends RecyclerView.Adapter<LiveFeedAdapter.ViewHolder> {
        private final List<LogItem> logs;
        private final OnItemClickListener listener;

        public interface OnItemClickListener { void onItemClick(int position); }

        public LiveFeedAdapter(List<LogItem> l, OnItemClickListener lsn) {
            this.logs = l;
            this.listener = lsn;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_log, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(logs.get(position).message);
            holder.btnDelete.setOnClickListener(v -> listener.onItemClick(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() { return logs.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageButton btnDelete;

            ViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.tv_log_message);
                btnDelete = v.findViewById(R.id.btn_delete_log);
            }
        }
    }
}