package com.melniknow.fd.betting.bookmakers._188bet;

import com.melniknow.fd.Context;
import com.melniknow.fd.betting.bookmakers.SeleniumSupport;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ClickSportsWin {
    static public void click(ChromeDriver driver, Parser.BetInfo info, boolean isNeedToClick) throws InterruptedException {
        var selectionName = "";
        if (info.BK_bet().contains("WIN__P1")) {
            selectionName = BetsSupport.getTeamFirstNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("WIN__P2")) {
            selectionName = BetsSupport.getTeamSecondNameByTitle(info.BK_game());
        } else if (info.BK_bet().contains("WIN__PX")) {
            selectionName = "Draw";
        }

        var marketName = info.BK_market_meta().get("marketName").getAsString();
        var partOfGame = BetsSupport.getPartOfGameByMarketName(marketName);

        if (info.BK_bet().contains("GAME__")) {
            selectionName = info.BK_market_meta().get("selectionName").getAsString();
            partOfGame = "";
        }

        if (!info.BK_bet().contains("GAME__")) {
            marketName = marketName.split(" - ")[0];
        }

        if (info.BK_bet().contains("WIN__2X") || info.BK_bet().contains("WIN__1X") || info.BK_bet().contains("WIN__12")) {
            marketName = info.BK_market_meta().get("marketName").getAsString();
            selectionName = info.BK_market_meta().get("selectionName").getAsString();
            partOfGame = "";
        }

        System.out.println("[188bet] MarketName = " + marketName);
        System.out.println("[188bet] partOfGame = " + partOfGame);
        System.out.println("[188bet] selectionName = " + selectionName);

        var market = BetsSupport.getMarketByMarketName(driver, SeleniumSupport.buildGlobalH4ByText(marketName), partOfGame);

        if (selectionName == null) throw new RuntimeException("selectionName is null WIN [188bet]");

        try {
            var button = BetsSupport.findElementWithClicking(market,
                By.xpath(".//div[contains(translate(text(),' ',''),'" + selectionName.replaceAll("\\s+", "") + "')]"));

            // getText() вернёт строку типа: Jannik Sinner (ITA) \n 1.43 - нам нужна 2-ая строка наш коэффициент
            var cfText = SeleniumSupport.getParentByDeep(button, 2).getText().split("\n")[1];
            var curCf = new BigDecimal(cfText);
            Context.log.info("[188bet]: CurCf from clickOnBetType = " + curCf);
            var inaccuracy = new BigDecimal("0.01");
            if (curCf.subtract(inaccuracy).compareTo(info.BK_cf().setScale(2, RoundingMode.DOWN)) < 0) {
                throw new RuntimeException("[pinnacle]: коэффициент упал - было %s, стало %s".formatted(info.BK_cf().setScale(2, RoundingMode.DOWN), curCf));
            }

            if (isNeedToClick) {
                driver.executeScript("arguments[0].click();", button);
            }
        } catch (NoSuchElementException | StaleElementReferenceException |
                 ElementNotInteractableException e) {
            throw new RuntimeException("[188bet]: Событие пропало со страницы");
        }
    }
}