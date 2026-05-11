package com.example.statscopeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MatchSelectionActivity extends AppCompatActivity {

    // Final list to prevent modification of the reference
    private final List<Match> matchList = new ArrayList<>();
    private MatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_selection);

        setupHeader();
        initRecyclerView();
        loadMatchesFromServer();
        setupBackNavigation();
    }

    private void setupHeader() {
        // Find the back button from your XML layout
        ImageButton btnBack = findViewById(R.id.btn_back_match_selection);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> handleBackAction());
        }
    }

    private void setupBackNavigation() {
        // Modern approach to handle system back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackAction();
            }
        });
    }

    private void handleBackAction() {
        // Closes activity with a smooth transition
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void initRecyclerView() {
        // RecyclerView as a local variable to optimize memory
        RecyclerView rvMatches = findViewById(R.id.rv_matches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MatchAdapter(matchList, match -> {
            // Open Lineup screen when a match is selected
            Intent intent = new Intent(MatchSelectionActivity.this, LineupActivity.class);
            intent.putExtra("MATCH_ID", match.getId());
            intent.putExtra("HOME_TEAM", match.getHomeTeam());
            intent.putExtra("AWAY_TEAM", match.getAwayTeam());
            startActivity(intent);
        });

        rvMatches.setAdapter(adapter);
    }

    private void loadMatchesFromServer() {
        // API URL (Make sure your backend folder name is correct)
        String url = "http://10.0.2.2/statscope_backend/api/getMatches.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Match>>(){}.getType();
                    List<Match> serverMatches = gson.fromJson(response, listType);

                    if (serverMatches != null) {
                        matchList.clear();
                        matchList.addAll(serverMatches);
                        adapter.notifyDataSetChanged();
                    }
                },
                error -> Toast.makeText(this, "Network Error: Check Connection", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }
}