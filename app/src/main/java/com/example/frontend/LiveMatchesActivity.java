package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LiveMatchesActivity extends AppCompatActivity {

    private static final String TAG = "LiveMatchesActivity";
    private RecyclerView rvMatches;
    private MatchAdapter adapter;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_matches);

        // UI Initialization
        btnBack = findViewById(R.id.btn_back_live);
        rvMatches = findViewById(R.id.rv_live_matches);

        // Setup LayoutManager to enable vertical scrolling
        rvMatches.setLayoutManager(new LinearLayoutManager(this));

        // Initialize with empty list to prevent "No adapter attached" error
        adapter = new MatchAdapter(new ArrayList<>());
        rvMatches.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        fetchMatches();
    }

    private void fetchMatches() {
        // Retrofit setup using Android Emulator's local IP address
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/statScopeApp/backend/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getAllMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Match> matches = response.body();

                    // Refresh adapter with new data from server
                    adapter = new MatchAdapter(matches);
                    rvMatches.setAdapter(adapter);

                    Log.d(TAG, "Matches successfully loaded: " + matches.size());
                } else {
                    Log.e(TAG, "Server Error: " + response.code());
                    Toast.makeText(LiveMatchesActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                Log.e(TAG, "Network Failure: " + t.getMessage());
                Toast.makeText(LiveMatchesActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}