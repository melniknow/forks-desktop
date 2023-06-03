package com.melniknow.fd.ui;

import com.melniknow.fd.Context;
import com.melniknow.fd.core.BotRunner;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.core.Security;
import com.melniknow.fd.ui.panels.impl.ProfileTab;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.melniknow.fd.ui.panels.impl.ProfileTab.tabConstructor;

public class Controller {
    @FXML
    private TabPane tabPane;
    @FXML
    public Button run;

    public static TabPane pane;
    public static Tab setting;
    public static Tab currency;
    public static Tab bookmakers;
    public static Tab bundleTab;
    public static Button runButton;

    public void initialize() {
        Security.PolypokerCheck();
        run.setDisable(true);
        runButton = run;

        var profileTab = tabConstructor("Профиль", new ProfileTab());

        pane = tabPane;
        tabPane.getTabs().addAll(profileTab);
    }

    @FXML
    protected void onRunButtonClick() {
        if (run.getText().equals("Старт")) start();
        else stop();
    }

    private void start() {
        Context.log.info("Запустили бота");
        Context.log.info("Настройки связок - " + Context.bundleStorage.getAll());
        Context.log.info("Исключения для букмекеров - " + Context.exceptionForBookmaker);

        Context.botPool.submit(new BotRunner());
        Platform.runLater(() -> run.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #000;"));
        Platform.runLater(() -> run.setText("Стоп"));
        Logger.writeToLogSession("Сессия запущена");
    }

    private void stop() {
        Context.log.info("Остановили бота");
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
}
