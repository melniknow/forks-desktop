package com.melniknow.fd.betting.bookmakers.impl;

import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.utils._188bet.soccer.SoccerHandicap;
import com.melniknow.fd.betting.utils._188bet.soccer.SoccerTotals;
import com.melniknow.fd.betting.utils._188bet.soccer.SoccerWin;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.BetType;
import com.melniknow.fd.domain.Sports;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import com.melniknow.fd.utils.BetUtils.Proxy;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    case WIN -> {
                        new SoccerWin().click(driver, proxy, info);
                    } case TOTALS -> {
                        new SoccerTotals().click(driver, proxy, info);
                    } case HANDICAP -> {
                        new SoccerHandicap().click(driver, proxy, info);
                    } default -> {
                        throw new RuntimeException("Sport`s not BetType");
                    }
                }
            } case TENNIS -> {
                switch (info.BK_bet_type()) {
                    case WIN -> {

                    } case TOTALS -> {

                    } case HANDICAP -> {

                    } default -> {
                        throw new RuntimeException("Sport`s not BetType");
                    }
                }

            } case BASKETBALL -> {
                switch (info.BK_bet_type()) {
                    case WIN -> {

                    } case TOTALS -> {

                    } case HANDICAP -> {

                    } default -> {
                        throw new RuntimeException("Sport`s not BetType");
                    }
                }
            }
            default -> {
                throw new RuntimeException("Sport`s not supported");
            }
        }
    }

    @Override
    public void enterSumAndCheckCf(ChromeDriver driver, Proxy proxy, Parser.BetInfo info) {

    }

    @Override
    public void placeBet(ChromeDriver driver, Proxy proxy, Parser.BetInfo info) {

    }

    private static BigDecimal getTotalsByStr(String str) {
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return BigDecimal.valueOf(Double.parseDouble(m.group(1)));
        }
        throw new RuntimeException("Not found a totals");
    }
}
