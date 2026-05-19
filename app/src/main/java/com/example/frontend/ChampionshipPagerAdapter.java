package com.example.frontend;

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
                return new TeamsFragment();     // Ομάδες
            case 2:
                // Περνάμε το ID στο Fragment των αγώνων!
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