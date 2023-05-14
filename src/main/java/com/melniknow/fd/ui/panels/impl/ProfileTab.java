package com.melniknow.fd.ui.panels.impl;

import com.google.gson.JsonParser;
import com.melniknow.fd.Context;
import com.melniknow.fd.profile.Database;
import com.melniknow.fd.profile.Profile;
import com.melniknow.fd.ui.Controller;
import com.melniknow.fd.ui.panels.IPanel;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Objects;

import static com.melniknow.fd.ui.Controller.*;
import static com.melniknow.fd.ui.Controller.tabConstructor;

public class ProfileTab implements IPanel {
    @Override
    public ScrollPane getNode() {
        var grid = new GridPane();

        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints columnOneConstraints = new ColumnConstraints(550, 550, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        ColumnConstraints columnTwoConstrains = new ColumnConstraints(550, 550, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        var y = 0;

        var profile = new Label("Введите имя профиля");
        grid.add(profile, 0, y);
        var profileField = new TextField();
        profileField.setPrefHeight(40);
        profileField.setPromptText("MyProfile");
        grid.add(profileField, 1, y++);

        var saveButton = new Button("Далее");
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(150);
        grid.add(saveButton, 0, ++y, 2, 1);
        GridPane.setHalignment(saveButton, HPos.CENTER);

        saveButton.setOnAction(event -> {
            if (profileField.getText() != null && !profileField.getText().equals("")) {
                var profileName = profileField.getText();
                var jsonString = Database.getJsonByProfileName(profileName);

                if (jsonString == null) {
                    Database.createProfile(profileName);
                    jsonString = Database.getJsonByProfileName(profileName);
                }

                var jsonData = JsonParser.parseString(Objects.requireNonNull(jsonString));
                Context.profile = new Profile(profileName, jsonData);
                Context.profile.save();
            }

            var settingTab = tabConstructor("Настройки", new SettingPanel());
            var currencyTab = tabConstructor("Валюты", new CurrencyPanel());
            var bookmakersTab = tabConstructor("Букмекеры", new BookmakersPanel());
            var sessionTab = tabConstructor("Сессия", new SessionPanel());

            settingTab.setDisable(true);
            currencyTab.setDisable(true);
            bookmakersTab.setDisable(true);

            setting = settingTab;
            currency = currencyTab;
            bookmakers = bookmakersTab;

            bookmakers.setOnSelectionChanged(event_ -> {
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

            while (pane.getTabs().size() > 1) {
                pane.getTabs().remove(pane.getTabs().size() - 1);
            }

            pane.getTabs().addAll(setting, currency, bookmakers, sessionTab);

            Controller.setting.setDisable(false);
        });

        return new ScrollPane(grid);
    }
}
