package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.Context;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
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
import java.util.HashMap;
import java.util.HashSet;

public class SettingPanel implements IPanel {
    private final HashMap<String, CheckBox> sportAndBetTypeToCheckbox = new HashMap<>();
    @Override
    public ScrollPane getNode() {
        var grid = new GridPane();

        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints columnOneConstraints = new ColumnConstraints(550, 550, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        ColumnConstraints columnTwoConstrains = new ColumnConstraints(100, 100, Double.MAX_VALUE);
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
        minimumRatioField.setPromptText("1");
        minimumRatioField.setPrefHeight(40);
        grid.add(minimumRatioField, 1, y++);

        var maximumRatio = new Label("Максимальный коэффициент в вилке *");
        grid.add(maximumRatio, 0, y);
        var maximumRatioField = new TextField();
        profileTextCheck("maximumRatioField", maximumRatioField);
        maximumRatioField.setPromptText("100");
        maximumRatioField.setPrefHeight(40);
        grid.add(maximumRatioField, 1, y++);

        var pauseAfterSuccess = new Label("Пауза после успешно проставленной вилки (сек) *");
        grid.add(pauseAfterSuccess, 0, y);
        var pauseAfterSuccessField = new TextField();
        profileTextCheck("pauseAfterSuccessField", pauseAfterSuccessField);
        pauseAfterSuccessField.setPromptText("3");
        pauseAfterSuccessField.setPrefHeight(40);
        grid.add(pauseAfterSuccessField, 1, y++);

        var countFork = new Label("Максимальное количество вилок в одном событии *");
        grid.add(countFork, 0, y);
        var countForkField = new TextField();
        profileTextCheck("countForkField", countForkField);
        countForkField.setPromptText("6");
        countForkField.setPrefHeight(40);
        grid.add(countForkField, 1, y++);

        var bookmakers = new Label("Букмекеры *");
        grid.add(bookmakers, 0, y);
        var pinnacle = new CheckBox("PINNACLE");
        profileBooleanCheck("pinnacle", pinnacle);
        grid.add(pinnacle, 1, y++);
        var _188Bet = new CheckBox("_188BET");
        profileBooleanCheck("_188Bet", _188Bet);
        grid.add(_188Bet, 1, y++);
        var bet365 = new CheckBox("BET365");
        profileBooleanCheck("bet365", bet365);
        grid.add(bet365, 1, y++);

        var middles = new Label("Коридоры *");
        grid.add(middles, 0, y);
        var middlesField = new TextField();
        profileTextCheck("middlesField", middlesField);
        middlesField.setPromptText("-1, 0, 1");
        middlesField.setPrefHeight(40);
        grid.add(middlesField, 1, y++);

        var forkLive = new Label("Минимальное время жизни вилки (сек) *");
        grid.add(forkLive, 0, y);
        var forkLiveField = new TextField();
        profileTextCheck("forkLiveField", forkLiveField);
        forkLiveField.setPromptText("1");
        forkLiveField.setPrefHeight(40);
        grid.add(forkLiveField, 1, y++);

        var sports = new Label("Вид спорта *");
        grid.add(sports, 0, y);
        var soccer = new CheckBox("Soccer");
        profileBooleanCheck("soccer", soccer);
        grid.add(soccer, 1, y++);
        y = viewTypesForSport("soccer", grid, y);

        var tennis = new CheckBox("Tennis");
        profileBooleanCheck("tennis", tennis);
        grid.add(tennis, 1, y++);
        y = viewTypesForSport("tennis", grid, y);
        var basketball = new CheckBox("Basketball");
        profileBooleanCheck("basketball", basketball);
        grid.add(basketball, 1, y++);
        y = viewTypesForSport("basketball", grid, y);
        var volleyball = new CheckBox("Volleyball");
        profileBooleanCheck("volleyball", volleyball);
        grid.add(volleyball, 1, y++);
        y = viewTypesForSport("volleyball", grid, y);
        var handball = new CheckBox("Handball");
        profileBooleanCheck("handball", handball);
        grid.add(handball, 1, y++);
        y = viewTypesForSport("handball", grid, y);
        var hockey = new CheckBox("Hockey");
        profileBooleanCheck("hockey", hockey);
        grid.add(hockey, 1, y++);
        y = viewTypesForSport("hockey", grid, y);

        var isRepeatFork = new Label("Повтор вилок");
        grid.add(isRepeatFork, 0, y);
        var isRepeatForkCheckBox = new CheckBox("Включить");
        profileBooleanCheck("isRepeatForkCheckBox", isRepeatForkCheckBox);
        grid.add(isRepeatForkCheckBox, 1, y++);

        var saveButton = new Button("Сохранить");
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(150);
        grid.add(saveButton, 1, ++y, 2, 1);
        GridPane.setHalignment(saveButton, HPos.CENTER);
        GridPane.setMargin(saveButton, new Insets(20, 0, 20, 0));

        saveButton.setOnAction(event -> {
            var bookmakersData = new ArrayList<CheckBox>() {{
                add(pinnacle);
                add(_188Bet);
                add(bet365);
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
                forkLiveField.getText().isEmpty() || sportsData.stream().noneMatch(CheckBox::isSelected) ||
                pauseAfterSuccessField.getText().isEmpty() ||
                countForkField.getText().isEmpty()) {

                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Корректно заполните все необходимые поля!");
                return;
            }

            var sportsType = sportsData.stream().filter(CheckBox::isSelected).map(n -> Sport.valueOf(n.getText().toUpperCase())).toList();
            var json = Context.profile.json;
            Context.sportToBetTypes.clear();

            try {
                for (Sport sport_ : Sport.values()) {
                    var sport = sport_.name().toLowerCase();
                    var data = new ArrayList<BetType>();

                    var isWins = sportAndBetTypeToCheckbox.get(sport + "wins").isSelected();
                    json.addProperty(sport + "wins", isWins);
                    if (isWins) data.add(BetType.WIN);

                    var isSetWin = sportAndBetTypeToCheckbox.get(sport + "setWin").isSelected();
                    json.addProperty(sport + "setWin", isSetWin);
                    if (isSetWin) data.add(BetType.SET_WIN);

                    var isHalfWin = sportAndBetTypeToCheckbox.get(sport + "halfWin").isSelected();
                    json.addProperty(sport + "halfWin", isHalfWin);
                    if (isHalfWin) data.add(BetType.HALF_WIN);

                    var isTotals = sportAndBetTypeToCheckbox.get(sport + "totals").isSelected();
                    json.addProperty(sport + "totals", isTotals);
                    if (isTotals) data.add(BetType.TOTALS);

                    var isSetTotals = sportAndBetTypeToCheckbox.get(sport + "setTotals").isSelected();
                    json.addProperty(sport + "setTotals", isSetTotals);
                    if (isSetTotals) data.add(BetType.SET_TOTALS);

                    var isHalfTotals = sportAndBetTypeToCheckbox.get(sport + "halfTotals").isSelected();
                    json.addProperty(sport + "halfTotals", isHalfTotals);
                    if (isHalfTotals) data.add(BetType.HALF_TOTALS);

                    var isHandicap = sportAndBetTypeToCheckbox.get(sport + "handicaps").isSelected();
                    json.addProperty(sport + "handicaps", isHandicap);
                    if (isHandicap) data.add(BetType.HANDICAP);

                    var isHalfHandicap = sportAndBetTypeToCheckbox.get(sport + "halfHandicap").isSelected();
                    json.addProperty(sport + "halfHandicap", isHalfHandicap);
                    if (isHalfHandicap) data.add(BetType.HALF_HANDICAP);

                    var isSetHandicap = sportAndBetTypeToCheckbox.get(sport + "setHandicap").isSelected();
                    json.addProperty(sport + "setHandicap", isSetHandicap);
                    if (isSetHandicap) data.add(BetType.SET_HANDICAP);

                    if (!sportsType.contains(sport_) && !data.isEmpty() ||
                        sportsType.contains(sport_) && data.isEmpty())
                        throw new RuntimeException();

                    if (data.isEmpty()) continue;

                    Context.sportToBetTypes.put(sport_, data);
                }


                var middlesParse = Integer.parseInt(middlesField.getText());
                if (middlesParse > 1 || middlesParse < -1) throw new RuntimeException();

                var setBetTypes = new HashSet<BetType>();

                for (Sport sport : Context.sportToBetTypes.keySet()) {
                    setBetTypes.addAll(Context.sportToBetTypes.get(sport));
                }

                if (setBetTypes.isEmpty()) throw new RuntimeException();

                var bookmakersParse = bookmakersData.stream().filter(CheckBox::isSelected).map(n -> Bookmaker.valueOf(n.getText())).toList();

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
                    setBetTypes.stream().toList(),
                    new BigDecimal(forkLiveField.getText()),
                    sportsType,
                    new BigDecimal(pauseAfterSuccessField.getText()),
                    new BigDecimal(countForkField.getText()),
                    isRepeatForkCheckBox.isSelected()
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

            json.addProperty("minimumField", minimumField.getText());
            json.addProperty("maximumField", maximumField.getText());
            json.addProperty("minimumRatioField", minimumRatioField.getText());
            json.addProperty("maximumRatioField", maximumRatioField.getText());
            json.addProperty("pauseAfterSuccessField", pauseAfterSuccessField.getText());
            json.addProperty("countForkField", countForkField.getText());
            json.addProperty("pinnacle", pinnacle.isSelected());
            json.addProperty("_188Bet", _188Bet.isSelected());
            json.addProperty("bet365", bet365.isSelected());
            json.addProperty("middlesField", middlesField.getText());
            json.addProperty("forkLiveField", forkLiveField.getText());
            json.addProperty("soccer", soccer.isSelected());
            json.addProperty("tennis", tennis.isSelected());
            json.addProperty("basketball", basketball.isSelected());
            json.addProperty("volleyball", volleyball.isSelected());
            json.addProperty("handball", handball.isSelected());
            json.addProperty("hockey", hockey.isSelected());
            json.addProperty("isRepeatForkCheckBox", isRepeatForkCheckBox.isSelected());

            Context.profile.save();
            Controller.currency.setDisable(false);
        });

        return new ScrollPane(grid);
    }
    private int viewTypesForSport(String sport, GridPane grid, int y) {
        var wins = new CheckBox("WIN");
        profileBooleanCheckAndPutHashMAp(sport + "wins", wins);
        grid.add(wins, 2, y++);
        var setWin = new CheckBox("SET_WIN");
        profileBooleanCheckAndPutHashMAp(sport + "setWin", setWin);
        grid.add(setWin, 2, y++);
        var halfWin = new CheckBox("HALF_WIN");
        profileBooleanCheckAndPutHashMAp(sport + "halfWin", halfWin);
        grid.add(halfWin, 2, y++);
        var totals = new CheckBox("TOTALS");
        profileBooleanCheckAndPutHashMAp(sport + "totals", totals);
        grid.add(totals, 2, y++);
        var setTotals = new CheckBox("SET_TOTALS");
        profileBooleanCheckAndPutHashMAp(sport + "setTotals", setTotals);
        grid.add(setTotals, 2, y++);
        var halfTotals = new CheckBox("HALF_TOTALS");
        profileBooleanCheckAndPutHashMAp(sport + "halfTotals", halfTotals);
        grid.add(halfTotals, 2, y++);
        var handicaps = new CheckBox("HANDICAP");
        profileBooleanCheckAndPutHashMAp(sport + "handicaps", handicaps);
        grid.add(handicaps, 2, y++);
        var halfHandicap = new CheckBox("HALF_HANDICAP");
        profileBooleanCheckAndPutHashMAp(sport + "halfHandicap", halfHandicap);
        grid.add(halfHandicap, 2, y++);
        var setHandicap = new CheckBox("SET_HANDICAP");
        profileBooleanCheckAndPutHashMAp(sport + "setHandicap", setHandicap);
        grid.add(setHandicap, 2, y++);
        return y;
    }
    private void profileBooleanCheckAndPutHashMAp(String name, CheckBox checkBox) {
        sportAndBetTypeToCheckbox.put(name, checkBox);
        try {
            var json = Context.profile.json;
            checkBox.setSelected(json.getAsJsonPrimitive(name).getAsBoolean());
        } catch (Exception ignored) { }
    }
    public static void profileTextCheck(String name, TextField field) {
        try {
            var json = Context.profile.json;
            var data = json.getAsJsonPrimitive(name).getAsString();
            if (data != null)
                field.setText(data);
        } catch (Exception ignored) { }
    }

    public static void profileBooleanCheck(String name, CheckBox checkBox) {
        try {
            var json = Context.profile.json;
            checkBox.setSelected(json.getAsJsonPrimitive(name).getAsBoolean());
        } catch (Exception ignored) { }
    }
}

