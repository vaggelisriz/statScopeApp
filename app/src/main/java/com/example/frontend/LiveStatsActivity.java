package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private int matchId, homeTeamId, awayTeamId;
    private String homeTeamName, awayTeamName, homeLogoPath, awayLogoPath;
    private TextView tvHomeName, tvAwayName, tvHomeScore, tvAwayScore, tvSelectorTeam, tvSelectorPlayer;
    private ImageView ivHomeLogo, ivAwayLogo;
    private RecyclerView rvLiveFeed;
    private LiveFeedAdapter feedAdapter;
    private final List<Player> homeStarters = new ArrayList<>();
    private final List<Player> awayStarters = new ArrayList<>();
    private final List<LogItem> feedLogList = new ArrayList<>();
    private boolean isTeamSelected = false, isHomeSelected = true;
    private Player selectedPlayer = null;
    private int homeScoreCount = 0, awayScoreCount = 0;
    private ApiService apiService;

    public static class LogItem {
        String message, eventType, outcome;
        int playerId;
        boolean isHome, isGoal;
        LogItem(String m, String e, String o, int pId, boolean h, boolean g) {
            message = m; eventType = e; outcome = o; playerId = pId; isHome = h; isGoal = g;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stats);

        apiService = RetrofitClient.getApiService();
        matchId = getIntent().getIntExtra("match_id", -1);

        // DEBUG: Εδώ θα δούμε αν φτάνει το σωστό ID
        Log.d("DEBUG_MATCH", "To matchId που έλαβα από το Intent είναι: " + matchId);

        homeTeamId = getIntent().getIntExtra("home_team_id", -1);
        awayTeamId = getIntent().getIntExtra("away_team_id", -1);
        homeTeamName = getIntent().getStringExtra("home_team_name");
        awayTeamName = getIntent().getStringExtra("away_team_name");
        homeLogoPath = getIntent().getStringExtra("home_logo");
        awayLogoPath = getIntent().getStringExtra("away_logo");

        initViews();
        setupScoreboard();

        feedAdapter = new LiveFeedAdapter(feedLogList, this::showUndoDialog);
        rvLiveFeed.setLayoutManager(new LinearLayoutManager(this));
        rvLiveFeed.setAdapter(feedAdapter);

        loadMatchLineups(matchId);
        loadExistingStats();
        setupCustomSelectors();
        setupActionButtons();

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
        tvHomeName.setText(homeTeamName);
        tvAwayName.setText(awayTeamName);
        String baseUrl = "http://10.0.2.2/statScopeApp/backend/";
        Glide.with(this).load(baseUrl + homeLogoPath).into(ivHomeLogo);
        Glide.with(this).load(baseUrl + awayLogoPath).into(ivAwayLogo);
    }

    private void loadMatchLineups(int mId) {
        apiService.getMatchLineups(mId).enqueue(new Callback<LineupResponse>() {
            @Override public void onResponse(@NonNull Call<LineupResponse> call, @NonNull Response<LineupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    homeStarters.clear(); awayStarters.clear();
                    for (Player p : response.body().getPlayers()) {
                        if (p.getTeamId() == homeTeamId) homeStarters.add(p);
                        else if (p.getTeamId() == awayTeamId) awayStarters.add(p);
                    }
                }
            }
            @Override public void onFailure(@NonNull Call<LineupResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadExistingStats() {
        apiService.getMatchStatistics(matchId).enqueue(new Callback<List<MatchStatistic>>() {
            @Override public void onResponse(@NonNull Call<List<MatchStatistic>> call, @NonNull Response<List<MatchStatistic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    feedLogList.clear(); homeScoreCount = 0; awayScoreCount = 0;
                    for (MatchStatistic stat : response.body()) {
                        boolean wasGoal = stat.getOutcome() != null && stat.getOutcome().contains("GOAL");
                        feedLogList.add(0, new LogItem("[" + stat.getTeamName() + "] " + stat.getPlayerName() + ": " + stat.getOutcome(),
                                stat.getEventType(), stat.getOutcome(), stat.getPlayerId(), stat.getTeamId() == homeTeamId, wasGoal));
                        if (wasGoal) { if (stat.getTeamId() == homeTeamId) homeScoreCount++; else awayScoreCount++; }
                    }
                    tvHomeScore.setText(String.valueOf(homeScoreCount));
                    tvAwayScore.setText(String.valueOf(awayScoreCount));
                    MatchScoreManager.getInstance().updateScore(matchId, homeScoreCount, awayScoreCount);
                    feedAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(@NonNull Call<List<MatchStatistic>> call, @NonNull Throwable t) {}
        });
    }

    private void setupCustomSelectors() {
        tvSelectorTeam.setOnClickListener(v -> {
            CharSequence[] teams = {homeTeamName, awayTeamName};
            new AlertDialog.Builder(this, R.style.CustomDarkDialog).setItems(teams, (d, w) -> {
                isHomeSelected = (w == 0); isTeamSelected = true;
                tvSelectorTeam.setText(teams[w]); selectedPlayer = null;
                tvSelectorPlayer.setText("Select Player");
            }).show();
        });

        tvSelectorPlayer.setOnClickListener(v -> {
            if (!isTeamSelected) return;
            List<Player> currentList = isHomeSelected ? homeStarters : awayStarters;
            CharSequence[] playerNames = new CharSequence[currentList.size()];
            for (int i = 0; i < currentList.size(); i++) playerNames[i] = currentList.get(i).getName();
            new AlertDialog.Builder(this, R.style.CustomDarkDialog).setItems(playerNames, (d, w) -> {
                selectedPlayer = currentList.get(w); tvSelectorPlayer.setText(playerNames[w]);
            }).show();
        });
    }

    private void setupActionButtons() {
        findViewById(R.id.btn_live_shot).setOnClickListener(v -> handleAction("SHOT", new String[]{"Foot Shot - Saved", "Foot Shot - Missed", "Foot Shot - GOAL"}));
    }

    private void handleAction(String type, String[] options) {
        if (selectedPlayer == null) { Toast.makeText(this, "Select a player!", Toast.LENGTH_SHORT).show(); return; }
        new AlertDialog.Builder(this, R.style.CustomDarkDialog).setItems(options, (d, w) -> sendStatisticToBackend(type, options[w], options[w].contains("GOAL"))).show();
    }

    private void sendStatisticToBackend(String type, String outcome, boolean isGoal) {
        if (selectedPlayer == null) return;
        if (isGoal) {
            if (isHomeSelected) tvHomeScore.setText(String.valueOf(++homeScoreCount));
            else tvAwayScore.setText(String.valueOf(++awayScoreCount));
            MatchScoreManager.getInstance().updateScore(matchId, homeScoreCount, awayScoreCount);
            apiService.updateScore(matchId, homeScoreCount, awayScoreCount).enqueue(new Callback<StatusResponse>() {
                @Override public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {}
                @Override public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {}
            });
        }
        feedLogList.add(0, new LogItem("[" + (isHomeSelected ? homeTeamName : awayTeamName) + "] " + selectedPlayer.getName() + ": " + outcome, type, outcome, selectedPlayer.getId(), isHomeSelected, isGoal));
        feedAdapter.notifyItemInserted(0); rvLiveFeed.scrollToPosition(0);
    }

    private void showUndoDialog(int position) {}

    public static class LiveFeedAdapter extends RecyclerView.Adapter<LiveFeedAdapter.ViewHolder> {
        private final List<LogItem> logs;
        private final OnItemClickListener listener;
        public interface OnItemClickListener { void onItemClick(int position); }
        public LiveFeedAdapter(List<LogItem> l, OnItemClickListener lsn) { logs = l; listener = lsn; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_log, parent, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(logs.get(position).message);
            holder.btnDelete.setOnClickListener(v -> listener.onItemClick(position));
        }
        @Override public int getItemCount() { return logs.size(); }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView; ImageButton btnDelete;
            ViewHolder(View v) { super(v); textView = v.findViewById(R.id.tv_log_message); btnDelete = v.findViewById(R.id.btn_delete_log); }
        }
    }
}