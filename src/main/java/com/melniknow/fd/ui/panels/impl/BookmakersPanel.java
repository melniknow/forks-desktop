package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.context.Context;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.ui.Controller;
import com.melniknow.fd.ui.panels.IPanel;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.math.BigDecimal;

public class BookmakersPanel implements IPanel {
    public static TabPane tabPane;

    @Override
    public Node getNode() {
        tabPane = new TabPane();
        tabPane.setSide(Side.LEFT);

        var box = new VBox(tabPane);
        box.setStyle("-fx-padding: 20 20 20 20;");

        return box;
    }

    private static void showErrorAlert(Window owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText("Ошибка сохранения данных букмекера");
        alert.initOwner(owner);
        alert.show();
    }

    private static void showSuccessAlert(Window owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText("Все настройки сохранены!");
        alert.initOwner(owner);
        alert.show();
    }

    public static GridPane getTabContent(Bookmaker bookmaker) {
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

        var link = new Label("Адрес сайта *");
        grid.add(link, 0, 0);
        var linkField = new TextField();
        linkField.setPrefHeight(40);
        linkField.setText(bookmaker.link);
        grid.add(linkField, 1, 0);

        var currency = new Label("Валюта *");
        grid.add(currency, 0, 1);
        var currencyField = new ComboBox<>(FXCollections.observableArrayList(Context.currencyToRubCourse.keySet()));
        currencyField.setPrefHeight(40);
        grid.add(currencyField, 1, 1);

        var minimum = new Label("Минимальная ставка *");
        grid.add(minimum, 0, 2);
        var minimumField = new TextField();
        minimumField.setPrefHeight(40);
        minimumField.setPromptText("В указанной валюте");
        grid.add(minimumField, 1, 2);

        var maximum = new Label("Максимальная ставка *");
        grid.add(maximum, 0, 3);
        var maximumField = new TextField();
        maximumField.setPrefHeight(40);
        maximumField.setPromptText("В указанной валюте");
        grid.add(maximumField, 1, 3);

        var agent = new Label("User Agent");
        grid.add(agent, 0, 4);
        var agentField = new TextField();
        agentField.setPrefHeight(40);
        agentField.setPromptText("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
        grid.add(agentField, 1, 4);

        var proxyIp = new Label("Proxy IP");
        grid.add(proxyIp, 0, 5);
        var proxyIpField = new TextField();
        proxyIpField.setPrefHeight(40);
        proxyIpField.setPromptText("193.67.67.124");
        grid.add(proxyIpField, 1, 5);

        var proxyPort = new Label("Proxy port");
        grid.add(proxyPort, 0, 6);
        var proxyPortField = new TextField();
        proxyPortField.setPrefHeight(40);
        proxyPortField.setPromptText("65233");
        grid.add(proxyPortField, 1, 6);

        var proxyLogin = new Label("Proxy login");
        grid.add(proxyLogin, 0, 7);
        var proxyLoginField = new TextField();
        proxyLoginField.setPrefHeight(40);
        proxyLoginField.setPromptText("admin");
        grid.add(proxyLoginField, 1, 7);

        var proxyPassword = new Label("Proxy password");
        grid.add(proxyPassword, 0, 8);
        var proxyPasswordField = new TextField();
        proxyPasswordField.setPrefHeight(40);
        proxyPasswordField.setPromptText("1234");
        grid.add(proxyPasswordField, 1, 8);

        var saveButton = new Button("Сохранить данные " + bookmaker.nameInAPI);
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(250);
        grid.add(saveButton, 0, 10, 2, 1);
        GridPane.setHalignment(saveButton, HPos.CENTER);
        GridPane.setMargin(saveButton, new Insets(20, 0, 20, 0));

        saveButton.setOnAction(event -> {
            try {
                if (currencyField.getValue() == null || minimumField.getText().isEmpty() || maximumField.getText().isEmpty())
                    throw new RuntimeException();

                var port = proxyPortField.getText().isEmpty() ? null : Integer.parseInt(proxyPortField.getText());

                Context.betsParams.put(bookmaker, new BetUtils.BetsParams(linkField.getText(), currencyField.getValue(),
                    new BigDecimal(minimumField.getText()), new BigDecimal(maximumField.getText()),
                    agentField.getText(), proxyIpField.getText(), port,
                    proxyLoginField.getText(), proxyPasswordField.getText()));

                Controller.runButton.setDisable(Context.parserParams.bookmakers().size() != Context.betsParams.size());

                Controller.screenManager.removeScreenForBookmaker(bookmaker);
                Controller.screenManager.createScreenForBookmaker(bookmaker);

                showSuccessAlert(grid.getScene().getWindow());
            } catch (Exception e) {
                Controller.screenManager.removeScreenForBookmaker(bookmaker);
                Controller.runButton.setDisable(true);
                Context.betsParams.remove(bookmaker);
                showErrorAlert(grid.getScene().getWindow());
            }
        });

        return grid;
    }
}
