package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.utils.MathUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class _188Bet implements IBookmaker {

    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
//        Context.log.info("Call openLink _188Bet");
//        try {
//            var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
//
//            driver.manage().window().setSize(new Dimension(1000, 1000));
//            driver.get(info.BK_href() + "?c=207&u=https://www.188bedt.com");
//
//            for (int i = 0; i < 30; ++i) {
//                var balanceButton = new WebDriverWait(driver, Duration.ofSeconds(30)).until(driver1
//                    -> driver1.findElement(By.className("print:text-black/80")).getText());
//                if (balanceButton != null && !balanceButton.isEmpty()) {
//                    balanceButton = balanceButton.substring(4);
//                    balanceButton = balanceButton.replace(",", "");
//                    var balance = new BigDecimal(balanceButton);
//                    if (!balance.equals(BigDecimal.ZERO)) {
//                        break;
//                    }
//                }
//                TimeUnit.SECONDS.sleep(1);
//            }
//
//            var wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//            wait.until(ExpectedConditions.elementToBeClickable(By.id("lc_container")));
//            driver.executeScript("document.getElementById('lc_container').classList.add('hidden');");
//
//        } catch (TimeoutException ignored) {
//            throw new RuntimeException("Page not loading! [188bet]");
//        } catch (InterruptedException e) {
//            throw new RuntimeException();
//        }
    }

    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport) throws InterruptedException {
//        Context.log.info("Call clickOnBetTypeAndReturnBalanceAsRub _188Bet");
//        switch (info.BK_bet_type()) {
//            case WIN, SET_WIN, HALF_WIN, GAME_WIN ->
//                ClickSportsWin.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
//            case TOTALS, SET_TOTALS, HALF_TOTALS ->
//                ClickSportsTotals.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
//            case HANDICAP, SET_HANDICAP, HALF_HANDICAP ->
//                ClickSportHandicap.click(Context.screenManager.getScreenForBookmaker(bookmaker), info);
//            default -> throw new RuntimeException("BetType`s not supported");
//        }
//        return BetsSupport.getBalance(Context.screenManager.getScreenForBookmaker(bookmaker), Context.betsParams.get(bookmaker).currency());
        return new BigDecimal("100000");
    }

    @Override
    public void enterSumAndCheckCf(Bookmaker bookmaker, Parser.BetInfo info, BigDecimal sum) {
//        Context.log.info("Call enterSumAndCheckCf _188Bet");
//        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
//
//        try {
//            var currentCf = BetsSupport.getCurrentCf(driver);
//
//            if (currentCf.compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
//                throw new RuntimeException("betCoef is too low [188bet] - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), currentCf));
//            }
//
//            if (sum.compareTo(new BigDecimal("50")) < 0) {
//                throw new RuntimeException("Very small min Bet [188bet]");
//            }
//
//            var wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//            var enterSnake = wait.until(driver_ -> driver_.findElement(By.cssSelector("[placeholder='Enter Stake']")));
//            enterSnake.sendKeys(sum.toString());
//
//        } catch (TimeoutException e) {
//            BetsSupport.closeBetWindow(driver);
//            BetsSupport.clearPreviousBets(driver);
//            throw new RuntimeException("Closed previous Bets Slip");
//        } catch (RuntimeException e) {
//            BetsSupport.closeBetWindow(driver);
//            BetsSupport.clearPreviousBets(driver);
//            throw new RuntimeException(e.getMessage());
//        }
    }

    private static final By byAccepChanges = By.xpath("//h4[text()='Accept Changes']");
    private static final By byPlaceBet = By.xpath("//h4[text()='Place Bet']");
    private static final By byClosedBet = By.xpath("//h4[text()='One or more of your selections are closed for betting.']");
    private static final By bySuccessBet = By.xpath("//h4[text()='Your bet has been successfully placed.']");
    // Your bet has been successfully placed.


    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, boolean isFirst, BigDecimal cf1) {
//        Context.log.info("Call placeBetAndGetRealCf _188Bet");
//        var driver = Context.screenManager.getScreenForBookmaker(bookmaker);
//        try {
//            waitLoop(driver, info.BK_cf(), cf1, isFirst);
//
//            var realCf = BetsSupport.getCurrentCf(driver);
//            BetsSupport.closeAfterSuccessfulBet(driver);
//            System.out.println("[188bet] Final cf = " + realCf);
//            return realCf;
//        } catch (RuntimeException e) {
//            BetsSupport.closeBetWindow(driver);
//            System.out.println("Don`t Place Bet [188bet]" + e.getMessage());
//            throw new RuntimeException(e.getMessage());
//        } catch (InterruptedException e) {
//            throw new RuntimeException();
//        }
        return info.BK_cf();
    }

    private void waitLoop(ChromeDriver driver, BigDecimal oldCf, BigDecimal cf1, boolean isFirst) throws InterruptedException {
        for (int i = 0; i < 15; ++i) {
            updateOdds(driver, oldCf, cf1, isFirst);
            if (waitSuccess(driver)) {
                return;
            }
        }
        throw new RuntimeException("Плечо не может быть проставлено [188bet]");
    }

    private boolean waitSuccess(ChromeDriver driver) {
        for (int i = 0; i < 15; ++i) {
            try {
                System.out.println("Wait.... [188bet]");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                wait.until(driver1 -> driver1.findElement(bySuccessBet));
                return true;
            } catch (Exception e) {
                if (windowContains(driver, byAccepChanges) || windowContains(driver, byPlaceBet)) {
                    System.out.println("Exit from wait [188bet]");
                    return false;
                }
            }
        }
        throw new RuntimeException("Плечо не может быть проставлено [188bet]");
    }

    private void updateOdds(ChromeDriver driver, BigDecimal oldCf, BigDecimal cf1, boolean isFirst) throws InterruptedException {
        if (clickIfIsClickable(driver, byAccepChanges)) {
            System.out.println("Click byAccepChanges [188bet]");
            TimeUnit.SECONDS.sleep(1);
        }
        if (windowContains(driver, byClosedBet)) {
            System.out.println("Bet is closed =( [188bet]");
            throw new RuntimeException("Bet is closed =( [188bet]");
        }
        var curCf = BetsSupport.getCurrentCf(driver);
        if (curCf.compareTo(oldCf) >= 0) {
            System.out.println("Click Place 1 [188bet]");
            clickIfIsClickable(driver, byPlaceBet);
        } else if (!isFirst) {
            var newIncome = MathUtils.calculateIncome(curCf, cf1);
            System.out.println("[188bet] newIncome = " + newIncome);
            if (newIncome.compareTo(Context.parserParams.maxMinus()) < 0) {
                throw new RuntimeException("Max minus [188bet]: newIncome = " + newIncome);
            } else {
                System.out.println("Click Place 2 [188bet]");
                clickIfIsClickable(driver, byPlaceBet);
            }
        } else {
            throw new RuntimeException("Коэфициент на первом плече упал [188bet]");
        }
    }

    private static boolean clickIfIsClickable(ChromeDriver driver, By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        try {
            var button = wait.until(driver_ -> driver_.findElement(by));
            driver.executeScript("arguments[0].click();", button);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean windowContains(ChromeDriver driver, By by) {
        try {
            var wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.until(
                driver1 -> driver1.findElement(by));
            return true;
        } catch (TimeoutException | NoSuchElementException ignored) {
            return false;
        }
    }
}
