package wordle.control;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import wordle.io.GameIO;
import wordle.model.*;
import wordle.view.GameView;

import java.io.File;
import java.util.Optional;

public class GameController {
    private final WordleModel model;
    private final GameView view;
    private final Stage stage;
    private final StringBuilder buffer = new StringBuilder();

    public GameController(WordleModel model, GameView view, Stage stage) {
        this.model = model;
        this.view = view;
        this.stage = stage;

        model.addListener(() -> {
            view.render(model.getState());
            maybeShowGameOverPrompt();
        });

        wireUI();
    }

    private void wireUI() {
        // Use an event filter so typing works even if a Button has focus
        view.getRoot().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (model.getState().getStatus() != GameStatus.IN_PROGRESS) return;

            if (e.getCode() == KeyCode.ENTER) { submit(); e.consume(); return; }
            if (e.getCode() == KeyCode.BACK_SPACE) { backspace(); e.consume(); return; }

            // robust letter detection
            String t = e.getText();
            if (t != null && t.length() == 1 && Character.isLetter(t.charAt(0))) {
                append(Character.toUpperCase(t.charAt(0)));
                e.consume();
            }
        });

        // On-screen keyboard
        view.getKeyboard().getKeys().forEach((ch,btn)-> btn.setOnAction(ev -> {
            append(ch);
            view.requestFocusForInput();
        }));
        view.getKeyboard().enterBtn.setOnAction(ev -> { submit(); view.requestFocusForInput(); });
        view.getKeyboard().backBtn.setOnAction(ev -> { backspace(); view.requestFocusForInput(); });

        // Top bar buttons
        view.newBtn.setOnAction(ev -> { onNew(); view.requestFocusForInput(); });
        view.playAgainBtn.setOnAction(ev -> { onNew(); view.requestFocusForInput(); });
        view.hardMode.setOnAction(ev -> { model.setHardMode(view.hardMode.isSelected()); view.requestFocusForInput(); });
        view.hintBtn.setOnAction(ev -> { view.setHint(model.hint()); view.requestFocusForInput(); });
        view.saveBtn.setOnAction(ev -> { onSave(); view.requestFocusForInput(); });
        view.loadBtn.setOnAction(ev -> { onLoad(); view.requestFocusForInput(); });
        view.statsBtn.setOnAction(ev -> { showStats(); view.requestFocusForInput(); });
    }

    private void onNew() {
        if (!model.getState().getGuesses().isEmpty() && model.getState().getStatus() == GameStatus.IN_PROGRESS) {
            var confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Start a new round and discard current progress?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText("New Game");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;
        }
        model.newGame();
        buffer.setLength(0);
        view.setCurrentBuffer("");
    }

    private void append(char ch){
        if(buffer.length() < GameState.WORD_LEN){
            buffer.append(ch);
            view.setCurrentBuffer(buffer.toString());
            view.render(model.getState());
        }
    }

    private void backspace(){
        if(buffer.length() > 0){
            buffer.deleteCharAt(buffer.length()-1);
            view.setCurrentBuffer(buffer.toString());
            view.render(model.getState());
        }
    }

    private void submit(){
        if (buffer.length() != GameState.WORD_LEN) return;
        String guess = buffer.toString();
        try {
            model.submitGuess(guess);
            buffer.setLength(0);
            view.setCurrentBuffer("");
            view.requestFocusForInput();
        } catch (IllegalArgumentException ex) {
            // Show why it failed, then reset input so the user can retype immediately
            alert("Invalid Guess", ex.getMessage());
            buffer.setLength(0);
            view.setCurrentBuffer("");
            view.render(model.getState());
            view.requestFocusForInput();
        }
    }


    private void onSave() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Wordle Game");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
            fc.setInitialFileName("wordle-save.json");
            File file = fc.showSaveDialog(stage);
            if (file == null) return;
            GameIO.save(model.getState(), model.getStats(), file.toPath());
            alert("Saved", "Game saved to:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            alert("Save Error", ex.getMessage());
        }
    }

    private void onLoad() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Load Wordle Game");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
            File file = fc.showOpenDialog(stage);
            if (file == null) return;
            GameIO.loadInto(model.getState(), model.getStats(), file.toPath());
            model.recomputeStatusAfterLoad();
            view.render(model.getState());
            alert("Loaded", "Game loaded from:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            alert("Load Error", ex.getMessage());
        }
    }

    private void showStats() {
        var s = model.getStats();
        StringBuilder sb = new StringBuilder();
        sb.append("Played: ").append(s.played).append("\n")
                .append("Wins: ").append(s.wins).append(" (").append(String.format("%.1f%%", s.winPct())).append(")\n")
                .append("Streak: ").append(s.currentStreak).append(" (best ").append(s.bestStreak).append(")\n")
                .append("Guess Distribution:\n");
        for (int i=0;i<s.dist.length;i++) sb.append(i+1).append(": ").append(s.dist[i]).append("\n");
        alert("Statistics", sb.toString());
    }

    private void maybeShowGameOverPrompt() {
        GameStatus st = model.getState().getStatus();
        if (st == GameStatus.IN_PROGRESS) return;

        String title = (st == GameStatus.WON) ? "You Won!" : "Round Over";
        String msg   = (st == GameStatus.WON)
                ? "Great job! Play another round?"
                : "The word was " + model.getState().getSecret() + ". Try again?";

        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(title);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) onNew();
    }

    private void alert(String title, String msg){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(title);
        a.setContentText(msg);
        // When the alert closes, put focus back on the root so key events work
        a.setOnHidden(ev -> view.requestFocusForInput());
        a.showAndWait();
    }

}
