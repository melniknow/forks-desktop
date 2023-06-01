package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ClickSportsTotals {
    static public void click(ChromeDriver driver, Parser.BetInfo info, boolean isNeedToClick) throws InterruptedException {
        var marketName = info.BK_market_meta().get("marketName").getAsString();
        var partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);

        marketName = marketName.split(" - ")[0];

        var market = BetsSupport.getMarketByMarketName(driver, SeleniumSupport.buildGlobalH4ByText(marketName), partOfGame);
        var selectionName = info.BK_market_meta().get("selectionName").getAsString();

        var buttons = BetsSupport.findElementsWithClicking(market,
                By.xpath(".//div[contains(translate(text(),' ',''),'" + selectionName.replaceAll("\\s+", "") + "')]"))
            .stream()
            .map(e -> {
                try {
                    return e.findElement(By.xpath("./.."));
                } catch (StaleElementReferenceException e1) {
                    throw new RuntimeException("[188bet]: Событие пропало со страницы");
                }
            })
            .toList();

        var line = info.BK_market_meta().get("line").getAsString();

        System.out.println("[188Bet]: MarketName = " + marketName);
        System.out.println("[188Bet]: partOfGame = " + partOfGame);
        System.out.println("[188Bet]: line = " + line);

        try {
            var button = Objects.requireNonNull(buttons.stream().filter(
                b -> BetsSupport.getTotalsByStr(b.getText()).equals(line)).findAny().orElse(null));

            // getText() вернёт строку типа: Over \n 5.5 \n 1.43 - нам нужна 3-яя строка наш коэффициент
            var cfText = SeleniumSupport.getParentByDeep(button, 2).getText().split("\n")[2];
            var curCf = new BigDecimal(cfText);
            Context.log.info("[188bet]: CurCf from clickOnBetType = " + curCf);
            var inaccuracy = new BigDecimal("0.01");
            if (curCf.subtract(inaccuracy).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
                throw new RuntimeException("[pinnacle]: коэффициент упал - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), curCf));
            }

            if (isNeedToClick) {
                driver.executeScript("arguments[0].click();", button);
            }

        } catch (NullPointerException | StaleElementReferenceException |
                 ElementNotInteractableException e) {
            throw new RuntimeException("[188bet]: Событие пропало со страницы");
        }
    }
}