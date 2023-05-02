package com.melniknow.fd.ui.panels.impl;

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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

public class SettingPanel implements IPanel {
    @Override
    public GridPane getNode() {
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

        var minimum = new Label("Минимальный доход по вилке *");
        grid.add(minimum, 0, y);
        var minimumField = new TextField();
        minimumField.setText("-1");
        minimumField.setPrefHeight(40);
        minimumField.setPromptText("%");
        grid.add(minimumField, 1, y++);

        var maximum = new Label("Максимальный доход по вилке *");
        grid.add(maximum, 0, y);
        var maximumField = new TextField();
        maximumField.setText("100");
        maximumField.setPromptText("%");
        maximumField.setPrefHeight(40);
        grid.add(maximumField, 1, y++);

        var minimumRatio = new Label("Минимальный коэффициент в вилке *");
        grid.add(minimumRatio, 0, y);
        var minimumRatioField = new TextField();
        minimumRatioField.setText("1.001");
        minimumRatioField.setPromptText("Минимальный коэффициент");
        minimumRatioField.setPrefHeight(40);
        grid.add(minimumRatioField, 1, y++);

        var maximumRatio = new Label("Максимальный коэффициент в вилке *");
        grid.add(maximumRatio, 0, y);
        var maximumRatioField = new TextField();
        maximumRatioField.setText("100");
        maximumRatioField.setPromptText("Максимальный коэффициент");
        maximumRatioField.setPrefHeight(40);
        grid.add(maximumRatioField, 1, y++);

        var forkLive = new Label("Минимальное время жизни вилки (сек) *");
        grid.add(forkLive, 0, y);
        var forkLiveField = new TextField();
        forkLiveField.setText("0");
        forkLiveField.setPromptText("Считается относительно процента -1");
        forkLiveField.setPrefHeight(40);
        grid.add(forkLiveField, 1, y++);

        var bookmakers = new Label("Букмекеры *");
        grid.add(bookmakers, 0, y);
        var pinnacle = new CheckBox("PINNACLE");
        pinnacle.setSelected(true);
        grid.add(pinnacle, 1, y++);
        var _188Bet = new CheckBox("_188BET");
        _188Bet.setSelected(true);
        grid.add(_188Bet, 1, y++);

        var middles = new Label("Коридоры *");
        grid.add(middles, 0, y);
        var middlesField = new TextField();
        middlesField.setText("0");
        middlesField.setPromptText("-1 - без коридоров. 0 - вилки и коридоры. 1 - только коридоры");
        middlesField.setPrefHeight(40);
        grid.add(middlesField, 1, y++);

        var typesBet = new Label("Виды ставок *");
        grid.add(typesBet, 0, y);
        var wins = new CheckBox("WIN");
        wins.setSelected(true);
        grid.add(wins, 1, y++);
        var totals = new CheckBox("TOTALS");
        totals.setSelected(true);
        grid.add(totals, 1, y++);
        var handicaps = new CheckBox("HANDICAP");
        handicaps.setSelected(true);
        grid.add(handicaps, 1, y++);

        var sports = new Label("Виды спорта *");
        grid.add(sports, 0, y);
        var soccer = new CheckBox("Soccer");
        soccer.setSelected(true);
        grid.add(soccer, 1, y++);
        var tennis = new CheckBox("Tennis");
        tennis.setSelected(true);
        grid.add(tennis, 1, y++);
        var basketball = new CheckBox("Basketball");
        basketball.setSelected(true);
        grid.add(basketball, 1, y++);

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
                add(totals);
                add(handicaps);
            }};

            var sportsData = new ArrayList<CheckBox>() {{
                add(soccer);
                add(tennis);
                add(basketball);
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
                var typesBetParse = typesBetData.stream().filter(CheckBox::isSelected).map(n -> BetType.valueOf(n.getText())).toList();
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

            PanelUtils.showSuccessAlert(grid.getScene().getWindow(), "Все настройки сохранены!");
            Controller.currency.setDisable(false);
        });

        return grid;
    }
}
