package com.example.frontend;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity to display and manage matches that are currently LIVE.
 */
public class LiveMatchesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private ImageButton btnBack;
    private final List<Match> liveMatches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_matches);

        // 1. Initialize UI components
        recyclerView = findViewById(R.id.rv_live_matches);
        btnBack = findViewById(R.id.btn_back_live);

        // Set layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Prevent "No adapter attached" error by setting an empty adapter initially
        adapter = new MatchAdapter(this, new ArrayList<>(), match -> {
            // Click is disabled for now as per requirements
        });
        recyclerView.setAdapter(adapter);

        // 3. Set up click listeners
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list every time the user returns to this screen
        fetchLiveMatches();
    }

    /**
     * Fetches all matches from the database and filters those with "live" status.
     */
    private void fetchLiveMatches() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getAllMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveMatches.clear();

                    // Filter matches locally to include only those currently in progress (LIVE)
                    for (Match m : response.body()) {
                        if ("live".equalsIgnoreCase(m.getStatus())) {
                            liveMatches.add(m);
                        }
                    }

                    // Update the adapter with the filtered live list
                    // Click listener is empty to prevent navigating to the control page again
                    adapter = new MatchAdapter(LiveMatchesActivity.this, liveMatches, match -> {
                        // Intent for stats entry will be added here in the future
                    });
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                // Silent failure as requested: no toasts or error messages displayed to the user
            }
        });
    }
}