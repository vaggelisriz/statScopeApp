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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    private Match selectedMatch;

    // Score card views (από το included item_match)
    private TextView tvHome, tvAway, tvScore, tvStatus, tvChamp;
    private ImageView ivHome, ivAway;

    // Statistics list
    private RecyclerView rvStatistics;
    private StatisticsAdapter statsAdapter;
    private final List<MatchStatistic> statsList = new ArrayList<>();

    // Empty state
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        selectedMatch = (Match) getIntent().getSerializableExtra("selected_match");

        initViews();

        findViewById(R.id.btn_back_statistics).setOnClickListener(v -> finish());

        if (selectedMatch != null) {
            setupMatchCard();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ανανέωση σκορ + στατιστικών κάθε φορά που επιστρέφουμε σε αυτή την οθόνη
        if (selectedMatch != null) {
            refreshScoreFromServer();
            loadStatistics(selectedMatch.getId());
        }
    }

    private void initViews() {
        // Match card (included layout)
        View card = findViewById(R.id.included_match_card);
        tvHome   = card.findViewById(R.id.tv_home_name);
        tvAway   = card.findViewById(R.id.tv_away_name);
        tvScore  = card.findViewById(R.id.tv_score);
        tvStatus = card.findViewById(R.id.tv_status);
        tvChamp  = card.findViewById(R.id.tv_championship_name);
        ivHome   = card.findViewById(R.id.iv_home_logo);
        ivAway   = card.findViewById(R.id.iv_away_logo);

        // Statistics RecyclerView
        rvStatistics = findViewById(R.id.rv_statistics);
        tvEmpty      = findViewById(R.id.tv_empty_statistics);

        rvStatistics.setLayoutManager(new LinearLayoutManager(this));
        statsAdapter = new StatisticsAdapter(statsList);
        rvStatistics.setAdapter(statsAdapter);
    }

    private void setupMatchCard() {
        tvHome.setText(selectedMatch.getHomeTeam());
        tvAway.setText(selectedMatch.getAwayTeam());
        tvScore.setText(selectedMatch.getHomeScore() + " - " + selectedMatch.getAwayScore());
        tvStatus.setText(selectedMatch.getStatus().toUpperCase());
        tvChamp.setText(selectedMatch.getChampionshipName());

        Glide.with(this)
                .load(selectedMatch.getHomeLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivHome);

        Glide.with(this)
                .load(selectedMatch.getAwayLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivAway);
    }

    private void refreshScoreFromServer() {
        RetrofitClient.getApiService().getAllMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Match m : response.body()) {
                        if (m.getId() == selectedMatch.getId()) {
                            selectedMatch.setHomeScore(m.getHomeScore());
                            selectedMatch.setAwayScore(m.getAwayScore());
                            selectedMatch.setStatus(m.getStatus());
                            setupMatchCard();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e(TAG, "Score refresh failed: " + t.getMessage());
            }
        });
    }

    private void loadStatistics(int matchId) {
        RetrofitClient.getApiService().getMatchStatistics(matchId).enqueue(new Callback<List<MatchStatistic>>() {
            @Override
            public void onResponse(Call<List<MatchStatistic>> call, Response<List<MatchStatistic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statsList.clear();
                    statsList.addAll(response.body());
                    statsAdapter.notifyDataSetChanged();

                    tvEmpty.setVisibility(statsList.isEmpty() ? View.VISIBLE : View.GONE);
                    rvStatistics.setVisibility(statsList.isEmpty() ? View.GONE : View.VISIBLE);

                    Log.d(TAG, "Loaded " + statsList.size() + " statistics");
                } else {
                    Log.e(TAG, "getMatchStatistics failed: HTTP " + response.code());
                    Toast.makeText(StatisticsActivity.this, "Error loading statistics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MatchStatistic>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(StatisticsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Inner Adapter ────────────────────────────────────────────────────────

    static class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatViewHolder> {

        private final List<MatchStatistic> items;

        StatisticsAdapter(List<MatchStatistic> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_statistic, parent, false);
            return new StatViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
            MatchStatistic stat = items.get(position);
            holder.tvPlayerName.setText(stat.getPlayerName());
            holder.tvTeamName.setText(stat.getTeamName());
            holder.tvEventType.setText(formatEventType(stat.getEventType()));
            holder.tvOutcome.setText(stat.getOutcome());

            // Χρωματισμός outcome
            int color;
            switch (stat.getOutcome().toLowerCase()) {
                case "goal":    color = 0xFF00E676; break; // πράσινο
                case "yellow":  color = 0xFFFFD600; break; // κίτρινο
                case "red":     color = 0xFFFF1744; break; // κόκκινο
                default:        color = 0xFF9E9E9E; break; // γκρι
            }
            holder.tvOutcome.setTextColor(color);
        }

        @Override
        public int getItemCount() { return items.size(); }

        private String formatEventType(String type) {
            if (type == null) return "";
            switch (type.toLowerCase()) {
                case "shot":         return "⚽ Shot";
                case "yellow_card":  return "🟨 Yellow Card";
                case "red_card":     return "🟥 Red Card";
                case "foul":         return "🤚 Foul";
                case "corner":       return "🚩 Corner";
                case "substitution": return "🔄 Substitution";
                default:             return type;
            }
        }

        static class StatViewHolder extends RecyclerView.ViewHolder {
            TextView tvPlayerName, tvTeamName, tvEventType, tvOutcome;

            StatViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPlayerName = itemView.findViewById(R.id.tv_stat_player_name);
                tvTeamName   = itemView.findViewById(R.id.tv_stat_team_name);
                tvEventType  = itemView.findViewById(R.id.tv_stat_event_type);
                tvOutcome    = itemView.findViewById(R.id.tv_stat_outcome);
            }
        }
    }
}