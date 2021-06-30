package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Cik uz loksnes?");
        primaryStage.setScene(new Scene(root, 1060, 590, Color.ROYALBLUE));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("file:CikDaudz.bmp"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
