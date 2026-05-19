package com.example.frontend;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ChampionshipPagerAdapter extends FragmentStateAdapter {

    private final int championshipId;

    // Ζητάμε το ID του πρωταθλήματος κατά τη δημιουργία του Adapter
    public ChampionshipPagerAdapter(@NonNull FragmentActivity fragmentActivity, int championshipId) {
        super(fragmentActivity);
        this.championshipId = championshipId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StandingsFragment(); // Βαθμολογία
            case 1:
                // ============================================================
                // ΔΙΟΡΘΩΣΗ: Περνάμε το ID και στο TeamsFragment μέσω Bundle
                // ============================================================
                TeamsFragment teamsFragment = new TeamsFragment();
                Bundle args = new Bundle();
                args.putInt("CHAMPIONSHIP_ID", championshipId);
                teamsFragment.setArguments(args);
                return teamsFragment;

            case 2:
                // Περνάμε το ID στο Fragment των αγώνων (έτοιμο από τη συνάδελφο)
                return MatchesFragment.newInstance(championshipId);
            default:
                return new StandingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}