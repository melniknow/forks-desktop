package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.utils._188bet.basketball.BasketballHandicap;
import com.melniknow.fd.betting.utils._188bet.basketball.BasketballTotals;
import com.melniknow.fd.betting.utils._188bet.basketball.BasketballWin;
import com.melniknow.fd.betting.utils._188bet.soccer.SoccerHandicap;
import com.melniknow.fd.betting.utils._188bet.soccer.SoccerTotals;
import com.melniknow.fd.betting.utils._188bet.soccer.SoccerWin;
import com.melniknow.fd.betting.utils._188bet.tennis.TennisHandicap;
import com.melniknow.fd.betting.utils._188bet.tennis.TennisTotals;
import com.melniknow.fd.betting.utils._188bet.tennis.TennisWin;
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
        switch (sport) {
            case SOCCER -> {
                switch (info.BK_bet_type()) {
                    case WIN -> SoccerWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    case TOTALS -> SoccerTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    case HANDICAP -> SoccerHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    default -> throw new RuntimeException("BetType`s not supported");
                }
            } case TENNIS -> {
                switch (info.BK_bet_type()) {
                    case WIN -> TennisWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    case TOTALS -> TennisTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    case HANDICAP -> TennisHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    default -> throw new RuntimeException("BetType`s not supported");
                }

            } case BASKETBALL -> {
                switch (info.BK_bet_type()) {
                    case WIN -> BasketballWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    case TOTALS -> BasketballTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    case HANDICAP -> BasketballHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
                    default -> throw new RuntimeException("BetType`s not supported");
                }
            }
            default -> throw new RuntimeException("Sport`s not supported");
        }
    }

    @Override
    public void enterSumAndCheckCf() {

    }

    @Override
    public void placeBet() {

    }
}
