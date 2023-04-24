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

    public static void showSuccessAlert(Window owner, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.initOwner(owner);
        alert.show();
    }
}
