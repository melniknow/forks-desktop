package com.melniknow.fd.betting.bookmakers.bet365;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.ShoulderInfo;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;

public class Bet365 implements IBookmaker {
    private WebElement curButton;
    private BigDecimal curSum;
    @Override
    public void openLink(Bookmaker bookmaker, Parser.BetInfo info) {
        curButton = null;
        curSum = null;

        Context.log.info("Call openLink Bet365");
        try {
            var driver = Context.screenManager.getScreenForBookmaker(bookmaker);

            driver.manage().window().setSize(new Dimension(1300, 1000));
            driver.get(info.BK_href());
        } catch (TimeoutException e) {
            throw new RuntimeException("[Bet365]: Страница не загружается!");
        }
    }
    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport, boolean isNeedToClick) throws InterruptedException {
        return null;
    }

    @Override
    public BigDecimal placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo, BigDecimal sum) throws InterruptedException {
        return null;
    }
}
