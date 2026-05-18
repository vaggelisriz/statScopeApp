package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayerDetailsActivity extends AppCompatActivity {

    private TextView tvPlayerAge; // Το κάνουμε καθολικό για να το βλέπει η fetch μέθοδος

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_details);

        ImageButton btnBack = findViewById(R.id.btn_back_player);
        ImageView ivPlayerPhoto = findViewById(R.id.iv_details_player_photo);
        TextView tvPlayerName = findViewById(R.id.tv_details_player_name);
        TextView tvPlayerPosition = findViewById(R.id.tv_details_player_position);
        TextView tvPlayerNumber = findViewById(R.id.tv_details_player_number);
        TextView tvPlayerTeam = findViewById(R.id.tv_details_player_team);

        // Αρχικοποίηση του TextView της ηλικίας
        tvPlayerAge = findViewById(R.id.tv_details_player_age);

        if (getIntent() != null) {
            int playerId = getIntent().getIntExtra("PLAYER_ID", -1);
            String name = getIntent().getStringExtra("PLAYER_NAME");
            String position = getIntent().getStringExtra("PLAYER_POSITION");
            String photo = getIntent().getStringExtra("PLAYER_PHOTO");
            String number = getIntent().getStringExtra("PLAYER_NUMBER");
            String teamName = getIntent().getStringExtra("PLAYER_TEAM_NAME");

            // Εμφάνιση των στοιχείων που ήδη έχουμε από τον Adapter
            tvPlayerName.setText(name);
            tvPlayerPosition.setText(position != null ? position : "N/A");
            tvPlayerNumber.setText(number != null && !number.isEmpty() ? "#" + number : "");
            tvPlayerTeam.setText(teamName != null ? teamName : "Unknown Team");

            Glide.with(this)
                    .load(photo)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .circleCrop()
                    .into(ivPlayerPhoto);

            // ΑΝ ΤΟ ID ΕΙΝΑΙ ΕΓΚΥΡΟ, ΤΡΑΒΑΜΕ ΤΗΝ ΗΛΙΚΙΑ LIVE ΑΠΟ ΤΟ BACKEND
            if (playerId != -1) {
                fetchPlayerAgeFromServer(playerId);
            }
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchPlayerAgeFromServer(int playerId) {
        new Thread(() -> {
            try {
                // Χρησιμοποιούμε ένα endpoint που επιστρέφει τα στοιχεία ενός συγκεκριμένου παίκτη βάσει ID
                String url = "http://10.140.7.36/statScopeApp/backend/api/getPlayerDetails.php?player_id=" + playerId;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Παίρνουμε την ηλικία από το JSON
                    int age = jsonObject.getInt("age");

                    // Ενημερώνουμε το UI
                    runOnUiThread(() -> {
                        if (age > 0) {
                            tvPlayerAge.setText(age + " years");
                        } else {
                            tvPlayerAge.setText("N/A");
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("PlayerDetails", "Error fetching age: " + e.getMessage());
                runOnUiThread(() -> tvPlayerAge.setText("N/A"));
            }
        }).start();
    }
}