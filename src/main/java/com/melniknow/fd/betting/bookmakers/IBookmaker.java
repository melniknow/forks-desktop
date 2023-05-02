package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;

import java.math.BigDecimal;

public interface IBookmaker {
    void openLink(Bookmaker bookmaker, Parser.BetInfo info);
    BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sports sport) throws InterruptedException;
    void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum);
    BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info) throws InterruptedException;
}