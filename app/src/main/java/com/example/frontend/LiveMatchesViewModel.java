package com.example.frontend;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

/**
 * ViewModel για το LiveMatchesActivity.
 * Επιζεί των configuration changes (π.χ. rotation).
 * Το UI απλώς παρατηρεί τα LiveData fields.
 */
public class LiveMatchesViewModel extends ViewModel {

    private final MatchRepository repository = new MatchRepository();

    // Το UI παρατηρεί αυτά τα τρία:
    public final MutableLiveData<List<Match>> liveMatches = new MutableLiveData<>();
    public final MutableLiveData<Boolean>     isLoading   = new MutableLiveData<>(false);
    public final MutableLiveData<String>      error       = new MutableLiveData<>();

    /**
     * Καλείται από το onResume() του Activity.
     * Κάθε φορά που η οθόνη εμφανίζεται ξανά,
     * φέρνουμε φρέσκα δεδομένα από τον server.
     */
    public void refresh() {
        repository.fetchLiveMatches(liveMatches, isLoading, error);
    }

    /**
     * Καλείται μετά από κάθε αλλαγή σκορ (γκολ ή διαγραφή γκολ).
     * Γράφει το νέο σκορ στη βάση ώστε το επόμενο refresh να επιστρέψει
     * την ενημερωμένη τιμή.
     */
    public void updateScore(int matchId, int homeScore, int awayScore) {
        // Χρησιμοποιούμε ανώνυμα LiveData για το αποτέλεσμα —
        // η λίστα θα ανανεωθεί αυτόματα μέσω του επόμενου onResume().
        MutableLiveData<Boolean> success = new MutableLiveData<>();
        repository.updateScore(matchId, homeScore, awayScore, success, error);
    }
}