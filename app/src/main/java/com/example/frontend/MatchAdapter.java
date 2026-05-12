package com.example.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList;

    public MatchAdapter(List<Match> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the match item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);

        // Bind data to the views using getters from Match class
        holder.tvHomeName.setText(match.getHomeTeam());
        holder.tvAwayName.setText(match.getAwayTeam());
        holder.tvScore.setText(match.getHomeScore() + " - " + match.getAwayScore());
    }

    @Override
    public int getItemCount() {
        return matchList != null ? matchList.size() : 0;
    }

    /**
     * ViewHolder class to hold references to the UI components
     */
    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeName, tvAwayName, tvScore;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            // Matching the IDs exactly as defined in your item_match.xml
            tvHomeName = itemView.findViewById(R.id.tv_home_team);
            tvAwayName = itemView.findViewById(R.id.tv_away_team);
            tvScore = itemView.findViewById(R.id.tv_score);
        }
    }
}