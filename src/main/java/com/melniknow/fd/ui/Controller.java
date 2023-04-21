package com.melniknow.fd.ui;

import com.google.gson.JsonParser;
import com.melniknow.fd.core.ForksBot;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.ui.panels.*;
import com.melniknow.fd.ui.panels.impl.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {
    public static ExecutorService pool = Executors.newSingleThreadExecutor();

    @FXML
    private TabPane tabPane;
    @FXML
    private Button run;

    public void initialize() {
        PolypokerCheck();

        var settingTab = tabConstructor("Настройки", new SettingPanel());
        var bookmakersTab = tabConstructor("Букмекеры", new BookmakersPanel());
        var sessionTab = tabConstructor("Сессия", new SessionPanel());

        tabPane.getTabs().addAll(settingTab, bookmakersTab, sessionTab);
    }

    @FXML
    protected void onRunButtonClick() {
        if (run.getText().equals("Старт")) start();
        else stop();
    }

    private void start() {
        pool.submit(new ForksBot());
        run.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #000;");
        run.setText("Стоп");
        Logger.writeToLogSession("Сессия запущена");
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
        Logger.writeToLogSession("Сессия остановлена");
    }

    private Tab tabConstructor(String label, IPanel panel) {
        var tab = new Tab(label);

        tab.setClosable(false);
        tab.setContent(panel.getGrid());

        return tab;
    }

    private void PolypokerCheck() {
        var uri = "http://nepolypoker.ru/flag.json";

        try (var httpClient = HttpClients.createDefault()) {
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
    }
}
