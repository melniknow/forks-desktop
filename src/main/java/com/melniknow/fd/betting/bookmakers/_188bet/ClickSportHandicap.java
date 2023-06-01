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

public class ClickSportHandicap {
    static public void click(ChromeDriver driver, Parser.BetInfo info, boolean isNeedToClick) throws InterruptedException {
        var selectionName = "";
        if (info.BK_bet().contains("__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else {
            throw new RuntimeException("[188Bet]: Not supported Handicap");
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

        System.out.println("[188bet]: MarketName = " + marketName);
        System.out.println("[188bet]: partOfGame = " + partOfGame);
        System.out.println("[188bet]: line = " + line);

        try {
            var button = Objects.requireNonNull(buttons.stream().filter(
                b -> isGoodLine(BetsSupport.getTotalsByStr(b.getText()), line)).findAny().orElse(null));

            // getText() вернёт строку типа: Handicap \n -2,5 \n 1.43 - нам нужна 3-яя строка наш коэффициент
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