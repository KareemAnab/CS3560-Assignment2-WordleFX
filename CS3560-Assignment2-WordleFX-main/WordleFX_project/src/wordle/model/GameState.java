package wordle.model;

import java.util.*;

// GameState Class - Stores the current progress and data of a Wordle game
public class GameState {
    // Constants for word length and number of turns
    public static final int WORD_LEN = 5;
    public static final int MAX_TURNS = 6;

    // The secret word the player is trying to guess
    private String secret;
    // Current turn number
    private int turn;
    // List of player guesses
    private final List<String> guesses = new ArrayList<>();
    // List of feedback arrays (color results for each guess)
    private final List<Feedback[]> feedbacks = new ArrayList<>();
    // Whether Hard Mode is enabled
    private boolean hardMode = false;
    // Current status of the game (In progress, Won, or Lost)
    private GameStatus status = GameStatus.IN_PROGRESS;

    // Constructor - initializes game with a secret word
    public GameState(String secret) {
        this.secret = secret.toUpperCase();
        this.turn = 0;
    }

    // Getter and Setter for secret word
    public String getSecret() { return secret; }
    public void setSecret(String s) { secret = s.toUpperCase(); }

    // Getter and Setter for turn count
    public int getTurn() { return turn; }
    public void setTurn(int t) { turn = t; }

    // Getter for all guesses made by the player
    public List<String> getGuesses() { return guesses; }

    // Getter for all feedback results for each guess
    public List<Feedback[]> getFeedbacks() { return feedbacks; }

    // Getter and Setter for Hard Mode toggle
    public boolean isHardMode() { return hardMode; }
    public void setHardMode(boolean v) { hardMode = v; }

    // Getter and Setter for game status (Won, Lost, or In Progress)
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus s) { status = s; }

    // Reset the game state for a new round
    public void reset(String newSecret) {
        guesses.clear();
        feedbacks.clear();
        turn = 0;
        status = GameStatus.IN_PROGRESS;
        secret = newSecret.toUpperCase();
    }
}
