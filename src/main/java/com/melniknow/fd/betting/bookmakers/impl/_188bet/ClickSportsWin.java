package com.melniknow.fd.betting.bookmakers.impl._188bet;

import com.melniknow.fd.betting.bookmakers.impl.BetsSupport;
import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class ClickSportsWin {
    static public void click(ChromeDriver driver, Parser.BetInfo info, Sports sport) {
        var selectionName = "";
        if (info.BK_bet().startsWith("WIN__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().startsWith("WIN__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else { // WIN__PX, WIN__1X, WIN__12, WIN__X2 -
            // TODO support others
            throw new RuntimeException("Not supported Win");
        }

        var market = BetsSupport.getMarketByMarketName(driver,
            By.xpath("//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']"), sport);

        BetsSupport.findElementWithClicking(market.getCorrectWebElement(), By.xpath(
            ".//div[text()='" + selectionName + "']")).click();
    }
}
