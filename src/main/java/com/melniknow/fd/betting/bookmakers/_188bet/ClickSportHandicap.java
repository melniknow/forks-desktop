package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.chrome.ChromeDriver;

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

        if (selectionName == null) throw new RuntimeException("selectionName is null");

        var buttons = BetsSupport.findElementsWithClicking(market,
                By.xpath(".//div[contains(translate(text(),' ',''),'" + selectionName.replaceAll("\\s+", "") + "')]"))
            .stream()
            .map(e -> {
                try {
                    return e.findElement(By.xpath("./.."));
                } catch (StaleElementReferenceException e1) {
                    throw new RuntimeException("[188bet]: Событие пропало со страницы");
                }
            }).toList();

        var line = info.BK_market_meta().get("line").getAsString();

        System.out.println("MarketName = " + marketName);
        System.out.println("partOfGame = " + partOfGame);
        System.out.println("line = " + line);

        try {
            var button = Objects.requireNonNull(buttons.stream().filter(
                b -> isGoodLine(BetsSupport.getTotalsByStr(b.getText()), line)).findAny().orElse(null));

            driver.executeScript("arguments[0].click();", button);
        } catch (NullPointerException | StaleElementReferenceException |
                 ElementNotInteractableException e) {
            throw new RuntimeException("[188bet]: Событие пропало со страницы");
        }
    }

    private static boolean isGoodLine(String title, String line) {
        if (line.startsWith("-")) {
            return title.equals(line);
        }
        if (title.startsWith("+")) {
            title = title.substring(1);
        }
        return title.equals(line);
    }
}