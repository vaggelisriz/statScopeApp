package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeamsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TeamAdapter adapter;
    private final List<Team> teamList = new ArrayList<>();

    private int championshipId = -1;
    private boolean isTeamClicked = false;

    public TeamsFragment() {
        // Απαιτούμενος άδειος κατασκευαστής
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Διάβασμα του championship_id αν έχει περαστεί ως όρισμα
        if (getArguments() != null) {
            championshipId = getArguments().getInt("CHAMPIONSHIP_ID", -1);
        }

        recyclerView = view.findViewById(R.id.rv_teams);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TeamAdapter(teamList, team -> {
            if (isTeamClicked) return;
            isTeamClicked = true;

            Intent intent = new Intent(getActivity(), TeamRosterActivity.class);
            intent.putExtra("TEAM_ID", team.getId());
            intent.putExtra("TEAM_NAME", team.getName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Εκτελείται πάντα. Αν το championshipId είναι -1, η PHP θα επιστρέψει όλες τις ομάδες
        fetchTeamsFromServer();
    }

    @Override
    public void onResume() {
        super.onResume();
        isTeamClicked = false;
    }

    private void fetchTeamsFromServer() {
        new Thread(() -> {
            try {
                String url = Config.BASE_URL+"/getTeams.php?championship_id=" + championshipId;

                // ΔΙΚΟ ΜΑΣ ΧΑΡΑΚΤΗΡΙΣΤΙΚΟ LOG
                System.out.println("=== ΜΥΣΤΙΚΟ LOG === ΧΤΥΠΑΩ ΤΟ URL: " + url);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    // ΔΙΚΟ ΜΑΣ ΧΑΡΑΚΤΗΡΙΣΤΙΚΟ LOG
                    System.out.println("=== ΜΥΣΤΙΚΟ LOG === ΑΠΑΝΤΗΣΗ ΑΠΟ SERVER: " + jsonResponse);

                    JSONArray jsonArray = new JSONArray(jsonResponse);
                    List<Team> tempTeams = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        tempTeams.add(new Team(
                                obj.getInt("id"),
                                obj.getString("name"),
                                obj.getString("city"),
                                obj.getString("logo")
                        ));
                    }

                    if (recyclerView != null) {
                        recyclerView.post(() -> {
                            teamList.clear();
                            teamList.addAll(tempTeams);
                            adapter.notifyDataSetChanged();
                        });
                    }
                }
            } catch (Exception e) {
                System.out.println("=== ΜΥΣΤΙΚΟ LOG === ΣΦΑΛΜΑ: " + e.getMessage());
            }
        }).start();
    }
}