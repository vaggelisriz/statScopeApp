package com.example.frontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class FanActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spinnerMatches;

    private TextView tvChampionshipsHeader;
    private ListView lvChampionshipsOptions;

    private ChampionshipList chlist;
    private OkHttpClient okHttpClient;

    private final String ip = "10.140.7.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);

        okHttpClient = new OkHttpClient();

        // UI Initialization
        btnBack = findViewById(R.id.btn_back_dashboard);
        tvChampionshipsHeader = findViewById(R.id.tv_championships_header);
        lvChampionshipsOptions = findViewById(R.id.lv_championships_options);
        spinnerMatches = findViewById(R.id.spinner_matches);

        btnBack.setOnClickListener(v -> finish());

        // ΕΞΑΝΑΓΚΑΣΜΟΣ: Το κάνουμε σκούρο με το που ανοίγει η οθόνη για να είμαστε σίγουροι
        tvChampionshipsHeader.getBackground().mutate().setTint(Color.parseColor("#0a1128"));

        // Λογική Accordion με τη σωστή μέθοδο αλλαγής χρώματος (.setTint)
        tvChampionshipsHeader.setOnClickListener(v -> {
            if (lvChampionshipsOptions.getVisibility() == View.GONE) {
                // ΑΝΟΙΓΜΑ: Εμφανίζεται η λίστα και το background γίνεται ΠΡΑΣΙΝΟ
                lvChampionshipsOptions.setVisibility(View.VISIBLE);
                tvChampionshipsHeader.getBackground().mutate().setTint(Color.parseColor("#00E676"));
            } else {
                // ΚΛΕΙΣΙΜΟ: Κρύβεται η λίστα και το background επιστρέφει στο ΣΚΟΥΡΟ
                lvChampionshipsOptions.setVisibility(View.GONE);
                tvChampionshipsHeader.getBackground().mutate().setTint(Color.parseColor("#0a1128"));
            }
        });

        // Φόρτωση Πρωταθλημάτων
        try {
            chlist = new ChampionshipList(ip);

            List<String> championshipNames = new ArrayList<>(chlist.getChampionships());

            ArrayAdapter<String> championshipsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, championshipNames) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    TextView tv = (TextView) v.findViewById(android.R.id.text1);
                    if (tv != null) {
                        tv.setTextColor(Color.WHITE);
                        tv.setTextSize(16);
                    }
                    return v;
                }
            };

            lvChampionshipsOptions.setAdapter(championshipsAdapter);

            // Όταν ο χρήστης επιλέγει ένα πρωτάθλημα από το dropdown
            lvChampionshipsOptions.setOnItemClickListener((parent, view, position, id) -> {
                String selectedChampionship = championshipNames.get(position);

                tvChampionshipsHeader.setText(selectedChampionship);

                // ΚΛΕΙΣΙΜΟ: Κλείνει η λίστα και επαναφέρουμε το background στο ΣΚΟΥΡΟ
                lvChampionshipsOptions.setVisibility(View.GONE);
                tvChampionshipsHeader.getBackground().mutate().setTint(Color.parseColor("#0a1128"));

                Toast.makeText(FanActivity.this, "Επιλέχθηκε: " + selectedChampionship, Toast.LENGTH_SHORT).show();

                // ====================================================================
                // ΝΕΑ ΛΕΙΤΟΥΡΓΙΚΟΤΗΤΑ: ΜΕΤΑΦΟΡΑ ΣΤΗ ΝΕΑ ΣΕΛΙΔΑ
                // ====================================================================
                Intent intent = new Intent(FanActivity.this, ChampionshipActivity.class);

                // Περνάμε το Όνομα και το ID για να τα διαβάσει η επόμενη σελίδα
                intent.putExtra("CHAMPIONSHIP_NAME", selectedChampionship);
                intent.putExtra("CHAMPIONSHIP_ID", chlist.getChampionshipId(position));

                startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά τη φόρτωση των πρωταθλημάτων", Toast.LENGTH_SHORT).show();
        }
    }
}