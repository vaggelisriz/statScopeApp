package com.example.frontend;

import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository: το μοναδικό σημείο επικοινωνίας με το API.
 * Το ViewModel το κρατά και το UI δεν ξέρει ότι υπάρχει.
 */
public class MatchRepository {

    private final ApiService apiService;

    public MatchRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class); //
    }

    // ─── Φέρνει μόνο τα live ματς ───────────────────────────────────────────
    public void fetchLiveMatches(
            MutableLiveData<List<Match>> result,
            MutableLiveData<Boolean> isLoading,
            MutableLiveData<String> error) {

        isLoading.setValue(true); //

        apiService.getAllMatches().enqueue(new Callback<List<Match>>() { //
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                isLoading.postValue(false); //
                if (response.isSuccessful() && response.body() != null) { //
                    List<Match> live = new ArrayList<>(); //
                    for (Match m : response.body()) { //
                        if ("live".equalsIgnoreCase(m.getStatus())) { //
                            live.add(m); //
                        }
                    }
                    result.postValue(live); //
                } else {
                    error.postValue("Server error: " + response.code()); //
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                isLoading.postValue(false); //
                error.postValue("Network error: " + t.getMessage()); //
            }
        });
    }

    // ─── Ενημερώνει το σκορ στη βάση ────────────────────────────────────────
    public void updateScore(
            int matchId,
            int homeScore,
            int awayScore,
            MutableLiveData<Boolean> success,
            MutableLiveData<String> error) {

        apiService.updateScore(matchId, homeScore, awayScore) //
                .enqueue(new Callback<StatusResponse>() { //
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null //
                                && response.body().isSuccess()) { //
                            success.postValue(true); //
                        } else {
                            error.postValue("Score update failed: " + response.code()); //
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        error.postValue("Network error: " + t.getMessage()); //
                    }
                });
    }

    // ─── 🆕 Φέρνει τη Βαθμολογία του Πρωταθλήματος ─────────────────────────────
    public void fetchChampionshipStandings(
            int championshipId,
            MutableLiveData<List<TeamStanding>> result,
            MutableLiveData<Boolean> isLoading,
            MutableLiveData<String> error) {

        isLoading.setValue(true);

        apiService.getChampionshipStandings(championshipId).enqueue(new Callback<List<TeamStanding>>() {
            @Override
            public void onResponse(Call<List<TeamStanding>> call, Response<List<TeamStanding>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Server error fetching standings: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TeamStanding>> call, Throwable t) {
                isLoading.postValue(false);
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }
}