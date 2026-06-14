package com.example.frontend;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ChampionshipPagerAdapter extends FragmentStateAdapter {

    private final int championshipId; //

    public ChampionshipPagerAdapter(@NonNull FragmentActivity fragmentActivity, int championshipId) {
        super(fragmentActivity); //
        this.championshipId = championshipId; //
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return StandingsFragment.newInstance(championshipId);

            case 1:
                TeamsFragment teamsFragment = new TeamsFragment(); //
                Bundle args = new Bundle(); //
                args.putInt("CHAMPIONSHIP_ID", championshipId); //
                teamsFragment.setArguments(args); //
                return teamsFragment; //

            case 2:
                return MatchesFragment.newInstance(championshipId); //
            default:
                return StandingsFragment.newInstance(championshipId);
        }
    }

    @Override
    public int getItemCount() {
        return 3; //
    }
}