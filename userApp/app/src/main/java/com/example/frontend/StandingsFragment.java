package com.example.frontend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;

public class StandingsFragment extends Fragment {

    private RecyclerView rvStandings;
    private ProgressBar progressBar;
    private OkHttpClient okHttpClient;
    private StandingsAdapter standingsAdapter;
    private final List<TeamStanding> standingsList = new ArrayList<>();
    private int championshipId = -1;

    public StandingsFragment() {
        // Υποχρεωτικός άδειος constructor
    }

    // Static μέθοδος (Factory) για τη σωστή δημιουργία του Fragment με πάσαρισμα του ID
    public static StandingsFragment newInstance(int championshipId) {
        StandingsFragment fragment = new StandingsFragment();
        Bundle args = new Bundle();
        args.putInt("championship_id", championshipId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpClient = new OkHttpClient();
        if (getArguments() != null) {
            championshipId = getArguments().getInt("championship_id", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Φουσκώνουμε το διορθωμένο layout με το HorizontalScrollView
        View view = inflater.inflate(R.layout.fragment_standings, container, false);

        rvStandings = view.findViewById(R.id.rv_frag_standings);
        progressBar = view.findViewById(R.id.pb_frag_standings_loading);

        // Setup του RecyclerView με τον StandingsAdapter που φτιάξαμε
        rvStandings.setLayoutManager(new LinearLayoutManager(getContext()));
        standingsAdapter = new StandingsAdapter(standingsList);
        rvStandings.setAdapter(standingsAdapter);

        // Αν έχουμε έγκυρο ID, τραβάμε αυτόματα τη βαθμολογία από το backend
        if (championshipId != -1) {
            fetchStandingsFromBackend(championshipId);
        } else {
            Toast.makeText(getContext(), "Invalid Championship ID", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    // ⚡ ΚΛΗΣΗ OKHTTP ΓΙΑ ΤΗ ΛΗΨΗ ΤΗΣ ΒΑΘΜΟΛΟΓΙΑΣ ΑΠΟ ΤΟ BACKEND
    private void fetchStandingsFromBackend(int id) {
        if (getActivity() == null) return;

        // Εμφάνιση του loader στην αρχή της σύνδεσης
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                // Σύνδεση με το getStandings.php περνώντας το id του πρωταθλήματος
                String url = Config.BASE_URL + "/getStandings.php?championship_id=" + id;
                Request request = new Request.Builder().url(url).build();
                Response response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    // Καθαρίζουμε την παλιά λίστα πριν βάλουμε τα νέα δεδομένα
                    standingsList.clear();

                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        TeamStanding standing = gson.fromJson(jsonArray.getJSONObject(i).toString(), TeamStanding.class);
                        standingsList.add(standing);
                    }

                    // Επιστροφή στο UI Thread για την ανανέωση του πίνακα
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (standingsList.isEmpty()) {
                                Toast.makeText(getContext(), "No completed matches for this championship yet.", Toast.LENGTH_LONG).show();
                            }
                            standingsAdapter.notifyDataSetChanged();
                        });
                    }
                } else {
                    hideLoaderWithToast("Server error fetching standings.");
                }
            } catch (Exception e) {
                Log.e("StandingsFragment", "Network error: " + e.getMessage());
                hideLoaderWithToast("Network error. Please check your XAMPP connection.");
            }
        }).start();
    }

    private void hideLoaderWithToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            });
        }
    }
}