package com.melniknow.fd.ui;

import com.google.gson.JsonParser;
import com.melniknow.fd.context.Context;
import com.melniknow.fd.core.ForksBot;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.oddscorp.Bookmakers;
import com.melniknow.fd.selenium.ScreensManager;
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
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {
    public static ExecutorService botPool = Executors.newSingleThreadExecutor();
    public static ExecutorService parsingPool = Executors.newCachedThreadPool();
    public static final ScreensManager screensManager = new ScreensManager();

    @FXML
    private TabPane tabPane;
    @FXML
    public Button run;

    public static Tab currency;
    public static Tab bookmakers;
    public static Tab session;
    public static Button runButton;

    public void initialize() {
        PolypokerCheck();

        var settingTab = tabConstructor("Настройки", new SettingPanel());
        var currencyTab = tabConstructor("Валюты", new CurrencyPanel());
        var bookmakersTab = tabConstructor("Букмекеры", new BookmakersPanel());
        var sessionTab = tabConstructor("Сессия", new SessionPanel());

        currencyTab.setDisable(true);
        bookmakersTab.setDisable(true);
        sessionTab.setDisable(true);

        currency = currencyTab;
        bookmakers = bookmakersTab;
        session = sessionTab;

        bookmakers.setOnSelectionChanged(event -> {
            if (Context.parserParams != null && !equalsBookmakersForPanel(Context.parserParams.bookmakers(), BookmakersPanel.tabPane.getTabs())) {
                BookmakersPanel.tabPane.getTabs().clear();
                BookmakersPanel.tabPane.getTabs().addAll(Context.parserParams.bookmakers().stream().map(n -> {
                    var tab = new Tab(n.nameInAPI.toUpperCase());
                    tab.setClosable(false);
                    tab.setContent(BookmakersPanel.getTabContent(n));

                    return tab;
                }).toList());
            }
        });

        run.setDisable(true);
        runButton = run;

        tabPane.getTabs().addAll(settingTab, currencyTab, bookmakersTab, sessionTab);
    }
    private boolean equalsBookmakersForPanel(List<Bookmakers> bookmakers, List<Tab> tabs) {
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
        botPool.submit(new ForksBot());
        Platform.runLater(() -> run.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #000;"));
        Platform.runLater(() -> run.setText("Стоп"));
        Logger.writeToLogSession("Сессия запущена");
    }

    private void stop() {
        boolean isInterrupted;

        try {
            botPool.shutdownNow();
            isInterrupted = botPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!isInterrupted) throw new RuntimeException();

        botPool = Executors.newSingleThreadExecutor();

        Platform.runLater(() -> run.setStyle("-fx-background-color: #00FF00; -fx-text-fill: #000;"));
        Platform.runLater(() -> run.setText("Старт"));
        Logger.writeToLogSession("Сессия остановлена");
    }

    private Tab tabConstructor(String label, IPanel panel) {
        var tab = new Tab(label);

        tab.setClosable(false);
        tab.setContent(panel.getGrid());

        return tab;
    }

    private void PolypokerCheck() {
        parsingPool.submit(() -> {
            var uri = "http://nepolypoker.ru/flag.json";
            var timeout = 2;

            var config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();

            try (var httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build()) {
                HttpGet request = new HttpGet(uri);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    if (response.getStatusLine().getStatusCode() != 200) {
                        throw new NullPointerException();
                    }
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        var status = JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject().get("flag").getAsBoolean();
                        if (!status) {
                            throw new NullPointerException();
                        }
                    }
                } catch (Exception e) {
                    throw new NullPointerException();
                }
            } catch (Exception e) {
                throw new NullPointerException();
            }
        });
    }
}
