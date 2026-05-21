package com.example.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StartersAdapter extends RecyclerView.Adapter<StartersAdapter.ViewHolder> {

    public interface OnSwapClickListener {
        void onSwapClick(Player player);
    }

    private List<Player> starters;
    private final OnSwapClickListener listener;

    public StartersAdapter(List<Player> starters, OnSwapClickListener listener) {
        this.starters = starters;
        this.listener = listener;
    }

    public void updateData(List<Player> newStarters) {
        this.starters = newStarters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_starter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = starters.get(position);

        // Εμφάνιση: Ονοματεπώνυμο (Αριθμός Φανέλας)
        String jerseyNumber = player.getNumber() != null ? player.getNumber() : "—";
        String displayName = player.getName() + " (" + jerseyNumber + ")";
        holder.tvName.setText(displayName);

        holder.tvPosition.setText(player.getPosition() != null ? player.getPosition() : "—");

        // Ακούει το κλικ σε ΟΛΟΚΛΗΡΗ τη σειρά του παίκτη για να μην κολλάει
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSwapClick(player);
            }
        });

        // Ακούει το κλικ και στο κουμπί ξεχωριστά
        holder.btnSwap.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSwapClick(player);
            }
        });

        // Μόνιμο Highlight για τους αλλαγμένους παίκτες
        if (player.isSubstituted()) {
            // Glass-effect στυλ με βαθύ αθλητικό πράσινο/κυανό υπόβαθρο
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#15242A"));

            holder.btnSwap.setText("CHANGED");
            holder.btnSwap.setEnabled(true); // Κρατάμε ενεργό το view

            View accentBar = holder.itemView.findViewById(R.id.accent_bar);
            if (accentBar != null) {
                accentBar.setBackgroundColor(android.graphics.Color.parseColor("#FFB300")); // Πορτοκαλί accent
            }
        } else {
            // Κανονική εμφάνιση βασικών
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#131833"));

            holder.btnSwap.setText("SUB OUT");
            holder.btnSwap.setEnabled(true);

            View accentBar = holder.itemView.findViewById(R.id.accent_bar);
            if (accentBar != null) {
                accentBar.setBackgroundColor(android.graphics.Color.parseColor("#00E676")); // Κλασικό neon πράσινο
            }
        }
    }

    @Override
    public int getItemCount() { return starters.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition;
        Button   btnSwap;

        ViewHolder(View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvPlayerName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            btnSwap    = itemView.findViewById(R.id.btnSwap);
        }
    }
}