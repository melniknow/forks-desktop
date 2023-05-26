package com.melniknow.fd.betting.bookmakers.pinnacle;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sport;

public class BetTypeParser {
    private Parser.BetInfo info;
    private Sport sport;

    public BetTypeParser(Parser.BetInfo info, Sport sport) {
        this.info = info;
        this.sport = sport;
    }

    public String getMarketName() {
        return "";
    }

    public String getSelectionName() {
        return "";
    }
}
