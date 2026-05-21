package com.example.frontend;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MatchScoreManager {

    private static final String TAG = "MATCH_SCORE";

    // STATIC map για να μη χάνεται
    private static final Map<Integer, String> matchScores =
            new HashMap<>();

    private static MatchScoreManager instance;

    private MatchScoreManager() {
    }

    public static synchronized MatchScoreManager getInstance() {

        if (instance == null) {
            instance = new MatchScoreManager();
        }

        return instance;
    }

    // SAVE SCORE
    public void updateScore(int matchId, int home, int away) {

        String score = home + " - " + away;

        matchScores.put(matchId, score);

        Log.d(TAG,
                "UPDATED -> matchId="
                        + matchId
                        + " score="
                        + score);
    }

    // GET SCORE
    public String getScore(int matchId) {

        String score = matchScores.get(matchId);

        Log.d(TAG,
                "GET -> matchId="
                        + matchId
                        + " score="
                        + score);

        if (score == null) {
            return null;
        }

        return score;
    }
}