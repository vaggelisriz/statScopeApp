package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class LiveMatchAdapter extends RecyclerView.Adapter<LiveMatchAdapter.MatchViewHolder> {

    private final JSONArray matchesArray;
    private final Context context;

    public LiveMatchAdapter(Context context, JSONArray matchesArray) {
        this.context = context;
        this.matchesArray = matchesArray;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_live_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        try {
            JSONObject matchObj = matchesArray.getJSONObject(position);

            // Διαβάζουμε ΜΟΝΟ τις στήλες που επιστρέφει η getMatches.php
            int matchId = matchObj.getInt("id");
            String homeTeam = matchObj.getString("home_team");
            String awayTeam = matchObj.getString("away_team");
            int homeScore = matchObj.getInt("home_score");
            int awayScore = matchObj.getInt("away_score");
            String homeLogo = matchObj.getString("home_logo");
            String awayLogo = matchObj.getString("away_logo");
            String championshipName = matchObj.getString("championship_name");
            int homeTeamId = matchObj.getInt("home_team_id");
            int awayTeamId = matchObj.getInt("away_team_id");

            // Τοποθέτηση στα TextViews (Το status είναι καρφωμένο LIVE από το XML)
            holder.tvHomeTeam.setText(homeTeam);
            holder.tvAwayTeam.setText(awayTeam);
            holder.tvHomeScore.setText(String.valueOf(homeScore));
            holder.tvAwayScore.setText(String.valueOf(awayScore));
            holder.tvChampionship.setText(championshipName);

            // Click listener για άνοιγμα των Match Details
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FanMatchDetailsActivity.class);
                intent.putExtra("MATCH_ID", matchId);
                intent.putExtra("HOME_TEAM_ID", homeTeamId);
                intent.putExtra("AWAY_TEAM_ID", awayTeamId);
                intent.putExtra("HOME_TEAM", homeTeam);
                intent.putExtra("AWAY_TEAM", awayTeam);
                intent.putExtra("HOME_SCORE", homeScore);
                intent.putExtra("AWAY_SCORE", awayScore);
                intent.putExtra("HOME_LOGO", homeLogo);
                intent.putExtra("AWAY_LOGO", awayLogo);
                context.startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return matchesArray != null ? matchesArray.length() : 0;
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeTeam, tvAwayTeam, tvHomeScore, tvAwayScore, tvChampionship;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeTeam = itemView.findViewById(R.id.tv_item_home_team);
            tvAwayTeam = itemView.findViewById(R.id.tv_item_away_team);
            tvHomeScore = itemView.findViewById(R.id.tv_item_home_score);
            tvAwayScore = itemView.findViewById(R.id.tv_item_away_score);
            tvChampionship = itemView.findViewById(R.id.tv_item_championship);
        }
    }
}