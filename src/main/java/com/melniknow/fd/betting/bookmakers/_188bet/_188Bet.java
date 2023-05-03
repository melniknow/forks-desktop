package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Logger;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
        driver.manage().window().setSize(new Dimension(1000, 1400));
        driver.get(info.BK_href() + "?c=207&u=https://www.188bedt.com");
        Logger.writePrettyJson(info);

        var wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("mtWidth")));

        for (int i = 0; i < 30; ++i) {
            var balanceButton = new WebDriverWait(driver, Duration.ofSeconds(1)).until(driver1
                -> driver1.findElement(By.className("print:text-black/80")).getText());
            balanceButton = balanceButton.substring(4);
            balanceButton = balanceButton.replace(",", "");
            var balance = new BigDecimal(balanceButton);
            if (!balance.equals(BigDecimal.ZERO)) {
                return;
            }
        }
        throw new RuntimeException("Page not loading [188bet]");
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
