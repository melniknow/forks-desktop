package com.melniknow.fd.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.melniknow.fd.ui.panels.impl.SessionPanel;
import com.melniknow.fd.utils.BetUtils;

public class Logger {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void writeToLogSession(String message) {
        SessionPanel.addMessageToLog(message);
    }
    public static void writePrettyMessageAboutFork(BetUtils.CompleteBetsFork completed) {
        writeToLogSession("Pretty message - " + completed);
    }

    public static <T> void writePrettyJson(T json) {
        writeToLogSession(gson.toJson(json));
    }
}
