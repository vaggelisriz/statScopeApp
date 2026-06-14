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
import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends Fragment {

    private int championshipId;
    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private final List<Match> championshipMatches = new ArrayList<>();
    public MatchesFragment() {
        // Απαιτούμενος άδειος δημόσιος κατασκευαστής
    }

    // Το εργοστασιακό newInstance που καλείται από τον PagerAdapter
    public static MatchesFragment newInstance(int championshipId) {
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putInt("CHAMPIONSHIP_ID", championshipId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Διαβάζουμε το ID του πρωταθλήματος που μας στάλθηκε
            championshipId = getArguments().getInt("CHAMPIONSHIP_ID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Φουσκώνουμε το έτοιμο XML fragment_matches.xml
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_matches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MatchAdapter(getContext(), championshipMatches, match -> {

            // Όταν γίνει κλικ σε ένα match, ανοίγει η MatchDetailsActivity
            Intent intent = new Intent(getActivity(), FanMatchDetailsActivity.class);

            // Περνάμε τα δεδομένα στην επόμενη Activity
            intent.putExtra("MATCH_ID", match.getId());
            intent.putExtra("HOME_TEAM_ID", match.getHomeTeamId());
            intent.putExtra("AWAY_TEAM_ID", match.getAwayTeamId());
            intent.putExtra("HOME_TEAM", match.getHomeTeam());
            intent.putExtra("AWAY_TEAM", match.getAwayTeam());
            intent.putExtra("HOME_SCORE", match.getHomeScore());
            intent.putExtra("AWAY_SCORE", match.getAwayScore());
            intent.putExtra("HOME_LOGO", match.getHomeLogo());
            intent.putExtra("AWAY_LOGO", match.getAwayLogo());

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        fetchChampionshipMatches();
    }

    private void fetchChampionshipMatches() {
        try {
            String url = Config.BASE_URL+"/getMatches.php";

            OkHttpHandler handler = new OkHttpHandler();
            ArrayList<Match> allMatches = handler.populateMatches(url);

            championshipMatches.clear();

            for (Match m : allMatches) {
                if (m.getChampionshipId() == championshipId) {
                    championshipMatches.add(m);
                }
            }
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}