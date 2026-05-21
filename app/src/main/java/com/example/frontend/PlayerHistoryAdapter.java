package com.example.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlayerHistoryAdapter extends RecyclerView.Adapter<PlayerHistoryAdapter.ViewHolder> {

    private final List<PlayerMatchHistory> historyList;

    public PlayerHistoryAdapter(List<PlayerMatchHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerMatchHistory item = historyList.get(position);

        holder.text1.setText(item.getMatchTitle());
        holder.text1.setTextColor(0xFFFFFFFF); // Λευκό κείμενο για το dark mode σου

        // Χτίζουμε το κείμενο των στατιστικών για αυτό το ματς
        StringBuilder stats = new StringBuilder();
        if (item.getGoals() > 0) stats.append("⚽ ").append(item.getGoals()).append(" Goals   ");
        if (item.getAssists() > 0) stats.append("🎯 ").append(item.getAssists()).append(" Assists   ");
        if (item.getYellowCards() > 0) stats.append("🟨 ").append(item.getYellowCards()).append(" Yellow   ");
        if (item.getRedCards() > 0) stats.append("🟥 ").append(item.getRedCards()).append(" Red   ");

        if (stats.length() == 0) stats.append("⏱️ Appeared in match");

        holder.text2.setText(stats.toString());
        holder.text2.setTextColor(0xFFAAAAAA); // Γκρίζο για τα στατιστικά
    }

    @Override
    public int getItemCount() { return historyList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        ViewHolder(View view) {
            super(view);
            text1 = view.findViewById(android.R.id.text1);
            text2 = view.findViewById(android.R.id.text2);
        }
    }
}