package com.melniknow.fd;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Core extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Core.class.getResource("core.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 800);

        stage.setTitle("Forks Desktop");
        stage.getIcons().add(new Image(Objects.requireNonNull(Core.class.getResourceAsStream("fork.png"))));

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}