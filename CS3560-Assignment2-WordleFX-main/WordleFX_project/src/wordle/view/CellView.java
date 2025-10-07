package wordle.view;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import wordle.model.Feedback;

public class CellView extends StackPane {
    private final Rectangle rect = new Rectangle(88, 88);   // slightly bigger cells
    private final Text letter = new Text("");
    private boolean active = false;

    public CellView() {
        rect.setArcWidth(12);
        rect.setArcHeight(12);
        rect.setFill(Color.web("#333"));
        rect.setStroke(Color.web("#555"));
        rect.setStrokeWidth(2.0);
        letter.setFill(Color.WHITE);
        letter.setFont(Font.font("Consolas", 30));
        setAlignment(Pos.CENTER);
        getChildren().addAll(rect, letter);
        setMinSize(88, 88);
        setMaxSize(88, 88);
    }

    public StackPane getRoot(){ return this; }
    public void setLetter(String s){ letter.setText(s); }

    public void setFeedback(Feedback fb){
        if(fb == null){
            rect.setFill(Color.web("#333"));
            rect.setStroke(active ? Color.web("#58a6ff") : Color.web("#555"));
            return;
        }
        switch (fb) {
            case GREEN -> { rect.setFill(Color.web("#238636")); rect.setStroke(Color.web("#1f6f2c")); }
            case YELLOW -> { rect.setFill(Color.web("#b3a11c")); rect.setStroke(Color.web("#8a7f14")); }
            case GRAY -> { rect.setFill(Color.web("#3a3a3c")); rect.setStroke(Color.web("#2a2a2c")); }
        }
    }

    public void setActive(boolean value) {
        this.active = value;
        rect.setStroke(active ? Color.web("#58a6ff") : Color.web("#555")); // blue focus ring
    }
}
