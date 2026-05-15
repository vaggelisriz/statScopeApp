package com.example.frontend;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matches;

    public MatchAdapter(List<Match> matches) {
        this.matches = matches;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matches.get(position);

        holder.tvHomeName.setText(match.getHomeTeam());
        holder.tvAwayName.setText(match.getAwayTeam());
        holder.tvScore.setText(match.getHomeScore() + " - " + match.getAwayScore());
        holder.tvStatus.setText(match.getStatus());

        // Φόρτωση Home Logo
        Glide.with(holder.itemView.getContext())
                .load(match.getHomeLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.ivHomeLogo);

        // Φόρτωση Away Logo
        Glide.with(holder.itemView.getContext())
                .load(match.getAwayLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.ivAwayLogo);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MatchDetailsActivity.class);
            intent.putExtra("selected_match", match);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return matches != null ? matches.size() : 0;
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeName, tvAwayName, tvScore, tvStatus;
        ImageView ivHomeLogo, ivAwayLogo;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeName = itemView.findViewById(R.id.tv_home_name);
            tvAwayName = itemView.findViewById(R.id.tv_away_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivHomeLogo = itemView.findViewById(R.id.iv_home_logo);
            ivAwayLogo = itemView.findViewById(R.id.iv_away_logo);
        }
    }
}