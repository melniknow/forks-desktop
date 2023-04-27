package com.melniknow.fd.betting.utils._188bet.soccer;

import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.melniknow.fd.betting.utils.Utils.getParentByDeep;

public class SoccerHandicap {

    private static final String firstHalf = "1st Half";
    private static final String secondHalf = "2st Half";

    static public void click(ChromeDriver driver, Parser.BetInfo info) {
        var markets = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElements(By.xpath("//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']")));

        markets = markets.stream().map(m -> getParentByDeep(m, 5)).toList();

        var line = info.BK_market_meta().get("line").getAsString();
        if (info.BK_bet().contains("HALF_01__HANDICAP__")) {
            clickOnMarket(markets, firstHalf, line);
        } else if (info.BK_bet().contains("HALF_02__HANDICAP__")) {
            clickOnMarket(markets, secondHalf, line);
        } else if (info.BK_bet().contains("HANDICAP__")) {
            removeAllAndClick(markets, line);
        } else {
            throw new RuntimeException("Not supported Handicap");
        }
    }

    private static void clickOnMarket(List<WebElement> markets, String half, String line) {
        var market = markets.stream().filter(m -> {
                try {
                    m.findElement(By.xpath(".//span[text()='" + half + "']"));
                    return true;
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        ).toList();

        if (market.size() != 0) {
            throw new RuntimeException("Not found market");
        }

        markets.get(0).findElement(By.xpath(".//h4[text()='" + line + "']")).click();
    }

    private static void removeAllAndClick(List<WebElement> markets, String line) {
        var goodMarkets = new ArrayList<WebElement>();
        for (var marker : markets) {
            try {
                // We must ignore totals on 1st and 2nd half
                marker.findElement(By.xpath(".//span[text()='" + firstHalf + "']"));
            } catch (NoSuchElementException e) {
                try {
                    marker.findElement(By.xpath(".//span[text()='" + secondHalf + "']"));
                } catch (NoSuchElementException e1) {
                    goodMarkets.add(marker);
                }
            }
        }
        if (goodMarkets.size() != 1) {
            throw new RuntimeException("Market not found");
        }
        goodMarkets.get(0).findElement(By.xpath(".//h4[text()='" + line + "']")).click();
    }
}