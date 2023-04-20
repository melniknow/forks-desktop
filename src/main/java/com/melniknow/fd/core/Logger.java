package com.melniknow.fd.core;

import com.melniknow.fd.ui.panels.impl.SessionPanel;

public class Logger {
    public static void writeToLogSession(String message) {
        SessionPanel.addMessageToLog(message);
    }
}
