package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.utils._188bet.TotalSportHandicap;
import com.melniknow.fd.betting.utils._188bet.TotalSportsTotals;
import com.melniknow.fd.betting.utils._188bet.TotalsSportsWin;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;

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
                    TotalsSportsWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                case TOTALS ->
                    TotalSportsTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                case HANDICAP ->
                    TotalSportHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                default -> throw new RuntimeException("BetType`s not supported");
            }

    }

    @Override
    public void enterSumAndCheckCf() {

    }

    @Override
    public void placeBet() {

    }
}
