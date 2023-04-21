package com.melniknow.fd.oddscorp;

public enum Bookmakers {
    PINNACLE("pinnacle"),
    _188BET("188bet"),
    BET365("bet365");

    public final String nameInAPI;
    Bookmakers(String nameInAPI) { this.nameInAPI = nameInAPI; }
}
