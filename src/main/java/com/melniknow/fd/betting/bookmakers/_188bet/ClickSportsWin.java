package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.core.Parser;
import com.melniknow.fd.domain.Sports;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;

public class ClickSportsWin {
    static public BigDecimal clickAndReturnBalanceAsRub(ChromeDriver driver, Parser.BetInfo info, Sports sport) throws InterruptedException {
        var selectionName = "";
        if (info.BK_bet().contains("WIN__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("WIN__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else { // WIN__PX, WIN__1X, WIN__12, WIN__X2
            // TODO support others
            throw new RuntimeException("Not supported Win [188Bet]");
        }

        var partOfGame = PartOfGame.fromString(info.BK_bet());

        var market = BetsSupport.getMarketByMarketName(driver,
            BetsSupport.buildH4ByText(info.BK_market_meta().get("marketName").getAsString()), sport, partOfGame);

        BetsSupport.findElementWithClicking(market.getCorrectWebElement(),
            BetsSupport.buildDivByText(selectionName)).click();

        return BetsSupport.getBalance(driver);
    }
}
