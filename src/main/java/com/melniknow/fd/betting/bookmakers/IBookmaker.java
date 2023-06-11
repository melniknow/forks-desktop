package com.melniknow.fd.betting.bookmakers;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.utils.BetUtils;

import java.math.BigDecimal;

public interface IBookmaker {
    void openLink(Bookmaker bookmaker, Parser.BetInfo info, Sport sport);
    BigDecimal clickOnBetTypeAndReturnBalanceAsRub() throws InterruptedException;

    void enterSum(BigDecimal sum);
    BetUtils.BetData placeBetAndGetRealCf(ShoulderInfo shoulderInfo) throws InterruptedException;
}