package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.Dimension;

import java.math.BigDecimal;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        driver.manage().window().setSize(new Dimension(1000, 1400));
        driver.get(info.BK_href() + "?c=207&u=https://www.188bedt.com");
        Logger.writePrettyJson(info);
    }

    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sports sport) throws InterruptedException {
        switch (info.BK_bet_type()) {
            case WIN, SET_WIN, HALF_WIN -> {
                ClickSportsWin.clickAndReturnBalanceAsRub(Context.screenManager.getScreenForBookmaker(bookmaker), info, sport);
                return BetsSupport.getBalance(Context.screenManager.getScreenForBookmaker(bookmaker), Context.betsParams.get(bookmaker).currency());
            }
            case TOTALS, SET_TOTALS, HALF_TOTALS -> {
                ClickSportsTotals.clickAndReturnBalanceAsRub(Context.screenManager.getScreenForBookmaker(bookmaker), info, sport);
                return BetsSupport.getBalance(Context.screenManager.getScreenForBookmaker(bookmaker), Context.betsParams.get(bookmaker).currency());
            }
            case HANDICAP, SET_HANDICAP, HALF_HANDICAP -> {
                ClickSportHandicap.clickAndReturnBalanceAsRub(Context.screenManager.getScreenForBookmaker(bookmaker), info, sport);
                return BetsSupport.getBalance(Context.screenManager.getScreenForBookmaker(bookmaker), Context.betsParams.get(bookmaker).currency());
            }
            default -> throw new RuntimeException("BetType`s not supported");
        }
    }

    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {
        EnterSumAndCheckCf.enterSumAndCheckCf(Context.screenManager.getScreenForBookmaker(bookmaker), info, sum);
    }


    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info) throws InterruptedException {
        return PlaceBet.placeBet(Context.screenManager.getScreenForBookmaker(bookmaker), info);
    }
}
