package com.melniknow.fd.betting.bookmakers.bet365;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;

import java.math.BigDecimal;

public class Bet365 implements IBookmaker {
    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        System.out.println("openLink Bet365");
    }
    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport) {
        System.out.println("clickOnBetTypeAndReturnBalanceAsRub Bet365");
        return BigDecimal.valueOf(1_000_000_000L);
    }
    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {
        System.out.println("enterSumAndCheckCf Bet365");
    }
    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info) {
        System.out.println("placeBetAndGetRealCf Bet365");
        return info.BK_cf();
    }
}
