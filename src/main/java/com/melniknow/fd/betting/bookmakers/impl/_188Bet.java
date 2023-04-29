package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.utils._188bet.*;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.Dimension;

import java.math.BigDecimal;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        driver.manage().window().setSize(new Dimension(1000, 1400)); // Need for scrolling
        driver.get(info.BK_href());
    }

    @Override
    public void clickOnBetType(Bookmaker bookmaker, Parser.BetInfo info, Sports sport) {
        if (sport.equals(Sports.BASKETBALL) || sport.equals(Sports.SOCCER) || sport.equals(Sports.TENNIS))
            switch (info.BK_bet_type()) {
                case WIN ->
                    ClickSportsWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info, sport);
                case TOTALS ->
                    ClickSportsTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info, sport);
                case HANDICAP ->
                    ClickSportHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info, sport);
                default -> throw new RuntimeException("BetType`s not supported");
            }
    }

    @Override
    public BigDecimal enterSumAndCheckCf(Bookmaker bookmaker, BigDecimal betCoef, Parser.BetInfo info) {
        return EnterSumAndCheckCf.enterSumAndCheckCf(Context.screenManager.getScreenForBookmaker(bookmaker),
            info, betCoef, Context.betsParams.get(bookmaker));
    }

    @Override
    public void placeBet(Bookmaker bookmaker, BigDecimal betCoef, BigDecimal curCf, Parser.BetInfo info) {
        PlaceBet.PlaceBet(Context.screenManager.getScreenForBookmaker(bookmaker), betCoef, curCf, info);
    }
}
