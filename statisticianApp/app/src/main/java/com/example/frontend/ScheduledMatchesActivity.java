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

public class ScheduledMatchesActivity extends AppCompatActivity {

    private static final String TAG = "ScheduledMatches";

    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private ImageButton btnBack;
    // η λίστα είναι final ώστε να τη μοιράζεται ο adapter
    private final List<Match> scheduledMatches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_matches);

        recyclerView = findViewById(R.id.rv_scheduled_matches);
        btnBack      = findViewById(R.id.btn_back_scheduled);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ο adapter δημιουργείται ΜΙΑ ΦΟΡΑ εδώ με click listener
        adapter = new MatchAdapter(this, scheduledMatches, match -> {
            Intent intent = new Intent(ScheduledMatchesActivity.this, MatchLiveControlActivity.class);
            intent.putExtra("selected_match", match);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadScheduledMatches();
    }

    private void loadScheduledMatches() {
        RetrofitClient.getApiService().getAllMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    scheduledMatches.clear();

                    for (Match m : response.body()) {
                        if ("scheduled".equalsIgnoreCase(m.getStatus())) {
                            scheduledMatches.add(m);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e(TAG, "Failed to load scheduled matches: " + t.getMessage());
            }
        });
    }
}