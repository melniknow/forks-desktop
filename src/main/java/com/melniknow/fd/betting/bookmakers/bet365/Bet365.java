package com.melniknow.fd.betting.bookmakers.bet365;

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
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sports sport) throws InterruptedException {
        return null;
    }
    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {

    }
    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info) throws InterruptedException {
        return null;
    }
}
