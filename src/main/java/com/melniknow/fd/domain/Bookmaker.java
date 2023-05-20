package com.melniknow.fd.domain;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.bet365.Bet365;
import com.melniknow.fd.betting.bookmakers.pinnacle.Pinnacle;
import com.melniknow.fd.betting.bookmakers._188bet._188Bet;

public enum Bookmaker {
    PINNACLE("pinnacle", "https://www.pinnacle.com", new Pinnacle(), false),
    BET365("bet365", "https://www.bet365.com", new Bet365(), false),
    _188BET("188bet", "https://www.188bedt.com/en-gb", new _188Bet(), false);

    public final String nameInAPI;
    public final String link;
    public final IBookmaker realization;
    public final boolean isApi;

    Bookmaker(String nameInAPI, String link, IBookmaker realization, boolean isApi) {
        this.nameInAPI = nameInAPI;
        this.link = link;
        this.realization = realization;
        this.isApi = isApi;
    }
}
