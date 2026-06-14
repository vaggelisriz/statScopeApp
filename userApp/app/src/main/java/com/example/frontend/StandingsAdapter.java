package com.example.frontend;

import android.content.Context;
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

public class StandingsAdapter extends RecyclerView.Adapter<StandingsAdapter.ViewHolder> {

    private final List<TeamStanding> standingsList;

    public StandingsAdapter(List<TeamStanding> standingsList) {
        this.standingsList = standingsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_standing_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamStanding standing = standingsList.get(position);

        // Η θέση στον πίνακα είναι το position + 1
        holder.tvPosition.setText(String.valueOf(position + 1));
        holder.tvTeamName.setText(standing.getTeamName());
        holder.tvMp.setText(String.valueOf(standing.getMatchesPlayed()));

        // Οι τιμές ξεχωριστά σε κάθε στήλη (W, D, L)
        holder.tvWins.setText(String.valueOf(standing.getWins()));
        holder.tvDraws.setText(String.valueOf(standing.getDraws()));
        holder.tvLosses.setText(String.valueOf(standing.getLosses()));

        // Μορφή Γκολ π.χ. "25:8"
        String goals = standing.getGoalsScored() + ":" + standing.getGoalsConceded();
        holder.tvGoals.setText(goals);

        holder.tvPoints.setText(String.valueOf(standing.getPoints()));

        // Φόρτωση Logo ομάδας με Glide
        Glide.with(holder.itemView.getContext())
                .load(standing.getTeamLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivTeamLogo);

        // ΚΛΙΚ ΓΙΑ ΜΕΤΑΒΑΣΗ ΣΤΗΝ TEAM ROSTER ACTIVITY
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, TeamRosterActivity.class);

            // Περνάμε το ID της ομάδας με το Key που περιμένει η TeamRosterActivity
            intent.putExtra("TEAM_ID", standing.getTeamId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return standingsList.size();
    }

    // Μέθοδος για να ανανεώνουμε τα δεδομένα του πίνακα όταν αλλάζει πρωτάθλημα
    public void updateData(List<TeamStanding> newList) {
        standingsList.clear();
        standingsList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosition, tvTeamName, tvMp, tvWins, tvDraws, tvLosses, tvGoals, tvPoints;
        ImageView ivTeamLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosition = itemView.findViewById(R.id.tv_position);
            ivTeamLogo = itemView.findViewById(R.id.iv_team_logo);
            tvTeamName = itemView.findViewById(R.id.tv_team_name);
            tvMp       = itemView.findViewById(R.id.tv_mp);

            tvWins     = itemView.findViewById(R.id.tv_wins);
            tvDraws    = itemView.findViewById(R.id.tv_draws);
            tvLosses   = itemView.findViewById(R.id.tv_losses);

            tvGoals    = itemView.findViewById(R.id.tv_goals);
            tvPoints   = itemView.findViewById(R.id.tv_points);
        }
    }
}