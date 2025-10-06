package wordle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wordle.control.GameController;
import wordle.model.Dictionary;
import wordle.model.Stats;
import wordle.model.WordleModel;
import wordle.view.GameView;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Dictionary dict = new Dictionary(false);
        Stats stats = new Stats();
        WordleModel model = new WordleModel(dict, stats);
        GameView view = new GameView();

        new GameController(model, view, stage);

        Scene scene = new Scene(view.getRoot(), 650, 1200);
        stage.setTitle("WordleFX");
        stage.setScene(scene);
        stage.setMinWidth(650);
        stage.setMinHeight(1200);
        stage.setResizable(false);
        stage.show();

        model.newGame();
        view.requestFocusForInput();
    }

    public static void main(String[] args) { launch(args); }
}
