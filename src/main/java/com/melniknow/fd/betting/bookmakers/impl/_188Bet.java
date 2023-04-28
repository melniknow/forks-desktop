package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.utils._188bet.ClickSportHandicap;
import com.melniknow.fd.betting.utils._188bet.ClickSportsTotals;
import com.melniknow.fd.betting.utils._188bet.ClickSportsWin;
import com.melniknow.fd.betting.utils._188bet.EnterSumAndCheckCf;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;
import com.melniknow.fd.utils.MathUtils;

import java.math.BigDecimal;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        Context.screenManager.getScreenForBookmaker(bookmaker).get(info.BK_href());
    }

    @Override
    public void clickOnBetType(Bookmaker bookmaker, Parser.BetInfo info, Sports sport) {
        if (sport.equals(Sports.BASKETBALL) || sport.equals(Sports.SOCCER) || sport.equals(Sports.TENNIS))
            switch (info.BK_bet_type()) {
                case WIN ->
                    ClickSportsWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                case TOTALS ->
                    ClickSportsTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                case HANDICAP ->
                    ClickSportHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                default -> throw new RuntimeException("BetType`s not supported");
            }
    }

    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, BigDecimal betCoef, Parser.BetInfo info) {
        EnterSumAndCheckCf.enterSumAndCheckCf(Context.screenManager.getScreenForBookmaker(bookmaker),
            info, betCoef, Context.betsParams.get(bookmaker));
    }

    @Override
    public void placeBet() {

    }
}
