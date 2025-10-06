package wordle.model;

import wordle.util.ObservableModel;
import java.util.*;

// WordleModel Class - Handles game logic, validation, scoring, and state updates
public class WordleModel extends ObservableModel {
    private final Dictionary dict;   // Word dictionary
    private final Stats stats;       // Player statistics
    private final GameState state;   // Current game state

    // Constructor - initializes model with dictionary, stats, and random secret word
    public WordleModel(Dictionary dict, Stats stats) {
        this.dict = dict;
        this.stats = stats;
        this.state = new GameState(dict.randomAnswer());
    }

    // Getters for game state and statistics
    public GameState getState() { return state; }
    public Stats getStats() { return stats; }

    // Start a new game with a new random secret word
    public void newGame() {
        state.reset(dict.randomAnswer());
        notifyListeners();
    }

    // Validate that the guess is a proper 5-letter word from the dictionary
    public boolean isValid(String guess) {
        return guess != null && guess.length() == GameState.WORD_LEN && dict.isValidWord(guess);
    }

    // Enable or disable hard mode
    public void setHardMode(boolean v) {
        state.setHardMode(v);
        notifyListeners();
    }

    // Submit a player's guess and process the feedback
    public boolean submitGuess(String guess) {
        if (state.getStatus() != GameStatus.IN_PROGRESS) return false;
        guess = guess.toUpperCase();

        if (!isValid(guess))
            throw new IllegalArgumentException("Word must be a valid 5-letter word.");
        if (state.isHardMode() && !satisfiesHardMode(guess))
            throw new IllegalArgumentException("Hard mode: reuse revealed letters and positions.");

        // Evaluate the guess and record feedback
        Feedback[] fb = evaluate(guess, state.getSecret());
        state.getGuesses().add(guess);
        state.getFeedbacks().add(fb);

        // Update status based on guess results
        if (guess.equals(state.getSecret())) {
            state.setStatus(GameStatus.WON);
            stats.recordWin(state.getGuesses().size());
        } else if (state.getGuesses().size() >= GameState.MAX_TURNS) {
            state.setStatus(GameStatus.LOST);
            stats.recordLoss();
        }

        state.setTurn(state.getGuesses().size());
        notifyListeners();
        return true;
    }

    // Evaluate a guess against the secret word and return feedback colors
    public Feedback[] evaluate(String guess, String secret) {
        int L = GameState.WORD_LEN;
        Feedback[] res = new Feedback[L];
        Map<Character, Integer> left = new HashMap<>();

        // Mark greens first and count remaining letters
        for (int i = 0; i < L; i++) {
            char s = secret.charAt(i), g = guess.charAt(i);
            if (g == s) res[i] = Feedback.GREEN;
            else left.put(s, left.getOrDefault(s, 0) + 1);
        }

        // Assign yellow or gray feedback
        for (int i = 0; i < L; i++) {
            if (res[i] != Feedback.GREEN) {
                char g = guess.charAt(i);
                if (left.getOrDefault(g, 0) > 0) {
                    res[i] = Feedback.YELLOW;
                    left.put(g, left.get(g) - 1);
                } else res[i] = Feedback.GRAY;
            }
        }
        return res;
    }

    // Check if the guess follows hard mode rules (reuse revealed hints)
    private boolean satisfiesHardMode(String guess) {
        List<String> gs = state.getGuesses();
        List<Feedback[]> fb = state.getFeedbacks();
        if (gs.isEmpty()) return true;

        final int L = GameState.WORD_LEN;

        // Enforce green letters remain fixed
        for (int k = 0; k < gs.size(); k++) {
            String gk = gs.get(k);
            Feedback[] fk = fb.get(k);
            for (int i = 0; i < L; i++) {
                if (fk[i] == Feedback.GREEN && guess.charAt(i) != gk.charAt(i)) {
                    return false;
                }
            }
        }

        // Enforce use of all revealed letters
        Map<Character, Integer> needMax = new HashMap<>();
        for (int k = 0; k < gs.size(); k++) {
            String gk = gs.get(k);
            Feedback[] fk = fb.get(k);

            Map<Character, Integer> rowCount = new HashMap<>();
            for (int i = 0; i < L; i++) {
                if (fk[i] == Feedback.GREEN || fk[i] == Feedback.YELLOW) {
                    char c = gk.charAt(i);
                    rowCount.merge(c, 1, Integer::sum);
                }
            }
            for (var e : rowCount.entrySet()) {
                needMax.merge(e.getKey(), e.getValue(), Math::max);
            }
        }

        Map<Character, Integer> guessCount = new HashMap<>();
        for (char c : guess.toCharArray()) guessCount.merge(c, 1, Integer::sum);

        for (var e : needMax.entrySet()) {
            if (guessCount.getOrDefault(e.getKey(), 0) < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    // Recalculate game status after loading a saved game
    public void recomputeStatusAfterLoad() {
        if (!state.getGuesses().isEmpty()) {
            String last = state.getGuesses().get(state.getGuesses().size() - 1);
            if (last.equals(state.getSecret())) state.setStatus(GameStatus.WON);
            else if (state.getGuesses().size() >= GameState.MAX_TURNS) state.setStatus(GameStatus.LOST);
            else state.setStatus(GameStatus.IN_PROGRESS);
        } else {
            state.setStatus(GameStatus.IN_PROGRESS);
        }
        state.setTurn(state.getGuesses().size());
        notifyListeners();
    }

    // Generate a hint using remaining possible candidates
    public String hint() {
        var cand = HintEngine.filterCandidates(
                dict.allCandidates(), state.getGuesses(), state.getFeedbacks()
        );
        return HintEngine.bestHint(cand);
    }
}
