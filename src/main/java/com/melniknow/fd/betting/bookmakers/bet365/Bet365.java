package com.melniknow.fd.betting.bookmakers.bet365;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.IBookmaker;
import com.melniknow.fd.betting.bookmakers.ShoulderInfo;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Bookmaker;
import com.melniknow.fd.domain.Sport;
import com.melniknow.fd.utils.BetUtils;
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
            driver.switchTo().window(driver.getWindowHandles().stream().findFirst().orElse(null));
            driver.navigate().to(info.BK_href());
        } catch (TimeoutException e) {
            throw new RuntimeException("[Bet365]: Страница не загружается!");
        }
    }
    // "Asian Handicap (0-0)|+1| +1|University Azzurri FC"
    //
    @Override
    public BigDecimal clickOnBetTypeAndReturnBalanceAsRub(Bookmaker bookmaker, Parser.BetInfo info, Sport sport, boolean isNeedToClick) throws InterruptedException {
        var marketMetaName = info.BK_market_meta().get("name").getAsString();

        return null;
    }

    @Override
    public BetUtils.BetData placeBetAndGetRealCf(Bookmaker bookmaker, Parser.BetInfo info, ShoulderInfo shoulderInfo, BigDecimal sum) throws InterruptedException {
        return null;
    }
}
