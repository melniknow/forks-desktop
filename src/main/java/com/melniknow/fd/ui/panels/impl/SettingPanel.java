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

        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
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

        var minusDeal = new Label("Максимальный минус при перекрытии *");
        grid.add(minusDeal, 0, y);
        var minusDealField = new TextField();
        profileTextCheck("minusDealField", minusDealField);
        minusDealField.setPromptText("-6");
        minusDealField.setPrefHeight(40);
        grid.add(minusDealField, 1, y++);

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

        // ---------
        var AMERICAN_FOOTBALL = new CheckBox("AMERICAN_FOOTBALL");
        profileBooleanCheck("AMERICAN_FOOTBALL".toLowerCase(), AMERICAN_FOOTBALL);
        grid.add(AMERICAN_FOOTBALL, 1, y++);
        y = viewTypesForSport("AMERICAN_FOOTBALL".toLowerCase(), grid, y);
        var BASEBALL = new CheckBox("BASEBALL");
        profileBooleanCheck("BASEBALL".toLowerCase(), BASEBALL);
        grid.add(BASEBALL, 1, y++);
        y = viewTypesForSport("BASEBALL".toLowerCase(), grid, y);
        var BADMINTON = new CheckBox("BADMINTON");
        profileBooleanCheck("BADMINTON".toLowerCase(), BADMINTON);
        grid.add(BADMINTON, 1, y++);
        y = viewTypesForSport("BADMINTON".toLowerCase(), grid, y);
        var CRICKET = new CheckBox("CRICKET");
        profileBooleanCheck("CRICKET".toLowerCase(), CRICKET);
        grid.add(CRICKET, 1, y++);
        y = viewTypesForSport("CRICKET".toLowerCase(), grid, y);
        var ESPORTS_SOCCER = new CheckBox("ESPORTS_SOCCER");
        profileBooleanCheck("ESPORTS_SOCCER".toLowerCase(), ESPORTS_SOCCER);
        grid.add(ESPORTS_SOCCER, 1, y++);
        y = viewTypesForSport("ESPORTS_SOCCER".toLowerCase(), grid, y);
        var ESPORTS_BASKETBALL = new CheckBox("ESPORTS_BASKETBALL");
        profileBooleanCheck("ESPORTS_BASKETBALL".toLowerCase(), ESPORTS_BASKETBALL);
        grid.add(ESPORTS_BASKETBALL, 1, y++);
        y = viewTypesForSport("ESPORTS_BASKETBALL".toLowerCase(), grid, y);
        var ESPORTS_CS = new CheckBox("ESPORTS_CS");
        profileBooleanCheck("ESPORTS_CS".toLowerCase(), ESPORTS_CS);
        grid.add(ESPORTS_CS, 1, y++);
        y = viewTypesForSport("ESPORTS_CS".toLowerCase(), grid, y);
        var ESPORTS_DOTA2 = new CheckBox("ESPORTS_DOTA2");
        profileBooleanCheck("ESPORTS_DOTA2".toLowerCase(), ESPORTS_DOTA2);
        grid.add(ESPORTS_DOTA2, 1, y++);
        y = viewTypesForSport("ESPORTS_DOTA2".toLowerCase(), grid, y);
        var ESPORTS_STARCRAFT = new CheckBox("ESPORTS_STARCRAFT");
        profileBooleanCheck("ESPORTS_STARCRAFT".toLowerCase(), ESPORTS_STARCRAFT);
        grid.add(ESPORTS_STARCRAFT, 1, y++);
        y = viewTypesForSport("ESPORTS_STARCRAFT".toLowerCase(), grid, y);
        var ESPORTS_HOCKEY = new CheckBox("ESPORTS_HOCKEY");
        profileBooleanCheck("ESPORTS_HOCKEY".toLowerCase(), ESPORTS_HOCKEY);
        grid.add(ESPORTS_HOCKEY, 1, y++);
        y = viewTypesForSport("ESPORTS_HOCKEY".toLowerCase(), grid, y);
        var ESPORTS_KOG = new CheckBox("ESPORTS_KOG");
        profileBooleanCheck("ESPORTS_KOG".toLowerCase(), ESPORTS_KOG);
        grid.add(ESPORTS_KOG, 1, y++);
        y = viewTypesForSport("ESPORTS_KOG".toLowerCase(), grid, y);
        var ESPORTS_LOL = new CheckBox("ESPORTS_LOL");
        profileBooleanCheck("ESPORTS_LOL".toLowerCase(), ESPORTS_LOL);
        grid.add(ESPORTS_LOL, 1, y++);
        y = viewTypesForSport("ESPORTS_LOL".toLowerCase(), grid, y);
        var ESPORTS_OVERWATCH = new CheckBox("ESPORTS_OVERWATCH");
        profileBooleanCheck("ESPORTS_OVERWATCH".toLowerCase(), ESPORTS_OVERWATCH);
        grid.add(ESPORTS_OVERWATCH, 1, y++);
        y = viewTypesForSport("ESPORTS_OVERWATCH".toLowerCase(), grid, y);
        var ESPORTS_RL = new CheckBox("ESPORTS_RL");
        profileBooleanCheck("ESPORTS_RL".toLowerCase(), ESPORTS_RL);
        grid.add(ESPORTS_RL, 1, y++);
        y = viewTypesForSport("ESPORTS_RL".toLowerCase(), grid, y);
        var ESPORTS_TENNIS = new CheckBox("ESPORTS_TENNIS");
        profileBooleanCheck("ESPORTS_TENNIS".toLowerCase(), ESPORTS_TENNIS);
        grid.add(ESPORTS_TENNIS, 1, y++);
        y = viewTypesForSport("ESPORTS_TENNIS".toLowerCase(), grid, y);
        var FLOORBALL = new CheckBox("FLOORBALL");
        profileBooleanCheck("FLOORBALL".toLowerCase(), FLOORBALL);
        grid.add(FLOORBALL, 1, y++);
        y = viewTypesForSport("FLOORBALL".toLowerCase(), grid, y);
        var FUTSAL = new CheckBox("FUTSAL");
        profileBooleanCheck("FUTSAL".toLowerCase(), FUTSAL);
        grid.add(FUTSAL, 1, y++);
        y = viewTypesForSport("FUTSAL".toLowerCase(), grid, y);
        var HORSE_RACING = new CheckBox("HORSE_RACING");
        profileBooleanCheck("HORSE_RACING".toLowerCase(), HORSE_RACING);
        grid.add(HORSE_RACING, 1, y++);
        y = viewTypesForSport("HORSE_RACING".toLowerCase(), grid, y);
        var RUGBY = new CheckBox("RUGBY");
        profileBooleanCheck("RUGBY".toLowerCase(), RUGBY);
        grid.add(RUGBY, 1, y++);
        y = viewTypesForSport("RUGBY".toLowerCase(), grid, y);
        var RUGBY_LEAGUE = new CheckBox("RUGBY_LEAGUE");
        profileBooleanCheck("RUGBY_LEAGUE".toLowerCase(), RUGBY_LEAGUE);
        grid.add(RUGBY_LEAGUE, 1, y++);
        y = viewTypesForSport("RUGBY_LEAGUE".toLowerCase(), grid, y);
        var RUGBY_UNION = new CheckBox("RUGBY_UNION");
        profileBooleanCheck("RUGBY_UNION".toLowerCase(), RUGBY_UNION);
        grid.add(RUGBY_UNION, 1, y++);
        y = viewTypesForSport("RUGBY_UNION".toLowerCase(), grid, y);
        var TABLE_TENNIS = new CheckBox("TABLE_TENNIS");
        profileBooleanCheck("TABLE_TENNIS".toLowerCase(), TABLE_TENNIS);
        grid.add(TABLE_TENNIS, 1, y++);
        y = viewTypesForSport("TABLE_TENNIS".toLowerCase(), grid, y);
        // ---------

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
                add(AMERICAN_FOOTBALL);
                add(BASEBALL);
                add(BADMINTON);
                add(CRICKET);
                add(ESPORTS_SOCCER);
                add(ESPORTS_BASKETBALL);
                add(ESPORTS_CS);
                add(ESPORTS_DOTA2);
                add(ESPORTS_STARCRAFT);
                add(ESPORTS_HOCKEY);
                add(ESPORTS_KOG);
                add(ESPORTS_LOL);
                add(ESPORTS_OVERWATCH);
                add(ESPORTS_RL);
                add(ESPORTS_TENNIS);
                add(FLOORBALL);
                add(FUTSAL);
                add(HORSE_RACING);
                add(RUGBY);
                add(RUGBY_LEAGUE);
                add(RUGBY_UNION);
                add(TABLE_TENNIS);
            }};

            if (maximumField.getText().isEmpty() || minimumField.getText().isEmpty() ||
                middlesField.getText().isEmpty() ||
                bookmakersData.stream().filter(CheckBox::isSelected).count() < 2 ||
                forkLiveField.getText().isEmpty() || sportsData.stream().noneMatch(CheckBox::isSelected) ||
                pauseAfterSuccessField.getText().isEmpty() ||
                countForkField.getText().isEmpty() || minusDealField.getText().isEmpty()) {

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

                    var CORRECT_SCORE = sportAndBetTypeToCheckbox.get(sport + "CORRECT_SCORE").isSelected();
                    json.addProperty(sport + "CORRECT_SCORE", CORRECT_SCORE);
                    if (CORRECT_SCORE) data.add(BetType.CORRECT_SCORE);

                    var WHO_SCORE = sportAndBetTypeToCheckbox.get(sport + "WHO_SCORE").isSelected();
                    json.addProperty(sport + "WHO_SCORE", WHO_SCORE);
                    if (WHO_SCORE) data.add(BetType.WHO_SCORE);

                    var isSelectedGameWin = sportAndBetTypeToCheckbox.get(sport + "gameWin").isSelected();
                    json.addProperty(sport + "gameWin", isSelectedGameWin);
                    if (isSelectedGameWin) data.add(BetType.GAME_WIN);

                    var WILL_BE_OT = sportAndBetTypeToCheckbox.get(sport + "WILL_BE_OT").isSelected();
                    json.addProperty(sport + "WILL_BE_OT", WILL_BE_OT);
                    if (WILL_BE_OT) data.add(BetType.WILL_BE_OT);

                    var FIRST_TO_SCORE = sportAndBetTypeToCheckbox.get(sport + "FIRST_TO_SCORE").isSelected();
                    json.addProperty(sport + "FIRST_TO_SCORE", FIRST_TO_SCORE);
                    if (FIRST_TO_SCORE) data.add(BetType.FIRST_TO_SCORE);

                    var TOTALS_ODD = sportAndBetTypeToCheckbox.get(sport + "TOTALS_ODD").isSelected();
                    json.addProperty(sport + "TOTALS_ODD", TOTALS_ODD);
                    if (TOTALS_ODD) data.add(BetType.TOTALS_ODD);

                    var TEAM_TOTALS_ODD = sportAndBetTypeToCheckbox.get(sport + "TEAM_TOTALS_ODD").isSelected();
                    json.addProperty(sport + "TEAM_TOTALS_ODD", TEAM_TOTALS_ODD);
                    if (TEAM_TOTALS_ODD) data.add(BetType.TEAM_TOTALS_ODD);

                    var CLEAN_SHEET = sportAndBetTypeToCheckbox.get(sport + "CLEAN_SHEET").isSelected();
                    json.addProperty(sport + "CLEAN_SHEET", CLEAN_SHEET);
                    if (CLEAN_SHEET) data.add(BetType.CLEAN_SHEET);

                    var SETS_TOTALS = sportAndBetTypeToCheckbox.get(sport + "SETS_TOTALS").isSelected();
                    json.addProperty(sport + "SETS_TOTALS", SETS_TOTALS);
                    if (SETS_TOTALS) data.add(BetType.SETS_TOTALS);

                    var SETS_HANDICAP = sportAndBetTypeToCheckbox.get(sport + "SETS_HANDICAP").isSelected();
                    json.addProperty(sport + "SETS_HANDICAP", SETS_HANDICAP);
                    if (SETS_HANDICAP) data.add(BetType.SETS_HANDICAP);

                    var WIN_HALF_MATCH = sportAndBetTypeToCheckbox.get(sport + "WIN_HALF_MATCH").isSelected();
                    json.addProperty(sport + "WIN_HALF_MATCH", WIN_HALF_MATCH);
                    if (WIN_HALF_MATCH) data.add(BetType.WIN_HALF_MATCH);

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
                    middlesParse,
                    bookmakersParse,
                    setBetTypes.stream().toList(),
                    new BigDecimal(forkLiveField.getText()),
                    sportsType,
                    new BigDecimal(pauseAfterSuccessField.getText()),
                    new BigDecimal(countForkField.getText()),
                    isRepeatForkCheckBox.isSelected(),
                    new BigDecimal(minusDealField.getText())
                );

                if (!noChangeBookmakers) {
                    Context.betsParams.clear();
                    Context.screenManager.clear();
                    Controller.runButton.setDisable(true);
                }

                Context.log.info("Сохранили настройки - " + Context.parserParams);
            } catch (Exception e) {
                PanelUtils.showErrorAlert(grid.getScene().getWindow(), "Корректно заполните все необходимые поля!");
                return;
            }

            json.addProperty("minimumField", minimumField.getText());
            json.addProperty("maximumField", maximumField.getText());
            json.addProperty("pauseAfterSuccessField", pauseAfterSuccessField.getText());
            json.addProperty("countForkField", countForkField.getText());
            json.addProperty("minusDealField", minusDealField.getText());
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
            json.addProperty("AMERICAN_FOOTBALL".toLowerCase(), AMERICAN_FOOTBALL.isSelected());
            json.addProperty("BASEBALL".toLowerCase(), BASEBALL.isSelected());
            json.addProperty("BADMINTON".toLowerCase(), BADMINTON.isSelected());
            json.addProperty("CRICKET".toLowerCase(), CRICKET.isSelected());
            json.addProperty("ESPORTS_SOCCER".toLowerCase(), ESPORTS_SOCCER.isSelected());
            json.addProperty("ESPORTS_BASKETBALL".toLowerCase(), ESPORTS_BASKETBALL.isSelected());
            json.addProperty("ESPORTS_CS".toLowerCase(), ESPORTS_CS.isSelected());
            json.addProperty("ESPORTS_DOTA2".toLowerCase(), ESPORTS_DOTA2.isSelected());
            json.addProperty("ESPORTS_STARCRAFT".toLowerCase(), ESPORTS_STARCRAFT.isSelected());
            json.addProperty("ESPORTS_HOCKEY".toLowerCase(), ESPORTS_HOCKEY.isSelected());
            json.addProperty("ESPORTS_KOG".toLowerCase(), ESPORTS_KOG.isSelected());
            json.addProperty("ESPORTS_LOL".toLowerCase(), ESPORTS_LOL.isSelected());
            json.addProperty("ESPORTS_OVERWATCH".toLowerCase(), ESPORTS_OVERWATCH.isSelected());
            json.addProperty("ESPORTS_RL".toLowerCase(), ESPORTS_RL.isSelected());
            json.addProperty("ESPORTS_TENNIS".toLowerCase(), ESPORTS_TENNIS.isSelected());
            json.addProperty("FLOORBALL".toLowerCase(), FLOORBALL.isSelected());
            json.addProperty("FUTSAL".toLowerCase(), FUTSAL.isSelected());
            json.addProperty("HORSE_RACING".toLowerCase(), HORSE_RACING.isSelected());
            json.addProperty("RUGBY".toLowerCase(), RUGBY.isSelected());
            json.addProperty("RUGBY_LEAGUE".toLowerCase(), RUGBY_LEAGUE.isSelected());
            json.addProperty("RUGBY_UNION".toLowerCase(), RUGBY_UNION.isSelected());
            json.addProperty("TABLE_TENNIS".toLowerCase(), TABLE_TENNIS.isSelected());
            json.addProperty("isRepeatForkCheckBox", isRepeatForkCheckBox.isSelected());

            Context.profile.save();
            Controller.currency.setDisable(false);
        });

        return new ScrollPane(grid);
    }
    private int viewTypesForSport(String sport, GridPane grid, int y) {
        var wins = new CheckBox("WIN");
        profileBooleanCheckAndPutHashMap(sport + "wins", wins);
        grid.add(wins, 2, y++);
        var setWin = new CheckBox("SET_WIN");
        profileBooleanCheckAndPutHashMap(sport + "setWin", setWin);
        grid.add(setWin, 2, y++);
        var halfWin = new CheckBox("HALF_WIN");
        profileBooleanCheckAndPutHashMap(sport + "halfWin", halfWin);
        grid.add(halfWin, 2, y++);
        var totals = new CheckBox("TOTALS");
        profileBooleanCheckAndPutHashMap(sport + "totals", totals);
        grid.add(totals, 2, y++);
        var setTotals = new CheckBox("SET_TOTALS");
        profileBooleanCheckAndPutHashMap(sport + "setTotals", setTotals);
        grid.add(setTotals, 2, y++);
        var halfTotals = new CheckBox("HALF_TOTALS");
        profileBooleanCheckAndPutHashMap(sport + "halfTotals", halfTotals);
        grid.add(halfTotals, 2, y++);
        var handicaps = new CheckBox("HANDICAP");
        profileBooleanCheckAndPutHashMap(sport + "handicaps", handicaps);
        grid.add(handicaps, 2, y++);
        var halfHandicap = new CheckBox("HALF_HANDICAP");
        profileBooleanCheckAndPutHashMap(sport + "halfHandicap", halfHandicap);
        grid.add(halfHandicap, 2, y++);
        var setHandicap = new CheckBox("SET_HANDICAP");
        profileBooleanCheckAndPutHashMap(sport + "setHandicap", setHandicap);
        grid.add(setHandicap, 2, y++);
        var gameWin = new CheckBox("GAME_WIN");
        profileBooleanCheckAndPutHashMap(sport + "gameWin", gameWin);
        grid.add(gameWin, 2, y++);
        var CORRECT_SCORE = new CheckBox("CORRECT_SCORE");
        profileBooleanCheckAndPutHashMap(sport + "CORRECT_SCORE", CORRECT_SCORE);
        grid.add(CORRECT_SCORE, 2, y++);
        var WHO_SCORE = new CheckBox("WHO_SCORE");
        profileBooleanCheckAndPutHashMap(sport + "WHO_SCORE", WHO_SCORE);
        grid.add(WHO_SCORE, 2, y++);
        var WILL_BE_OT = new CheckBox("WILL_BE_OT");
        profileBooleanCheckAndPutHashMap(sport + "WILL_BE_OT", WILL_BE_OT);
        grid.add(WILL_BE_OT, 2, y++);
        var FIRST_TO_SCORE = new CheckBox("FIRST_TO_SCORE");
        profileBooleanCheckAndPutHashMap(sport + "FIRST_TO_SCORE", FIRST_TO_SCORE);
        grid.add(FIRST_TO_SCORE, 2, y++);
        var TOTALS_ODD = new CheckBox("TOTALS_ODD");
        profileBooleanCheckAndPutHashMap(sport + "TOTALS_ODD", TOTALS_ODD);
        grid.add(TOTALS_ODD, 2, y++);
        var TEAM_TOTALS_ODD = new CheckBox("TEAM_TOTALS_ODD");
        profileBooleanCheckAndPutHashMap(sport + "TEAM_TOTALS_ODD", TEAM_TOTALS_ODD);
        grid.add(TEAM_TOTALS_ODD, 2, y++);
        var CLEAN_SHEET = new CheckBox("CLEAN_SHEET");
        profileBooleanCheckAndPutHashMap(sport + "CLEAN_SHEET", CLEAN_SHEET);
        grid.add(CLEAN_SHEET, 2, y++);
        var SETS_TOTALS = new CheckBox("SETS_TOTALS");
        profileBooleanCheckAndPutHashMap(sport + "SETS_TOTALS", SETS_TOTALS);
        grid.add(SETS_TOTALS, 2, y++);
        var SETS_HANDICAP = new CheckBox("SETS_HANDICAP");
        profileBooleanCheckAndPutHashMap(sport + "SETS_HANDICAP", SETS_HANDICAP);
        grid.add(SETS_HANDICAP, 2, y++);
        var WIN_HALF_MATCH = new CheckBox("WIN_HALF_MATCH");
        profileBooleanCheckAndPutHashMap(sport + "WIN_HALF_MATCH", WIN_HALF_MATCH);
        grid.add(WIN_HALF_MATCH, 2, y++);
        return y;
    }
    private void profileBooleanCheckAndPutHashMap(String name, CheckBox checkBox) {
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

