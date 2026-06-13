package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FanMatchDetailsActivity extends AppCompatActivity {

    private RecyclerView rvHomePlayers, rvAwayPlayers;
    private ImageView ivHomeArrow, ivAwayArrow;

    private int matchId, homeTeamId, awayTeamId;

    private String homeTeam, awayTeam;
    private PlayerAdapter homeAdapter, awayAdapter;

    private final List<Player> homePlayersList = new ArrayList<>();
    private final List<Player> awayPlayersList = new ArrayList<>();

    // Δήλωση των TextViews για τα στατιστικά
    private TextView tvStatHomePossession, tvStatAwayPossession;
    private TextView tvStatHomeShots, tvStatAwayShots;
    private TextView tvStatHomePasses, tvStatAwayPasses;
    private TextView tvStatHomeFouls, tvStatAwayFouls;
    private TextView tvStatHomeCards, tvStatAwayCards;
    private TextView tvStatHomeAssists, tvStatAwayAssists;
    private TextView tvStatHomeTackles, tvStatAwayTackles;
    private TextView tvStatHomeCorners, tvStatAwayCorners;
    private TextView tvStatHomeMistakes, tvStatAwayMistakes;

    // 🌟 Διορθωμένο: Μετατροπή των βασικών TextViews της κορυφής σε καθολικές μεταβλητές (Global)
    private TextView tvDetailHome, tvDetailAway, tvScore;

    // 🆕 Μεταβλητές για το Live Feed των Συμβάντων (Διορθωμένο σε List<JSONObject>)
    private RecyclerView rvMatchEvents;
    private final List<JSONObject> eventsList = new ArrayList<>();
    private EventLogAdapter eventLogAdapter;

    // LIVE Polling
    private final Handler liveHandler = new Handler(android.os.Looper.getMainLooper());
    private Runnable liveRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fanmatchdetails);

        // 1. Σύνδεση των βασικών Views (Διορθωμένο: Χρήση των global μεταβλητών)
        ImageButton btnBack = findViewById(R.id.btn_back_details);
        tvDetailHome = findViewById(R.id.tv_detail_home);
        tvDetailAway = findViewById(R.id.tv_detail_away);
        tvScore = findViewById(R.id.tv_detail_score);
        ImageView ivHomeLogo = findViewById(R.id.iv_detail_home_logo);
        ImageView ivAwayLogo = findViewById(R.id.iv_detail_away_logo);

        // 2. Σύνδεση και setup του Live Feed Adapter
        rvMatchEvents = findViewById(R.id.rv_match_events);
        rvMatchEvents.setLayoutManager(new LinearLayoutManager(this));
        eventLogAdapter = new EventLogAdapter(eventsList);
        rvMatchEvents.setAdapter(eventLogAdapter);

        // 3. Σύνδεση των Expandable Views (Headers & Arrows)
        LinearLayout layoutHomeHeader = findViewById(R.id.layout_home_header);
        LinearLayout layoutAwayHeader = findViewById(R.id.layout_away_header);
        TextView tvHomeHeaderTitle = findViewById(R.id.tv_home_header_title);
        TextView tvAwayHeaderTitle = findViewById(R.id.tv_away_header_title);
        ivHomeArrow = findViewById(R.id.iv_home_arrow);
        ivAwayArrow = findViewById(R.id.iv_away_arrow);

        // 4. Σύνδεση όλων των Views για τα Stats (16 συνολικά FindViewById)
        tvStatHomePossession = findViewById(R.id.tv_stat_home_possession);
        tvStatAwayPossession = findViewById(R.id.tv_stat_away_possession);
        tvStatHomeShots = findViewById(R.id.tv_stat_home_shots);
        tvStatAwayShots = findViewById(R.id.tv_stat_away_shots);
        tvStatHomePasses = findViewById(R.id.tv_stat_home_passes);
        tvStatAwayPasses = findViewById(R.id.tv_stat_away_passes);
        tvStatHomeFouls = findViewById(R.id.tv_stat_home_fouls);
        tvStatAwayFouls = findViewById(R.id.tv_stat_away_fouls);
        tvStatHomeCards = findViewById(R.id.tv_stat_home_cards);
        tvStatAwayCards = findViewById(R.id.tv_stat_away_cards);

        tvStatHomeAssists = findViewById(R.id.tv_stat_home_assists);
        tvStatAwayAssists = findViewById(R.id.tv_stat_away_assists);
        tvStatHomeTackles = findViewById(R.id.tv_stat_home_tackles);
        tvStatAwayTackles = findViewById(R.id.tv_stat_away_tackles);
        tvStatHomeCorners = findViewById(R.id.tv_stat_home_corners);
        tvStatAwayCorners = findViewById(R.id.tv_stat_away_corners);
        tvStatHomeMistakes = findViewById(R.id.tv_stat_home_mistakes);
        tvStatAwayMistakes = findViewById(R.id.tv_stat_away_mistakes);

        // 4. Λήψη και εμφάνιση δεδομένων από το Intent
        if (getIntent() != null) {
            matchId = getIntent().getIntExtra("MATCH_ID", -1);
            homeTeamId = getIntent().getIntExtra("HOME_TEAM_ID", -1);
            awayTeamId = getIntent().getIntExtra("AWAY_TEAM_ID", -1);

            homeTeam = getIntent().getStringExtra("HOME_TEAM");
            awayTeam = getIntent().getStringExtra("AWAY_TEAM");
            Log.d("DETAILS_CHECK", "Home Team Name received: " + homeTeam + " | Away Team Name received: " + awayTeam);
            int homeScore = getIntent().getIntExtra("HOME_SCORE", 0);
            int awayScore = getIntent().getIntExtra("AWAY_SCORE", 0);
            String homeLogo = getIntent().getStringExtra("HOME_LOGO");
            String awayLogo = getIntent().getStringExtra("AWAY_LOGO");

            // Εμφάνιση στο Scoreboard
            tvDetailHome.setText(homeTeam);
            tvDetailAway.setText(awayTeam);
            tvScore.setText(homeScore + " - " + awayScore);

            // Δυναμική μετονομασία των πτυσσόμενων κουμπιών με το όνομα της ομάδας
            tvHomeHeaderTitle.setText(homeTeam);
            tvAwayHeaderTitle.setText(awayTeam);

            Glide.with(this).load(homeLogo).into(ivHomeLogo);
            Glide.with(this).load(awayLogo).into(ivAwayLogo);
        }

        // Click Listener για τη Γηπεδούχο Ομάδα (Home Team)
        View.OnClickListener homeTeamClickListener = v -> {
            if (homeTeamId != -1) {
                Intent intent = new Intent(FanMatchDetailsActivity.this, TeamRosterActivity.class);
                intent.putExtra("TEAM_ID", homeTeamId);
                startActivity(intent);
            }
        };
        ivHomeLogo.setOnClickListener(homeTeamClickListener);
        tvDetailHome.setOnClickListener(homeTeamClickListener);

        // Click Listener για τη Φιλοξενούμενη Ομάδα (Away Team)
        View.OnClickListener awayTeamClickListener = v -> {
            if (awayTeamId != -1) {
                Intent intent = new Intent(FanMatchDetailsActivity.this, TeamRosterActivity.class);
                intent.putExtra("TEAM_ID", awayTeamId);
                startActivity(intent);
            }
        };
        ivAwayLogo.setOnClickListener(awayTeamClickListener);
        tvDetailAway.setOnClickListener(awayTeamClickListener);

        // 5. Setup των RecyclerViews
        setupLineupsRecyclerViews();

        // 6. Φόρτωμα των δεδομένων live μέσω OkHttp
        fetchLineupsFromBackend();

        // Φόρτωμα στατιστικών αγώνα αν το matchId είναι έγκυρο
        if (matchId != -1) {
            fetchMatchStatsFromBackend();
        }

        // 7. Click Listeners για το Άνοιγμα/Κλείσιμο (Expand/Collapse)
        layoutHomeHeader.setOnClickListener(v -> toggleLayout(rvHomePlayers, ivHomeArrow));
        layoutAwayHeader.setOnClickListener(v -> toggleLayout(rvAwayPlayers, ivAwayArrow));

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        liveRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMatchStatsFromBackend();
                liveHandler.postDelayed(this, 5000);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        liveHandler.post(liveRunnable); // Έναρξη live polling
    }

    @Override
    protected void onPause() {
        super.onPause();
        liveHandler.removeCallbacks(liveRunnable); // Σταμάτημα polling για οικονομία μπαταρίας
    }

    private void setupLineupsRecyclerViews() {
        rvHomePlayers = findViewById(R.id.rv_home_players);
        rvAwayPlayers = findViewById(R.id.rv_away_players);

        rvHomePlayers.setLayoutManager(new LinearLayoutManager(this));
        rvAwayPlayers.setLayoutManager(new LinearLayoutManager(this));

        homeAdapter = new PlayerAdapter(homePlayersList, homeTeam);
        awayAdapter = new PlayerAdapter(awayPlayersList, awayTeam);

        rvHomePlayers.setAdapter(homeAdapter);
        rvAwayPlayers.setAdapter(awayAdapter);
    }

    private void fetchLineupsFromBackend() {
        new Thread(() -> {
            try {
                String url = Config.BASE_URL+"/getMatchLineups.php?match_id=" + matchId;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("players");

                    homePlayersList.clear();
                    awayPlayersList.clear();

                    com.google.gson.Gson gson = new com.google.gson.Gson();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject playerObj = jsonArray.getJSONObject(i);
                        Player player = gson.fromJson(playerObj.toString(), Player.class);

                        if (player.getTeamId() == homeTeamId) {
                            homePlayersList.add(player);
                        } else if (player.getTeamId() == awayTeamId) {
                            awayPlayersList.add(player);
                        }
                    }

                    runOnUiThread(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            homeAdapter.notifyDataSetChanged();
                            awayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("Lineups", "Error fetching lineups: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // Τράβηγμα στατιστικών από το getMatchStats.php και getMatchEvents.php APIs
    private void fetchMatchStatsFromBackend() {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // ─────────────────────────────────────────────────────────────────
                // 🌟 CALL 1: Τραβάμε τα 16 αριθμητικά στατιστικά (getMatchStats.php)
                // ─────────────────────────────────────────────────────────────────
                String urlStats = Config.BASE_URL + "/getMatchStats.php?match_id=" + matchId;
                Request requestStats = new Request.Builder().url(urlStats).build();
                Response responseStats = client.newCall(requestStats).execute();

                String homeShots = "0", awayShots = "0", homePasses = "0", awayPasses = "0";
                String homeFouls = "0", awayFouls = "0", homeCards = "0", awayCards = "0";
                String homeAssists = "0", awayAssists = "0", homeTackles = "0", awayTackles = "0";
                String homeCorners = "0", awayCorners = "0", homeMistakes = "0", awayMistakes = "0";

                if (responseStats.isSuccessful() && responseStats.body() != null) {
                    String jsonResponse = responseStats.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    homeShots = jsonObject.getString("home_shots");
                    awayShots = jsonObject.getString("away_shots");
                    homePasses = jsonObject.getString("home_passes");
                    awayPasses = jsonObject.getString("away_passes");
                    homeFouls = jsonObject.getString("home_fouls");
                    awayFouls = jsonObject.getString("away_fouls");
                    homeCards = jsonObject.getString("home_cards");
                    awayCards = jsonObject.getString("away_cards");
                    homeAssists = jsonObject.getString("home_assists");
                    awayAssists = jsonObject.getString("away_assists");
                    homeTackles = jsonObject.getString("home_tackles");
                    awayTackles = jsonObject.getString("away_tackles");
                    homeCorners = jsonObject.getString("home_corners");
                    awayCorners = jsonObject.getString("away_corners");
                    homeMistakes = jsonObject.getString("home_mistakes");
                    awayMistakes = jsonObject.getString("away_mistakes");
                }

                // ─────────────────────────────────────────────────────────────────
                // 🌟 CALL 2: Τραβάμε τις live ενέργειες από το δικό σου getMatchEvents.php
                // ─────────────────────────────────────────────────────────────────
                String urlEvents = Config.BASE_URL + "/getMatchEvents.php?match_id=" + matchId;
                Request requestEvents = new Request.Builder().url(urlEvents).build();
                Response responseEvents = client.newCall(requestEvents).execute();

                final List<JSONObject> fetchedEvents = new ArrayList<>();
                if (responseEvents.isSuccessful() && responseEvents.body() != null) {
                    String jsonEvents = responseEvents.body().string();
                    JSONArray jsonArray = new JSONArray(jsonEvents);

                    // 1. Προσθέτουμε κανονικά όλα τα events στη λίστα
                    for (int i = 0; i < jsonArray.length(); i++) {
                        fetchedEvents.add(jsonArray.getJSONObject(i));
                    }

                    // 🛠️ 2. ΤΑΞΙΝΟΜΗΣΗ: Ταξινομούμε τη λίστα ώστε το μεγαλύτερο λεπτό να πηγαίνει ΠΑΝΤΑ πρώτο!
                    java.util.Collections.sort(fetchedEvents, new java.util.Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject a, JSONObject b) {
                            int minA = a.optInt("event_minute", 0);
                            int minB = b.optInt("event_minute", 0);

                            // Φθίνουσα σειρά (από το μεγαλύτερο λεπτό στο μικρότερο)
                            return Integer.compare(minB, minA);
                        }
                    });
                }

                // ─────────────────────────────────────────────────────────────────
                // 🌟 CALL 3: Ανανέωση του UI στο Main Thread
                // ─────────────────────────────────────────────────────────────────
                final String fHomeShots = homeShots; final String fAwayShots = awayShots;
                final String fHomePasses = homePasses; final String fAwayPasses = awayPasses;
                final String fHomeFouls = homeFouls; final String fAwayFouls = awayFouls;
                final String fHomeCards = homeCards; final String fAwayCards = awayCards;
                final String fHomeAssists = homeAssists; final String fAwayAssists = awayAssists;
                final String fHomeTackles = homeTackles; final String fAwayTackles = awayTackles;
                final String fHomeCorners = homeCorners; final String fAwayCorners = awayCorners;
                final String fHomeMistakes = homeMistakes; final String fAwayMistakes = awayMistakes;

                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        // Γεμίζουμε το Live Feed (που πλέον δέχεται JSONObject)
                        eventsList.clear();
                        eventsList.addAll(fetchedEvents);
                        eventLogAdapter.notifyDataSetChanged();
                        int homePassesInt = 0;
                        int awayPassesInt = 0;
                        try {
                            homePassesInt = Integer.parseInt(fHomePasses);
                            awayPassesInt = Integer.parseInt(fAwayPasses);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        int homePossession = 50;
                        int awayPossession = 50;
                        int totalPasses = homePassesInt + awayPassesInt;

                        if (totalPasses > 0) {
                            homePossession = Math.round(((float) homePassesInt / totalPasses) * 100);
                            awayPossession = 100 - homePossession;
                        }


                        // Ενημερώνουμε τα TextViews των στατιστικών
                        tvStatHomePossession.setText(homePossession + "%");
                        tvStatAwayPossession.setText(awayPossession + "%");
                        tvStatHomeShots.setText(fHomeShots);
                        tvStatAwayShots.setText(fAwayShots);
                        tvStatHomePasses.setText(fHomePasses);
                        tvStatAwayPasses.setText(fAwayPasses);
                        tvStatHomeFouls.setText(fHomeFouls);
                        tvStatAwayFouls.setText(fAwayFouls);
                        tvStatHomeCards.setText(fHomeCards);
                        tvStatAwayCards.setText(fAwayCards);
                        tvStatHomeAssists.setText(fHomeAssists);
                        tvStatAwayAssists.setText(fAwayAssists);
                        tvStatHomeTackles.setText(fHomeTackles);
                        tvStatAwayTackles.setText(fAwayTackles);
                        tvStatHomeCorners.setText(fHomeCorners);
                        tvStatAwayCorners.setText(fAwayCorners);
                        tvStatHomeMistakes.setText(fHomeMistakes);
                        tvStatAwayMistakes.setText(fAwayMistakes);
                    }
                });

            } catch (Exception e) {
                Log.e("MatchStats", "Error: " + e.getMessage());
            }
        }).start();
    }

    private void toggleLayout(RecyclerView recyclerView, ImageView arrowIcon) {
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
            arrowIcon.setImageResource(android.R.drawable.arrow_up_float);
        } else {
            recyclerView.setVisibility(View.GONE);
            arrowIcon.setImageResource(android.R.drawable.arrow_down_float);
        }
    }


    // 🌟 Διορθωμένος και πλήρης Adapter με δυναμική στοίχιση και λεπτό αγώνα
    private class EventLogAdapter extends RecyclerView.Adapter<EventLogAdapter.ViewHolder> {
        private final List<JSONObject> logs;

        EventLogAdapter(List<JSONObject> logs) { this.logs = logs; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_event, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                JSONObject statObj = logs.get(position);

                // 1. Διάβασμα δεδομένων από το API με ασφάλεια (optString)
                String teamName = statObj.optString("team_name", "UNKNOWN");
                String playerName = statObj.optString("player_name", "TEAM");
                String eventType = statObj.optString("event_type", "").toUpperCase().replace("_", " ");
                String outcome = statObj.optString("outcome", "").toLowerCase();
                int minute = statObj.optInt("event_minute", 0);

                // Αν το player_name επιστρέψει null ως String ή είναι άδειο, το κάνουμε "TEAM"
                if (playerName == null || playerName.equalsIgnoreCase("null") || playerName.trim().isEmpty()) {
                    playerName = "TEAM";
                }

                // Αν το teamName είναι null, βάζουμε μια προσωρινή τιμή για να μη σκάσει ο έλεγχος παρακάτω
                if (teamName == null || teamName.equalsIgnoreCase("null")) {
                    teamName = "UNKNOWN";
                }

                // 2. Εμφάνιση λεπτού
                holder.tvEventMinute.setText(minute + "'");

                // 🛠️ 3. ΕΞΥΠΝΗ ΜΟΡΦΟΠΟΙΗΣΗ ΓΙΑ ΓΚΟΛ
                String formattedText;
                boolean isRealGoal = outcome.equalsIgnoreCase("goal");

                if (isRealGoal) {
                    // Αν είναι γκολ, γράφουμε GOAL με κεφαλαία!
                    formattedText = playerName + " (GOAL!).";
                } else {
                    // Για όλα τα υπόλοιπα στατιστικά, κρατάμε τη default μορφή
                    formattedText = playerName + " (" + eventType + ").";
                }
                holder.tvEventText.setText(formattedText);

                // 4. ΕΠΑΝΑΦΟΡΑ ΣΕΙΡΑΣ (Reset των Views)
                holder.rowContainer.removeAllViews();
                holder.rowContainer.addView(holder.tvEventMinute);
                holder.rowContainer.addView(holder.tvEventText);

                // 5. Έλεγχος ομάδας για στοίχιση και χρώματα
                String homeTeamTitle = tvDetailHome.getText().toString();

                if (teamName.equalsIgnoreCase(homeTeamTitle)) {
                    // 🏠 HOME TEAM: Στοίχιση Αριστερά
                    holder.tvEventText.setGravity(android.view.Gravity.START);

                    if (isRealGoal) {
                        // Αν είναι γκολ της Home Team, το βάφουμε με το φωτεινό πράσινο της εφαρμογής σου!
                        holder.tvEventText.setTextColor(android.graphics.Color.parseColor("#00E676"));
                    } else {
                        holder.tvEventText.setTextColor(android.graphics.Color.WHITE);
                    }
                    holder.tvEventMinute.setPadding(0, 0, 8, 0);

                } else {
                    // 🚀 AWAY TEAM: Στοίχιση Δεξιά
                    holder.rowContainer.removeView(holder.tvEventMinute);
                    holder.rowContainer.addView(holder.tvEventMinute); // Το λεπτό πάει δεξιά

                    holder.tvEventText.setGravity(android.view.Gravity.END);

                    if (isRealGoal) {
                        // Αν είναι γκολ της Away Team, το βάφουμε επίσης πράσινο για να βγάζει μάτι!
                        holder.tvEventText.setTextColor(android.graphics.Color.parseColor("#00E676"));
                    } else {
                        holder.tvEventText.setTextColor(android.graphics.Color.parseColor("#8A92B2"));
                    }
                    holder.tvEventMinute.setPadding(8, 0, 0, 0);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() { return logs.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout rowContainer; // 🆕 Σύνδεση με το οριζόντιο LinearLayout
            TextView tvEventText;
            TextView tvEventMinute; // 🆕 Σύνδεση με το TextView του λεπτού

            ViewHolder(View v) {
                super(v);
                rowContainer = v.findViewById(R.id.layout_event_row_container);
                tvEventText = v.findViewById(R.id.tv_live_event_text);
                tvEventMinute = v.findViewById(R.id.tv_live_event_minute);
            }
        }
    }
}