package com.melniknow.fd.utils;

import com.melniknow.fd.Context;
import javafx.scene.control.Alert;
import javafx.stage.Window;

public class PanelUtils {
    public static void showErrorAlert(Window owner, String text) {
        Context.log.warning("Всплыло окно с ошибкой в UI - " + text);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.initOwner(owner);
        alert.show();
    }
}
