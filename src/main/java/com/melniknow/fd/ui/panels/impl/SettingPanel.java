package com.melniknow.fd.ui.panels.impl;

import com.google.gson.JsonParseException;
import com.melniknow.fd.Context;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;
import com.melniknow.fd.ui.Controller;
import com.melniknow.fd.ui.panels.IPanel;
import com.melniknow.fd.utils.PanelUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

public class SettingPanel implements IPanel {
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

        var minimum = new Label("Минимальный доход по вилке *");
        grid.add(minimum, 0, y);
        var minimumField = new TextField();
        profileTextCheck("minimumField", minimumField);
        minimumField.setPrefHeight(40);
        minimumField.setPromptText("%");
        grid.add(minimumField, 1, y++);

        var maximum = new Label("Максимальный доход по вилке *");
        grid.add(maximum, 0, y);
        var maximumField = new TextField();
        profileTextCheck("maximumField", maximumField);
        maximumField.setPromptText("%");
        maximumField.setPrefHeight(40);
        grid.add(maximumField, 1, y++);

        var minimumRatio = new Label("Минимальный коэффициент в вилке *");
        grid.add(minimumRatio, 0, y);
        var minimumRatioField = new TextField();
        profileTextCheck("minimumRatioField", minimumRatioField);
        minimumRatioField.setPromptText("Минимальный коэффициент");
        minimumRatioField.setPrefHeight(40);
        grid.add(minimumRatioField, 1, y++);

        var maximumRatio = new Label("Максимальный коэффициент в вилке *");
        grid.add(maximumRatio, 0, y);
        var maximumRatioField = new TextField();
        profileTextCheck("maximumRatioField", maximumRatioField);
        maximumRatioField.setPromptText("Максимальный коэффициент");
        maximumRatioField.setPrefHeight(40);
        grid.add(maximumRatioField, 1, y++);

        var bookmakers = new Label("Букмекеры *");
        grid.add(bookmakers, 0, y);
        var pinnacle = new CheckBox("PINNACLE");
        profileBooleanCheck("pinnacle", pinnacle);
        grid.add(pinnacle, 1, y++);
        var _188Bet = new CheckBox("_188BET");
        profileBooleanCheck("_188Bet", _188Bet);
        grid.add(_188Bet, 1, y++);

        var middles = new Label("Коридоры *");
        grid.add(middles, 0, y);
        var middlesField = new TextField();
        profileTextCheck("middlesField", middlesField);
        middlesField.setPromptText("-1 - без коридоров. 0 - вилки и коридоры. 1 - только коридоры");
        middlesField.setPrefHeight(40);
        grid.add(middlesField, 1, y++);

        var typesBet = new Label("Виды ставок *");
        grid.add(typesBet, 0, y);
        var wins = new CheckBox("WIN");
        profileBooleanCheck("wins", wins);
        grid.add(wins, 1, y++);
        var setWin = new CheckBox("SET_WIN");
        profileBooleanCheck("setWin", setWin);
        grid.add(setWin, 1, y++);
        var halfWin = new CheckBox("HALF_WIN");
        profileBooleanCheck("halfWin", halfWin);
        grid.add(halfWin, 1, y++);
        var totals = new CheckBox("TOTALS");
        profileBooleanCheck("totals", totals);
        grid.add(totals, 1, y++);
        var setTotals = new CheckBox("SET_TOTALS");
        profileBooleanCheck("setTotals", setTotals);
        grid.add(setTotals, 1, y++);
        var halfTotals = new CheckBox("HALF_TOTALS");
        profileBooleanCheck("halfTotals", halfTotals);
        grid.add(halfTotals, 1, y++);
        var handicaps = new CheckBox("HANDICAP");
        profileBooleanCheck("handicaps", handicaps);
        grid.add(handicaps, 1, y++);
        var halfHandicap = new CheckBox("HALF_HANDICAP");
        profileBooleanCheck("halfHandicap", halfHandicap);
        grid.add(halfHandicap, 1, y++);
        var setHandicap = new CheckBox("SET_HANDICAP");
        profileBooleanCheck("setHandicap", setHandicap);
        grid.add(setHandicap, 1, y++);

        var forkLive = new Label("Минимальное время жизни вилки (сек) *");
        grid.add(forkLive, 0, y);
        var forkLiveField = new TextField();
        profileTextCheck("forkLiveField", forkLiveField);
        forkLiveField.setPromptText("Считается относительно процента -1");
        forkLiveField.setPrefHeight(40);
        grid.add(forkLiveField, 1, y++);

        var sports = new Label("Виды спорта *");
        grid.add(sports, 0, y);
        var soccer = new CheckBox("Soccer");
        profileBooleanCheck("soccer", soccer);
        grid.add(soccer, 1, y++);
        var tennis = new CheckBox("Tennis");
        profileBooleanCheck("tennis", tennis);
        grid.add(tennis, 1, y++);
        var basketball = new CheckBox("Basketball");
        profileBooleanCheck("basketball", basketball);
        grid.add(basketball, 1, y++);
        var volleyball = new CheckBox("Volleyball");
        profileBooleanCheck("volleyball", volleyball);
        grid.add(volleyball, 1, y++);
        var handball = new CheckBox("Handball");
        profileBooleanCheck("handball", handball);
        grid.add(handball, 1, y++);
        var hockey = new CheckBox("Hockey");
        profileBooleanCheck("hockey", hockey);
        grid.add(hockey, 1, y++);

