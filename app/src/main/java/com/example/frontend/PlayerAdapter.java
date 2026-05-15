package com.example.frontend;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> players;

    public PlayerAdapter(List<Player> players) {
        this.players = players;
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

        String photoUrl = player.getPhoto();
        String finalUrl = "";

        if (photoUrl != null && !photoUrl.isEmpty()) {
            // Έλεγχος αν είναι εξωτερικό link ή τοπικό path
            if (photoUrl.startsWith("http")) {
                finalUrl = photoUrl;
            } else {
                // Προσαρμογή για τοπικές εικόνες στον XAMPP (μέσω emulator 10.0.2.2)
                finalUrl = "http://10.0.2.2/statScopeApp/backend/" + photoUrl.replace("../", "");
            }

            Log.d("GLIDE_DEBUG", "Attempting to load: " + finalUrl);

            Glide.with(holder.itemView.getContext())
                    .load(finalUrl)
                    .circleCrop()
                    .timeout(30000) // Timeout 30 δευτερόλεπτα
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GLIDE_DEBUG", "Load FAILED for URL: " + model);
                            if (e != null) {
                                e.logRootCauses("GLIDE_DEBUG");
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("GLIDE_DEBUG", "Load SUCCESS for URL: " + model);
                            return false;
                        }
                    })
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.ivPhoto);
        } else {
            // Αν δεν υπάρχει καθόλου link, βάλε ένα default εικονίδιο
            holder.ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return players != null ? players.size() : 0;
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition;
        ImageView ivPhoto;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_player_name);
            tvPosition = itemView.findViewById(R.id.tv_player_position);
            ivPhoto = itemView.findViewById(R.id.iv_player_photo);
        }
    }
}