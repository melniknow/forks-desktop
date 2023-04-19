package com.melniknow.fd.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.lang.reflect.InvocationTargetException;

public class TabsController {
    @FXML
    private TabPane tabPane;

    @FXML
    private Button run;

    public void initialize() {
        run.setStyle("-fx-background-color: #00FF00; -fx-text-fill: #000;");

        var settingTab = tabConstructor("Настройки бк", SettingPanel.class);
        var bookmakersTab = tabConstructor("Букмекеры", BookmakersPanel.class);
        var securityTab = tabConstructor("Безопасность", SecurityPanel.class);
        var currencyTab = tabConstructor("Валюта", CurrencyPanel.class);
        var betsTab = tabConstructor("Вилки и ставки", BetsPanel.class);

        tabPane.getTabs().addAll(settingTab, bookmakersTab, securityTab, currencyTab, betsTab);
    }

    @FXML
    protected void onRunButtonClick() {
        if (run.getText().equals("Стоп")) {
            run.setStyle("-fx-background-color: #00FF00; -fx-text-fill: #000;");
            run.setText("Старт");
        } else {
            run.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #000;");
            run.setText("Стоп");
        }
    }

    private Tab tabConstructor(String label, Class<? extends IPanel> clazz) {
        var tab = new Tab(label);
        tab.setClosable(false);

        try {
            tab.setContent(clazz.getDeclaredConstructor().newInstance().getGrid());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }

        return tab;
    }
}
