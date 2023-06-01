package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;

import java.math.BigDecimal;

public interface IBookmaker {
    void openLink(Bookmaker bookmaker, Parser.BetInfo info);
    BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport, boolean isNeedToClick) throws InterruptedException;
    void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum);
    BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo) throws InterruptedException;
}