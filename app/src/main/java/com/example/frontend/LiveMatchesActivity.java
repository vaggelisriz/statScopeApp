package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveMatchesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private ImageButton btnBack;
    private final List<Match> liveMatches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_matches);

        recyclerView = findViewById(R.id.rv_live_matches);
        btnBack = findViewById(R.id.btn_back_live);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Αρχικοποίηση του adapter μία φορά
        adapter = new MatchAdapter(this, liveMatches, match -> {
            Intent intent = new Intent(LiveMatchesActivity.this, LiveMatchActionsActivity.class);
            intent.putExtra("selected_match", match);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLiveMatches();
    }

    private void fetchLiveMatches() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getAllMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveMatches.clear();
                    for (Match m : response.body()) {
                        if ("live".equalsIgnoreCase(m.getStatus())) {
                            liveMatches.add(m);
                        }
                    }
                    // Ενημερώνουμε τον ήδη υπάρχοντα adapter
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e("LIVE_FETCH", "Error: " + t.getMessage());
            }
        });
    }
}