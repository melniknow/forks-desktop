package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class ClickSportsTotals {
    static public void click(ChromeDriver driver, Parser.BetInfo info) throws InterruptedException {
        var marketName = info.BK_market_meta().get("marketName").getAsString();
        var partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);

        marketName = marketName.split(" - ")[0];

        var market = BetsSupport.getMarketByMarketName(driver, SeleniumSupport.buildGlobalH4ByText(marketName), partOfGame);

        var buttons = BetsSupport.findElementsWithClicking(market, SeleniumSupport.buildLocalDivByText(
                info.BK_market_meta().get("selectionName").getAsString()))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        var line = info.BK_market_meta().get("line").getAsString();

        Objects.requireNonNull(buttons.stream().filter(n -> BetsSupport.getTotalsByStr(n.getText()).equals(line)).findAny().orElse(null)).click();
    }
}
