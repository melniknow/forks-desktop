package com.melniknow.fd;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("app.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 700);

        stage.setTitle("Forks Desktop");
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("fork.png"))));

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        Context.botPool.shutdownNow();
        Context.parsingPool.shutdownNow();
        Context.screenManager.clear();

        for (File deleteTempFile : Context.deleteTempFiles)
            if (!deleteTempFile.delete()) Context.log.info("Не удалось удалить временный файл");
    }

    public static void main(String[] args) {
        launch();
    }
}