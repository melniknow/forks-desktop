package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.utils.BetUtils;

import java.math.BigDecimal;

public interface IBookmaker {
    void openLink(Bookmaker bookmaker, Parser.BetInfo info);
    BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport, boolean isNeedToClick) throws InterruptedException;
    BetUtils.BetData placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo, BigDecimal sum) throws InterruptedException;
}