package com.example.frontend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduledMatchesActivity extends AppCompatActivity {

    private static final String TAG = "ScheduledMatches";

    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private ImageButton btnBack;

    private final List<Match> scheduledMatches = new ArrayList<>();

    // Στοιχεία UI από το έτοιμο Constraint XML σου
    private TabLayout tabLayoutChampionships;
    private TabLayout tabLayoutRounds;
    private TextView tvEmptyState;

    // Καθαρές λίστες δεδομένων
    private final List<Championship> championshipsList = new ArrayList<>();
    private final List<Match> allFetchedMatches = new ArrayList<>();
    private final List<Integer> availableRoundNumbers = new ArrayList<>();

    private int selectedChampionshipIndex = 0;
    private int selectedRoundIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_matches);

        // Σύνδεση των Views
        recyclerView = findViewById(R.id.rv_scheduled_matches);
        btnBack      = findViewById(R.id.btn_back_scheduled);

        // Σύνδεση των TabLayout και Empty State από το XML
        tabLayoutChampionships = findViewById(R.id.tabLayout_scheduled_championships);
        tabLayoutRounds        = findViewById(R.id.tabLayout_scheduled_rounds);
        tvEmptyState           = findViewById(R.id.tv_scheduled_empty_state);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MatchAdapter(this, scheduledMatches, match -> {
            Intent intent = new Intent(ScheduledMatchesActivity.this, MatchLiveControlActivity.class);
            intent.putExtra("selected_match", match);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        // Listener 1: Όταν αλλάζει το Πρωτάθλημα (Πάνω Tabs)
        if (tabLayoutChampionships != null) {
            tabLayoutChampionships.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    selectedChampionshipIndex = tab.getPosition();
                    selectedRoundIndex = 0; // Reset στις αγωνιστικές για το νέο πρωτάθλημα
                    buildRoundsTabsDynamically();
                }
                @Override public void onTabUnselected(TabLayout.Tab tab) {}
                @Override public void onTabReselected(TabLayout.Tab tab) {}
            });
        }

        // Listener 2: Όταν αλλάζει η Αγωνιστική (Κάτω Tabs)
        if (tabLayoutRounds != null) {
            tabLayoutRounds.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    selectedRoundIndex = tab.getPosition();
                    filterMatchesFinal();
                }
                @Override public void onTabUnselected(TabLayout.Tab tab) {}
                @Override public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialData();
    }

    private void loadInitialData() {
        // 1. Τραβάμε τα επίσημα Πρωταθλήματα από τη βάση δεδομένων
        RetrofitClient.getApiService().getChampionships().enqueue(new Callback<List<Championship>>() {
            @Override
            public void onResponse(@NonNull Call<List<Championship>> call, @NonNull Response<List<Championship>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    championshipsList.clear();
                    championshipsList.addAll(response.body());

                    // Μόλις έρθουν τα πρωταθλήματα, τραβάμε και τους αγώνες
                    loadAllScheduledMatches();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Championship>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to load championships: " + t.getMessage());
            }
        });
    }

    private void loadAllScheduledMatches() {
        // 2. Τραβάμε όλα τα προγραμματισμένα παιχνίδια
        RetrofitClient.getApiService().getAllMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(@NonNull Call<List<Match>> call, @NonNull Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFetchedMatches.clear();

                    for (Match m : response.body()) {
                        if ("scheduled".equalsIgnoreCase(m.getStatus())) {
                            allFetchedMatches.add(m);
                        }
                    }

                    // Χτίζουμε τα πάνω Tabs (Πρωταθλήματα)
                    buildChampionshipsTabsDynamically();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Match>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to load matches: " + t.getMessage());
            }
        });
    }

    private void buildChampionshipsTabsDynamically() {
        if (tabLayoutChampionships == null) return;
        tabLayoutChampionships.removeAllTabs();

        if (championshipsList.isEmpty()) {
            if (tabLayoutRounds != null) tabLayoutRounds.removeAllTabs();
            scheduledMatches.clear();
            adapter.notifyDataSetChanged();
            if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        // Προσθέτουμε τα επίσημα ονόματα των Πρωταθλημάτων στα Tabs
        for (Championship c : championshipsList) {
            tabLayoutChampionships.addTab(tabLayoutChampionships.newTab().setText(c.getName()));
        }

        if (selectedChampionshipIndex >= championshipsList.size()) {
            selectedChampionshipIndex = 0;
        }

        TabLayout.Tab targetTab = tabLayoutChampionships.getTabAt(selectedChampionshipIndex);
        if (targetTab != null) {
            targetTab.select();
        }

        buildRoundsTabsDynamically();
    }

    private void buildRoundsTabsDynamically() {
        if (tabLayoutRounds == null) return;
        tabLayoutRounds.removeAllTabs();

        if (championshipsList.isEmpty() || selectedChampionshipIndex >= championshipsList.size()) {
            return;
        }

        // Παίρνουμε το ID του τρέχοντος επιλεγμένου πρωταθλήματος
        int activeChampionshipId = championshipsList.get(selectedChampionshipIndex).getId();
        Set<Integer> uniqueRounds = new HashSet<>();

        // Ψάχνουμε στα matches ποια rounds υπάρχουν ΜΟΝΟ για αυτό το πρωτάθλημα
        for (Match m : allFetchedMatches) {
            if (m.getChampionshipId() == activeChampionshipId) {
                uniqueRounds.add(m.getRound());
            }
        }

        availableRoundNumbers.clear();
        availableRoundNumbers.addAll(uniqueRounds);
        Collections.sort(availableRoundNumbers);

        if (availableRoundNumbers.isEmpty()) {
            scheduledMatches.clear();
            adapter.notifyDataSetChanged();
            if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        // Δημιουργούμε τα κάτω Tabs για τις αγωνιστικές
        for (Integer roundNum : availableRoundNumbers) {
            tabLayoutRounds.addTab(tabLayoutRounds.newTab().setText("Rnd " + roundNum));
        }

        if (selectedRoundIndex >= availableRoundNumbers.size()) {
            selectedRoundIndex = 0;
        }

        TabLayout.Tab targetTab = tabLayoutRounds.getTabAt(selectedRoundIndex);
        if (targetTab != null) {
            targetTab.select();
        }

        filterMatchesFinal();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterMatchesFinal() {
        scheduledMatches.clear();

        if (championshipsList.isEmpty() || availableRoundNumbers.isEmpty() ||
                selectedChampionshipIndex >= championshipsList.size() || selectedRoundIndex >= availableRoundNumbers.size()) {
            if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            return;
        }

        int targetChampionshipId = championshipsList.get(selectedChampionshipIndex).getId();
        int targetRound = availableRoundNumbers.get(selectedRoundIndex);

        // ΦΙΛΤΡΑΡΙΣΜΑ: Κρατάμε μόνο τα ματς που ανήκουν στο επιλεγμένο Πρωτάθλημα ΚΑΙ στην επιλεγμένη Αγωνιστική
        for (Match m : allFetchedMatches) {
            if (m.getChampionshipId() == targetChampionshipId && m.getRound() == targetRound) {
                scheduledMatches.add(m);
            }
        }

        // Ενημερώνουμε τον Adapter
        adapter.notifyDataSetChanged();

        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(scheduledMatches.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}