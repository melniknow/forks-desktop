package com.melniknow.fd.betting.bookmakers.parsers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sport;

import java.util.Arrays;

public class PinnacleParser implements IParser {
    private final Parser.BetInfo info;
    private final Sport sport;

    private String marketName;
    private String partOfGame;
    private String selectionName;

    // market - partOfGame - selectionName

    public PinnacleParser(Parser.BetInfo info, Sport sport) {
        this.info = info;
        this.sport = sport;
    }

    @Override
    public ClickBox parse() {
        parseImpl();
        return new ClickBox(marketName + " – " + partOfGame, selectionName);
    }


    private void parseImpl() {
        var betComponents = Arrays.stream(info.BK_bet().split("_")).toList();
        for (var component : betComponents) {
            component = clearComponent(component);
            switch (component) {
                case "WIN" -> marketName = "Money Line" + getMarketPart();
                case "GAME" -> {
                    marketName = "Money Line (Games)";
                    partOfGame = getGameTennis();
                }
                case "TOTALS" -> marketName = "Total" + getMarketPart();
                case "SET" -> partOfGame = getSet(info.BK_bet().split("SET_0")[1].substring(0, 1));
                case "HALF" -> partOfGame = info.BK_bet().contains("01") ? "1st Half" : "2nd Half";
                case "HANDICAP" -> marketName = "Handicap" + getMarketPart();
                case "P1" -> selectionName = info.BK_game().substring(0, info.BK_game().indexOf("vs") - 1);
                case "P2" -> selectionName = info.BK_game().substring(info.BK_game().indexOf("vs") + 3);
                case "OVER" -> selectionName = getSelectionName("Over");
                case "UNDER" -> selectionName = getSelectionName("Under");
            }
        }
        if (partOfGame == null) {
            switch (sport) {
                case SOCCER, TENNIS, HANDBALL, VOLLEYBALL -> partOfGame = "Match";
                case BASKETBALL -> partOfGame = "Game";
                case HOCKEY -> partOfGame = "Regulation Time";
            }
        }
    }

    private String clearComponent(String com) {
        if (com.contains("(")) {
            return com.substring(0, com.indexOf("("));
        }
        return com;
    }

    private String getSet(String set) {
        switch (this.sport) {
            case TENNIS, VOLLEYBALL -> {
                switch (set) {
                    case "1" -> { return "1st Set"; }
                    case "2" -> { return "2nd Set"; }
                    case "3" -> { return "3rd Set"; }
                    case "4" -> { return "4th Set"; }
                }
            }
            case BASKETBALL -> {
                switch (set) {
                    case "1" -> { return "1st Quarter"; }
                    case "2" -> { return "2nd Quarter"; }
                    case "3" -> { return "3rd Quarter"; }
                    case "4" -> { return "4th Quarter"; }
                }
            }
        }
        throw new RuntimeException("Not support Bet Type with Set for sport: " + sport);
    }

    private String getMarketPart() {
        if (sport.equals(Sport.TENNIS) && info.BK_href().contains("(games)")) {
            return " (Games)";
        } else if (sport.equals(Sport.TENNIS)) {
            return " (Sets)";
        }
        return "";
    }

    private String getGameTennis() {
        var betType = info.BK_bet().substring(6);
        betType = removePrefix(betType);
        var set = Integer.parseInt(betType.split("_")[0]);
        betType = betType.substring(betType.indexOf("_") + 1);
        betType = removePrefix(betType);
        var game = Integer.parseInt(betType.split("_")[0]);
        return "Set " + set + " Game " + game;
    }

    private String getSelectionName(String overUnder) {
        var digits = info.BK_bet().substring(info.BK_bet().indexOf("(") + 1);
        digits = digits.substring(0, digits.indexOf(")"));
        return overUnder + " " + digits;
    }

    private String removePrefix(String str) {
        var newStr = str;
        while (newStr.startsWith("_") || newStr.startsWith("0")) {
            newStr = newStr.substring(1);
        }
        return newStr;
    }
}
