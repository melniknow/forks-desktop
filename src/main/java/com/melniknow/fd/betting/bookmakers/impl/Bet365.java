package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;

import java.math.BigDecimal;

public class Bet365 implements IBookmaker {
    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {

    }

    @Override
    public void clickOnBetType(Bookmaker bookmaker, Parser.BetInfo info, Sports sport) {

    }

    @Override
    public BigDecimal enterSumAndCheckCf(Bookmaker bookmaker, BigDecimal betCoef, Parser.BetInfo info) {
        return null;
    }

    @Override
    public void placeBet() {

    }
}
