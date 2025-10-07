# Project Report

## Design Decisions

### Architecture
- **MVC Separation:**
    - **Model** (`wordle.model`): game rules, state, validation, feedback, hint logic, statistics, dictionary, persistence adapter use.
    - **View** (`wordle.view`): JavaFX UI that renders board, keyboard, feedback colors, and HUD; no game logic.
    - **Controller** (`wordle.control.GameController`): routes input (keys/buttons) to the model, manages focus, and small user alerts.
- **Observer Pattern:**  
  `wordle.util.ObservableModel` + listener in `GameView`. The model notifies the view to re-render after any state change.
- **Why JavaFX over Swing:**  
  Modern API, better layout/controls, cleaner CSS-like styling, simpler scene graph for animations and focus handling.

### Data Structures
- **GameState:**
    - `String secret`, `int turn`, `List<String> guesses`, `List<Feedback[]> feedbacks`, `boolean hardMode`, `GameStatus status`.
    - Chosen for clarity and direct mapping to the board.
- **Feedback Calculation:**
    - Temporary `Map<Character,Integer>` counts remaining unmatched letters to handle duplicates.
- **Dictionary:**
    - `Set<String> valid` (fast membership) and `List<String> answers` (random selection).
- **Stats:**
    - Scalar counters + `int[6] dist` for guess distribution.
- **Observable:**
    - `List<ModelListener>` for lightweight publish/subscribe.

### Algorithms
- **Feedback (evaluate):**  
  Two-pass algorithm. Pass 1 marks **GREEN** and counts remaining secret letters; pass 2 assigns **YELLOW/GRAY** (handles duplicates correctly).  
  **Complexity:** `O(L)` per guess where `L=5`.
- **Hard Mode Check:**  
  Ensures fixed greens and minimum counts of revealed letters across previous guesses using maps.  
  **Complexity:** `O(G * L)` for `G` prior guesses.
- **Hint Engine:**
    1) Filters candidates against prior feedback; 
    1) 2) Scores words by positional letter frequency + uniqueness bonus.  
       **Complexity:** filtering `O(C * L)`, scoring `O(C * L)` where `C` is remaining candidates.
- **Persistence:**  
  Gson serialization of `GameState`+`Stats` to JSON; constant time relative to object size.

## Challenges Faced

1. **Duplicate Letters in Feedback**  
   **Solution:** Two-pass evaluation (greens first, then yellows using remaining counts) to avoid over-marking yellows.

2. **Keyboard Focus & Input Getting “Stuck” After Clicking Buttons**  
   **Solution:** Attached a key event filter to the root node and explicitly refocus the board after each button action.

3. **Dictionary Completeness vs. Fairness**  
   **Solution:** Split into `valid.txt` (large list for guesses) and `answers.txt` (curated solutions). Allowed easy expansion later.

## What We Learned
- Encapsulation + small methods improve testability and readability.
- The observer pattern keeps UI in sync without manual repaint logic.
- Writing tests early for model logic catches edge cases (duplicates, hard mode) before UI work.

## If We Had More Time
- **Features:** Colorblind mode, animated tile flips, richer stats (histograms), daily seed mode.
- **Refactoring:** Extract a `Rules` service for easier unit tests and alternate game modes.
- **Performance:** Cache filtered candidate sets between guesses for faster hints with very large dictionaries.