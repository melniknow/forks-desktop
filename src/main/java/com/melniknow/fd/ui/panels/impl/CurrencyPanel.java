package com.melniknow.fd.ui.panels.impl;

import com.google.gson.JsonParser;
import com.melniknow.fd.context.Context;
import com.melniknow.fd.core.Currency;
import com.melniknow.fd.ui.Controller;
import com.melniknow.fd.ui.panels.IPanel;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.math.BigDecimal;
import java.util.HashMap;

public class CurrencyPanel implements IPanel {
    @Override
    public Node getGrid() {
        var grid = new GridPane();

        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints columnOneConstraints = new ColumnConstraints(400, 400, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        ColumnConstraints columnTwoConstrains = new ColumnConstraints(400, 400, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        var rub = new Label("Рубль");
        grid.add(rub, 0, 1);
        var rubField = new TextField();
        rubField.setPrefHeight(40);
        rubField.setText("1");
        rubField.setEditable(false);
        grid.add(rubField, 1, 1);

        var usd = new Label("Доллар *");
        grid.add(usd, 0, 2);
        var usdField = new TextField();
        usdField.setPrefHeight(40);
        usdField.setPromptText("USD");
        grid.add(usdField, 1, 2);

        var eur = new Label("Евро *");
        grid.add(eur, 0, 3);
        var eurField = new TextField();
        eurField.setPrefHeight(40);
        eurField.setPromptText("EUR");
        grid.add(eurField, 1, 3);

        var thb = new Label("Таиландский бат *");
        grid.add(thb, 0, 4);
        var thbField = new TextField();
        thbField.setPrefHeight(40);
        thbField.setPromptText("TNB");
        grid.add(thbField, 1, 4);

        var updateButton = new Button("Получить данные с сервера ЦБ РФ");
        updateButton.setPrefHeight(40);
        updateButton.setStyle("-fx-background-color: #A600A6;");
        updateButton.setDefaultButton(true);
        updateButton.setPrefWidth(400);
        grid.add(updateButton, 0, 6, 2, 1);
        GridPane.setHalignment(updateButton, HPos.CENTER);
        GridPane.setMargin(updateButton, new Insets(20, 0, 20, 0));

        var saveButton = new Button("Сохранить");
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(150);
        grid.add(saveButton, 0, 7, 2, 1);
        GridPane.setHalignment(saveButton, HPos.CENTER);

        saveButton.setOnAction(event -> {
            if (updateCurrencyValue(usdField.getText(), eurField.getText(), thbField.getText())) {
                showSuccessAlert(grid.getScene().getWindow());
                Controller.bookmakers.setDisable(false);
            } else
                showErrorAlert(grid.getScene().getWindow(), "Корректно заполните все необходимые поля!");
        });

        updateButton.setOnAction(event -> Controller.parsingPool.submit(() -> {
            var uri = "https://www.cbr-xml-daily.ru/daily_json.js";
            var isGood = true;

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
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            var obj = JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject();
                            var array = obj.getAsJsonObject("Valute");

                            var parsedUsd = array.getAsJsonObject("USD").get("Value").getAsBigDecimal();
                            var parsedEur = array.getAsJsonObject("EUR").get("Value").getAsBigDecimal();
                            var parsedTnb = array.getAsJsonObject("THB").get("Value").getAsBigDecimal();

                            Platform.runLater(() -> usdField.setText(parsedUsd.toPlainString()));
                            Platform.runLater(() -> eurField.setText(parsedEur.toPlainString()));
                            Platform.runLater(() -> thbField.setText(parsedTnb.toPlainString()));
                        }
                    } else isGood = false;
                } catch (Exception e) {
                    isGood = false;
                }
            } catch (Exception e) {
                isGood = false;
            }

            if (!isGood)
                Platform.runLater(() -> showErrorAlert(grid.getScene().getWindow(), "Ошибка получения данных с сервера!"));
        }));

        return grid;
    }

    private boolean updateCurrencyValue(String usd, String eur, String tnb) {
        try {
            var res = new HashMap<Currency, BigDecimal>();
            res.put(Currency.USD, new BigDecimal(usd));
            res.put(Currency.EUR, new BigDecimal(eur));
            res.put(Currency.THB, new BigDecimal(tnb));

            Context.currencyToRubCourse = res;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void showErrorAlert(Window owner, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initOwner(owner);
        alert.show();
    }

    private void showSuccessAlert(Window owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText("Все настройки сохранены!");
        alert.initOwner(owner);
        alert.show();
    }
}
