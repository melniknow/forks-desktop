package com.melniknow.fd.ui;

import com.melniknow.fd.Context;
import com.melniknow.fd.core.BotRunner;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.core.Security;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.ui.panels.IPanel;
import com.melniknow.fd.ui.panels.impl.BookmakersPanel;
import com.melniknow.fd.ui.panels.impl.CurrencyPanel;
import com.melniknow.fd.ui.panels.impl.SessionPanel;
import com.melniknow.fd.ui.panels.impl.SettingPanel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {
    @FXML
    private TabPane tabPane;
    @FXML
    public Button run;

    public static Tab currency;
    public static Tab bookmakers;
    public static Button runButton;

    public void initialize() {
        Security.PolypokerCheck();

        var settingTab = tabConstructor("Настройки", new SettingPanel());
        var currencyTab = tabConstructor("Валюты", new CurrencyPanel());
        var bookmakersTab = tabConstructor("Букмекеры", new BookmakersPanel());
        var sessionTab = tabConstructor("Сессия", new SessionPanel());

        currencyTab.setDisable(true);
        bookmakersTab.setDisable(true);

        currency = currencyTab;
        bookmakers = bookmakersTab;

        bookmakers.setOnSelectionChanged(event -> {
            if (Context.parserParams != null && !equalsBookmakersForPanel(Context.parserParams.bookmakers(), BookmakersPanel.tabPane.getTabs())) {
                BookmakersPanel.tabPane.getTabs().clear();
                BookmakersPanel.tabPane.getTabs().addAll(Context.parserParams.bookmakers().stream().map(bookmaker -> {
                    var tab = new Tab(bookmaker.nameInAPI.toUpperCase());
                    tab.setClosable(false);
                    tab.setContent(BookmakersPanel.getTabContent(bookmaker));

                    return tab;
                }).toList());
            }
        });

        run.setDisable(true);
        runButton = run;

        tabPane.getTabs().addAll(settingTab, currencyTab, bookmakersTab, sessionTab);
    }
    private boolean equalsBookmakersForPanel(List<Bookmaker> bookmakers, List<Tab> tabs) {
        var data = bookmakers.stream().map(n -> n.nameInAPI.toUpperCase()).toList();
        var data2 = new ArrayList<String>();

        for (Tab tab : tabs) {
            data2.add(tab.getText());
        }

        return new HashSet<>(data).containsAll(data2) && data2.containsAll(data);
    }

    @FXML
    protected void onRunButtonClick() {
        if (run.getText().equals("Старт")) start();
        else stop();
    }

    private void start() {
        Context.botPool.submit(new BotRunner());
        Platform.runLater(() -> run.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #000;"));
        Platform.runLater(() -> run.setText("Стоп"));
        Logger.writeToLogSession("Сессия запущена");
    }

    private void stop() {
        boolean isInterrupted;

        try {
            Context.botPool.shutdownNow();
            isInterrupted = Context.botPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!isInterrupted) throw new RuntimeException();

        Context.botPool = Executors.newSingleThreadExecutor();

        Platform.runLater(() -> run.setStyle("-fx-background-color: #00FF00; -fx-text-fill: #000;"));
        Platform.runLater(() -> run.setText("Старт"));
        Logger.writeToLogSession("Сессия остановлена");
    }

    private Tab tabConstructor(String label, IPanel panel) {
        var tab = new Tab(label);

        tab.setClosable(false);
        tab.setContent(panel.getNode());

        return tab;
    }
}
