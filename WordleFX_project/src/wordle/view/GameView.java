package wordle.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import wordle.model.*;

public class GameView {
    private final BorderPane root = new BorderPane();

    private final GridPane grid = new GridPane();
    private final Label status = new Label("");
    private final KeyboardView keyboard = new KeyboardView();

    public final Button newBtn = new Button("New");
    public final Button saveBtn = new Button("Save");
    public final Button loadBtn = new Button("Load");
    public final Button hintBtn = new Button("Hint");
    public final Button statsBtn = new Button("Stats");
    public final CheckBox hardMode = new CheckBox("Hard");
    public final Button playAgainBtn = new Button("Play Again");

    public final Label hintLabel = new Label("");

    private String typedBuffer = "";

    public GameView() {
        root.setStyle("-fx-background-color:#0f0f0f; -fx-text-fill:white;");
        root.setFocusTraversable(true);
        root.setOnMouseClicked(e -> requestFocusForInput());

        // === TOP BAR ===
        status.setStyle("-fx-text-fill:#fff; -fx-font-size:18; -fx-font-weight:bold;");
        HBox left = new HBox(10, newBtn, saveBtn, loadBtn);
        HBox right = new HBox(10, hardMode, hintBtn, statsBtn, playAgainBtn);
        left.setAlignment(Pos.CENTER_LEFT);
        right.setAlignment(Pos.CENTER_RIGHT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(10, left, spacer, right);
        bar.setPadding(new Insets(10));
        bar.setStyle("-fx-background-color:#1e1e1e;");
        root.setTop(bar);

        // === CENTER (status + grid + hint) ===
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        VBox center = new VBox(16, status, grid, hintLabel);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(20));
        VBox.setVgrow(grid, Priority.NEVER);
        root.setCenter(center);

        // === BOTTOM (keyboard) ===
        root.setBottom(keyboard.getRoot());

        hintLabel.setStyle("-fx-text-fill:#cfcfcf;");
        playAgainBtn.setVisible(false);
        playAgainBtn.setManaged(false);

        buildGridCells();
    }

    private void buildGridCells() {
        grid.getChildren().clear();
        for (int r = 0; r < GameState.MAX_TURNS; r++)
            for (int c = 0; c < GameState.WORD_LEN; c++)
                grid.add(new CellView().getRoot(), c, r);
    }

    public void render(GameState state) {
        // populate past rows
        for (int r = 0; r < GameState.MAX_TURNS; r++) {
            for (int c = 0; c < GameState.WORD_LEN; c++) {
                CellView cell = (CellView) getCell(r, c);
                cell.setActive(false);
                if (r < state.getGuesses().size()) {
                    String g = state.getGuesses().get(r);
                    Feedback fb = state.getFeedbacks().get(r)[c];
                    cell.setLetter(String.valueOf(g.charAt(c)));
                    cell.setFeedback(fb);
                } else {
                    cell.setLetter("");
                    cell.setFeedback(null);
                }
            }
        }

        // live typing row
        int r = state.getTurn();
        if (state.getStatus() == GameStatus.IN_PROGRESS && r < GameState.MAX_TURNS) {
            for (int i = 0; i < GameState.WORD_LEN; i++) {
                CellView cell = (CellView) getCell(r, i);
                cell.setActive(true); // <-- highlight active row
                String ch = i < typedBuffer.length() ? String.valueOf(typedBuffer.charAt(i)) : "";
                cell.setLetter(ch);
                if (ch.isEmpty()) cell.setFeedback(null);
            }
        }

        // status banner color
        String base = "Turn " + (state.getTurn() + 1) + " / " + GameState.MAX_TURNS + (state.isHardMode() ? " (Hard)" : "");
        switch (state.getStatus()) {
            case IN_PROGRESS -> {
                status.setText(base);
                status.setStyle("-fx-text-fill:#ffffff; -fx-font-size:18; -fx-font-weight:bold;");
            }
            case WON -> {
                status.setText("✔ You won in " + state.getGuesses().size() + "!");
                status.setStyle("-fx-text-fill:#3fb950; -fx-font-size:18; -fx-font-weight:bold;");
            }
            case LOST -> {
                status.setText("✖ You lost. Word was " + state.getSecret());
                status.setStyle("-fx-text-fill:#f85149; -fx-font-size:18; -fx-font-weight:bold;");
            }
        }

        hardMode.setSelected(state.isHardMode());
        keyboard.render(state);

        boolean over = state.getStatus() != GameStatus.IN_PROGRESS;
        playAgainBtn.setVisible(over);
        playAgainBtn.setManaged(over);
        keyboard.getRoot().setDisable(over);
    }

    private Node getCell(int row, int col) {
        for (Node n : grid.getChildren())
            if (GridPane.getRowIndex(n) == row && GridPane.getColumnIndex(n) == col)
                return n;
        return null;
    }

    public BorderPane getRoot(){ return root; }
    public KeyboardView getKeyboard(){ return keyboard; }
    public void requestFocusForInput(){ root.requestFocus(); }
    public void setHint(String s){ hintLabel.setText("Hint: " + s); }
    public void setCurrentBuffer(String s){ typedBuffer = s; }
}
