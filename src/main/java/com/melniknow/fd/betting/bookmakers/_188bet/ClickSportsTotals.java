package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ClickSportsTotals {
    static public WebElement click(ChromeDriver driver, Parser.BetInfo info, boolean isNeedToClick) throws InterruptedException {
        var marketName = info.BK_market_meta().get("marketName").getAsString();
        var partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);

        marketName = marketName.split(" - ")[0];
        var line = info.BK_market_meta().get("line").getAsString();
        Context.log.info("[188bet]: required Cf = " + info.BK_cf() + "\n" +
            "[188Bet]: info.BK_bet() = " + info.BK_bet() + "\n" +
            "[188Bet]: info.BK_game() = " + info.BK_game() + "\n" +
            "[188Bet]: MarketName = " + marketName + "\n" +
            "[188Bet]: partOfGame = " + partOfGame + "\n" +
            "[188Bet]: line = " + line);

        var market = BetsSupport.getMarketByMarketName(driver, marketName, partOfGame);
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

        try {
            var button = Objects.requireNonNull(buttons.stream().filter(
                b -> BetsSupport.getTotalsByStr(b.getText()).equals(line)).findAny().orElse(null));

            // getText() вернёт строку типа: Over \n 5.5 \n 1.43 - нам нужна 3-яя строка наш коэффициент
            var cfText = SeleniumSupport.getParentByDeep(button, 2).getText().split("\n")[2];
            var curCf = new BigDecimal(cfText);
            Context.log.info("[188bet]: CurCf from clickOnBetType = " + curCf);
            var inaccuracy = new BigDecimal("0.01");
            if (curCf.add(inaccuracy).setScale(2, RoundingMode.DOWN).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
                throw new RuntimeException("[188bet]: коэффициент упал - было %s, стало %s"
                    .formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), curCf.setScale(2, RoundingMode.DOWN)));
            }

            return button;
        } catch (NullPointerException | StaleElementReferenceException |
                 ElementNotInteractableException | IndexOutOfBoundsException e) {
            throw new RuntimeException("[188bet]: Событие пропало со страницы");
        }
    }
}