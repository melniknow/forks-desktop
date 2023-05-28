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

        if (info.BK_bet().contains("GAME__")) {
            selectionName = info.BK_market_meta().get("selectionName").getAsString();
            partOfGame = "";
        }

        System.out.println("[188bet] MarketName = " + marketName);
        System.out.println("[188bet] partOfGame = " + partOfGame);
        System.out.println("[188bet] selectionName = " + selectionName);

        if (!info.BK_bet().contains("GAME__")) {
            marketName = marketName.split(" - ")[0];
        }

        if (info.BK_bet().contains("WIN__2X") || info.BK_bet().contains("WIN__1X") || info.BK_bet().contains("WIN__12")) {
            marketName = info.BK_market_meta().get("marketName").getAsString();
            selectionName = info.BK_market_meta().get("selectionName").getAsString();
            partOfGame = "";
        }

        var market = BetsSupport.getMarketByMarketName(driver, SeleniumSupport.buildGlobalH4ByText(marketName), partOfGame);

        try {
            var button = BetsSupport.findElementWithClicking(market, SeleniumSupport.buildLocalDivByText(selectionName));
            driver.executeScript("arguments[0].click();", button);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Button not found! [188bet]");
        }
    }
}