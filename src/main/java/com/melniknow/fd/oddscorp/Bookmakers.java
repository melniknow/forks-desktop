package com.melniknow.fd.oddscorp;

public enum Bookmakers {
    PINNACLE("pinnacle", "https://www.pinnacle.com"),
    _188BET("188bet", "https://www.188bet.com"),
    BET365("bet365", "https://www.bet365.com");

    public final String nameInAPI;
    public final String link;
    Bookmakers(String nameInAPI, String link) {
        this.nameInAPI = nameInAPI;
        this.link = link;
    }
}
