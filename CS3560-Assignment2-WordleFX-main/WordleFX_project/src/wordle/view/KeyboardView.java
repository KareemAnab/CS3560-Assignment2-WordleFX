package wordle.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import wordle.model.*;

import java.util.HashMap;
import java.util.Map;

public class KeyboardView {
    private final VBox root = new VBox(6);
    private final Map<Character, Button> keys = new HashMap<>();
    public final Button enterBtn = new Button("ENTER");
    public final Button backBtn = new Button("⌫");

    public KeyboardView() {
        root.setPadding(new Insets(12));
        root.setAlignment(Pos.CENTER);

        String[] rows = {"QWERTYUIOP","ASDFGHJKL","ZXCVBNM"};
        for (int i=0;i<rows.length;i++){
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER);
            for (char ch: rows[i].toCharArray()){
                Button b = new Button(String.valueOf(ch));
                b.setPrefWidth(44); b.setPrefHeight(44);
                b.setFocusTraversable(false); // <-- prevents focus stealing
                keys.put(ch, b);
                row.getChildren().add(b);
            }
            if(i==2){
                enterBtn.setPrefWidth(72); enterBtn.setPrefHeight(44); enterBtn.setFocusTraversable(false);
                backBtn.setPrefWidth(52); backBtn.setPrefHeight(44); backBtn.setFocusTraversable(false);
                row.getChildren().add(0, enterBtn);
                row.getChildren().add(backBtn);
            }
            root.getChildren().add(row);
        }
    }

    public VBox getRoot(){ return root; }
    public Map<Character,Button> getKeys(){ return keys; }

    public void render(GameState state){
        Map<Character,Feedback> best = new HashMap<>();
        for (int r=0;r<state.getGuesses().size();r++){
            String g = state.getGuesses().get(r);
            Feedback[] fb = state.getFeedbacks().get(r);
            for (int i=0;i<g.length();i++){
                char c = g.charAt(i);
                Feedback f = fb[i], prev = best.get(c);
                if(prev==Feedback.GREEN || f==prev) continue;
                if(prev==null || (prev==Feedback.GRAY && f!=Feedback.GRAY) || (prev==Feedback.YELLOW && f==Feedback.GREEN)) best.put(c,f);
            }
        }
        for (var e: keys.entrySet()){
            Feedback f = best.get(e.getKey());
            String style = "-fx-background-color:#444; -fx-text-fill:white;";
            if(f==Feedback.GREEN) style="-fx-background-color:#238636; -fx-text-fill:white;";
            else if(f==Feedback.YELLOW) style="-fx-background-color:#b3a11c; -fx-text-fill:white;";
            else if(f==Feedback.GRAY) style="-fx-background-color:#3a3a3c; -fx-text-fill:white;";
            e.getValue().setStyle(style);
        }
    }
}
