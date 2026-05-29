package com.example.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> players;
    private List<Integer> selectedPlayerIds = new ArrayList<>();
    private OnSelectionChangedListener listener;
    private boolean isReadOnly = false;

    private String teamName;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public PlayerAdapter(List<Player> players, OnSelectionChangedListener listener) {
        this.players = players != null ? players : new ArrayList<>();
        this.listener = listener;
        this.isReadOnly = false;
    }

    public PlayerAdapter(List<Player> players) {
        this.players = players != null ? players : new ArrayList<>();
        this.isReadOnly = true;
    }

    public PlayerAdapter(List<Player> players, String teamName) {
        this.players = players != null ? players : new ArrayList<>();
        this.teamName = teamName;
        this.isReadOnly = true;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = players.get(position);

        holder.tvName.setText(player.getName());
        holder.tvPosition.setText(player.getPosition());

        Glide.with(holder.itemView.getContext())
                .load(player.getPhoto())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .circleCrop()
                .into(holder.ivPhoto);

        holder.cbStarter.setVisibility(View.VISIBLE);
        holder.cbStarter.setOnCheckedChangeListener(null);
        holder.cbStarter.setChecked(selectedPlayerIds.contains(player.getId()));

        holder.itemView.setOnClickListener(v -> {
            int id = player.getId();
            if (selectedPlayerIds.contains(id)) {
                selectedPlayerIds.remove(Integer.valueOf(id));
            } else {
                if (selectedPlayerIds.size() < 11) {
                    selectedPlayerIds.add(id);
                }
            }
            notifyItemChanged(position);
            if (listener != null) {
                listener.onSelectionChanged(selectedPlayerIds.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public List<Integer> getSelectedPlayerIds() {
        return selectedPlayerIds;
    }

    // Μέθοδος για να αρχικοποιούμε τα IDs όταν ανοίγει το dialog
    public void setSelectedPlayerIds(List<Integer> ids) {
        this.selectedPlayerIds = new ArrayList<>(ids);
        notifyDataSetChanged();
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition;
        ImageView ivPhoto;
        CheckBox cbStarter;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_player_name);
            tvPosition = itemView.findViewById(R.id.tv_player_position);
            ivPhoto = itemView.findViewById(R.id.iv_player_photo);
            cbStarter = itemView.findViewById(R.id.cb_is_starter);
        }
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}