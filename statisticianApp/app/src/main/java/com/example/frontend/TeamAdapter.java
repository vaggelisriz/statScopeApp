package com.example.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {

    private final List<Team> teamList;
    private final OnTeamClickListener listener;

    public interface OnTeamClickListener {
        void onTeamClick(Team team);
    }

    public TeamAdapter(List<Team> teamList, OnTeamClickListener listener) {
        this.teamList = teamList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.tvName.setText(team.getName());
        holder.tvCity.setText(team.getCity());

        Glide.with(holder.itemView.getContext())
                .load(team.getLogo())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivLogo);

        holder.itemView.setOnClickListener(v -> listener.onTeamClick(team));
    }

    @Override
    public int getItemCount() { return teamList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName, tvCity;

        ViewHolder(View view) {
            super(view);
            ivLogo = view.findViewById(R.id.iv_team_logo);
            tvName = view.findViewById(R.id.tv_team_name);
            tvCity = view.findViewById(R.id.tv_team_city);
        }
    }
}