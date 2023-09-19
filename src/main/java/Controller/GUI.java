package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\iconSudoku.png");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Sudoku.fxml"));
        Parent root = loader.load();

        SudokuController gameController = loader.getController();
        Scene gameScene = new Scene(root);
        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, gameController::keyPressed);
        gameController.init();

        stage.setTitle("Sudoku");
        stage.getIcons().add(icon);
        stage.setScene(gameScene);
        stage.setResizable(false);
        stage.requestFocus();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}