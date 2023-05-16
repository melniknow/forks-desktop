package com.melniknow.fd.utils;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class PanelUtils {
    public static void showErrorAlert(Window owner, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.initOwner(owner);
        alert.show();
    }
}
