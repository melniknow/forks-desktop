package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

public class ClickSportHandicap {
    static public void click(ChromeDriver driver, Parser.BetInfo info) throws InterruptedException {
        var selectionName = "";
        if (info.BK_bet().contains("__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else {
            throw new RuntimeException("Not supported Handicap [188Bet]");
        }

        var marketName = info.BK_market_meta().get("marketName").getAsString();
        var partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);

        marketName = marketName.split(" - ")[0];

        var market = BetsSupport.getMarketByMarketName(driver, SeleniumSupport.buildGlobalH4ByText(marketName), partOfGame);

        var buttons = BetsSupport.findElementsWithClicking(market, SeleniumSupport.buildLocalDivByText(selectionName))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        var line = info.BK_market_meta().get("line").getAsString();

        System.out.println("MarketName = " + marketName);
        System.out.println("partOfGame = " + partOfGame);
        System.out.println("line = " + line);

        try {
            var button = Objects.requireNonNull(buttons.stream().filter(
                b -> BetsSupport.getTotalsByStr(b.getText()).contains(line)).findAny().orElse(null));

            driver.executeScript("arguments[0].click();", button);
        } catch (NullPointerException e) {
            throw new RuntimeException("Button not found! [188bet]");
        }
    }
}
