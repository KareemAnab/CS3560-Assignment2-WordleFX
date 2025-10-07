package wordle.model;

import com.google.gson.Gson;

// Stats Class - Tracks player statistics and handles save/load functionality
public class Stats {
    // Total games played, wins, and streaks
    public int played = 0, wins = 0, currentStreak = 0, bestStreak = 0;
    // Distribution of wins by number of guesses (1–6)
    public int[] dist = new int[6];

    // Record a win and update streaks and distribution
    public void recordWin(int n) {
        played++;
        wins++;
        currentStreak++;
        bestStreak = Math.max(bestStreak, currentStreak);
        if (n >= 1 && n <= 6) dist[n - 1]++;
    }

    // Record a loss and reset current streak
    public void recordLoss() {
        played++;
        currentStreak = 0;
    }

    // Calculate and return win percentage
    public double winPct() {
        return played == 0 ? 0 : (wins * 100.0) / played;
    }

    // Convert stats to JSON format for saving
    public String toJson() {
        return new Gson().toJson(this);
    }

    // Load stats from JSON format; return new Stats if invalid
    public static Stats fromJson(String json) {
        try {
            return new Gson().fromJson(json, Stats.class);
        } catch (Exception e) {
            return new Stats();
        }
    }
}
