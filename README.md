# Project Title
WordleFX (JavaFX Wordle Clone)

## Team Members
- Kareem Anabtawi (Cal Poly ID: 016297723)

## How to Run
1. Clone repository: `git clone <YOUR_REPO_URL>`
2. Open in IntelliJ or Eclipse
3. [If JavaFX] Configure VM options:  
   `--module-path "PATH/TO/javafx-sdk-XX/lib" --add-modules javafx.controls`
4. Run `wordle.Main`  
   *(Alternatively, use Gradle commands: `gradle run` to launch, `gradle test` to run tests.)*

## Features Implemented
- Core Gameplay: 5-letter Wordle-style guessing with 6 attempts and visual feedback.
- Dictionary Validation: Only accepts valid English 5-letter words from `assets/words/valid.txt`.
- Save & Load (Persistence): Allows saving and reloading game state as `.json` files.
- Statistics Dashboard: Displays played games, wins, streaks, and win percentage.
- [Extra Credit] Smart Hint System: Suggests next optimal guess using letter frequency and uniqueness.
- [Extra Credit] Hard Mode: Requires reuse of revealed letters and positions between guesses.

## Controls
- Type Letters: Enter letters A–Z using keyboard or on-screen buttons.
- Enter: Submit current word.
- Backspace: Delete last letter.
- Buttons:
    - `New`: Start new game
    - `Save`: Save progress
    - `Load`: Load previous game
    - `Hint`: Show smart suggestion
    - `Stats`: View statistics
    - `Hard`: Toggle hard mode
    - `Play Again`: Restart after win/loss

## Known Issues
- None currently known.  
  *If words fail validation, verify that `assets/words/valid.txt` and `answers.txt` exist and contain valid uppercase 5-letter words.*

## External Libraries
- Gson 2.10.1 (JSON parsing and serialization)
- JUnit 4.13 (unit testing)
