package com.melniknow.fd.betting.bookmakers.impl;

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
import com.melniknow.fd.domain.Sports;
import com.melniknow.fd.utils.BetUtils.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(ChromeDriver driver, Proxy proxy, String link) {
        driver.get(link);
    }

    @Override
    public void clickOnBetType(ChromeDriver driver, Proxy proxy, Parser.BetInfo info, Sports sport) {
        switch (sport) {
            case SOCCER -> {
                switch (info.BK_bet_type()) {
                    case WIN -> SoccerWin.click(driver, proxy, info);
                    case TOTALS -> SoccerTotals.click(driver, proxy, info);
                    case HANDICAP -> SoccerHandicap.click(driver, proxy, info);
                    default -> throw new RuntimeException("BetType`s not supported");
                }
            } case TENNIS -> {
                switch (info.BK_bet_type()) {
                    case WIN -> TennisWin.click(driver, proxy, info);
                    case TOTALS -> TennisTotals.click(driver, proxy, info);
                    case HANDICAP -> TennisHandicap.click(driver, proxy, info);
                    default -> throw new RuntimeException("BetType`s not supported");
                }

            } case BASKETBALL -> {
                switch (info.BK_bet_type()) {
                    case WIN -> BasketballWin.click(driver, proxy, info);
                    case TOTALS -> BasketballTotals.click(driver, proxy, info);
                    case HANDICAP -> BasketballHandicap.click(driver, proxy, info);
                    default -> throw new RuntimeException("BetType`s not supported");
                }
            }
            default -> throw new RuntimeException("Sport`s not supported");
        }
    }

    @Override
    public void enterSumAndCheckCf(ChromeDriver driver, Proxy proxy, Parser.BetInfo info) {

    }

    @Override
    public void placeBet(ChromeDriver driver, Proxy proxy, Parser.BetInfo info) {

    }
}
