package com.melniknow.fd.ui.panels.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.melniknow.fd.Context;
import com.melniknow.fd.advanced.Exception;
import com.melniknow.fd.advanced.ExceptionType;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Currency;
import com.melniknow.fd.domain.Sport;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.melniknow.fd.ui.panels.impl.SettingPanel.profileTextCheck;

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

    public static ScrollPane getTabContent(Bookmaker bookmaker) {
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
        profileTextCheck(bookmaker.name() + "linkField", linkField);
        grid.add(linkField, 1, y++);

        var login = new Label("Логин");
        grid.add(login, 0, y);
        var loginField = new TextField();
        profileTextCheck(bookmaker.name() + "loginField", loginField);
        loginField.setPrefHeight(40);
        loginField.setPromptText("login");
        grid.add(loginField, 1, y++);

        var password = new Label("Пароль");
        grid.add(password, 0, y);
        var passwordField = new TextField();
        profileTextCheck(bookmaker.name() + "passwordField", passwordField);
        passwordField.setPrefHeight(40);
        passwordField.setPromptText("password");
        grid.add(passwordField, 1, y++);

        var currency = new Label("Валюта *");
        grid.add(currency, 0, y);
        var currencyField = new ComboBox<>(FXCollections.observableArrayList(Context.currencyToRubCourse.keySet()));
        setCurrency(bookmaker.name() + "currencyField", currencyField);
        currencyField.setPrefHeight(40);
        grid.add(currencyField, 1, y++);

        var minimum = new Label("Минимальная ставка *");
        grid.add(minimum, 0, y);
        var minimumField = new TextField();
        profileTextCheck(bookmaker.name() + "minimumField", minimumField);
        minimumField.setPrefHeight(40);
        minimumField.setPromptText("В указанной валюте");
        grid.add(minimumField, 1, y++);

        var maximum = new Label("Максимальная ставка *");
        grid.add(maximum, 0, y);
        var maximumField = new TextField();
        profileTextCheck(bookmaker.name() + "maximumField", maximumField);
        maximumField.setPrefHeight(40);
        maximumField.setPromptText("В указанной валюте");
        grid.add(maximumField, 1, y++);

        var screenSize = new Label("Разрешение");
        grid.add(screenSize, 0, y);
        var screenSizeField = new ComboBox<>(FXCollections.observableArrayList("1920/1200", "1920/1080", "1600/900", "1440/900"));
        setString(bookmaker.name() + "screenSizeField", screenSizeField);
        screenSizeField.setPrefHeight(40);
        grid.add(screenSizeField, 1, y++);

        var agent = new Label("User Agent");
        grid.add(agent, 0, y);
        var agentField = new TextField();
        profileTextCheck(bookmaker.name() + "agentField", agentField);
        agentField.setPrefHeight(40);
        agentField.setPromptText("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
        grid.add(agentField, 1, y++);

        var lang = new Label("Язык");
        grid.add(lang, 0, y);
        var langField = new TextField();
        profileTextCheck(bookmaker.name() + "langField", langField);
        langField.setPrefHeight(40);
        langField.setPromptText("en-GB");
        grid.add(langField, 1, y++);

        var proxyIp = new Label("Proxy IP (HTTP)");
        grid.add(proxyIp, 0, y);
        var proxyIpField = new TextField();
        profileTextCheck(bookmaker.name() + "proxyIpField", proxyIpField);
        proxyIpField.setPrefHeight(40);
        proxyIpField.setPromptText("193.67.67.124");
        grid.add(proxyIpField, 1, y++);

        var proxyPort = new Label("Proxy port");
        grid.add(proxyPort, 0, y);
        var proxyPortField = new TextField();
        profileTextCheck(bookmaker.name() + "proxyPortField", proxyPortField);
        proxyPortField.setPrefHeight(40);
        proxyPortField.setPromptText("65233");
        grid.add(proxyPortField, 1, y++);

        var proxyLogin = new Label("Proxy login");
        grid.add(proxyLogin, 0, y);
        var proxyLoginField = new TextField();
        profileTextCheck(bookmaker.name() + "proxyLoginField", proxyLoginField);
        proxyLoginField.setPrefHeight(40);
        proxyLoginField.setPromptText("admin");
        grid.add(proxyLoginField, 1, y++);

        var proxyPassword = new Label("Proxy password");
        grid.add(proxyPassword, 0, y);
        var proxyPasswordField = new TextField();
        profileTextCheck(bookmaker.name() + "proxyPasswordField", proxyPasswordField);
        proxyPasswordField.setPrefHeight(40);
        proxyPasswordField.setPromptText("1234");
        grid.add(proxyPasswordField, 1, y++);

        var saveButton = new Button("Сохранить данные " + bookmaker.nameInAPI);
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(250);
        grid.add(saveButton, 0, ++y, 2, 1);

        var rules = new Label("Исключения спорта");
        grid.add(rules, 0, ++y);
        var nameRulesField = new TextField();
        nameRulesField.setPrefHeight(40);
        nameRulesField.setPromptText("Имя исключения");
        grid.add(nameRulesField, 1, y, 1, 1);
        var sportRulesField = new ComboBox<>(FXCollections.observableArrayList(Arrays.stream(Sport.values()).map(Enum::name).toList()));
        sportRulesField.setPrefHeight(40);
        grid.add(sportRulesField, 2, y++, 1, 1);
        var typeRulesField = new ComboBox<>(FXCollections.observableArrayList(Arrays.stream(ExceptionType.values()).map(Enum::name).toList()));
        typeRulesField.setPrefHeight(40);
        grid.add(typeRulesField, 1, y, 1, 1);

        var addButton = new Button("Добавить исключение");
        addButton.setPrefHeight(40);
        addButton.setDefaultButton(true);
        addButton.setPrefWidth(250);
        grid.add(addButton, 2, y, 1, 1);

        var list = new Label("Список исключений");
        grid.add(list, 0, ++y);

        y = loadRulesFromDb(grid, y, bookmaker);

        AtomicInteger finalY = new AtomicInteger(y);
        addButton.setOnAction(event -> {
            try {
                if (nameRulesField.getText().isEmpty() || sportRulesField.getValue() == null ||
                    typeRulesField.getValue() == null) throw new RuntimeException();

                var rule = new Exception(nameRulesField.getText(), Sport.valueOf(sportRulesField.getValue()),
                    ExceptionType.valueOf(typeRulesField.getValue()));

                var array = Context.exceptionForBookmaker.get(bookmaker);
                if (array == null) Context.exceptionForBookmaker.put(bookmaker, new ArrayList<>() {{
                    add(rule);
                }});
                else {
                    for (Exception exception1 : array) {
                        if (exception1.name().equals(rule.name())) throw new RuntimeException();
                    }
                    array.add(rule);
                }

                var delButton = new Button(rule.name() + "_ " + rule.sport() + " " + rule.type());

                delButton.setOnAction(ev -> {
                    Context.exceptionForBookmaker.get(bookmaker).remove(rule);
                    grid.getChildren().remove(delButton);
                    rulesToJsonAndSave(Context.exceptionForBookmaker);
                });

                grid.add(delButton, 1, finalY.getAndIncrement(), 1, 1);
                rulesToJsonAndSave(Context.exceptionForBookmaker);
            } catch (java.lang.Exception e) {
                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Ошибка добавления правила");
            }
        });


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
                Controller.bundleTab.setDisable(Context.parserParams.bookmakers().size() != Context.betsParams.size());

                Context.screenManager.removeScreenForBookmaker(bookmaker);
                Context.screenManager.createScreenForBookmaker(bookmaker);

                var json = Context.profile.json;
                json.addProperty(bookmaker.name() + "linkField", linkField.getText());
                json.addProperty(bookmaker.name() + "loginField", loginField.getText());
                json.addProperty(bookmaker.name() + "passwordField", passwordField.getText());
                json.addProperty(bookmaker.name() + "minimumField", minimumField.getText());
                json.addProperty(bookmaker.name() + "maximumField", maximumField.getText());
                json.addProperty(bookmaker.name() + "agentField", agentField.getText());
                json.addProperty(bookmaker.name() + "langField", langField.getText());
                json.addProperty(bookmaker.name() + "proxyIpField", proxyIpField.getText());
                json.addProperty(bookmaker.name() + "proxyPortField", proxyPortField.getText());
                json.addProperty(bookmaker.name() + "proxyLoginField", proxyLoginField.getText());
                json.addProperty(bookmaker.name() + "proxyPasswordField", proxyPasswordField.getText());
                json.addProperty(bookmaker.name() + "proxyPasswordField", proxyPasswordField.getText());
                json.addProperty(bookmaker.name() + "currencyField", currencyField.getValue().name());
                json.addProperty(bookmaker.name() + "screenSizeField", screenSizeField.getValue());

                Context.profile.save();
            } catch (java.lang.Exception e) {
                Context.screenManager.removeScreenForBookmaker(bookmaker);
                Controller.runButton.setDisable(true);
                Context.betsParams.remove(bookmaker);
                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Ошибка сохранения данных букмекера");
            }
        });

        return new ScrollPane(grid);
    }
    private static int loadRulesFromDb(GridPane grid, int y, Bookmaker bookmaker) {
        var y_ = y;

        try {
            var array = Context.profile.json.getAsJsonObject("rules").getAsJsonArray(bookmaker.name());
            for (JsonElement jsonElement : array) {
                var rule = new Exception(jsonElement.getAsJsonObject().getAsJsonPrimitive("name").getAsString(),
                    Sport.valueOf(jsonElement.getAsJsonObject().getAsJsonPrimitive("sport").getAsString()),
                    ExceptionType.valueOf(jsonElement.getAsJsonObject().getAsJsonPrimitive("type").getAsString()));

                if (Context.exceptionForBookmaker.containsKey(bookmaker)) {
                    Context.exceptionForBookmaker.get(bookmaker).add(rule);
                } else {
                    Context.exceptionForBookmaker.put(bookmaker, new ArrayList<>() {{
                        add(rule);
                    }});
                }

                var delButton = new Button(rule.name() + "_ " + rule.sport() + " " + rule.type());

                delButton.setOnAction(ev -> {
                    Context.exceptionForBookmaker.get(bookmaker).remove(rule);
                    grid.getChildren().remove(delButton);
                    rulesToJsonAndSave(Context.exceptionForBookmaker);
                });

                grid.add(delButton, 1, y_++, 1, 1);
            }

        } catch (java.lang.Exception ignored) {
        }

        return y_;
    }
    private static void rulesToJsonAndSave(ConcurrentMap<Bookmaker, ArrayList<Exception>> rulesForBookmaker) {
        var obj = new JsonObject();

        for (Bookmaker bookmaker : rulesForBookmaker.keySet()) {
            var bookmakerObj = new JsonArray();

            for (var rule : rulesForBookmaker.get(bookmaker)) {
                var ruleObj = new JsonObject();
                ruleObj.addProperty("name", rule.name());
                ruleObj.addProperty("sport", rule.sport().name());
                ruleObj.addProperty("type", rule.type().name());

                bookmakerObj.add(ruleObj);
            }

            obj.add(bookmaker.name(), bookmakerObj);
        }

        Context.profile.json.add("rules", obj);
        Context.profile.save();
    }

    private static void setString(String name, ComboBox<String> screenSizeField) {
        try {
            var json = Context.profile.json;
            var data = json.getAsJsonPrimitive(name).getAsString();
            if (data != null)
                screenSizeField.setValue(data);
        } catch (java.lang.Exception ignored) { }
    }

    private static void setCurrency(String name, ComboBox<Currency> currencyField1) {
        try {
            var json = Context.profile.json;
            currencyField1.setValue(Currency.valueOf(json.getAsJsonPrimitive(name).getAsString()));
        } catch (java.lang.Exception ignored) { }
    }
}
