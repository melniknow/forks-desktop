package com.melniknow.fd.betting.bookmakers._188bet;

public enum PartOfGame {
    totalGame(""),

    firstQuarter("1st Quarter"),
    secondQuarter("2nd Quarter"),
    thirdQuarter("3rd Quarter"),
    fourthQuarter("4th Quarter"),

    firstHalf("1st Half"),
    secondHalf("2nd Half"),

    firstSet("1st Set"),
    secondSet("2nd Set");

    private final String part;

    private PartOfGame(String part) {
        this.part = part;
    }

    public String toString() {
        return this.part;
    }

    public static PartOfGame fromString(String bet_type) {
        if (bet_type.contains("HALF_01__")) {
            return firstHalf;
        } else if (bet_type.contains("HALF_02___")) {
            return secondHalf;
        } else if (bet_type.contains("SET_01___")) {
            return firstSet;
        } else if (bet_type.contains("SET_02__")) {
            return secondSet;
        } else if (bet_type.startsWith("HANDICAP__P1(") || bet_type.startsWith("HANDICAP__P2(") ||
            bet_type.startsWith("TOTALS__UNDER(") || bet_type.startsWith("TOTALS__OVER(") ||
            bet_type.startsWith("WIN__P1") || bet_type.startsWith("WIN__P2")) {
            return totalGame;
        } else {
            throw new RuntimeException("Not supported Handicap [188Bet]");
        }
    }
}
