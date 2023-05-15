package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ForksPanel implements IPanel {
    private final static TextArea forksLog = new TextArea();

    public static void addMessageToForkLog(String message) {
        Platform.runLater(() -> forksLog.appendText(message + "\n"));
    }

    @Override
    public ScrollPane getNode() {
        forksLog.clear();
        forksLog.setEditable(false);
        forksLog.setMinSize(1200, 600);
        forksLog.setPadding(new Insets(20, 20, 20, 20));

        var box = new VBox(forksLog);
        box.setStyle("-fx-padding: 20 20 20 20;");

        return new ScrollPane(box);
    }
}
