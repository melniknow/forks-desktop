package com.melniknow.fd.UI;

import com.melniknow.fd.tg.TgApi;
import com.melniknow.fd.tg.TgMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private TgApi tgApi = new TgApi();

    @FXML
    protected void onHelloButtonClick() {
        tgApi.SendToAll(new TgMessage("Hello, bitch!"));
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}