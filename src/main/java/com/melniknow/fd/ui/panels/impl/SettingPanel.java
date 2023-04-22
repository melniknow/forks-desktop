package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.context.Context;
import com.melniknow.fd.oddscorp.BetType;
import com.melniknow.fd.oddscorp.Bookmakers;
import com.melniknow.fd.oddscorp.Parser;
import com.melniknow.fd.ui.Controller;
import com.melniknow.fd.ui.panels.IPanel;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SettingPanel implements IPanel {
    @Override
    public GridPane getGrid() {
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

        var minimum = new Label("Минимальный доход по вилке *");
        grid.add(minimum, 0, 1);
        var minimumField = new TextField();
        minimumField.setPrefHeight(40);
        minimumField.setPromptText("%");
        grid.add(minimumField, 1, 1);

        var maximum = new Label("Максимальный доход по вилке *");
        grid.add(maximum, 0, 2);
        var maximumField = new TextField();
        maximumField.setPromptText("%");
        maximumField.setPrefHeight(40);
        grid.add(maximumField, 1, 2);

        var minimumRatio = new Label("Минимальный коэффициент в вилке *");
        grid.add(minimumRatio, 0, 3);
        var minimumRatioField = new TextField();
        minimumRatioField.setPromptText("Минимальный коэффициент");
        minimumRatioField.setPrefHeight(40);
        grid.add(minimumRatioField, 1, 3);

        var maximumRatio = new Label("Максимальный коэффициент в вилке *");
        grid.add(maximumRatio, 0, 4);
        var maximumRatioField = new TextField();
        maximumRatioField.setPromptText("Максимальный коэффициент");
        maximumRatioField.setPrefHeight(40);
        grid.add(maximumRatioField, 1, 4);

        var forkLive = new Label("Минимальное время жизни вилки (сек) *");
        grid.add(forkLive, 0, 5);
        var forkLiveField = new TextField();
        forkLiveField.setPromptText("Считается относительно процента -1");
        forkLiveField.setPrefHeight(40);
        grid.add(forkLiveField, 1, 5);

        var bookmakers = new Label("Букмекеры *");
        grid.add(bookmakers, 0, 6);
        var pinnacle = new CheckBox("PINNACLE");
        grid.add(pinnacle, 1, 6);
        var _188Bet = new CheckBox("_188BET");
        grid.add(_188Bet, 1, 7);
        var bet365 = new CheckBox("BET365");
        grid.add(bet365, 1, 8);

        var middles = new Label("Коридоры *");
        grid.add(middles, 0, 9);
        var middlesField = new TextField();
        middlesField.setPromptText("-1 - без коридоров. 0 - вилки и коридоры. 1 - только коридоры");
        middlesField.setPrefHeight(40);
        grid.add(middlesField, 1, 9);

        var typesBet = new Label("Виды ставок *");
        grid.add(typesBet, 0, 10);
        var wins = new CheckBox("WIN");
        grid.add(wins, 1, 10);
        var totals = new CheckBox("TOTALS");
        grid.add(totals, 1, 11);
        var handicaps = new CheckBox("HANDICAP");
        grid.add(handicaps, 1, 12);

        var saveButton = new Button("Сохранить");
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(150);
        grid.add(saveButton, 0, 14, 2, 1);
        GridPane.setHalignment(saveButton, HPos.CENTER);
        GridPane.setMargin(saveButton, new Insets(20, 0, 20, 0));

        saveButton.setOnAction(event -> {
            var bookmakersData = new ArrayList<CheckBox>() {{
                add(pinnacle);
                add(_188Bet);
                add(bet365);
            }};

            var typesBetData = new ArrayList<CheckBox>() {{
                add(wins);
                add(totals);
                add(handicaps);
            }};

            if (maximumField.getText().isEmpty() || minimumField.getText().isEmpty() ||
                minimumRatioField.getText().isEmpty() || maximumRatioField.getText().isEmpty() ||
                middlesField.getText().isEmpty() ||
                bookmakersData.stream().filter(CheckBox::isSelected).count() < 2 ||
                typesBetData.stream().noneMatch(CheckBox::isSelected) ||
                forkLiveField.getText().isEmpty()) {

                showErrorAlert(grid.getScene().getWindow());
                return;
            }

            try {
                var middlesParse = Integer.parseInt(middlesField.getText());
                if (middlesParse > 1 || middlesParse < -1) throw new RuntimeException();

                var bookmakersParse = bookmakersData.stream().filter(CheckBox::isSelected).map(n -> Bookmakers.valueOf(n.getText())).toList();
                var typesBetParse = typesBetData.stream().filter(CheckBox::isSelected).map(n -> BetType.valueOf(n.getText())).toList();

                Context.parserParams = new Parser.ParserParams(
                    new BigDecimal(minimumField.getText()),
                    new BigDecimal(maximumField.getText()),
                    new BigDecimal(minimumRatioField.getText()),
                    new BigDecimal(maximumRatioField.getText()),
                    middlesParse,
                    bookmakersParse,
                    typesBetParse,
                    new BigDecimal(forkLiveField.getText())
                );
            } catch (Exception e) {
                showErrorAlert(grid.getScene().getWindow());
                return;
            }

            showSuccessAlert(grid.getScene().getWindow());
            Controller.currency.setDisable(false);
        });

        return grid;
    }

    private void showErrorAlert(Window owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText("Корректно заполните все необходимые поля!");
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
