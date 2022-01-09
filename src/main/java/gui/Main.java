package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import user.User;

import java.util.Objects;

public class Main extends Application{
    static User user;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image("file:src/main/resources/images/icon.png"));
        stage.setResizable(false);

        dragStage(root, stage);

        stage.setScene(scene);
        stage.show();
    }

    private void dragStage(Parent root, Stage stage){
        root.setOnMousePressed((javafx.scene.input.MouseEvent event) -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        root.setOnMouseDragged((javafx.scene.input.MouseEvent event) ->{
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }

    public static void centerStage(Stage stage){
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
