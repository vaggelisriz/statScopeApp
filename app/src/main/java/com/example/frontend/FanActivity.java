package com.example.frontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FanActivity extends AppCompatActivity {

    private ImageButton btnBack;

    // Στοιχεία για το Dropdown των Championships
    private LinearLayout layoutChampionshipHeader;
    private LinearLayout layoutChampionshipOptionsContainer;
    private TextView tvChampionshipsHeader;
    private ImageView ivChampionshipArrow;

    // ΔΙΟΡΘΩΘΗΚΕ: Στοιχεία για το Dropdown των Matches (Χρήση RecyclerView αντί για LinearLayout)
    private LinearLayout layoutMatchesHeader;
    private RecyclerView rvLiveMatches;
    private ImageView ivMatchesArrow;

    private ChampionshipList chlist;
    private OkHttpClient okHttpClient;

    private final String ip = "10.140.9.120";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);

        okHttpClient = new OkHttpClient();

        // UI Initialization
        btnBack = findViewById(R.id.btn_back_dashboard);
        btnBack.setOnClickListener(v -> finish());

        // 1. Σύνδεση των Views για τα Championships
        layoutChampionshipHeader = findViewById(R.id.layout_championship_header);
        layoutChampionshipOptionsContainer = findViewById(R.id.layout_championship_options_container);
        tvChampionshipsHeader = findViewById(R.id.tv_championships_header);
        ivChampionshipArrow = findViewById(R.id.iv_championship_arrow);

        // 2. ΔΙΟΡΘΩΘΗΚΕ: Σύνδεση του RecyclerView για τα Matches
        layoutMatchesHeader = findViewById(R.id.layout_matches_header);
        rvLiveMatches = findViewById(R.id.rv_live_matches);
        ivMatchesArrow = findViewById(R.id.iv_matches_arrow);

        // ΑΠΑΡΑΙΤΗΤΟ: Ορίζουμε τη διάταξη του RecyclerView σε κάθετη (LinearLayoutManager)
        rvLiveMatches.setLayoutManager(new LinearLayoutManager(this));

        // ====================================================================
        // ΛΟΓΙΚΗ ACCORDION ΓΙΑ ΤΑ CHAMPIONSHIPS
        // ====================================================================
        layoutChampionshipHeader.setOnClickListener(v -> {
            if (layoutChampionshipOptionsContainer.getVisibility() == View.GONE) {
                layoutChampionshipOptionsContainer.setVisibility(View.VISIBLE);
                ivChampionshipArrow.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                layoutChampionshipOptionsContainer.setVisibility(View.GONE);
                ivChampionshipArrow.setImageResource(android.R.drawable.arrow_down_float);
                tvChampionshipsHeader.setText("SELECT CHAMPIONSHIP");
            }
        });

        // ====================================================================
        // ΔΙΟΡΘΩΘΗΚΕ: ΛΟΓΙΚΗ ACCORDION ΓΙΑ ΤΑ MATCHES (Διαχείριση visibility του RecyclerView)
        // ====================================================================
        layoutMatchesHeader.setOnClickListener(v -> {
            if (rvLiveMatches.getVisibility() == View.GONE) {
                rvLiveMatches.setVisibility(View.VISIBLE);
                ivMatchesArrow.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                rvLiveMatches.setVisibility(View.GONE);
                ivMatchesArrow.setImageResource(android.R.drawable.arrow_down_float);
            }
        });

        // Φόρτωση Πρωταθλημάτων
        loadChampionships();
    }

    // ΔΙΟΡΘΩΘΗΚΕ: Φόρτωση των LIVE αγώνων και σύνδεση με τον LiveMatchAdapter
    private void fetchLiveMatchesFromBackend() {
        new Thread(() -> {
            try {
                String url = "http://" + ip + "/statScopeApp/backend/api/getMatches.php?status=live";

                Request request = new Request.Builder().url(url).build();
                Response response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    runOnUiThread(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            // ΜΑΓΙΚΗ ΓΡΑΜΜΗ: Αντί να φτιάχνουμε TextViews, δίνουμε τη λίστα στον Adapter
                            LiveMatchAdapter adapter = new LiveMatchAdapter(this, jsonArray);
                            rvLiveMatches.setAdapter(adapter);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("LiveMatches", "Error fetching live matches: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void loadChampionships() {
        try {
            chlist = new ChampionshipList(ip);
            List<String> championshipNames = new ArrayList<>(chlist.getChampionships());

            layoutChampionshipOptionsContainer.removeAllViews();

            for (int i = 0; i < championshipNames.size(); i++) {
                final int position = i;
                final String championshipName = championshipNames.get(i);

                TextView tvOption = new TextView(this);
                tvOption.setText(championshipName);
                tvOption.setTextColor(Color.WHITE);
                tvOption.setTextSize(16);
                tvOption.setPadding(32, 24, 32, 24);
                tvOption.setClickable(true);
                tvOption.setFocusable(true);
                tvOption.setBackgroundColor(Color.parseColor("#0a1128"));

                tvOption.setOnClickListener(view -> {
                    tvChampionshipsHeader.setText(championshipName);
                    layoutChampionshipOptionsContainer.setVisibility(View.GONE);
                    ivChampionshipArrow.setImageResource(android.R.drawable.arrow_down_float);

                    Intent intent = new Intent(FanActivity.this, ChampionshipActivity.class);
                    intent.putExtra("CHAMPIONSHIP_NAME", championshipName);
                    intent.putExtra("CHAMPIONSHIP_ID", chlist.getChampionshipId(position));
                    startActivity(intent);
                });

                if (i > 0) {
                    View divider = new View(this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    divider.setBackgroundColor(Color.parseColor("#1FFFFFFF"));
                    layoutChampionshipOptionsContainer.addView(divider);
                }

                layoutChampionshipOptionsContainer.addView(tvOption);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά τη φόρτωση των πρωταθλημάτων", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset του Dropdown των Championships στο SELECT
        if (tvChampionshipsHeader != null) {
            tvChampionshipsHeader.setText("SELECT CHAMPIONSHIP");
        }
        if (layoutChampionshipOptionsContainer != null) {
            layoutChampionshipOptionsContainer.setVisibility(View.GONE);
        }
        if (ivChampionshipArrow != null) {
            ivChampionshipArrow.setImageResource(android.R.drawable.arrow_down_float);
        }

        // ΔΙΟΡΘΩΘΗΚΕ: Τα Matches θέλουμε να μένουν ανοιχτά (RecyclerView)
        if (rvLiveMatches != null) {
            rvLiveMatches.setVisibility(View.VISIBLE);
        }
        if (ivMatchesArrow != null) {
            ivMatchesArrow.setImageResource(android.R.drawable.arrow_up_float);
        }

        // Φρεσκάρισμα των Live αγώνων κάθε φορά που ανοίγει ή επιστρέφουμε στην οθόνη
        fetchLiveMatchesFromBackend();
    }
}