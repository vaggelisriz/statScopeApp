package com.example.frontend;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveStatsActivity extends AppCompatActivity {

    private static final String TAG = "LiveStatsActivity";

    private android.widget.EditText etMatchMinute;

    private int matchId, homeTeamId, awayTeamId;
    private String homeTeamName, awayTeamName, homeLogoPath, awayLogoPath;

    private TextView tvHomeName, tvAwayName, tvHomeScore, tvAwayScore;
    private ImageView ivHomeLogo, ivAwayLogo;

    private Button btnSelectHomeTeam, btnSelectAwayTeam;

    private RecyclerView rvLiveFeed;
    private LiveFeedAdapter feedAdapter;
    private final List<LogItem> feedLogList = new ArrayList<>();

    private final List<Player> homeStarters = new ArrayList<>();
    private final List<Player> awayStarters = new ArrayList<>();

    private int homeScoreCount = 0;
    private int awayScoreCount = 0;

    private ApiService apiService;
    private final Map<String, String[]> eventOptionsMap = new HashMap<>();

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
        initializeEventOptions();

        matchId = getIntent().getIntExtra("match_id", -1);
        homeTeamId = getIntent().getIntExtra("home_team_id", -1);
        awayTeamId = getIntent().getIntExtra("away_team_id", -1);
        homeTeamName = getIntent().getStringExtra("home_team_name");
        awayTeamName = getIntent().getStringExtra("away_team_name");
        homeLogoPath = getIntent().getStringExtra("home_logo");
        awayLogoPath = getIntent().getStringExtra("away_logo");

        initViews();

        if (etMatchMinute != null) {
            etMatchMinute.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                        actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT ||
                        (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {

                    hideKeyboard();
                    etMatchMinute.clearFocus();
                    return true;
                }
                return false;
            });
        }

        homeScoreCount = getIntent().getIntExtra("home_score", 0);
        awayScoreCount = getIntent().getIntExtra("away_score", 0);
        tvHomeScore.setText(String.valueOf(homeScoreCount));
        tvAwayScore.setText(String.valueOf(awayScoreCount));

        setupScoreboard();
        setupMainActionListeners();

        feedAdapter = new LiveFeedAdapter(feedLogList, this::showUndoDialog);
        rvLiveFeed.setLayoutManager(new LinearLayoutManager(this));
        rvLiveFeed.setAdapter(feedAdapter);

        loadMatchLineups(matchId);
        loadExistingStats();

        findViewById(R.id.btn_back_control).setOnClickListener(v -> finish());
        findViewById(android.R.id.content).setOnClickListener(v -> hideKeyboard());
    }

    private void initViews() {
        tvHomeName = findViewById(R.id.tv_live_home_name);
        tvAwayName = findViewById(R.id.tv_live_away_name);
        tvHomeScore = findViewById(R.id.tv_live_home_score);
        tvAwayScore = findViewById(R.id.tv_live_away_score);
        ivHomeLogo = findViewById(R.id.iv_live_home_logo);
        ivAwayLogo = findViewById(R.id.iv_live_away_logo);
        btnSelectHomeTeam = findViewById(R.id.btn_select_home_team);
        btnSelectAwayTeam = findViewById(R.id.btn_select_away_team);
        rvLiveFeed = findViewById(R.id.rv_live_feed);
        etMatchMinute = findViewById(R.id.et_match_minute);
    }

    private void initializeEventOptions() {
        eventOptionsMap.put("SHOTS", new String[]{"Goal", "Saved", "Off Target", "Blocked"});
        eventOptionsMap.put("PASSES", new String[]{"Accurate", "Inaccurate"});
        eventOptionsMap.put("TACKLES", new String[]{"Successful Tackle", "Unsuccessful Tackle"});
        eventOptionsMap.put("FOULS", new String[]{"Foul Won", "Foul Committed"});
        eventOptionsMap.put("ERRORS", new String[]{"Simple Mistake", "Mistake Leading to Opponent Goal"});
    }

    private void setupMainActionListeners() {
        btnSelectHomeTeam.setText(homeTeamName != null ? homeTeamName : "HOME TEAM");
        btnSelectAwayTeam.setText(awayTeamName != null ? awayTeamName : "AWAY TEAM");

        btnSelectHomeTeam.setOnClickListener(v -> {
            hideKeyboard();
            openLineupDialog(homeStarters, true);
        });

        btnSelectAwayTeam.setOnClickListener(v -> {
            hideKeyboard();
            openLineupDialog(awayStarters, false);
        });
    }

    private void openLineupDialog(List<Player> playersList, boolean isHome) {
        if (playersList.isEmpty()) {
            Toast.makeText(this, "Lineup is empty or loading...", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_lineup_grid, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_team_title);
        RecyclerView rvPlayers = dialogView.findViewById(R.id.rv_dialog_players);
        Button btnTeamEvent = dialogView.findViewById(R.id.btn_dialog_team_event);

        tvTitle.setText(isHome ? homeTeamName : awayTeamName);

        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setView(dialogView)
                .create();

        if (btnTeamEvent != null) {
            btnTeamEvent.setOnClickListener(v -> {
                alertDialog.dismiss();
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    openTeamEventsMenu(isHome);
                }, 100);
            });
        }

        LineupAdapter adapter = new LineupAdapter(playersList, player -> {
            alertDialog.dismiss();
            openEventSelectionDialog(player, isHome);
        });

        rvPlayers.setLayoutManager(new GridLayoutManager(this, 3));
        rvPlayers.setAdapter(adapter);
        alertDialog.show();
    }

    private void openEventSelectionDialog(Player player, boolean isHome) {
        String[] events = eventOptionsMap.keySet().toArray(new String[0]);

        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Select Event Type for " + player.getName())
                .setItems(events, (dialog, which) -> {
                    String selectedCategory = events[which];
                    openOutcomeSelectionDialog(player, selectedCategory, isHome);
                }).show();
    }

    private void openOutcomeSelectionDialog(Player player, String displayCategory, boolean isHome) {
        String[] options = eventOptionsMap.get(displayCategory);
        if (options == null) return;

        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle(player.getName() + " -> " + displayCategory)
                .setItems(options, (dialog, which) -> {
                    String selectedOption = options[which];

                    switch (displayCategory) {
                        case "SHOTS":
                            if (selectedOption.equals("Goal")) {
                                sendStatisticToBackend(player, "shot", "goal", true, isHome);
                                triggerAssistWizard(player, isHome);
                            } else {
                                String shotOutcome = selectedOption.toLowerCase().replace(" ", "_");
                                sendStatisticToBackend(player, "shot", shotOutcome, false, isHome);
                                if (selectedOption.equals("Saved") || selectedOption.equals("Blocked")) {
                                    triggerCornerCheckWizard(isHome);
                                }
                            }
                            break;

                        case "PASSES":
                            String passOutcome = selectedOption.equals("Accurate") ? "success" : "failure";
                            sendStatisticToBackend(player, "pass", passOutcome, false, isHome);
                            if (passOutcome.equals("success")) {
                                triggerPassAssistCheck(player, isHome);
                            }
                            break;

                        case "TACKLES":
                            if (selectedOption.equals("Successful Tackle")) {
                                sendStatisticToBackend(player, "tackle", "success", false, isHome);
                            } else {
                                sendStatisticToBackend(player, "tackle", "failure", false, isHome);
                                triggerTackleFoulWizard(player, isHome);
                            }
                            break;

                        case "FOULS":
                            if (selectedOption.equals("Foul Won")) {
                                sendStatisticToBackend(player, "foul_won", "success", false, isHome);
                            } else {
                                sendStatisticToBackend(player, "foul_committed", "success", false, isHome);
                                triggerCardWizard(player, isHome);
                            }
                            break;

                        case "ERRORS":
                            sendStatisticToBackend(player, "mistake", "success", false, isHome);
                            if (selectedOption.equals("Mistake Leading to Opponent Goal")) {
                                sendStatisticToBackend(null, "shot", "goal", true, !isHome);
                            }
                            break;
                    }
                }).show();
    }

    private void triggerAssistWizard(Player scorer, boolean isHome) {
        List<Player> teammates = isHome ? homeStarters : awayStarters;
        List<String> playerNames = new ArrayList<>();
        List<Player> validTeammates = new ArrayList<>();

        playerNames.add("No Assist (Solo Effort)");

        for (Player p : teammates) {
            if (p.getId() != scorer.getId()) {
                playerNames.add(p.getName());
                validTeammates.add(p);
            }
        }

        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Assist for " + scorer.getName() + "?")
                .setItems(playerNames.toArray(new String[0]), (dialog, which) -> {
                    if (which > 0) {
                        Player assistPlayer = validTeammates.get(which - 1);
                        sendStatisticToBackend(assistPlayer, "assist", "success", false, isHome);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void triggerPassAssistCheck(Player passer, boolean isHome) {
        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Did " + passer.getName() + "'s pass lead to a Goal?")
                .setPositiveButton("Yes, Assist!", (dialog, which) -> {
                    sendStatisticToBackend(passer, "assist", "success", false, isHome);
                    triggerScorerWizard(passer, isHome);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void triggerScorerWizard(Player passer, boolean isHome) {
        List<Player> teammates = isHome ? homeStarters : awayStarters;
        List<String> names = new ArrayList<>();
        List<Player> validScorers = new ArrayList<>();

        for (Player p : teammates) {
            if (p.getId() != passer.getId()) {
                names.add(p.getName());
                validScorers.add(p);
            }
        }

        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Who scored the Goal?")
                .setItems(names.toArray(new String[0]), (dialog, which) -> {
                    Player scorer = validScorers.get(which);
                    sendStatisticToBackend(scorer, "shot", "goal", true, isHome);
                })
                .setCancelable(false)
                .show();
    }

    private void triggerTackleFoulWizard(Player player, boolean isHome) {
        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Was the unsuccessful tackle a Foul?")
                .setPositiveButton("Yes, Foul Committed", (dialog, which) -> {
                    sendStatisticToBackend(player, "foul_committed", "success", false, isHome);
                    triggerCardWizard(player, isHome);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void triggerCardWizard(Player player, boolean isHome) {
        String[] cards = new String[]{"No Card", "Yellow Card", "Red Card"};

        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Was a card shown to " + player.getName() + "?")
                .setItems(cards, (dialog, which) -> {
                    if (which == 1) {
                        sendStatisticToBackend(player, "card_yellow", "success", false, isHome);
                    } else if (which == 2) {
                        sendStatisticToBackend(player, "card_red", "success", false, isHome);
                    }
                })
                .show();
    }

    private void triggerCornerCheckWizard(boolean isHome) {
        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Did it result in a Corner?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sendStatisticToBackend(null, "corner_won", "success", false, isHome);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void openTeamEventsMenu(boolean isHome) {
        String[] teamOptions = new String[]{"Corner Won", "Team Mistake"};

        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Team Statistic - " + (isHome ? homeTeamName : awayTeamName))
                .setItems(teamOptions, (dialog, which) -> {
                    if (which == 0) {
                        sendStatisticToBackend(null, "corner_won", "success", false, isHome);
                        Toast.makeText(this, "Team corner recorded!", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        sendStatisticToBackend(null, "mistake", "success", false, isHome);
                        Toast.makeText(this, "Team mistake recorded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void sendStatisticToBackend(Player player, String type, String outcome, boolean isGoal, boolean isHomeSelected) {
        int currentMinuteToSend = 0;
        if (etMatchMinute != null && !etMatchMinute.getText().toString().trim().isEmpty()) {
            try {
                currentMinuteToSend = Integer.parseInt(etMatchMinute.getText().toString().trim());
            } catch (NumberFormatException e) {
                currentMinuteToSend = 0;
            }
        }

        int pIdToSend = (player != null) ? player.getId() : 0;
        String displayNameForLog = (player != null) ? player.getName() : "TEAM";

        apiService.saveLiveEvent(matchId, pIdToSend, type, outcome, currentMinuteToSend).enqueue(new Callback<StatusResponse>() {
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
                    runOnUiThread(() -> {
                        String teamLabel = isHomeSelected ? homeTeamName : awayTeamName;

                        String displayMsg;
                        if (outcome.equalsIgnoreCase("success") || type.startsWith("foul") || type.startsWith("card")) {
                            displayMsg = "[" + teamLabel + "] " + displayNameForLog + ": " + type.toUpperCase();
                        } else {
                            displayMsg = "[" + teamLabel + "] " + displayNameForLog + ": " + type.toUpperCase() + " -> " + outcome.toUpperCase();
                        }

                        feedLogList.add(0, new LogItem(displayMsg, type, outcome, pIdToSend, isHomeSelected, isGoal));
                        feedAdapter.notifyItemInserted(0);
                        rvLiveFeed.scrollToPosition(0);
                    });
                } else {
                    Toast.makeText(LiveStatsActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                Toast.makeText(LiveStatsActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupScoreboard() {
        tvHomeName.setText(homeTeamName != null ? homeTeamName : "HOME");
        tvAwayName.setText(awayTeamName != null ? awayTeamName : "AWAY");

        String finalHomeLogo = homeLogoPath;
        String finalAwayLogo = awayLogoPath;

        if (homeLogoPath != null && !homeLogoPath.startsWith("http")) {
            finalHomeLogo = Config.BASE_URL.replace("api/", "") + homeLogoPath;
        }
        if (awayLogoPath != null && !awayLogoPath.startsWith("http")) {
            finalAwayLogo = Config.BASE_URL.replace("api/", "") + awayLogoPath;
        }

        Glide.with(this).load(finalHomeLogo).placeholder(android.R.drawable.ic_menu_gallery).into(ivHomeLogo);
        Glide.with(this).load(finalAwayLogo).placeholder(android.R.drawable.ic_menu_gallery).into(ivAwayLogo);
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
                            if (p.getTeamId() == homeTeamId) {
                                homeStarters.add(p);
                            } else if (p.getTeamId() == awayTeamId) {
                                awayStarters.add(p);
                            }
                        }
                    }
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
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<MatchStatistic>> call, @NonNull Response<List<MatchStatistic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    feedLogList.clear();
                    homeScoreCount = 0;
                    awayScoreCount = 0;

                    for (MatchStatistic stat : response.body()) {
                        boolean wasGoal = stat.getOutcome() != null && stat.getOutcome().toLowerCase().contains("goal");
                        String pName = (stat.getPlayerName() != null) ? stat.getPlayerName() : "TEAM";

                        String outcomeClean = (stat.getOutcome() != null) ? stat.getOutcome() : "";
                        String typeClean = (stat.getEventType() != null) ? stat.getEventType() : "";

                        String displayMsg;
                        if (outcomeClean.equalsIgnoreCase("success") || typeClean.startsWith("foul") || typeClean.startsWith("card")) {
                            displayMsg = "[" + stat.getTeamName() + "] " + pName + ": " + typeClean.toUpperCase();
                        } else {
                            displayMsg = "[" + stat.getTeamName() + "] " + pName + ": " + typeClean.toUpperCase() + " -> " + outcomeClean.toUpperCase();
                        }

                        feedLogList.add(0, new LogItem(
                                displayMsg, typeClean, outcomeClean, stat.getPlayerId(),
                                stat.getTeamId() == homeTeamId, wasGoal
                        ));
                        if (wasGoal) {
                            if (stat.getTeamId() == homeTeamId) homeScoreCount++;
                            else awayScoreCount++;
                        }
                    }
                    tvHomeScore.setText(String.valueOf(homeScoreCount));
                    tvAwayScore.setText(String.valueOf(awayScoreCount));
                    feedAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<MatchStatistic>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch existing stats: " + t.getMessage());
            }
        });
    }

    private void showUndoDialog(int position) {
        LogItem item = feedLogList.get(position);
        new AlertDialog.Builder(this, R.style.CustomDarkDialog)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    apiService.deleteMatchStatistic(matchId, item.playerId, item.eventType, item.outcome).enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                if (item.isGoal) {
                                    if (item.isHome) { if (homeScoreCount > 0) homeScoreCount--; }
                                    else { if (awayScoreCount > 0) awayScoreCount--; }
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
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                            Toast.makeText(LiveStatsActivity.this, "Undo failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public interface OnPlayerClickListener { void onPlayerClick(Player player); }

    public static class LineupAdapter extends RecyclerView.Adapter<LineupAdapter.PlayerViewHolder> {
        private final List<Player> players;
        private final OnPlayerClickListener clickListener;

        public LineupAdapter(List<Player> players, OnPlayerClickListener listener) {
            this.players = players;
            this.clickListener = listener;
        }

        @NonNull
        @Override
        public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_newstat_player, parent, false);
            return new PlayerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
            Player player = players.get(position);
            holder.tvName.setText(player.getName());
            holder.itemView.setOnClickListener(v -> clickListener.onPlayerClick(player));
        }

        @Override
        public int getItemCount() { return players.size(); }

        static class PlayerViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            PlayerViewHolder(View v) { super(v); tvName = (TextView) v; }
        }
    }

    public static class LiveFeedAdapter extends RecyclerView.Adapter<LiveFeedAdapter.ViewHolder> {
        private final List<LogItem> logs;
        private final OnItemClickListener listener;

        public interface OnItemClickListener { void onItemClick(int position); }

        public LiveFeedAdapter(List<LogItem> l, OnItemClickListener lsn) { this.logs = l; this.listener = lsn; }

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
            ViewHolder(View v) { super(v); textView = v.findViewById(R.id.tv_log_message); btnDelete = v.findViewById(R.id.btn_delete_log); }
        }
    }
}