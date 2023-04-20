package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class SessionPanel implements IPanel {

    private static final TextArea sessionLog = new TextArea();

    public static void addMessageToLog(String message) {
        sessionLog.appendText(message + '\n');
    }

    @Override
    public VBox getGrid() {
        sessionLog.appendText("Start Session...\n");
        sessionLog.setEditable(false);
        sessionLog.setMinSize(1200, 600);
        sessionLog.setPadding(new Insets(20, 20, 20, 20));

        VBox box = new VBox(sessionLog);
        box.setStyle("-fx-padding: 20 20 20 20;");

        return box;
    }
}
