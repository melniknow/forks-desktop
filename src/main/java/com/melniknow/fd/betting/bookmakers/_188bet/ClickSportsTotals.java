package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Objects;

public class ClickSportsTotals {
    static public void click(ChromeDriver driver, Parser.BetInfo info) throws InterruptedException {
        var marketName = info.BK_market_meta().get("marketName").getAsString();
        var partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);

        marketName = marketName.split(" - ")[0];

        var market = BetsSupport.getMarketByMarketName(driver, SeleniumSupport.buildGlobalH4ByText(marketName), partOfGame);
        var selectionName = info.BK_market_meta().get("selectionName").getAsString();

        var buttons = BetsSupport.findElementsWithClicking(market,
                By.xpath(".//div[contains(translate(text(),' ',''),'" + selectionName.replaceAll("\\s+", "") + "')]"))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        var line = info.BK_market_meta().get("line").getAsString();

        System.out.println("MarketName = " + marketName);
        System.out.println("partOfGame = " + partOfGame);
        System.out.println("line = " + line);

        try {
            var button = Objects.requireNonNull(buttons.stream().filter(
                b -> BetsSupport.getTotalsByStr(b.getText()).equals(line)).findAny().orElse(null));

            driver.executeScript("arguments[0].click();", button);
        } catch (NullPointerException | StaleElementReferenceException |
                 ElementNotInteractableException e) {
            throw new RuntimeException("[188bet]: Событие пропало со страницы");
        }
    }
}