        var saveButton = new Button("Сохранить");
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(150);
        grid.add(saveButton, 0, ++y, 2, 1);
        GridPane.setHalignment(saveButton, HPos.CENTER);
        GridPane.setMargin(saveButton, new Insets(20, 0, 20, 0));

        saveButton.setOnAction(event -> {
            var bookmakersData = new ArrayList<CheckBox>() {{
                add(pinnacle);
                add(_188Bet);
            }};

            var typesBetData = new ArrayList<CheckBox>() {{
                add(wins);
                add(setWin);
                add(halfWin);
                add(totals);
                add(setTotals);
                add(halfTotals);
                add(handicaps);
                add(setHandicap);
                add(halfHandicap);
            }};

            var sportsData = new ArrayList<CheckBox>() {{
                add(soccer);
                add(tennis);
                add(basketball);
                add(volleyball);
                add(handball);
                add(hockey);
            }};

            if (maximumField.getText().isEmpty() || minimumField.getText().isEmpty() ||
                minimumRatioField.getText().isEmpty() || maximumRatioField.getText().isEmpty() ||
                middlesField.getText().isEmpty() ||
                bookmakersData.stream().filter(CheckBox::isSelected).count() < 2 ||
                typesBetData.stream().noneMatch(CheckBox::isSelected) ||
                forkLiveField.getText().isEmpty() || sportsData.stream().noneMatch(CheckBox::isSelected)) {

                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Корректно заполните все необходимые поля!");
                return;
            }

            try {
                var middlesParse = Integer.parseInt(middlesField.getText());
                if (middlesParse > 1 || middlesParse < -1) throw new RuntimeException();

                var bookmakersParse = bookmakersData.stream().filter(CheckBox::isSelected).map(n -> Bookmaker.valueOf(n.getText())).toList();
                var typesBetParse = typesBetData.stream().filter(CheckBox::isSelected).map(n -> BetType.valueOf(n.getText().toUpperCase())).toList();
                var sportsType = sportsData.stream().filter(CheckBox::isSelected).map(n -> Sports.valueOf(n.getText().toUpperCase())).toList();

                var noChangeBookmakers = Context.parserParams != null &&
                    new HashSet<>(Context.parserParams.bookmakers()).containsAll(bookmakersParse) &&
                    new HashSet<>(bookmakersParse).containsAll(Context.parserParams.bookmakers());

                Context.parserParams = new Parser.ParserParams(
                    new BigDecimal(minimumField.getText()),
                    new BigDecimal(maximumField.getText()),
                    new BigDecimal(minimumRatioField.getText()),
                    new BigDecimal(maximumRatioField.getText()),
                    middlesParse,
                    bookmakersParse,
                    typesBetParse,
                    new BigDecimal(forkLiveField.getText()),
                    sportsType
                );

                if (!noChangeBookmakers) {
                    Context.betsParams.clear();
                    Context.screenManager.clear();
                    Controller.runButton.setDisable(true);
                }
            } catch (Exception e) {
                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Корректно заполните все необходимые поля!");
                return;
            }

            var json = Context.profile.json;
            json.addProperty("minimumField", minimumField.getText());
            json.addProperty("maximumField", maximumField.getText());
            json.addProperty("minimumRatioField", minimumRatioField.getText());
            json.addProperty("maximumRatioField", maximumRatioField.getText());
            json.addProperty("pinnacle", pinnacle.isSelected());
            json.addProperty("_188Bet", _188Bet.isSelected());
            json.addProperty("middlesField", middlesField.getText());
            json.addProperty("wins", wins.isSelected());
            json.addProperty("setWin", setWin.isSelected());
            json.addProperty("halfWin", halfWin.isSelected());
            json.addProperty("totals", totals.isSelected());
            json.addProperty("setTotals", setTotals.isSelected());
            json.addProperty("halfTotals", halfTotals.isSelected());
            json.addProperty("handicaps", handicaps.isSelected());
            json.addProperty("halfHandicap", halfHandicap.isSelected());
            json.addProperty("setHandicap", setHandicap.isSelected());
            json.addProperty("forkLiveField", forkLiveField.getText());
            json.addProperty("soccer", soccer.isSelected());
            json.addProperty("tennis", tennis.isSelected());
            json.addProperty("basketball", basketball.isSelected());
            json.addProperty("volleyball", volleyball.isSelected());
            json.addProperty("handball", handball.isSelected());
            json.addProperty("hockey", hockey.isSelected());

            Context.profile.save();

            PanelUtils.showSuccessAlert(grid.getScene().getWindow(), "Все настройки сохранены!");
            Controller.currency.setDisable(false);
        });

        return new ScrollPane(grid);
    }
    private void profileTextCheck(String name, TextField field) {
        try {
            var json = Context.profile.json;
            field.setText(json.getAsJsonPrimitive(name).getAsString());
        } catch (NullPointerException | JsonParseException ignored) { }
    }

    private void profileBooleanCheck(String name, CheckBox checkBox) {
        try {
            var json = Context.profile.json;
            checkBox.setSelected(json.getAsJsonPrimitive(name).getAsBoolean());
        } catch (NullPointerException | JsonParseException ignored) { }
    }
}

