package com.melniknow.fd.domain;

public enum Sport {
    AMERICAN_FOOTBALL("american-football"),
    BASEBALL("baseball"),
    BADMINTON("badminton"),
    BASKETBALL("basketball"),
    CRICKET("cricket"),
    ESPORTS_SOCCER("esports.soccer"),
    ESPORTS_BASKETBALL("esports.basketball"),
    ESPORTS_CS("esports.cs"),
    ESPORTS_DOTA2("esports.dota2"),
    ESPORTS_STARCRAFT("esports.starcraft"),
    ESPORTS_HOCKEY("esports.hockey"),
    ESPORTS_KOG("esports.kog"),
    ESPORTS_LOL("esports.lol"),
    ESPORTS_OVERWATCH("esports.overwatch"),
    ESPORTS_RL("esports.rl"),
    ESPORTS_TENNIS("esports.tennis"),
    FLOORBALL("floorball"),
    FUTSAL("futsal"),
    HANDBALL("handball"),
    HOCKEY("hockey"),
    HORSE_RACING("horse-racing"),
    RUGBY("rugby"),
    RUGBY_LEAGUE("rugby-league"),
    RUGBY_UNION("rugby-union"),
    SOCCER("soccer"),
    TENNIS("tennis"),
    TABLE_TENNIS("table-tennis"),
    VOLLEYBALL("volleyball");

    public final String name;
    Sport(String name) { this.name = name; }
}
