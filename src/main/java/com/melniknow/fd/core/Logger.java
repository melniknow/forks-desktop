package com.melniknow.fd.core;

import com.melniknow.fd.ui.panels.impl.SessionPanel;
import com.melniknow.fd.utils.BetsUtils;

public class Logger {
    public static void writeToLogSession(String message) {
        SessionPanel.addMessageToLog(message);
    }
    public static void writePrettyMessageAboutFork(BetsUtils.CompleteBetsFork completed) {
        writeToLogSession("Pretty message - " + completed);
    }
}
