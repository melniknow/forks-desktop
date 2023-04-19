package com.melniknow.fd;

import com.melniknow.fd.tg.TgBot;
import com.melniknow.fd.tg.TgApi;
import com.melniknow.fd.tg.TgMessage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Core extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        TgApi tgApi = new TgApi();
        TgBot tgApi2 = new TgBot();
        FXMLLoader fxmlLoader = new FXMLLoader(Core.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        tgApi.SendToAll(new TgMessage("Hello, Bitch!"));
    }

    public static void main(String[] args) {
        launch();
    }
}