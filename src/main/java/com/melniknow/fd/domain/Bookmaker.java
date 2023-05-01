package com.melniknow.fd.domain;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.impl.pinnacle.Pinnacle;
import com.melniknow.fd.betting.bookmakers.impl._188bet._188Bet;

public enum Bookmaker {
    PINNACLE("pinnacle", "https://api.pinnacle.com", new Pinnacle(), true),
    _188BET("188bet", "https://www.188bet.com", new _188Bet(), false);

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
