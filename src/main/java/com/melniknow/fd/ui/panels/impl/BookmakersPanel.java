package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.Context;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.ui.Controller;
import com.melniknow.fd.ui.panels.IPanel;
import com.melniknow.fd.utils.BetUtils;
import com.melniknow.fd.utils.PanelUtils;
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

        var y = 0;

        var link = new Label("Адрес сайта *");
        grid.add(link, 0, y);
        var linkField = new TextField();
        linkField.setPrefHeight(40);
        linkField.setEditable(!bookmaker.isApi);
        linkField.setText(bookmaker.link);
        grid.add(linkField, 1, y++);

        var login = new Label("Логин");
        grid.add(login, 0, y);
        var loginField = new TextField();
        loginField.setText("IT1323513");
        loginField.setPrefHeight(40);
        loginField.setPromptText("login");
        grid.add(loginField, 1, y++);

        var password = new Label("Пароль");
        grid.add(password, 0, y);
        var passwordField = new TextField();
        passwordField.setPrefHeight(40);
        passwordField.setText("33527qA!");
        passwordField.setPromptText("password");
        grid.add(passwordField, 1, y++);

        var currency = new Label("Валюта *");
        grid.add(currency, 0, y);
        var currencyField = new ComboBox<>(FXCollections.observableArrayList(Context.currencyToRubCourse.keySet()));
        currencyField.setPrefHeight(40);
        grid.add(currencyField, 1, y++);

        var minimum = new Label("Минимальная ставка *");
        grid.add(minimum, 0, y);
        var minimumField = new TextField();
        minimumField.setText("1");
        minimumField.setPrefHeight(40);
        minimumField.setPromptText("В указанной валюте");
        grid.add(minimumField, 1, y++);

        var maximum = new Label("Максимальная ставка *");
        grid.add(maximum, 0, y);
        var maximumField = new TextField();
        maximumField.setText("1000");
        maximumField.setPrefHeight(40);
        maximumField.setPromptText("В указанной валюте");
        grid.add(maximumField, 1, y++);

        var screenSize = new Label("Разрешение");
        grid.add(screenSize, 0, y);
        var screenSizeField = new ComboBox<>(FXCollections.observableArrayList("1920/1200", "1920/1080", "1600/900", "1440/900"));
        screenSizeField.setPrefHeight(40);
        grid.add(screenSizeField, 1, y++);

        var agent = new Label("User Agent");
        grid.add(agent, 0, y);
        var agentField = new TextField();
        agentField.setText("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36 RuxitSynthetic/1.0 v2056842072111261838 t23539024778059721 ath1fb31b7a altpriv cvcv=2 cexpw=1 smf=0");
        agentField.setPrefHeight(40);
        agentField.setPromptText("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
        grid.add(agentField, 1, y++);

        var lang = new Label("Язык");
        grid.add(lang, 0, y);
        var langField = new TextField();
        langField.setText("en-GB");
        langField.setPrefHeight(40);
        langField.setPromptText("en-GB");
        grid.add(langField, 1, y++);

        var proxyIp = new Label("Proxy IP (HTTP)");
        grid.add(proxyIp, 0, y);
        var proxyIpField = new TextField();
        proxyIpField.setText("62.113.105.132");
        proxyIpField.setPrefHeight(40);
        proxyIpField.setPromptText("193.67.67.124");
        grid.add(proxyIpField, 1, y++);

        var proxyPort = new Label("Proxy port");
        grid.add(proxyPort, 0, y);
        var proxyPortField = new TextField();
        proxyPortField.setText("39229");
        proxyPortField.setPrefHeight(40);
        proxyPortField.setPromptText("65233");
        grid.add(proxyPortField, 1, y++);

        var proxyLogin = new Label("Proxy login");
        grid.add(proxyLogin, 0, y);
        var proxyLoginField = new TextField();
        proxyLoginField.setText("5lfnqi");
        proxyLoginField.setPrefHeight(40);
        proxyLoginField.setPromptText("admin");
        grid.add(proxyLoginField, 1, y++);

        var proxyPassword = new Label("Proxy password");
        grid.add(proxyPassword, 0, y);
        var proxyPasswordField = new TextField();
        proxyPasswordField.setText("dsxozk");
        proxyPasswordField.setPrefHeight(40);
        proxyPasswordField.setPromptText("1234");
        grid.add(proxyPasswordField, 1, y++);

        var saveButton = new Button("Сохранить данные " + bookmaker.nameInAPI);
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(250);
        grid.add(saveButton, 0, ++y, 2, 1);
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
                    proxyLoginField.getText(), proxyPasswordField.getText(), screenSizeField.getValue(), langField.getText(),
                    loginField.getText(), passwordField.getText()));

                Controller.runButton.setDisable(Context.parserParams.bookmakers().size() != Context.betsParams.size());

                Context.screenManager.removeScreenForBookmaker(bookmaker);
                Context.screenManager.createScreenForBookmaker(bookmaker);

                PanelUtils.showSuccessAlert(grid.getScene().getWindow(), "Все настройки сохранены!");
            } catch (Exception e) {
                Context.screenManager.removeScreenForBookmaker(bookmaker);
                Controller.runButton.setDisable(true);
                Context.betsParams.remove(bookmaker);
                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Ошибка сохранения данных букмекера");
            }
        });

        return grid;
    }
}
