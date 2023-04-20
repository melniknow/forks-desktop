package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.ui.panels.IPanel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

public class SessionPanel implements IPanel {

    private static final TextArea sessionLog = new TextArea();

    public static void addMessageToLog(String message) {
        sessionLog.appendText(message + '\n');
    }

    @Override
    public VBox getGrid() {
        sessionLog.setEditable(false);
        sessionLog.appendText("Start Session...\n");
        sessionLog.setMinSize(1200, 600);
        sessionLog.setPadding(new Insets(20, 20, 20, 20));

        VBox box = new VBox(sessionLog);
        box.setStyle("-fx-padding: 20 20 20 20;");

        return box;
    }
}
