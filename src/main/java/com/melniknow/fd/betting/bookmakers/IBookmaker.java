package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;

import java.math.BigDecimal;

public interface IBookmaker {
    void openLink(Bookmaker bookmaker, Parser.BetInfo info);
    BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sports sport);
    BigDecimal enterSumAndGetCf(Bookmaker bookmaker, BigDecimal betCoef, Parser.BetInfo info);
    void placeBet(Bookmaker bookmaker, BigDecimal betCoef, BigDecimal curCf, Parser.BetInfo info) throws InterruptedException;
}
