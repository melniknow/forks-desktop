package com.melniknow.fd.ui;

import com.melniknow.fd.core.Core;
import com.melniknow.fd.ui.panels.*;
import com.melniknow.fd.ui.panels.impl.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {
    private ExecutorService pool = Executors.newSingleThreadExecutor();

    @FXML
    private TabPane tabPane;
    @FXML
    private Button run;

    public void initialize() {
        var settingTab = tabConstructor("Настройки бк", new SettingPanel());
        var bookmakersTab = tabConstructor("Букмекеры", new BookmakersPanel());
        var securityTab = tabConstructor("Безопасность", new SecurityPanel());
        var currencyTab = tabConstructor("Валюта", new CurrencyPanel());
        var betsTab = tabConstructor("Вилки и ставки", new BetsPanel());

        tabPane.getTabs().addAll(settingTab, bookmakersTab, securityTab, currencyTab, betsTab);
    }

    @FXML
    protected void onRunButtonClick() {
        if (run.getText().equals("Старт")) start();
        else stop();
    }

    private void start() {
        pool.submit(new Core());
        run.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #000;");
        run.setText("Стоп");
    }

    private void stop() {
        boolean isInterrupted;

        try {
            pool.shutdownNow();
            isInterrupted = pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!isInterrupted) throw new RuntimeException("Поток не прервался");

        pool = Executors.newSingleThreadExecutor();

        run.setStyle("-fx-background-color: #00FF00; -fx-text-fill: #000;");
        run.setText("Старт");
    }

    private Tab tabConstructor(String label, IPanel panel) {
        var tab = new Tab(label);

        tab.setClosable(false);
        tab.setContent(panel.getGrid());

        return tab;
    }
}
