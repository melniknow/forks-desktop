package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;

public class ClickSportsWin {
    static public void click(ChromeDriver driver, Parser.BetInfo info) throws InterruptedException {
        var selectionName = "";
        if (info.BK_bet().contains("WIN__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("WIN__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("WIN__PX")) {
            selectionName = "Draw";
        } else {
            throw new RuntimeException("Not supported Win [188Bet]");
        }

        var marketName = info.BK_market_meta().get("marketName").getAsString();
        var partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);

        System.out.println("MarketName = " + marketName);
        System.out.println("partOfGame = " + partOfGame);
        System.out.println("selectionName = " + selectionName);

        marketName = marketName.split(" - ")[0];

        var market = BetsSupport.getMarketByMarketName(driver, SeleniumSupport.buildGlobalH4ByText(marketName), partOfGame);

        try {
            BetsSupport.findElementWithClicking(market, SeleniumSupport.buildLocalDivByText(selectionName)).click();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Button not found! [188bet]");
        }
    }
}
