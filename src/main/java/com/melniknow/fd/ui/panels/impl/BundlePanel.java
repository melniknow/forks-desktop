package com.melniknow.fd.ui.panels.impl;

import com.google.gson.JsonElement;
import com.melniknow.fd.Context;
import com.melniknow.fd.advanced.BundleSetting;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.ui.panels.IPanel;
import com.melniknow.fd.utils.PanelUtils;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.concurrent.atomic.AtomicInteger;

public class BundlePanel implements IPanel {
    @Override
    public ScrollPane getNode() {
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

        AtomicInteger y = new AtomicInteger();

        var rules = new Label("Настройки связок");
        grid.add(rules, 0, y.get());

        var nameRulesField = new TextField();
        nameRulesField.setPrefHeight(40);
        nameRulesField.setPromptText("Имя правила");
        grid.add(nameRulesField, 1, y.get(), 1, 1);
        var isValue = new CheckBox("Валуй");
        grid.add(isValue, 2, y.getAndIncrement(), 1, 1);

        var isVerifiedValue = new CheckBox("Проверяемый валуй");
        grid.add(isVerifiedValue, 2, y.getAndIncrement(), 1, 1);

        grid.add(new Label("Первое плечо"), 1, y.get());
        var bk1Field = new ComboBox<>(FXCollections.observableArrayList(Bookmaker.values()));
        bk1Field.setPrefHeight(40);
        grid.add(bk1Field, 2, y.getAndIncrement(), 1, 1);

        grid.add(new Label("Второе плечо"), 1, y.get());
        var bk2Field = new ComboBox<>(FXCollections.observableArrayList(Bookmaker.values()));
        bk2Field.setPrefHeight(40);
        grid.add(bk2Field, 2, y.getAndIncrement(), 1, 1);

        var addButton = new Button("Добавить правило");
        addButton.setPrefHeight(40);
        addButton.setDefaultButton(true);
        addButton.setPrefWidth(250);
        grid.add(addButton, 1, y.incrementAndGet(), 2, 1);
        y.incrementAndGet();
        GridPane.setHalignment(addButton, HPos.CENTER);
        GridPane.setMargin(addButton, new Insets(20, 0, 20, 0));

        var list = new Label("Список правил");
        grid.add(list, 0, y.get());

        loadRulesFromDb(grid, y);

        addButton.setOnAction(event -> {
            try {
                if (nameRulesField.getText().isEmpty() || bk1Field.getValue() == null ||
                    bk2Field.getValue() == null || bk2Field.getValue().equals(bk1Field.getValue()) ||
                    (isValue.isSelected() && isVerifiedValue.isSelected()))
                    throw new RuntimeException();

                var bundle = new BundleSetting(nameRulesField.getText(), isValue.isSelected(),
                    isVerifiedValue.isSelected(),
                    bk1Field.getValue(), bk2Field.getValue());

                Context.bundleStorage.add(bundle);
                Context.bundleStorage.saveToDb();

                var delButton = new Button(bundle.name() + " " + bundle.bk1() + " " + bundle.bk2() +
                    " " + (
                    bundle.isValue() ? "Валуй" :
                        bundle.isVerifiedValue() ? "Проверяемый валуй" : "Не валуй")
                );

                GridPane.setHalignment(delButton, HPos.CENTER);

                delButton.setOnAction(ev -> {
                    Context.bundleStorage.remove(bundle);
                    grid.getChildren().remove(delButton);
                    Context.bundleStorage.saveToDb();
                });

                grid.add(delButton, 1, y.getAndIncrement(), 2, 1);
            } catch (Exception e) {
                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Ошибка добавления правила");
            }
        });

        return new ScrollPane(grid);
    }

    private static void loadRulesFromDb(GridPane grid, AtomicInteger y) {
        try {
            var array = Context.profile.json.getAsJsonArray("bundle");
            for (JsonElement jsonElement : array) {
                var bundle = new BundleSetting(jsonElement.getAsJsonObject().getAsJsonPrimitive("name").getAsString(),
                    jsonElement.getAsJsonObject().getAsJsonPrimitive("isValue").getAsBoolean(),
                    jsonElement.getAsJsonObject().getAsJsonPrimitive("isVerifiedValue").getAsBoolean(),
                    Bookmaker.valueOf(jsonElement.getAsJsonObject().getAsJsonPrimitive("bk1").getAsString()),
                    Bookmaker.valueOf(jsonElement.getAsJsonObject().getAsJsonPrimitive("bk2").getAsString()));

                Context.bundleStorage.add(bundle);

                var delButton = new Button(bundle.name() + " " + bundle.bk1() + "-" + bundle.bk2() +
                    " " + (
                    bundle.isValue() ? "Валуй" :
                        bundle.isVerifiedValue() ? "Проверяемый валуй" : "Не валуй"));

                delButton.setOnAction(ev -> {
                    Context.bundleStorage.remove(bundle);
                    grid.getChildren().remove(delButton);
                    Context.bundleStorage.saveToDb();
                });

                GridPane.setHalignment(delButton, HPos.CENTER);

                grid.add(delButton, 1, y.getAndIncrement(), 2, 1);
            }

        } catch (Exception ignored) {
        }
    }
}
