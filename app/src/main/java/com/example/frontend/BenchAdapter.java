package com.example.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter για τη λίστα του πάγκου μέσα στο BottomSheetDialog.
 * Κάνοντας tap σε έναν παίκτη τον επιλέγεις για αλλαγή.
 *
 * Layout απαιτήσεις: item_bench_player.xml με:
 *   - tvBenchName     (TextView)
 *   - tvBenchPosition (TextView)
 */
public class BenchAdapter extends RecyclerView.Adapter<BenchAdapter.ViewHolder> {

    public interface OnPlayerSelectedListener {
        void onPlayerSelected(Player player);
    }

    private final List<Player> bench;
    private final OnPlayerSelectedListener listener;

    public BenchAdapter(List<Player> bench, OnPlayerSelectedListener listener) {
        this.bench    = bench;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bench_player, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = bench.get(position);
        holder.tvName.setText(player.getName());
        holder.tvPosition.setText(player.getPosition() != null ? player.getPosition() : "—");
        holder.itemView.setOnClickListener(v -> listener.onPlayerSelected(player));
    }

    @Override
    public int getItemCount() { return bench.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition;

        ViewHolder(View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvBenchName);
            tvPosition = itemView.findViewById(R.id.tvBenchPosition);
        }
    }
}