package com.example.frontend;

import android.content.Context;
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
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Match match);
    }

    public MatchAdapter(Context context, List<Match> matches, OnItemClickListener listener) {
        this.context = context;
        this.matches = matches;
        this.listener = listener;
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

        // DISPLAY THE CHAMPIONSHIP NAME
        // It takes the value we added in Match.java from the PHP JOIN
        holder.tvChampionship.setText(match.getChampionshipName());

        Glide.with(context).load(match.getHomeLogo()).into(holder.ivHomeLogo);
        Glide.with(context).load(match.getAwayLogo()).into(holder.ivAwayLogo);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(match));
    }

    @Override
    public int getItemCount() { return matches != null ? matches.size() : 0; }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvHomeName, tvAwayName, tvScore, tvStatus, tvChampionship;
        ImageView ivHomeLogo, ivAwayLogo;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeName = itemView.findViewById(R.id.tv_home_name);
            tvAwayName = itemView.findViewById(R.id.tv_away_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvStatus = itemView.findViewById(R.id.tv_status);
            // NEW: Link the Championship TextView
            tvChampionship = itemView.findViewById(R.id.tv_championship_name);
            ivHomeLogo = itemView.findViewById(R.id.iv_home_logo);
            ivAwayLogo = itemView.findViewById(R.id.iv_away_logo);
        }
    }
}