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

import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.MonetaryConversions;
import java.util.Date;
import java.util.Objects;

/**
 *
 */
public class Main extends Application{
    /**
     *
     */
    static User user;

    /**
     *
     */
    static double xOffset = 0;

    /**
     *
     */
    static double yOffset = 0;

    /**
     *
     */
    static Parent root;

    /**
     *
     */
    static Stage stage;

    /**
     *
     */
    @Override
    public void start(Stage stage) throws Exception {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(new Image("file:src/main/resources/images/icon.png"));
        stage.setResizable(false);

        Main.stage = stage;

        dragStage();
        Main.stage.setScene(scene);
        Main.stage.show();
        centerStage();

        loadCurrencyConversion();
    }

    /**
     *
     */
    static void dragStage(){
        root.setOnMousePressed((javafx.scene.input.MouseEvent event) -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        root.setOnMouseDragged((javafx.scene.input.MouseEvent event) ->{
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }

    /**
     *
     */
    static void centerStage(){
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    /**
     *
     */
    public void loadCurrencyConversion(){
        new Thread(() -> {
            ConversionQuery conversionQuery = ConversionQueryBuilder.of().setTermCurrency("RON").
                    set(Date.class, new Date()).build();

            MonetaryConversions.getExchangeRateProvider().getCurrencyConversion(conversionQuery);
        }).start();
    }

    /**
     *
     */
    public static void main(String[] args) {
        launch(args);
    }
}
