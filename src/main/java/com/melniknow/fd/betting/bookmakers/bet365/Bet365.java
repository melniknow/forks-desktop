package com.melniknow.fd.betting.bookmakers.bet365;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;

import java.math.BigDecimal;

public class Bet365 implements IBookmaker {
    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {

    }
    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport) {
        return BigDecimal.valueOf(10000L);
    }
    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {

    }
    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info) {
        return info.BK_cf();
    }
}
