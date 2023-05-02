package com.melniknow.fd.core;

import com.melniknow.fd.ui.panels.impl.SessionPanel;
import com.melniknow.fd.utils.BetUtils;

public class Logger {
    public static void writeToLogSession(String message) {
        SessionPanel.addMessageToLog(message);
    }
    public static void writePrettyMessageAboutFork(BetUtils.CompleteBetsFork completed) {
        writeToLogSession("Поставлена вилка: %s - %s, доход: %s ₽".formatted(completed.calculatedFork().fork().betInfo1().BK_name(),
            completed.calculatedFork().fork().betInfo2().BK_name(),
            completed.income()));
    }
}
