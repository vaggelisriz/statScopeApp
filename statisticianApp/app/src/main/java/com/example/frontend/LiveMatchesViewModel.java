package com.example.frontend;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class LiveMatchesViewModel extends ViewModel {

    private final MatchRepository repository = new MatchRepository();

    // Το UI παρατηρεί αυτά τα τρία:
    public final MutableLiveData<List<Match>> liveMatches = new MutableLiveData<>();
    public final MutableLiveData<Boolean>     isLoading   = new MutableLiveData<>(false);
    public final MutableLiveData<String>      error       = new MutableLiveData<>();

    public void refresh() {
        repository.fetchLiveMatches(liveMatches, isLoading, error);
    }


    public void updateScore(int matchId, int homeScore, int awayScore) {
        // Χρησιμοποιούμε ανώνυμα LiveData για το αποτέλεσμα —
        // η λίστα θα ανανεωθεί αυτόματα μέσω του επόμενου onResume().
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        repository.updateScore(matchId, homeScore, awayScore, success, error);
    }
}