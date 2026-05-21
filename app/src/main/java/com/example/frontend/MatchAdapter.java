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

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter
        extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matches;

    private final Context context;
    private final OnItemClickListener listener;

    // ─────────────────────────────────────────────
    // CLICK LISTENER
    // ─────────────────────────────────────────────
    public interface OnItemClickListener {
        void onItemClick(Match match);
    }

    // ─────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────
    public MatchAdapter(
            Context context,
            List<Match> matches,
            OnItemClickListener listener
    ) {

        this.context = context;

        this.matches =
                matches != null
                        ? matches
                        : new ArrayList<>();

        this.listener = listener;
    }

    // ─────────────────────────────────────────────
    // UPDATE LIST
    // ─────────────────────────────────────────────
    public void updateList(List<Match> newMatches) {

        this.matches.clear();

        if (newMatches != null) {
            this.matches.addAll(newMatches);
        }

        notifyDataSetChanged();
    }

    // ─────────────────────────────────────────────
    // CREATE VIEW HOLDER
    // ─────────────────────────────────────────────
    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_match,
                        parent,
                        false
                );

        return new MatchViewHolder(view);
    }

    // ─────────────────────────────────────────────
    // BIND DATA
    // ─────────────────────────────────────────────
    @Override
    public void onBindViewHolder(
            @NonNull MatchViewHolder holder,
            int position
    ) {

        Match match = matches.get(position);

        // TEAM NAMES
        holder.tvHomeName.setText(
                match.getHomeTeam()
        );

        holder.tvAwayName.setText(
                match.getAwayTeam()
        );

        // SCORE FROM DATABASE
        String score =
                match.getHomeScore()
                        + " - " +
                        match.getAwayScore();

        holder.tvScore.setText(score);

        // STATUS
        holder.tvStatus.setText(
                match.getStatus()
        );

        // CHAMPIONSHIP
        holder.tvChampionship.setText(
                match.getChampionshipName()
        );

        // HOME LOGO
        Glide.with(context)
                .load(match.getHomeLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivHomeLogo);

        // AWAY LOGO
        Glide.with(context)
                .load(match.getAwayLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivAwayLogo);

        // CLICK
        holder.itemView.setOnClickListener(v ->
                listener.onItemClick(match)
        );
    }

    // ─────────────────────────────────────────────
    // ITEM COUNT
    // ─────────────────────────────────────────────
    @Override
    public int getItemCount() {

        return matches != null
                ? matches.size()
                : 0;
    }

    // ─────────────────────────────────────────────
    // VIEW HOLDER
    // ─────────────────────────────────────────────
    static class MatchViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvHomeName;
        TextView tvAwayName;
        TextView tvScore;
        TextView tvStatus;
        TextView tvChampionship;

        ImageView ivHomeLogo;
        ImageView ivAwayLogo;

        public MatchViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvHomeName =
                    itemView.findViewById(
                            R.id.tv_home_name
                    );

            tvAwayName =
                    itemView.findViewById(
                            R.id.tv_away_name
                    );

            tvScore =
                    itemView.findViewById(
                            R.id.tv_score
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tv_status
                    );

            tvChampionship =
                    itemView.findViewById(
                            R.id.tv_championship_name
                    );

            ivHomeLogo =
                    itemView.findViewById(
                            R.id.iv_home_logo
                    );

            ivAwayLogo =
                    itemView.findViewById(
                            R.id.iv_away_logo
                    );
        }
    }
}