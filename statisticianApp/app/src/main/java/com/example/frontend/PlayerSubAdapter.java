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

public class PlayerSubAdapter extends RecyclerView.Adapter<PlayerSubAdapter.PlayerViewHolder> {

    private List<Player> playerList;
    private final OnPlayerClickListener listener;
    private final boolean isBench; // true αν εμφανίζεται στο BottomSheet, false για την 11άδα

    // Interface για το κλικ στον παίκτη
    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    // Constructor
    public PlayerSubAdapter(List<Player> playerList, boolean isBench, OnPlayerClickListener listener) {
        this.playerList = playerList;
        this.isBench = isBench;
        this.listener = listener;
    }

    public void updateList(List<Player> newList) {
        this.playerList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_sub, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);

        holder.tvName.setText(player.getName());
        holder.tvPosition.setText(player.getPosition());
        holder.tvNumber.setText(String.valueOf(player.getNumber()));

        // SOS ΔΙΟΡΘΩΣΗ: Χρήση του getPhoto() που αντιστοιχεί στο μοντέλο Player
        Glide.with(holder.itemView.getContext())
                .load(player.getPhoto())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPhoto);

        // Διαμόρφωση του εικονιδίου δεξιά ανάλογα με το αν είναι 11άδα ή πάγκος
        if (isBench) {
            // Στον πάγκο δείχνουμε ένα πράσινο βέλος εισόδου
            holder.ivSubIcon.setImageResource(android.R.drawable.ic_input_add);
            holder.ivSubIcon.setColorFilter(android.graphics.Color.parseColor("#00E676"));
        } else {
            // Στην 11άδα δείχνουμε το κλασικό icon αλλαγής (rotate)
            holder.ivSubIcon.setImageResource(android.R.drawable.ic_menu_rotate);
            holder.ivSubIcon.setColorFilter(android.graphics.Color.parseColor("#888888"));
        }

        // Event Listener στο κλικ του item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayerClick(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playerList != null ? playerList.size() : 0;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition, tvNumber;
        ImageView ivPhoto, ivSubIcon;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_player_name);
            tvPosition = itemView.findViewById(R.id.tv_player_position);
            tvNumber = itemView.findViewById(R.id.tv_player_number);
            ivPhoto = itemView.findViewById(R.id.iv_player_photo);
            ivSubIcon = itemView.findViewById(R.id.iv_sub_icon);
        }
    }
}