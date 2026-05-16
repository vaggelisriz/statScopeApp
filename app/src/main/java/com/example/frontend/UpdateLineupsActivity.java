package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateLineupsActivity extends AppCompatActivity {

    private static final String TAG = "UpdateLineups";
    private static final int STARTERS_COUNT = 11;

    public static final String EXTRA_MATCH_ID     = "match_id";
    public static final String EXTRA_HOME_TEAM_ID = "home_team_id";
    public static final String EXTRA_AWAY_TEAM_ID = "away_team_id";
    public static final String EXTRA_HOME_TEAM    = "home_team";
    public static final String EXTRA_AWAY_TEAM    = "away_team";

    private TabLayout   tabLayout;
    private RecyclerView startersRecycler;
    private ProgressBar progressBar;
    private TextView    tvEmptyState;
    private Button      btnSave;

    private int matchId, homeTeamId, awayTeamId;
    private String homeTeamName, awayTeamName;

    private final List<Player> homeStarters = new ArrayList<>();
    private final List<Player> homeBench    = new ArrayList<>();
    private final List<Player> awayStarters = new ArrayList<>();
    private final List<Player> awayBench    = new ArrayList<>();

    private int activeTab = 0;

    private StartersAdapter startersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lineups);

        readIntentExtras();
        initViews();
        setupTabs();
        setupSaveButton();

        loadLineups();
    }

    private void readIntentExtras() {
        matchId      = getIntent().getIntExtra(EXTRA_MATCH_ID, -1);
        homeTeamId   = getIntent().getIntExtra(EXTRA_HOME_TEAM_ID, -1);
        awayTeamId   = getIntent().getIntExtra(EXTRA_AWAY_TEAM_ID, -1);
        homeTeamName = getIntent().getStringExtra(EXTRA_HOME_TEAM);
        awayTeamName = getIntent().getStringExtra(EXTRA_AWAY_TEAM);

        if (matchId == -1 || homeTeamId == -1 || awayTeamId == -1) {
            Toast.makeText(this, "Error: Match details not found", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        tabLayout       = findViewById(R.id.tabLayout);
        startersRecycler= findViewById(R.id.startersRecycler);
        progressBar     = findViewById(R.id.progressBar);
        tvEmptyState    = findViewById(R.id.tvEmptyState);
        btnSave         = findViewById(R.id.btnSave);

        View btnBack = findViewById(R.id.btn_back_lineups);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        startersRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupTabs() {
        String homeName = (homeTeamName != null) ? homeTeamName : "Home";
        String awayName = (awayTeamName != null) ? awayTeamName : "Away";

        tabLayout.addTab(tabLayout.newTab().setText(homeName));
        tabLayout.addTab(tabLayout.newTab().setText(awayName));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activeTab = tab.getPosition();
                refreshStartersView();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveLineups());
    }

    private void loadLineups() {
        showLoading(true);

        RetrofitClient.getApiService()
                .getMatchLineups(matchId)
                .enqueue(new Callback<LineupResponse>() {
                    @Override
                    public void onResponse(Call<LineupResponse> call, Response<LineupResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Player> players = response.body().getPlayers();

                            if (players != null && !players.isEmpty()) {
                                Log.d(TAG, "Lineup loaded: " + players.size() + " players. Now loading bench...");
                                distributeExistingLineup(players);
                                loadRemainingPlayersForBench();
                            } else {
                                Log.d(TAG, "Empty lineup — loading team players as default");
                                loadDefaultLineupFromTeams();
                            }
                        } else {
                            Log.e(TAG, "getMatchLineups failed: HTTP " + response.code());
                            showLoading(false);
                            showError("Error loading lineup (HTTP " + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<LineupResponse> call, Throwable t) {
                        Log.e(TAG, "getMatchLineups network error", t);
                        showLoading(false);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void distributeExistingLineup(List<Player> players) {
        homeStarters.clear(); homeBench.clear();
        awayStarters.clear(); awayBench.clear();

        for (Player p : players) {
            p.setStarter(true);
            if (p.getTeamId() == homeTeamId) {
                homeStarters.add(p);
            } else if (p.getTeamId() == awayTeamId) {
                awayStarters.add(p);
            }
        }
    }

    private void loadRemainingPlayersForBench() {
        AtomicInteger pendingCalls = new AtomicInteger(2);
        final List<Player> allHome = new ArrayList<>();
        final List<Player> allAway = new ArrayList<>();

        RetrofitClient.getApiService().getTeamPlayers(homeTeamId).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allHome.addAll(response.body());
                }
                if (pendingCalls.decrementAndGet() == 0) {
                    mergePlayersIntoBench(allHome, allAway);
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e(TAG, "getPlayers for bench (home) failed", t);
                if (pendingCalls.decrementAndGet() == 0) {
                    mergePlayersIntoBench(allHome, allAway);
                }
            }
        });

        RetrofitClient.getApiService().getTeamPlayers(awayTeamId).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allAway.addAll(response.body());
                }
                if (pendingCalls.decrementAndGet() == 0) {
                    mergePlayersIntoBench(allHome, allAway);
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                Log.e(TAG, "getPlayers for bench (away) failed", t);
                if (pendingCalls.decrementAndGet() == 0) {
                    mergePlayersIntoBench(allHome, allAway);
                }
            }
        });
    }

    private void mergePlayersIntoBench(List<Player> allHome, List<Player> allAway) {
        runOnUiThread(() -> {
            for (Player p : allHome) {
                boolean isAlreadyStarter = false;
                for (Player starter : homeStarters) {
                    if (starter.getId() == p.getId()) {
                        isAlreadyStarter = true;
                        break;
                    }
                }
                if (!isAlreadyStarter) {
                    p.setStarter(false);
                    homeBench.add(p);
                }
            }

            for (Player p : allAway) {
                boolean isAlreadyStarter = false;
                for (Player starter : awayStarters) {
                    if (starter.getId() == p.getId()) {
                        isAlreadyStarter = true;
                        break;
                    }
                }
                if (!isAlreadyStarter) {
                    p.setStarter(false);
                    awayBench.add(p);
                }
            }

            showLoading(false);
            refreshStartersView();
            Log.d(TAG, "Bench loaded. Home bench: " + homeBench.size() + ", Away bench: " + awayBench.size());
        });
    }

    private void loadDefaultLineupFromTeams() {
        AtomicInteger pendingCalls = new AtomicInteger(2);

        final List<Player> homePlayers = new ArrayList<>();
        final List<Player> awayPlayers = new ArrayList<>();

        RetrofitClient.getApiService()
                .getTeamPlayers(homeTeamId)
                .enqueue(new Callback<List<Player>>() {
                    @Override
                    public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            homePlayers.addAll(response.body());
                            Log.d(TAG, "Home players loaded: " + homePlayers.size());
                        }
                        if (pendingCalls.decrementAndGet() == 0) {
                            buildDefaultLineup(homePlayers, awayPlayers);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Player>> call, Throwable t) {
                        Log.e(TAG, "getPlayers (home) network error", t);
                        if (pendingCalls.decrementAndGet() == 0) {
                            buildDefaultLineup(homePlayers, awayPlayers);
                        }
                    }
                });

        RetrofitClient.getApiService()
                .getTeamPlayers(awayTeamId)
                .enqueue(new Callback<List<Player>>() {
                    @Override
                    public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            awayPlayers.addAll(response.body());
                            Log.d(TAG, "Away players loaded: " + awayPlayers.size());
                        }
                        if (pendingCalls.decrementAndGet() == 0) {
                            buildDefaultLineup(homePlayers, awayPlayers);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Player>> call, Throwable t) {
                        Log.e(TAG, "getPlayers (away) network error", t);
                        if (pendingCalls.decrementAndGet() == 0) {
                            buildDefaultLineup(homePlayers, awayPlayers);
                        }
                    }
                });
    }

    private void buildDefaultLineup(List<Player> homePlayers, List<Player> awayPlayers) {
        runOnUiThread(() -> {
            homeStarters.clear(); homeBench.clear();
            awayStarters.clear(); awayBench.clear();

            splitIntoStartersAndBench(homePlayers, homeStarters, homeBench);
            splitIntoStartersAndBench(awayPlayers, awayStarters, awayBench);

            showLoading(false);

            if (homeStarters.isEmpty() && awayStarters.isEmpty()) {
                showError("No players found for the teams.");
            } else {
                refreshStartersView();
                Toast.makeText(this,
                        "New match: placed " + STARTERS_COUNT + " default starters per team",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void splitIntoStartersAndBench(List<Player> all, List<Player> starters, List<Player> bench) {
        for (int i = 0; i < all.size(); i++) {
            Player p = all.get(i);
            if (i < STARTERS_COUNT) {
                p.setStarter(true);
                starters.add(p);
            } else {
                p.setStarter(false);
                bench.add(p);
            }
        }
    }

    private void refreshStartersView() {
        List<Player> currentStarters = (activeTab == 0) ? homeStarters : awayStarters;

        if (startersAdapter == null) {
            startersAdapter = new StartersAdapter(currentStarters, player -> {
                List<Player> bench = (activeTab == 0) ? homeBench : awayBench;
                List<Player> starters = (activeTab == 0) ? homeStarters : awayStarters;
                showBenchSheet(player, starters, bench);
            });
            startersRecycler.setAdapter(startersAdapter);
        } else {
            startersAdapter.updateData(currentStarters);
        }

        tvEmptyState.setVisibility(currentStarters.isEmpty() ? View.VISIBLE : View.GONE);
        startersRecycler.setVisibility(currentStarters.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showBenchSheet(Player playerOut, List<Player> starters, List<Player> bench) {
        if (bench.isEmpty()) {
            Toast.makeText(this, "No players available on the bench", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_bench, null);
        sheet.setContentView(sheetView);

        RecyclerView benchRecycler = sheetView.findViewById(R.id.benchRecycler);
        benchRecycler.setLayoutManager(new LinearLayoutManager(this));

        BenchAdapter benchAdapter = new BenchAdapter(bench, playerIn -> {
            int outIdx = starters.indexOf(playerOut);
            int inIdx  = bench.indexOf(playerIn);

            if (outIdx != -1 && inIdx != -1) {
                playerOut.setStarter(false);
                playerOut.setSubstituted(false); // Χάνει το highlight αν βγει στον πάγκο

                playerIn.setStarter(true);
                playerIn.setSubstituted(true); // Ενεργοποίηση του μόνιμου highlight για τον νέο βασικό

                starters.set(outIdx, playerIn);
                bench.set(inIdx, playerOut);

                startersAdapter.notifyItemChanged(outIdx);
                Log.d(TAG, "Swap: " + playerIn.getName() + " ← " + playerOut.getName());
            }

            sheet.dismiss();
        });

        benchRecycler.setAdapter(benchAdapter);
        sheet.show();
    }

    private void saveLineups() {
        if (homeStarters.size() < STARTERS_COUNT || awayStarters.size() < STARTERS_COUNT) {
            Toast.makeText(this, "At least " + STARTERS_COUNT + " starters are required per team", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        showLoading(true);

        List<Integer> homeIds = extractIds(homeStarters);
        List<Integer> awayIds = extractIds(awayStarters);

        RetrofitClient.getApiService()
                .updateMatchStatusAndLineups(matchId, "live", homeIds, awayIds)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        showLoading(false);
                        btnSave.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(UpdateLineupsActivity.this, "Lineup saved successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Save failed: HTTP " + response.code());
                            Toast.makeText(UpdateLineupsActivity.this, "Save failed (HTTP " + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        showLoading(false);
                        btnSave.setEnabled(true);
                        Log.e(TAG, "Save network error", t);
                        Toast.makeText(UpdateLineupsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<Integer> extractIds(List<Player> players) {
        List<Integer> ids = new ArrayList<>();
        for (Player p : players) ids.add(p.getId());
        return ids;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            startersRecycler.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        tvEmptyState.setText(message);
        tvEmptyState.setVisibility(View.VISIBLE);
        startersRecycler.setVisibility(View.GONE);
    }
}