package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LiveMatchesActivity extends AppCompatActivity {

    private MatchAdapter adapter;
    private LiveMatchesViewModel viewModel;
    private ProgressBar progressBar;

    // Auto refresh κάθε 3 δευτερόλεπτα
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            viewModel.refresh();

            // ξανατρέχει μετά από 3 sec
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_matches);

        // ── UI ─────────────────────────────────────────────
        RecyclerView recyclerView = findViewById(R.id.rv_live_matches);
        ImageButton btnBack = findViewById(R.id.btn_back_live);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter
        adapter = new MatchAdapter(this, new ArrayList<>(), match -> {
            Intent intent = new Intent(
                    LiveMatchesActivity.this,
                    LiveMatchActionsActivity.class
            );

            intent.putExtra("selected_match", match);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // ── ViewModel ─────────────────────────────────────
        viewModel = new ViewModelProvider(this)
                .get(LiveMatchesViewModel.class);

        // Live matches observer
        viewModel.liveMatches.observe(this, matches -> {
            adapter.updateList(matches);
        });

        // Loading observer
        viewModel.isLoading.observe(this, loading -> {
            if (progressBar != null) {
                progressBar.setVisibility(
                        loading ? View.VISIBLE : View.GONE
                );
            }
        });

        // Error observer
        viewModel.error.observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(
                        LiveMatchesActivity.this,
                        msg,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Back button
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Άμεσο refresh
        viewModel.refresh();

        // Start auto refresh
        handler.post(refreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Σταματά το auto refresh όταν φύγουμε από τη σελίδα
        handler.removeCallbacks(refreshRunnable);
    }
}