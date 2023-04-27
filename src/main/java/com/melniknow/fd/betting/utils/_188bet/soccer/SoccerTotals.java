package com.melniknow.fd.betting.utils._188bet.soccer;

import com.melniknow.fd.betting.utils.Utils;
import com.melniknow.fd.core.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;

import static com.melniknow.fd.betting.utils.Utils.getParentByDeep;

public class SoccerTotals {

    private static final String firstHalf = "1st Half";
    private static final String secondHalf = "2st Half";

    static public void click(ChromeDriver driver, Parser.BetInfo info) {
        var markets = new WebDriverWait(driver, Duration.ofSeconds(200))
            .until(driver_ -> driver_.findElements(By.xpath("//h4[text()='" + info.BK_market_meta().get("marketName").getAsString() + "']")));

        markets = markets.stream().map(m -> getParentByDeep(m, 5)).toList();

        var goodMarkets = new ArrayList<WebElement>();
        for (var market : markets) {
            // We must ignore totals on 1st and 2nd half
            try {
                market.findElement(By.xpath(".//div[text()='" + firstHalf + "']"));
            } catch (NoSuchElementException e) {
                try {
                    market.findElement(By.xpath(".//div[text()='" + secondHalf + "']"));
                } catch (NoSuchElementException e1) {
                    goodMarkets.add(market);
                }
            }
        }

        if (goodMarkets.size() != 1) {
            throw new RuntimeException("Not Found market");
        }

        var market = goodMarkets.get(0);

        var buttons = market.findElements(By.xpath(
                ".//div[text()='" + info.BK_market_meta().get("selectionName").getAsString() + "']"))
            .stream()
            .map(e -> e.findElement(By.xpath("./..")))
            .toList();

        Objects.requireNonNull(buttons.stream().filter(n -> Utils.getTotalsByStr(n.getText()).equals(info.BK_market_meta().get("line").getAsString())).findAny().orElse(null)).click();
    }
}


/*
Example:
        String data = """
            {\"marketName\":\"Goals: Over \\/ Under\",
            \"selectionName\":\"Over\",
            \"marketId\":117056061,
            \"outcomeId\":9616351025,
            \"original_cf\":\"0.83\",
            \"line\":\"2.5\"}""";

        var parsed = JsonParser.parseString(data).getAsJsonObject();

 */